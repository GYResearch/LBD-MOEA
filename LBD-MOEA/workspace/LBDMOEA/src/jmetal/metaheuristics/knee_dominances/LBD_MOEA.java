package jmetal.metaheuristics.knee_dominances;


import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import jmetal.core.*;
import jmetal.encodings.variable.Int;
import jmetal.util.comparators.CrowdingComparator;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.util.*;

/**
 * This class implements the NSGA-II algorithm. 
 * 
 * Self-adjusting parameters in (LBD-MOEA)
 * 
 * LBD-MOEA£º using the weights to partition the objective space and do sorting in the environmental selection.
 * 
 * uses two different dominance ranking methods(KD-dominance, A-dominance)
 * 
 * 
 */
public class LBD_MOEA extends Algorithm {

  /**
   * Constructor
   * @param problem Problem to solve
   */
  public LBD_MOEA(Problem problem) {
    super (problem) ;
  }  

  /**   
   * Runs the NSGA-II algorithm.
   * @return a <code>SolutionSet</code> that is a set of non dominated solutions
   * as a result of the algorithm execution
   * @throws JMException 
   */
  public SolutionSet execute() throws JMException, ClassNotFoundException {
    int populationSize;
    int maxEvaluations;
    int evaluations;

    QualityIndicator indicators; // QualityIndicator object
    int requiredEvaluations; // Use in the example of use of the
                                // indicators object (see below)

    SolutionSet population;
    SolutionSet offspringPopulation;
    SolutionSet union;

    Operator mutationOperator;
    Operator crossoverOperator;
    Operator selectionOperator;

    Distance distance = new Distance();

    //Read the parameters
    populationSize = ((Integer) getInputParameter("populationSize")).intValue();
    maxEvaluations = ((Integer) getInputParameter("maxEvaluations")).intValue();
    indicators = (QualityIndicator) getInputParameter("indicators");

    //Initialize the variables
    population = new SolutionSet(populationSize);
    evaluations = 0;

    requiredEvaluations = 0;

    //Read the operators
    mutationOperator = operators_.get("mutation");
    crossoverOperator = operators_.get("crossover");
    selectionOperator = operators_.get("selection");

    // Create the initial solutionSet
    Solution newSolution;
    for (int i = 0; i < populationSize; i++) {
      newSolution = new Solution(problem_);
      problem_.evaluate(newSolution);
      problem_.evaluateConstraints(newSolution);
      evaluations++;
      population.add(newSolution);
    } //for       
    
    int numberofobjective = problem_.getNumberOfObjectives();

    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%   
    // weight generation 
    ArrayList<double[]> list = new ArrayList<>();
    list = new InitWeight(numberofobjective, populationSize).createWeightArray();
    int weightsize = list.size();
    System.out.println("Weight-Size:"+weightsize);
    double[][] weights = new double[weightsize][numberofobjective];
    updateWeights(weights,list,numberofobjective);
    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%   

    
    
    double[] alpha = new double[weightsize]; 
    for(int i=0;i<alpha.length;i++) {
    	alpha[i] = 0.75;
    }
    double gama = 0.5;
    
 // get extreme points
    double[][] extre = new double[2][problem_.getNumberOfObjectives()]; 
    int nobjs = problem_.getNumberOfObjectives();
    extre = getEXtreme(population); // init (idea point and nadir point)
    
    // Generations 
    while (evaluations < maxEvaluations) {

      // Create the offSpring solutionSet      
      offspringPopulation = new SolutionSet(populationSize);
      Solution[] parents = new Solution[2];
      for (int i = 0; i < (populationSize / 2); i++) { // ./2: because add two solutions into the offspringPopulation
        if (evaluations < maxEvaluations) {
          //obtain parents
          parents[0] = (Solution) selectionOperator.execute(population);
          parents[1] = (Solution) selectionOperator.execute(population);
          Solution[] offSpring = (Solution[]) crossoverOperator.execute(parents);
          mutationOperator.execute(offSpring[0]);
          mutationOperator.execute(offSpring[1]);
          problem_.evaluate(offSpring[0]);
          problem_.evaluateConstraints(offSpring[0]);
          problem_.evaluate(offSpring[1]);
          problem_.evaluateConstraints(offSpring[1]);
          offspringPopulation.add(offSpring[0]);
          offspringPopulation.add(offSpring[1]);
          evaluations += 2;
        } // if                            
      } // for

      // Create the solutionSet union of solutionSet and offSpring
      union = ((SolutionSet) population).union(offspringPopulation);
      
      // update extreme using the whole pop
      updateExtreme(extre,union); 
      

      // each solution associated with a weight
      int[] weightsindx = new int[union.size()];
      weightsindx = sortWeightIndex(union,weights);  
      
      // after a number of generations, tune the weights.
      if (evaluations%(10*populationSize)==0) {
    	  System.out.println("-----------------Tuning weight vectors-----------------");
    	  // count the indices of the weights
	      int[] countindex = new int[weightsize];
	      countindex = countWeights(weightsindx, weightsize);// weights associated with how many solutions
	      updateReferenceVectors(union,weightsindx,countindex, weights, numberofobjective);     
      }
      
      // do ranking on union
      A_Ranking ranking = new A_Ranking(union,weightsindx,alpha);    
      
      int remain = populationSize;
      int index = 0;
      SolutionSet front = null;
      population.clear();      

      // Obtain the next front
      front = ranking.getSubfront(index);      
            
      while ((remain > 0) && (remain >= front.size())) {
    	  
        //Add the individuals of this front
        for (int k = 0; k < front.size(); k++) {
          population.add(front.get(k));
        } // for
        //Decrement remain
        remain = remain - front.size();
        //Obtain the next front
        index++;
        if (remain > 0) {
          front = ranking.getSubfront(index);
        } // if        
      } // while
      // Remain is less than front(index).size, insert only the best one
      if (remain > 0) {  // front contains individuals to insert   
    	
      	int[] weightsindx2 = new int[front.size()];
        weightsindx2 = sortWeightIndex(front,weights);// solution with which weight
        // count the indices of the weights
	    int[] countindex2 = new int[weightsize];
	    countindex2 = countWeights(weightsindx2, weightsize);// weights associated with how many solutions
	      
	    // gama will be self-adjusted during the optimization
        KneeOrientedRanking2   tkranking = new KneeOrientedRanking2(front, extre, gama, weightsindx2,countindex2,
        		evaluations,maxEvaluations);
        
      	int level = tkranking.getNumberOfSubfronts();
      	SolutionSet[] frontClusters = new SolutionSet[front.size()];
  	  	  for(int j=0;j<level;j++) {
  	  			  frontClusters[j]= tkranking.getSubfront(j);
  	  	  }
      	  int remains = remain;
      	  int indexs = 0;
      	  
      	  SolutionSet s_front = null;
      	  s_front = frontClusters[indexs];
      	  
      	  while((remains>0) && (remains >= s_front.size())) {
      		  
            	//Add the individuals of this front
                  for (int k = 0; k < s_front.size(); k++) {
                    population.add(s_front.get(k));
                  } // for
                //Decrement remain
                  remains = remains - s_front.size();
                //Obtain the next front
                  indexs++;
                  if (remains > 0) {
                	  s_front = frontClusters[indexs];
                  } // if        
              } // while
      	  if (remains > 0) {  // front contains individuals to insert                        
                distance.crowdingDistanceAssignment(s_front, problem_.getNumberOfObjectives());
//                distance.crowdingDistanceAssignment2(extre,s_front, problem_.getNumberOfObjectives());
                s_front.sort(new CrowdingComparator());
                for (int k = 0; k < remains; k++) {
                  population.add(s_front.get(k));
                } // for

                remains = 0;
              } // if  

          } // if                               
          remain = 0;      
 
          
          System.out.println("Evolutions: "+evaluations);
	      
	    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%//
	      int gen = evaluations/populationSize;
//	      outputPop2(population,gen); // out the current population
	      //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%//
	           
      
    } // while

    // Return the first non-dominated front
    Ranking ranking = new Ranking(population);
    return ranking.getSubfront(0);
  } // execute
  
  
  
public String getName() {
		return "LBD_MOEA";
	}

public void outputPop2(SolutionSet pop,int gen) {
	  //// new saving method...
  String methodName = this.getName();
  String[] problemName = (problem_.getClass().toString()).split("\\.");
  int len = problemName.length;
  String pr = problemName[len-1]+"-"+problem_.getNumberOfObjectives();
  
  String filename = "C:\\Users\\Lursonkj\\Desktop\\exp\\"+pr+"\\"+gen+".txt";
  File file = new File(filename);
  if (!file.exists()) {
		File dir = new File(file.getParent());
		dir.mkdirs();
	}
  pop.printObjectivesToFile(filename); 
}
  
  
  /**
   * The reference adaption is the shortcoming of the proposed method. 
   * The method is sensitive to the reference vectors 
   */
  public void updateReferenceVectors(SolutionSet pop, int[] weightIndex2solution, int[] countNumberofSolutions, double[][] weight, int nobj) {	  
	  for(int i=0;i<countNumberofSolutions.length;i++) {
		  if (countNumberofSolutions[i]==0) {
			  weight[i] = generateRandomWeight(nobj);
		  }
		  if (countNumberofSolutions[i]==1&&problem_.getNumberOfObjectives()>3) {
			  // this step may show negative influence on some problems
			  double[][] extre = getEXtreme(pop);
			  for(int j=0;j<weightIndex2solution.length;j++) {
				 if(weightIndex2solution[j]==i) {
					 weight[i] = tuneWeight(pop.get(i),extre);
				 }
			 }
		  }
	  }
  }
  
