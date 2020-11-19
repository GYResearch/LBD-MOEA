package jmetal.metaheuristics.knee_dominances;
/**
 * this weight generator is different with others.
 * 
 * It is to generate the *unit* reference vectors
 */


import java.util.ArrayList;
import java.util.List;

import jmetal.core.Problem;
import jmetal.util.LatinHypercube;

public class InitWeight {
	public int numberofobjectives;
	public int populationsize;
	
	public InitWeight(int numberofobjectives,int populationsize) {
		super();
		this.numberofobjectives = numberofobjectives;
		this.populationsize =populationsize;
	}
	
	/**
	 * Create the array of the weights
	 * @return
	 */
	public ArrayList<double[]> createWeightArray() {
		ArrayList<double[]> list = new ArrayList<>(); 
		if(numberofobjectives==2) {
			NormalBoundaryIntersectionGenerateWeight nbi = new 
					NormalBoundaryIntersectionGenerateWeight(2, 1, 3); // (2,1,3)
			list = (ArrayList<double[]>) nbi.generate();
		}
		if(numberofobjectives==3) {
			NormalBoundaryIntersectionGenerateWeight nbi = new 
					NormalBoundaryIntersectionGenerateWeight(3, 1, 2);//(3,1,3)
			list = (ArrayList<double[]>) nbi.generate();
		}
		if(numberofobjectives==5) {
			ArrayList<double[]> cadilist = new ArrayList<>(); 
			NormalBoundaryIntersectionGenerateWeight nbi = new 
					NormalBoundaryIntersectionGenerateWeight(5, 1, 3);
			cadilist = (ArrayList<double[]>) nbi.generate();
			int count = 0;
			for(int i=0;i<cadilist.size();i++) {
				count = 0;
				for(int j=0;j<numberofobjectives;j++) {
					if (cadilist.get(i)[j]==0) {
						count++;
					}
				}
				if (count<(numberofobjectives-1)) {  			
					list.add(cadilist.get(i));
				}
						
			}
		}
		if(numberofobjectives==8) {  // (8,1,2)
			ArrayList<double[]> cadilist = new ArrayList<>(); 
			NormalBoundaryIntersectionGenerateWeight nbi = new 
					NormalBoundaryIntersectionGenerateWeight(8, 1, 2);
			cadilist = (ArrayList<double[]>) nbi.generate();
			int count = 0;
			for(int i=0;i<cadilist.size();i++) {
				count = 0;
				for(int j=0;j<numberofobjectives;j++) {
					if (cadilist.get(i)[j]==0) {
						count++;
					}
				}
				if (count<(numberofobjectives-1)) {  				
					list.add(cadilist.get(i));
				}
						
			}
		}
		return list;
	}
	
	

	/**
	 * (m,H1,H2,size) 
	 * generate weight
	 * vectors
	 * 
	 * @return
	 */
	public double[][] createWeight() {
		int H1 = 0, H2 = 0;
		if (numberofobjectives == 2) {//  
			H1 = 1;
			H2 = 3;
			NormalBoundaryIntersectionGenerateWeight nn = new NormalBoundaryIntersectionGenerateWeight(
					numberofobjectives, H1, H2);
			return exchange(nn.generate());
		} else if (numberofobjectives == 3) {//
			H1 = 1;
			H2 = 2;
			NormalBoundaryIntersectionGenerateWeight nn = new NormalBoundaryIntersectionGenerateWeight(
					numberofobjectives, H1, H2);
			return exchange(nn.generate());
		} else if (numberofobjectives == 5) {//  
			H1 = 1;
			H2 = 3;
			NormalBoundaryIntersectionGenerateWeight nn = new NormalBoundaryIntersectionGenerateWeight(
					numberofobjectives, H1, H2);
			return exchange(nn.generate());
		}else if (numberofobjectives == 6) {// 
			H1 = 1;
			H2 = 3;
			NormalBoundaryIntersectionGenerateWeight nn = new NormalBoundaryIntersectionGenerateWeight(
					numberofobjectives, H1, H2);
			return exchange(nn.generate());
		} else if (numberofobjectives == 8) {//  
			H1 = 1;
			H2 = 2;
			NormalBoundaryIntersectionGenerateWeight nn = new NormalBoundaryIntersectionGenerateWeight(
					numberofobjectives, H1, H2);
			return exchange(nn.generate());
		} else if (numberofobjectives == 10) {
			H1 = 1;
			H2 = 2;
			NormalBoundaryIntersectionGenerateWeight nn = new NormalBoundaryIntersectionGenerateWeight(
					numberofobjectives, H1, H2);
			return exchange(nn.generate());
		} else {
			System.out.println("You need to update the InitWeight!!!!");
			return null;
		}

	}
	
	// unit the weight vectors
	public double[][] exchange( List<double[]> list) {
		double[][] temp = new double[populationsize][numberofobjectives];
		for (int i = 0; i < list.size(); i++) {
			double lenth = 0.0;
			for (int j = 0; j < numberofobjectives; j++) {// the lenth of the vector
				lenth+= list.get(i)[j]*list.get(i)[j];
			}
			lenth = Math.sqrt(lenth);
			for (int j = 0; j < numberofobjectives; j++) {// unit
				temp[i][j] = list.get(i)[j]/lenth;
			}
		}
		return temp;
	}

	/**
	 * different preference in different objectives, 
	 * @param n: number of vectors will be generated
	 * @param d: the dimension size
	 * @return
	 */
	public double[][] createWeightD(double[][] preference, int n, int d) {
		// TODO Auto-generated method stub
		List<double[]> list = new ArrayList<>();
		
		double[][] cub = new double[n][d];
		cub = new LatinHypercube().generate(n, d);
		
		for(int i=0;i<n;i++) {
			double[] weight = new double[d];
			for(int j=0;j<d;j++) {
				weight[j] = preference[j][0]+cub[i][j]*(preference[j][1]-preference[j][0]);
			}
			list.add(weight);
		}
		return exchangeD(list);
	}
	
	public double[][] exchangeD( List<double[]> list) {
		double[][] temp = new double[list.size()][numberofobjectives];
		for (int i = 0; i < list.size(); i++) {
			double lenth = 0.0;
			for (int j = 0; j < numberofobjectives; j++) {// the lenth of the vector
				lenth+= list.get(i)[j]*list.get(i)[j];
			}
			lenth = Math.sqrt(lenth);
			for (int j = 0; j < numberofobjectives; j++) {// unit
				temp[i][j] = list.get(i)[j]/lenth;
			}
		}
		return temp;
	}
}
