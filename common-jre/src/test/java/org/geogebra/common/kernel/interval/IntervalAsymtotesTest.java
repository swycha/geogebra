package org.geogebra.common.kernel.interval;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.euclidian.plot.interval.PlotterUtils;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.junit.Ignore;
import org.junit.Test;

public class IntervalAsymtotesTest extends BaseUnitTest {

	@Test
	public void tanX() {
		GeoFunction tanX = add("tan(x)");
		double xMin = -Math.PI/4;
		IntervalTuple range = PlotterUtils.newRange(xMin, (3 * Math.PI) / 4, 8, 8);
		IntervalFunctionSampler sampler =
				new IntervalFunctionSampler(tanX, range, 100);
		IntervalTupleList result = sampler.result();
		assertTrue(result.get(74).y().isEmpty());
	}

	@Test
	public void tanXLeftRangeBorder() {
		GeoFunction tanX = add("tan(x)");
		double xMin = -Math.PI/2;
		IntervalTuple range = PlotterUtils.newRange(xMin, Math.PI / 2, 8, 8);
		IntervalFunctionSampler sampler =
				new IntervalFunctionSampler(tanX, range, 100);
		IntervalTupleList result = sampler.result();
		assertTrue(result.get(0).y().isEmpty() && result.get(99).y().isEmpty());
	}

	@Ignore
	@Test
	public void cscLnXInverseTimesMinusTen() {
		GeoFunction tanX = add("(-10)/csc(ln(x))");
		IntervalTuple range = PlotterUtils.newRange(0, 2, -10, 10);
		IntervalFunctionSampler sampler =
				new IntervalFunctionSampler(tanX, range, 100);
		IntervalTupleList result = sampler.result();
		assertTrue(false);
	}

	@Test
	public void secCscXInverseCutOff() {
		GeoFunction function = add("1/sec(csc(x))");
		IntervalTuple range = PlotterUtils.newRange(-2.9, 2.9, -8, 8);
		IntervalFunctionSampler sampler =
				new IntervalFunctionSampler(function, range, 100);
		IntervalTupleList result = sampler.result();
		List<Integer> cutOffIndexes = Arrays.asList(7, 38, 61, 92);
		for (int index: cutOffIndexes) {
			assertFalse(result.get(index).y().isWhole());
		}
	}

	@Test
	public void secCscXInverseMiddle() {
		GeoFunction function = add("1/sec(csc(x))");
		IntervalTuple range = PlotterUtils.newRange(-0.2, 0.2, -8, 8);
		IntervalFunctionSampler sampler =
				new IntervalFunctionSampler(function, range, 100);
		IntervalTupleList result = sampler.result();
		Interval fill = new Interval(-1, 1);
		for (int i = 39; i < 60; i++) {
			assertTrue(result.get(i).y().almostEqual(fill));
		}
	}

	@Test
	public void cotInverseMiddle() {
		GeoFunction function = add("-9*(cot(-3/x))");
		IntervalTuple range = PlotterUtils.newRange(-0.45, -0.25, -8, 8);
		IntervalFunctionSampler sampler =
				new IntervalFunctionSampler(function, range, 100);
		IntervalTupleList result = sampler.result();
		assertTrue(false);
	}

	@Test
	public void squareRootOfTanInverse() {
		GeoFunction function = add("1/sqrt(tan(x))");
		IntervalTuple range = PlotterUtils.newRange(0, 5, -8, 8);
		IntervalFunctionSampler sampler =
				new IntervalFunctionSampler(function, range, 100);
		IntervalTupleList result = sampler.result();
		for (IntervalTuple tuple: result) {
			assertFalse(tuple.y().isHalfNegativeInfinity());
		}

	}

	@Test
	public void testExtendValuesToInfinity() {
		IntervalTupleList sec = functionValues("sec(x)", -2, 2, -7, 7);
		IntervalTupleList lnln = functionValues("ln(ln(sec(x)))", -2, 2, -7, 7);
		assertFalse(true);
	}

	private IntervalTupleList functionValues(String functionDescription,
			double xmin, double xmax, double ymin, double ymax) {
		GeoFunction function = add(functionDescription);
		IntervalTuple range = PlotterUtils.newRange(-Math.PI, Math.PI, -8, 8);
		IntervalFunctionSampler sampler = PlotterUtils.newSampler(function, range, 100);
		return sampler.result();
	}

}