package jmetal.util.comparators;

import jmetal.core.Solution;

import java.util.Collections;
import java.util.Comparator;

/**
 * This class implements a <code>Comparator</code> (a method for comparing
 * <code>Solution</code> objects) based on a constraint violation test + 
 * dominance checking, as in NSGA-II.
 */
public class Extreme_DominanceComparator2 implements Comparator{
 
  /** 
   * stores a comparator for check the OverallConstraintComparator
   */
  private static final Comparator overallConstraintViolationComparator_ =
                              new OverallConstraintViolationComparator();
 /**
  * Compares two solutions.
  * @param object1 Object representing the first <code>Solution</code>.
  * @param object2 Object representing the second <code>Solution</code>.
  * @return -1, or 0, or 1 if solution1 dominates solution2, both are 
  * non-dominated, or solution1  is dominated by solution22, respectively.
  */
  public int compare(Object object1, Object object2) {
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
                                                
    // Equal number of violated constraints. Applying a dominance Test then
    double value1, value2;
    for (int i = 0; i < solution1.numberOfObjectives(); i++) {
      value1 = solution1.getObjective(i);
      value2 = solution2.getObjective(i);
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
   * Extreme_DOminance_Relationship
   * @param A
   * @param B
   * @param extreme[0]: ideal point 
   * @param extreme[1]: nadir point 
   * @return
   */
  public int compare(Solution A, Solution B, double[][] extreme,double gama)  {
	  
	  if (A.getOverallConstraintViolation()!= 
		        B.getOverallConstraintViolation() &&
		       (A.getOverallConstraintViolation() < 0) ||         
		       (B.getOverallConstraintViolation() < 0)){            
		      return (overallConstraintViolationComparator_.compare(A,B));
	   }

	  int nobj = A.numberOfObjectives();
	  double[] s1 = new double[nobj]; // normalized 
	  double[] s2 = new double[nobj]; // normalized
	  double[] s1_t = new double[nobj]; // tangent angle
	  double[] s2_t = new double[nobj]; // tangent angle
	  
	  int dominate1 ; // dominate1 indicates if some objective of solution1 
      // dominates the same objective in solution2. dominate2
	  int dominate2 ; // is the complementary of dominate1.
	
	  dominate1 = 0 ; 
	  dominate2 = 0 ;
	  
	  int flag; //stores the result of the comparison
	  
	  // normalization
	  for(int i=0;i<nobj;i++) {
		  s1[i] = (A.getObjective(i)-extreme[0][i])/(extreme[1][i]-extreme[0][i]);
		  s2[i] = (B.getObjective(i)-extreme[0][i])/(extreme[1][i]-extreme[0][i]);
	  }
	  
	  // translation
	  s1_t = translate(s1, nobj); //  tangent on each objective 
	  s2_t = translate(s2, nobj); //  tangent on each objective 
	  
	  // the upper and lower limit angle
	  double[] stan1 = new double[2], stan2 = new double[2];
	  
	  stan1[0] = getMin(s1_t);
	  stan1[1] = getMax(s1_t);
	 
	  stan2[0] = getMin(s2_t);
	  stan2[1] = getMax(s2_t);
	  
	  // Comparing solutions among the lower limit angle
	  double[] AB = new double[nobj];
	  double[] BA = new double[nobj];
	  
	  for(int i=0;i<nobj;i++) {
		AB[i] = s2[i] - s1[i];
		BA[i] = -AB[i];
	  }

//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% new %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%  
//	  double gama = 1.0/3;
//	  double gama = 1.0/2;
//	  double gama = 2.0/3;
//	  double gama = 3.0/4;
//	  double gama = 1.0;
//	  double gama = 3.0/2;
//	  double gama = 2.0;
//	  double gama = 5.0/2;
	// -1 : solution1 better; 1 solution2 better; 0 equal.
	  if(calcAngle(AB, s1)<(stan1[1]+s1_t[0])*gama) {//
		  return -1;
	  }else if(calcAngle(BA, s2)<(stan2[1]+s2_t[0])*gama) {//
		  return 1;
	  }else {
		  return 0;
	  }
	  	  
	  
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% old %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%  
//	  int indx = (int) (Math.floor(nobj*Math.random()));
////	  double gama = 2.0/3;
////	  double gama = 3.0/4;
//	  /*------------------ classification -------------------------*/
//	  // -1 : solution1 better; 1 solution2 better; 0 equal.
//	  if(calcAngle(AB, s1)<(stan1[1]+s1_t[indx])*gama) {//
//		  return -1;
//	  }else if(calcAngle(BA, s2)<(stan2[1]+s2_t[indx])*gama) {//
//		  return 1;
//	  }else {
//		  return 0;
//	  }
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%  

	  
  } // compare
  
  /**
   * get Max value
   * @param arr
   * @return
   */
  public static double getMax(double[] arr)  
  {  
      double max = arr[0];  
      for(int i=1;i<arr.length;i++)  
      {  
          if(arr[i]>max)  
              max = arr[i];  
      }  
      return max;  
  }  
  
  /**
   * get Min value
   * @param arr
   * @return
   */
  public static double getMin(double[] arr)  
  {  
      double min = arr[0];  
      for(int i=1;i<arr.length;i++)  
      {  
          if(arr[i]<min)  
        	  min = arr[i];  
      }  
      return min;  
  }
  
  
  /**
   * Do translation of the objectives
   * @param s is already normalized
   * @param objs
   * @return
   */
  	public double[] translate(double[] s,int objs) {
		double[] str = new double[objs];
		double tmp,sum;
		for(int i=0;i<objs;i++) {
			tmp = Math.abs(1 - s[i]);
			sum=0.0;
			for(int j=0;j<objs;j++) {
				if(j!=i)
					sum += (1 - s[i])*(1 - s[i]);
			}
			str[i] = Math.atan(Math.sqrt(sum)/tmp);
		}
		return str;
	}
  
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
  
} // DominanceComparator
