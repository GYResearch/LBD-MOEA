
package jmetal.problems.XPMOPS;

import jmetal.core.*;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.util.JMException;
import jmetal.util.Configuration.*;

/**
 * Class representing problem PMOP10: g_3/g_7, l_2, k_5, h_1
  * 
    * You need to set the parameters 
 * int A = 6, B = 1, S = -2; // in knee function
 * int P = 1; // in underlying shape function
 * LINKAGE= TRUE OR FALSE, need linkage function or not...
 */
public class XPMOP10 extends Problem {
	
//	private boolean LINKAGE = true;
	private boolean LINKAGE = false;

	/**
	 * Creates a default PMOP1 problem (7 variables and 3 objectives)
	 * 
	 * @param solutionType
	 *            The solution type must "Real" or "BinaryReal".
	 */
	public XPMOP10(String solutionType) throws ClassNotFoundException {
//		  this(solutionType, 7,2);
//		 this(solutionType, 12,3); 
//		 this(solutionType, 14,5);//(7,3)
		 this(solutionType, 17,8);//(7,3)
		// this(solutionType, 14,10);//(7,3)
	}

	/**
	 * 
	 * @param solutionType
	 * @param numberOfVariables
	 * @param numberOfObjectives
	 * @throws ClassNotFoundException
	 */
	public XPMOP10(String solutionType, Integer numberOfVariables, Integer numberOfObjectives)
			throws ClassNotFoundException {
		numberOfVariables_ = numberOfVariables.intValue();
		numberOfObjectives_ = numberOfObjectives.intValue();
		numberOfConstraints_ = 0;

		problemName_ = "PMOP10";

		lowerLimit_ = new double[numberOfVariables_];
		upperLimit_ = new double[numberOfVariables_];
		// the decision variables m-1 [0,1], others [0,10]
		for (int var = 0; var < numberOfObjectives_ - 1; var++) {
			lowerLimit_[var] = 0.0;
			upperLimit_[var] = 1.0;
		} // for
		for (int var = numberOfObjectives_-1; var < numberOfVariables_; var++) {
			lowerLimit_[var] = 0.0;
			upperLimit_[var] = 10.0;
		} // for

		if (solutionType.compareTo("BinaryReal") == 0)
			solutionType_ = new BinaryRealSolutionType(this);
		else if (solutionType.compareTo("Real") == 0)
			solutionType_ = new RealSolutionType(this);
		else {
			System.out.println("Error: solution type " + solutionType + " invalid");
			System.exit(-1);
		}
	}

	/**
	 * Evaluates a solution
	 * 
	 * @param solution
	 *            The solution to evaluate
	 * @throws JMException
	 */
	public void evaluate(Solution solution) throws JMException {
		Variable[] gen = solution.getDecisionVariables();

		double[] x = new double[numberOfVariables_];
		double[] f = new double[numberOfObjectives_];
		int k = numberOfVariables_ - numberOfObjectives_ + 1;

		for (int i = 0; i < numberOfVariables_; i++)
			x[i] = gen[i].getValue();

		double g3 = 0.0, g7 = 0.0;
		double tmp1 = 0.0, temp = 0.0, temp2=1.0;
		for (int i = numberOfVariables_ - k; i < numberOfVariables_; i++) {// The
																			// first
																			// variable
																			// is
																			// x[0]
				if (LINKAGE==true) {
					tmp1 = (1 + Math.cos(0.5 * Math.PI * 0.1 * (i-numberOfObjectives_+2) / (0.1 * k))) * (x[i] - lowerLimit_[i])
							- x[0] * (upperLimit_[i] - lowerLimit_[i]); // l_2 function : transform x[i] to tmp1
				}else {
					tmp1 = x[i];
				}
					g3+= tmp1*tmp1-10*Math.cos(4*Math.PI*tmp1); //g_3 function
				
				temp+= tmp1*tmp1/4000; //g_7 function
		    	temp2*=Math.cos(tmp1/Math.sqrt(i));//g_7 function
		}
		g3+=1+10*(k);  //g_3 function
		g7= temp-temp2+1; //g_7 function

		
//		g3=1.0;
//		g7=0.0;
		
		double k_=1.0;
	    int A=1,B=1,S=2,l=12; // in knee function  l>=3
	    int P=1; // in underlying shape function
	    for(int i=0;i<numberOfObjectives_-1;i++){// knee function k_5 with no degerneration
	    	double tmp=2+Math.min(Math.sin(2*A*Math.pow(x[i], P)*Math.PI), Math.cos(2*A*Math.pow(x[i], P)*Math.PI-Math.PI/l))/(Math.pow(2, S)*A);
	    	k_=k_*tmp;
	    }
	    k_=Math.pow(k_/(numberOfObjectives_-1),0.2); // k_5
	    
	    
	    for (int i = 0; i < numberOfObjectives_; i++) // objective function : g_3.7, k_5,
	    {
	    	if (i%2==0) {
	    		f[i] = (1.0 + g3) * k_; // odd index
			}else {
				f[i] = (1.0 + g7) * k_; // even index
			}
	    	
	    }

		for (int i = 0; i < numberOfObjectives_; i++) {// g_3.7, k_5, l2, h1
			for (int j = 0; j < numberOfObjectives_ - (i + 1); j++)
				f[i] *= Math.pow(x[j], P);
			if (i != 0) {
				int aux = numberOfObjectives_ - (i + 1);
				f[i] *= 1 - Math.pow(x[aux], P);
			} // if
		} // for
		
		for (int i = 0; i < numberOfObjectives_; i++)
			solution.setObjective(i, f[i]);
	} // evaluate

}
