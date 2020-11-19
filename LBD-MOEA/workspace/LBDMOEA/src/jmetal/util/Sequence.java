package jmetal.util;


/**
 * Interface for generating a sequence of real numbers. The nature of the
 * sequence is specific to the implementation; generated sequences may be
 * deterministic or stochastic, uniform or non-uniform, etc. Refer to the
 * implementing class' documentation for details.
 */
public interface Sequence {

	/**
	 * Returns a {@code N x D} matrix of real numbers in the range {@code [0,
	 * 1]}.
	 * 
	 * @param N the number of sample points
	 * @param D the dimension of each sample point
	 * @return a {@code N x D} matrix of real numbers in the range {@code [0,
	 *         1]}
	 */
	public double[][] generate(int N, int D);

}
