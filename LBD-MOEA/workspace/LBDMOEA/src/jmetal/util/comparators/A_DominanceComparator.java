package jmetal.util.comparators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import jmetal.core.Solution;

public class A_DominanceComparator implements Comparator{
	
	/** 
	   * stores a comparator for check the OverallConstraintComparator
	   */
	  private static final Comparator overallConstraintViolationComparator_ =
	                              new OverallConstraintViolationComparator();

	  public int compare(Object o1, Object o2) {
		    if (o1==null)
		      return 1;
		    else if (o2 == null)
		      return -1;
		    
		    double fitness1 = ((Solution)o1).getFitness();
		    double fitness2 = ((Solution)o2).getFitness();
		    if (fitness1 <  fitness2) {
		      return -1;
		    }
		    
		    if (fitness1 >  fitness2) {
		      return 1;
		    }
		    
		    return 0;
		  } // compare 
	
	
	public int compare(Object object1, Object object2, int weight1,int weight2, double alpha){
		// TODO Auto-generated method stub
		if (object1==null)
		      return 1;
		    else if (object2 == null)
		      return -1;
		
		Solution solution1 = (Solution)object1;
	    Solution solution2 = (Solution)object2;

	    int dominate1 ; // dominate1 indicates if some objective of solution1 
	                    // dominates the same objective in solution2. dominate2
	    int dominate2 ; // is the complementary of dominate1.

	    dominate1 = 0 ; 
	    dominate2 = 0 ;
	    
	    int flag; //stores the result of the comparison

	    int objs = solution1.numberOfObjectives();
	  
	    
	    // -1 : solution1 better; 1 solution2 better; 0 equal.
	    double[] xy = new double[objs];
	    
	    
	    int xin = weight1;// find the closest weight
	    int yin = weight2;// find the closest weight
	    
	    for(int i=0;i<objs;i++) {
	    	xy[i] = solution1.getObjective(i) - solution2.getObjective(i);
	    	for(int j=0;j<objs;j++) {
	    		if (j!=i) {
	    			xy[i] += alpha*(solution1.getObjective(j) - solution2.getObjective(j));
				}
	    	}
	    }
	    
	    double value1, value2;
	    // for minimization problem;
	    for (int i = 0; i < solution1.numberOfObjectives(); i++) {
	        value1 = xy[i];
	        value2 = 0;
	        if (value1 < value2 && xin==yin) {
	          flag = -1;
	        } else if (value1 > value2 && xin==yin) {
	          flag = 1;
	        } else {
	          flag = 0;
	        }
	        
	        if (flag == -1) {
	          dominate1 = 1;
	        }
	        
	        if (flag == 1) {
	          dominate2 = 1;           
	        }
	      }
	    
	    if (dominate1 == dominate2) {            
	        return 0; //No one dominate the other
	      }
	      if (dominate1 == 1) {
	        return -1; // solution1 dominate
	      }
	      return 1;    // solution2 dominate 
	    
} // compare
	
	
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
	public double getD2(Solution individual,double[] lambda, int nobj){
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
	
}