 /**
  * tune weight
  * @param solution
  * @param extre
  * @return
  */
  private double[] tuneWeight(Solution solution,double[][] extre) {
	// TODO Auto-generated method stub
	  int nobj = problem_.getNumberOfObjectives();
	  double[] weight = new double[nobj];
	  double sum = 0.0;
	  for(int i=0;i<nobj;i++) {
		  sum += solution.getObjective(i)-extre[0][i];
	  }
	  for(int j=0;j<nobj;j++) {
		  weight[j] = solution.getObjective(j)/sum;
	  }
	return weight;
}

/**
   * generate a random weight
   * @return
   */
  public double[] generateRandomWeight(int nobj) {
	  double[] rweight = new double[nobj];
	  double sum = 0.0;
	  for(int i=0;i<nobj;i++) {
		  rweight[i] = Math.random();
		  sum=sum+rweight[i];
	  }
	  for(int i=0;i<nobj;i++) {
		  rweight[i] =  rweight[i]/sum;
	  }
	  return rweight;
  }
  
  /**
   * count the number of weights
   * @param weightindex
   * @param weightsize
   * @return
   */
  public int[] countWeights(int[] weightindex, int weightsize) {
	int[] countindex = new int[weightsize];
	int count;  
	for(int i=0;i<weightsize;i++) {
		count = 0;
		for(int j=0;j<weightindex.length;j++) {
			if(i==weightindex[j]) {
				count++;
			}
		}
		countindex[i] = count;
	}
	return countindex;
}
  
  
  /**
   * set the alpha for dominance sorting
   * @param alpha
   * @param location
   * @param clustersize
   * @param weightsize
   * @return
   */
  public double setAlpha(int[] weight, double[] alphalist, int location, int[] clustersize, int weightsize) {
	double updatedAlpha = 0.0;
	int sumofsolutions = clustersize.length;  
	int fixnumber = sumofsolutions/weightsize;
	
	int currentsize = clustersize[location];
	
	// weight[location] indicates the location of the weight. 
	double alpha = alphalist[weight[location]];
	
	if(fixnumber>currentsize) {
		updatedAlpha = alpha*(1-0.1*fixnumber/(0.1*currentsize));
	}else {
		updatedAlpha = alpha*(1+0.1*fixnumber/(0.1*currentsize));
	}
//	
//	System.out.println("Alpha= "+alpha);
	return updatedAlpha;  
  }
  
  
  /**
   * count numbers in an array
   * @param array
   * @return
   */
  public static int[][] countArray(int array[]) {
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		for(int a : array) {
		map.put(a, map.get(a) == null ? 1 : map.get(a) + 1);
		}
		int numberofkeys = map.keySet().size();
		int [][] remember = new int[numberofkeys][2];
		
		int index = 0;
		for(int key : map.keySet()) {
			remember[index][0] = key;
			remember[index][1] = map.get(key);
			index++;
		}
		return remember;
	}
  
