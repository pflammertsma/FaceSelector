package nl.cs.uu.faceselector;

import java.util.Collections;
import java.util.LinkedList;

public class GaussianCurveDemo {

	public static void main(final String[] args) {
		LinkedList<Double> vals = new LinkedList<Double>();
		int count = 500 / 2;
		for (int i = 0; i < count; i++) {
			double val = Math.pow((double) i / (double) count - 1, 2) * 10;
			vals.add(val);
			vals.add(-val);
		}
		Collections.sort(vals);
		double sigma = MatrixMath.stdDev(vals);
		double mean = MatrixMath.mean(vals);
		for (double val : vals) {
			System.out.println(
					MatrixMath.format(val)
							+ "\t"
							+ MatrixMath.format(MatrixMath.gaussianFilter(val,
									mean, sigma)));
		}
		System.out.println("sigma:\t" + MatrixMath.format(sigma));
		System.out.println("mean:\t" + MatrixMath.format(mean));
	}

}
