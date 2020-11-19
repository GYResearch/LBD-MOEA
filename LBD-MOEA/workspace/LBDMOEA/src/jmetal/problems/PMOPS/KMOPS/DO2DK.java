package jmetal.problems.PMOPS.KMOPS;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.util.JMException;

public class DO2DK extends Problem{
	private int K= 4; //3.4
	
	
	private int s= 1;
	
	
	public DO2DK(String solutionType) throws ClassNotFoundException {
		 this(solutionType, 7,2);
	}

	public DO2DK(String solutionType, Integer numberOfVariables, Integer numberOfObjectives) throws ClassNotFoundException{
		// TODO Auto-generated constructor stub
		numberOfVariables_ = numberOfVariables.intValue();
		numberOfObjectives_ = numberOfObjectives.intValue();
		numberOfConstraints_ = 0;

		problemName_ = "DO2DK";

		lowerLimit_ = new double[numberOfVariables_];
		upperLimit_ = new double[numberOfVariables_];
		// the decision variables m-1 [0,1], others [0,10]
		for (int var = 0; var < numberOfVariables_; var++) {
			lowerLimit_[var] = 0;
			upperLimit_[var] = 1;
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
	
	@Override
	public void evaluate(Solution solution) throws JMException {
		// TODO Auto-generated method stub
				Variable[] gen = solution.getDecisionVariables();

				double[] x = new double[numberOfVariables_];
				double[] f = new double[numberOfObjectives_];
				
				for (int i = 0; i < numberOfVariables_; i++)
					x[i] = gen[i].getValue();
				
				double g = 0.0,r=0.0;
				for(int i=1;i<numberOfVariables_;i++) {
					g+=x[i];
				}
				g=1+9*g/(numberOfVariables_-1);
				
				r=5+10*(x[0]-0.5)*(x[0]-0.5)+Math.cos(2*K*Math.PI*x[0])*Math.pow(2, s/2)/K;
				
				f[0]= g*r*(Math.sin(Math.PI*x[0]/Math.pow(2, s+1)+(1+(Math.pow(2, s)-1)/Math.pow(2, s+2))*Math.PI)+1);
				f[1]= g*r*(Math.cos(Math.PI*x[0]/2+Math.PI)+1);
				
				for (int i = 0; i < numberOfObjectives_; i++)
					solution.setObjective(i, f[i]);
	}

}
