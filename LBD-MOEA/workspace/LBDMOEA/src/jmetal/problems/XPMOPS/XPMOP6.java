
package jmetal.problems.XPMOPS;

import jmetal.core.*;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.util.JMException;
import jmetal.util.Configuration.*;

/**
 * Class representing problem PMOP6: g_6, l_2, k_6, h_3
 * 
   * You need to set the parameters 
 * int A = 6, B = 1, S = -2; // in knee function
 * int P = 1; // in underlying shape function
 * LINKAGE= TRUE OR FALSE, need linkage function or not...
 */
public class XPMOP6 extends Problem {
	
//	private boolean LINKAGE = true;
	private boolean LINKAGE = false;

	/**
	 * Creates a default PMOP1 problem (7 variables and 3 objectives)
	 * 
	 * @param solutionType
	 *            The solution type must "Real" or "BinaryReal".
	 */
	public XPMOP6(String solutionType) throws ClassNotFoundException {
//		  this(solutionType, 7,2);
		 this(solutionType, 12,3);//K=5 //n=m+k-1 k=5
//		 this(solutionType, 14,5);//(7,3)
//		 this(solutionType, 17,8);//(7,3)
		// this(solutionType, 14,10);//(7,3)
	}

	/**
	 * 
	 * @param solutionType
	 * @param numberOfVariables
	 * @param numberOfObjectives
	 * @throws ClassNotFoundException
	 */
	public XPMOP6(String solutionType, Integer numberOfVariables, Integer numberOfObjectives)
			throws ClassNotFoundException {
		numberOfVariables_ = numberOfVariables.intValue();
		numberOfObjectives_ = numberOfObjectives.intValue();
		numberOfConstraints_ = 0;

		problemName_ = "PMOP6";

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

		double g = 0.0, tmp = 0.0;
		double tmp1 = 0.0, temp = 0.0;
		for (int i = numberOfVariables_ - k; i < numberOfVariables_; i++) {// The
																			// first
																			// variable
																			// is
																			// x[0]
				
				if (LINKAGE==false) {
					tmp1 = x[i];
				}else {
					tmp1 = (1 + Math.cos(0.5 * Math.PI * 0.1 * (i-numberOfObjectives_+2) / (0.1 * k))) * (x[i] - lowerLimit_[i])
							- x[0] * (upperLimit_[i] - lowerLimit_[i]); // l_2 function : transform x[i] to tmp1
				}
				
				temp += tmp1*tmp1-10*Math.cos(2*Math.PI*tmp1)+10; // g_6 function
		}
		g=temp; // g_6 function

		double k_ = 1.0;
		int A = 2, B = 1, S = 2; // in knee function
		int P = 1; // in underlying shape function
		double tmp_ =0.0;
		for (int i = 0; i < numberOfObjectives_ - 1; i++) {// knee function k_6 with no degerneration 
			tmp_ = 2 - (Math.exp(Math.cos(A*Math.pow(x[i], B)*Math.PI))+0.5*Math.pow((Math.cos(A*Math.pow(x[i], B)*Math.PI)-0.5),4))/ (Math.pow(2, S) * A);
			k_ =k_*tmp_;
		}

		k_ = Math.pow(2, k_ /  (numberOfObjectives_ - 1)); //  k_6

		for (int i = 0; i < numberOfObjectives_; i++) // objective function :
														// g_6, k_6,
			f[i] = (1.0 + g) * k_;

		for (int i = 0; i < numberOfObjectives_; i++) {// g_6, k_6, l2, h3
			for (int j = 0; j < numberOfObjectives_ - (i + 1); j++)
				f[i] *= 1-Math.cos(Math.pow(x[j], P)*Math.PI/2);
			if (i != 0) {
				int aux = numberOfObjectives_ - (i + 1);
				f[i] *= 1-Math.sin(Math.pow(x[aux], P)*Math.PI/2);
			} // if
		} // for

		for (int i = 0; i < numberOfObjectives_; i++)
			solution.setObjective(i, f[i]);
	} // evaluate

}
