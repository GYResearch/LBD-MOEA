package jmetal.util.comparators;

import java.util.Comparator;

import jmetal.core.Solution;

public class CDAS_Comparator implements Comparator{
	
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
	    double alpha = 0.9; // an constant value is set. 
	  
	    
	    // -1 : solution1 better; 1 solution2 better; 0 equal.
	    double[] x1 = new double[objs];// the objectives
	    double[] x2 = new double[objs];
	    double[] y1 = new double[objs];// the modified objectives
	    double[] y2 = new double[objs];
	    double[] angle1 = new double[objs];// the angles
	    double[] angle2 = new double[objs];
	    
	    double norm1 = 0, norm2=0;
	    
	    for(int i=0;i<objs;i++) {
	    	x1[i] = solution1.getObjective(i);
	    	x2[i] = solution2.getObjective(i);
	    	norm1 += solution1.getObjective(i)*solution1.getObjective(i);
	    	norm2 += solution2.getObjective(i)*solution1.getObjective(i);
	    }
	    norm1 = Math.sqrt(norm1);
	    norm2 = Math.sqrt(norm2);
	    
	    for(int i=0;i<objs;i++) {
	    	double[] declination1 = new double[objs];
	    	double[] declination2 = new double[objs];
	    	declination1[i] = solution1.getObjective(i);
	    	declination2[i] = solution2.getObjective(i);
	    	
	    	angle1[i] = calcAngle(x1, declination1);
	    	angle2[i] = calcAngle(x2, declination2);
	    }
	    
	    for(int i=0;i<objs;i++) {
	    	y1[i] = norm1*Math.sin(angle1[i]+alpha*Math.PI)/Math.sin(alpha*Math.PI); 
	    	y2[i] = norm2*Math.sin(angle2[i]+alpha*Math.PI)/Math.sin(alpha*Math.PI); 
	    }
	    
	    double value1, value2;
	    // for minimization problem;
	    for (int i = 0; i < solution1.numberOfObjectives(); i++) {
	        value1 = y1[i];
	        value2 = y2[i];
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
	
	
	/**
	 * Calc the angel between two vectors
	 * @param A
	 * @param standard
	 * @return
	 */
	public double calcAngle(double[] A, double[] standard){
		int size = A.length;
		double lenA = 0.0, lenSta = 0.0;
		double theta = 0.0;
		
		for(int i=0;i<size;i++) {
			lenA += A[i]*A[i];
			lenSta += standard[i]*standard[i];
		}
		lenA = Math.sqrt(lenA);
		lenSta = Math.sqrt(lenSta);
		
		double tmp = 0.0;
		for(int j=0;j<size;j++) {
			tmp += A[j]*standard[j];
		}
		
		theta = Math.acos(tmp/(lenA*lenSta));
		
		return theta;	
	}
	
}
