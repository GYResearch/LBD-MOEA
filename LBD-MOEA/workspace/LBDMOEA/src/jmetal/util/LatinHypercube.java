package jmetal.util;


/**
 * Generates sequences using Latin hypercube sampling (LHS). Each axis is
 * divided into {@code N} stripes and exactly one point may exist in each
 * stripe.
 * <p>
 * References:
 * <ol>
 * <li>McKay M.D., Beckman, R.J., and Conover W.J. "A Comparison of Three
 * Methods for Selecting Values of Input Variables in the Analysis of Output
 * from a Computer Code." Technometrics, 21(2):239-245, 1979.
 * </ol>
 */
public class LatinHypercube implements Sequence {

	/**
	 * Constructs a Latin hypercube sequence generator.
	 */
	public LatinHypercube() {
		super();
	}

	@Override
	public double[][] generate(int N, int D) {
		double[][] result = new double[N][D];
		double[] temp = new double[N];
		double d = 1.0 / N;

		for (int i = 0; i < D; i++) {
			for (int j = 0; j < N; j++) {
				temp[j] = PRNG.nextDouble(j * d, (j + 1) * d);
			}

			PRNG.shuffle(temp);

			for (int j = 0; j < N; j++) {
				result[j][i] = temp[j];
			}
		}

		return result;
	}

}
