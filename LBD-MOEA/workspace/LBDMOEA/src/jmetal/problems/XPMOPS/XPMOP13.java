
package jmetal.problems.XPMOPS;

import jmetal.core.*;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.util.JMException;
import jmetal.util.Configuration.*;

/**
 * Class representing problem PMOP13: g_1, l_1, k_1, h_1 with degeneration. 
 * Only m - 2 variables embedded into the knee function.
   * 
    * You need to set the parameters 
 * int A = 6, B = 1, S = -2; // in knee function
 * int P = 1; // in underlying shape function
 * LINKAGE= TRUE OR FALSE, need linkage function or not...
 */
public class XPMOP13 extends Problem {
	
//	private boolean LINKAGE = true;
	private boolean LINKAGE = false;

	/**
	 * Creates a default PMOP1 problem (7 variables and 3 objectives)
	 * 
	 * @param solutionType
	 *            The solution type must "Real" or "BinaryReal".
	 */
	public XPMOP13(String solutionType) throws ClassNotFoundException {
//		 this(solutionType, 12,3);//K=5 //n=m+k-1 k=5
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
	public XPMOP13(String solutionType, Integer numberOfVariables, Integer numberOfObjectives)
			throws ClassNotFoundException {
		numberOfVariables_ = numberOfVariables.intValue();
		numberOfObjectives_ = numberOfObjectives.intValue();
		numberOfConstraints_ = 0;

		problemName_ = "PMOP13";

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

		double g = Double.MIN_VALUE, tmp1 = 0.0, temp = 0.0;
		for(int i=0;i<k;i++) {
			if (LINKAGE==true) {// linkage function: linear 
				tmp1 = (1+ (i+1)/k)*(x[numberOfObjectives_-1+i]-lowerLimit_[numberOfObjectives_-1+i])
						- x[0]*(upperLimit_[numberOfObjectives_-1+i]-lowerLimit_[numberOfObjectives_-1+i]);
			}else {
				tmp1 = x[numberOfObjectives_-1+i];
			}
			temp = Math.abs(tmp1); // g_1 function
			if (g < temp) {
				g = temp;
			} // g_1 function
		}
		
//		g=0; // to get the solutions on the PoF.

		double k_ = 1.0;
		int A = 2, B = 1, S = -2; // in knee function
		int P = 1; // in underlying shape function
		for (int i = 0; i < numberOfObjectives_ - 2; i++) {// knee function k_1
															// with no
															// degerneration
			double tmp = 5 + 10 * (x[i] - 0.5) * (x[i] - 0.5)
					+ Math.cos(A * Math.PI * Math.pow(x[i], B)) / (Math.pow(2, S) * A);
			k_ =k_*tmp;
		}
		
		k_ = Math.sqrt(k_ / (numberOfObjectives_ - 2)); //

		for (int i = 0; i < numberOfObjectives_; i++) // objective function :
														// g_1, k_1,
			f[i] = (1.0 + g) * k_;

		for (int i = 0; i < numberOfObjectives_; i++) {// g_1, k_1, l1, h1
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
