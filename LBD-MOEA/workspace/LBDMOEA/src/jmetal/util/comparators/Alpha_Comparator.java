package jmetal.util.comparators;

import java.util.Comparator;

import jmetal.core.Solution;

public class Alpha_Comparator implements Comparator{
	
	/** 
	   * stores a comparator for check the OverallConstraintComparator
	   */
	  private static final Comparator overallConstraintViolationComparator_ =
	                              new OverallConstraintViolationComparator();

	@Override
	public int compare(Object object1, Object object2){
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

	    if (solution1.getOverallConstraintViolation()!= 
	        solution2.getOverallConstraintViolation() &&
	       (solution1.getOverallConstraintViolation() < 0) ||         
	       (solution2.getOverallConstraintViolation() < 0)){            
	      return (overallConstraintViolationComparator_.compare(solution1,solution2));
	    }
	        
	    int objs = solution1.numberOfObjectives();
	    
	    
	    // In the alpha-dominance, a rate vector needs to be set
	    double alpha = 3.0/4.0; // an constant value is set. 
	  
	    
	    // -1 : solution1 better; 1 solution2 better; 0 equal.
	    double[] xy = new double[objs];
	    
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
	        if (value1 < value2) {
	          flag = -1;
	        } else if (value1 > value2) {
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
	
}
