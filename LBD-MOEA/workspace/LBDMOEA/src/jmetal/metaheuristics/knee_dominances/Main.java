package jmetal.metaheuristics.knee_dominances;

//import com.sun.org.apache.xml.internal.serializer.ToStream;
import jmetal.core.*;
import jmetal.operators.crossover.*;
import jmetal.operators.mutation.*;
import jmetal.operators.selection.*;
import jmetal.problems.*                  ;
import jmetal.problems.PMOPS.KMOPS.DEB2DK;
import jmetal.problems.PMOPS.KMOPS.DEB3DK;
import jmetal.problems.PMOPS.KMOPS.DO2DK;
import jmetal.problems.XPMOPS.*;
import jmetal.problems.PMOPS.KMOPS.CKP;
import jmetal.util.Configuration;
import jmetal.util.JMException;

import java.io.File;
import java.io.IOException;
import java.util.* ;

import java.util.logging.FileHandler;
import java.util.logging.Logger;

import jmetal.qualityIndicator.QualityIndicator;


public class Main {
  public static Logger      logger_ ;      // Logger object
  public static FileHandler fileHandler_ ; // FileHandler object

  /**
   * @param args Command line arguments.
 * @throws Exception 
   */
  public static void main(String [] args) throws 
                                  Exception {
    Problem   problem   ; // The problem to solve
    Algorithm algorithm1 ; // The algorithm to use
    Algorithm algorithm2 ; // The algorithm to use
    Operator  crossover ; // Crossover operator
    Operator  mutation  ; // Mutation operator
    Operator  selection ; // Selection operator
    
    HashMap  parameters ; // Operator parameters
    
    QualityIndicator indicators ; // Object to get quality indicators
   
/*******************************************************************************************************************/
   int count = 0;
   for(int indx=0;indx<1;indx++) {
    
    indicators = null ;
    if (args.length == 1) {
      Object [] params = {"Real"};
      problem = (new ProblemFactory()).getProblem(args[0],params);
    } // if
    else if (args.length == 2) {
      Object [] params = {"Real"};
      problem = (new ProblemFactory()).getProblem(args[0],params);
      indicators = new QualityIndicator(problem, args[1]) ;
    } // if
    else { // Default problem
    	// first test set :  	
//      problem = new DO2DK("Real");    // K= 3/4   //100* 5000
//      problem = new DEB2DK("Real");   // K= 4/5   //100* 1000
//      problem = new CKP("Real");      // K= 4/5   //100* 1000
      problem = new DEB3DK("Real");     // K= 2/3   //105* 1000
    
// second test set :   
//    problem = new XPMOP1("Real");// 3/5/8  //3000    // 105-132-156
//    problem = new XPMOP2("Real");// 3/5/8  //3000  // 105-132-156
//    problem = new XPMOP3("Real");// 3/5/8  //3000	 // 105-132-156
//    problem = new XPMOP4("Real"); // 3/5/8  //10000 // 105-132-156 
//    problem = new XPMOP5("Real"); // 3/5/8  //10000 // 105-132-156
//    problem = new XPMOP6("Real"); // 3/5/8  //3000 // 105-132-156
//    problem = new XPMOP7("Real"); // 3/5/8  //3000 // 105-132-156
//    problem = new XPMOP8("Real"); // 3/5/8  //3000 // 105-132-156
//    problem = new XPMOP9("Real"); // 3/5/8  //3000 // 105-132-156
//    problem = new XPMOP10("Real"); // 3/5/8  //5000 // 105-132-156
//    problem = new XPMOP11("Real"); // 3/5/8  //5000 // 105-132-156
//    problem = new XPMOP12("Real"); // 3/5/8  //5000 // 105-132-156
//    problem = new XPMOP13("Real"); // 3/5/8  //3000 // 105-132-156
//    problem = new XPMOP14("Real"); // 3/5/8  //5000 // 105-132-156     
    } // else 
    
    
    algorithm1 = new LBD_MOEA(problem);  
    
    // Algorithm parameters
  algorithm1.setInputParameter("populationSize",100);
  algorithm1.setInputParameter("maxEvaluations",100*500);

    // Mutation and Crossover for Real codification 
    parameters = new HashMap() ;
    parameters.put("probability", 0.9) ;
    parameters.put("distributionIndex", 20.0) ;
    crossover = CrossoverFactory.getCrossoverOperator("SBXCrossover", parameters);                   

    parameters = new HashMap() ;
    parameters.put("probability", 1.0/problem.getNumberOfVariables()) ;
    parameters.put("distributionIndex", 20.0) ;
    mutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);                    

    // Selection Operator 
    parameters = null ;
    selection = SelectionFactory.getSelectionOperator("BinaryTournament2", parameters) ;                           
//    selection = SelectionFactory.getSelectionOperator("RandomSelection", parameters) ;                           

    // Add the operators to the algorithm
    algorithm1.addOperator("crossover",crossover);
    algorithm1.addOperator("mutation",mutation);
    algorithm1.addOperator("selection",selection);
    
//    algorithm2.addOperator("crossover",crossover);
//    algorithm2.addOperator("mutation",mutation);
//    algorithm2.addOperator("selection",selection);
    
    // Execute the Algorithm
//    long initTime = System.currentTimeMillis();
    SolutionSet population1 = algorithm1.execute();
    System.out.println("------------------------------------------------------------------------");
//    SolutionSet population2 = algorithm2.execute();
//    long estimatedTime = System.currentTimeMillis() - initTime;

    
  String[] problemName = (problem.getClass().toString()).split("\\.");
  int len = problemName.length;
  String pr = problemName[len-1];
  
  String filename1 = "C:\\Users\\Lursonkj\\Desktop\\exp\\"+pr+"\\"+problem.getNumberOfObjectives()+"\\"+indx+".txt";
  //1.  second algorithm
  File file1 = new File(filename1);
  if (!file1.exists()) {	 
		File dir = new File(file1.getParent());
		dir.mkdirs();
	}
  population1.printObjectivesToFile(filename1);
  
    
   }// end for
   /*******************************************************************************************************************/

  } //main
} // NSGAII_main