  /**
   * get the number of combined solution to each weight
   * @return
   */
  public int[] getClusterSizeforEachWeight(int[] weightIndex) {
	  int size = weightIndex.length;
	  int[] clustersize = new int[size];
	  int[][] countNum = new int[size][2];
	  countNum = countArray(weightIndex);
	  for(int i=0;i<size;i++) {
		  for(int j=0;j<countNum.length;j++) {
			  if(weightIndex[i]==countNum[j][0]) {
				  clustersize[i] = countNum[j][1];
			  }
		  }
	  }
	  return clustersize;
  }
  
  /**
   * Get the extreme points£º using the nadir point to replace the extreme ppints.
   * @return
   */
  public double[][] getEXtreme(SolutionSet ss){
	  if (ss.size()==0) {
		System.out.println("The POP is empty!");
	  }
	  int nobj = ss.get(0).numberOfObjectives();
	  double[][] extre = new double[2][nobj];
	  
	  // tmp1 -- ideal point
	  // tmp2 -- nadir point
	  double tmp1, tmp2;
	  for(int i=0;i<nobj;i++) {
		  tmp1 = Double.MAX_VALUE;
		  tmp2 = Double.MIN_VALUE;
		  for(int j=0;j<ss.size();j++) {
			  if (tmp1>ss.get(j).getObjective(i)) {
				  tmp1 = ss.get(j).getObjective(i); // decrease
			}else if (tmp2<ss.get(j).getObjective(i)) {
				  tmp2 = ss.get(j).getObjective(i); // increase
			}
		  }
		  extre[0][i] = tmp1;// ideal point
		  extre[1][i] = tmp2;// nadir point
	  }
	  return extre;
  }
  
  
  
