package jmetal.metaheuristics.knee_dominances;

import java.util.ArrayList;
import java.util.List;

public class NormalBoundaryIntersectionGenerateWeight {
	/**
	 * The number of objectives.
	 */
	private int numberOfobjectives;
	/**
	 * The number of outer divisions.
	 */
	private int divisionsOuter;
	/**
	 * The number of inner divisions, or {@code 0} if no inner divisions should
	 * be used.
	 */
	private int divisionsInner;

	/**
	 * constructor
	 * 
	 * @param numberOfObjectives
	 * @param divisionsOuter
	 * @param divisionsInner
	 */
	public NormalBoundaryIntersectionGenerateWeight(int numberOfObjectives,
			int divisionsOuter, int divisionsInner) {
		super();
		this.numberOfobjectives = numberOfObjectives;
		this.divisionsOuter = divisionsOuter;
		this.divisionsInner = divisionsInner;
	}

	public List<double[]> generate() {
		List<double[]> weights = new ArrayList<double[]>();

		if (divisionsInner > 0) {
			if (divisionsOuter >= numberOfobjectives) {
				System.err
						.println("The specified number of outer divisions produces intermediate reference points, recommend setting divisionsOuter < numberOfObjectives.");
			}

			weights = generateWeights(divisionsOuter);

			// offset the inner weights
			List<double[]> inner = generateWeights(divisionsInner);

			for (int i = 0; i < inner.size(); i++) {
				double[] weight = inner.get(i);

				for (int j = 0; j < weight.length; j++) {
					weight[j] = (1.0 / numberOfobjectives + weight[j]) / 2;
				}
			}

			weights.addAll(inner);
		} else {
			if (divisionsOuter < numberOfobjectives) {
				System.err
						.println("No intermediate reference points will be generated for the specified number of divisions, recommend increasing divisions");
			}

			weights = generateWeights(divisionsOuter);
		}
		return weights;
	}

	/**
	 * Generates the reference points (weights) for the given number of
	 * divisions.
	 * 
	 * @param divisions
	 *            the number of divisions
	 * @return the list of reference points
	 */
	private List<double[]> generateWeights(int divisions) {
		List<double[]> result = new ArrayList<double[]>();
		double[] weight = new double[numberOfobjectives];

		generateRecursive(result, weight, numberOfobjectives, divisions,
				divisions, 0);

		return result;
	}

	/**
	 * Generate reference points (weights) recursively.
	 * 
	 * @param weights
	 *            list storing the generated reference points
	 * @param weight
	 *            the partial reference point being recursively generated
	 * @param numberOfObjectives
	 *            the number of objectives
	 * @param left
	 *            the number of remaining divisions
	 * @param total
	 *            the total number of divisions
	 * @param index
	 *            the current index being generated
	 */
	private void generateRecursive(List<double[]> weights, double[] weight,
			int numberOfObjectives, int left, int total, int index) {
		if (index == (numberOfObjectives - 1)) {
			weight[index] = (double) left / total;
			weights.add(weight.clone());
		} else {
			for (int i = 0; i <= left; i += 1) {
				weight[index] = (double) i / total;
				generateRecursive(weights, weight, numberOfObjectives,
						left - i, total, index + 1);
			}
		}
	}

}