  /**
   * Update the extreme points
   * extre[0]: ideal point
   * extre[1]: nadir point
   * @param extre
   * @param union
   */
  private void updateExtreme(double[][] extre, SolutionSet union) {
	// TODO Auto-generated method stub
	for(int j=0;j<problem_.getNumberOfObjectives();j++) {  
		for(int i=0;i<union.size();i++) {
			if(union.get(i).getObjective(j)<extre[0][j]) {
				extre[0][j] = union.get(i).getObjective(j);
			}
			if (union.get(i).getObjective(j)>extre[1][j]) {
				extre[1][j] = union.get(i).getObjective(j);
			}
		}
	}
}
  
  /**
   * sort the closest weights
   * @param union
   * @param weight
   * @return
   */
  public int[] sortWeightIndex(SolutionSet union, double[][] weight) {
	  int[] index = new int[union.size()];
	  int nobj = problem_.getNumberOfObjectives();
	 
	  for(int i=0;i<union.size();i++) {
		  index[i] = findIndex(union.get(i), weight, nobj);
	  }
	  return index;
  }
	
	/**
	 * find the closest weight 
	 * @param a
	 * @param weights
	 * @return
	 */
	public int findIndex(Solution a,double[][] weights, int nobj) {
		int index = -1;
		int wsize = weights.length;
		
		// list = [distance + " "+ index]
		ArrayList<String> list= new ArrayList<>();
		
		double d2 = -1;
		for(int i=0;i<wsize;i++) {
			d2 = getD2(a, weights[i], nobj);
			list.add(d2+" "+i);
		}
		Collections.sort(list); // sort the distances 
//		System.out.println(list);
		
		String[] sp = list.get(0).split("\\s+");
		index = Integer.parseInt(sp[1]);
		return index;
	}

	
	/**
	 * calc the distance to the weight
	 * @param individual
	 * @param lambda
	 * @param nobj
	 * @return
	 */
	public double getD2( Solution individual,double[] lambda, int nobj){
	      double d1 = 0.0;
	      double d2 = 0.0;
	      double sum=0.0;
	      
	      for(int i=0;i<nobj;i++){
	          sum +=Math.pow(lambda[i], 2); 
	      }
	      sum = Math.sqrt(sum);
	      
	      for(int j = 0;j<nobj;j++){
	          d1+=Math.abs((individual.getObjective(j) -0)*lambda[j]/sum);
	      }
	      
	      for(int k = 0;k<nobj;k++){
	          d2+=Math.pow((individual.getObjective(k)-(0+d1*lambda[k])),2);
	     }
	      d2 = Math.sqrt(d2);
	      return d2;
	      
	}
	
	/**
	 * Normalize solution
	 * @param extre
	 * @param individual
	 * @return
	 */
	public double[] normalizeSolution(double[][] extre, Solution individual) {
		int nobj = problem_.getNumberOfObjectives();
		double[] normSolution = new double[nobj];
		for(int i=0;i<nobj;i++) {
			normSolution[i] = (individual.getObjective(i)-extre[0][i])/(extre[1][i]-extre[0][i]);
		}
		return normSolution;
	}
	
	
  /**
   * print the weights
   * @param weights
   * @param obj
   */
  public void printWeights(double[][] weights, int objs) {
	int size = weights.length;
	for(int i=0;i<size;i++) {
		for(int j=0;j<objs;j++) {
			System.out.print(weights[i][j]+"\t");
		}
		System.out.println();
	}
  }
  
  private void updateWeights(double[][] weights, ArrayList<double[]> list, int objs) {
	// TODO Auto-generated method stub
	for(int i=0;i<list.size();i++) {
		for(int j=0;j<objs;j++) {
			weights[i][j] = list.get(i)[j];
		}
	}
  }
} //
