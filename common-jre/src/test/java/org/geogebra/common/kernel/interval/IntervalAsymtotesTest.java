package org.geogebra.common.kernel.interval;

import static org.junit.Assert.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.junit.Test;

public class IntervalAsymtotesTest extends BaseUnitTest {

	@Test
	public void tanX() {
		GeoFunction tanX = add("tan(x)");
		IntervalTuple range = newRange(-Math.PI/4, (3*Math.PI)/4, 8, 8);
		IntervalFunctionSampler sampler =
				new IntervalFunctionSampler(tanX, range, 100);
		IntervalTupleList result = sampler.result();
		assertTrue(result.get(74).y().isEmpty());
	}

	@Test
	public void tanXLeftRangeBorder() {
		GeoFunction tanX = add("tan(x)");
		IntervalTuple range = newRange(-Math.PI/2, Math.PI/2, 8, 8);
		IntervalFunctionSampler sampler =
				new IntervalFunctionSampler(tanX, range, 100);
		IntervalTupleList result = sampler.result();
		assertTrue(result.get(0).y().isEmpty() && result.get(99).y().isEmpty());
	}

	@Test
	public void cscLnXInverseTimesMinusTen() {
		GeoFunction tanX = add("(-10)/csc(ln(x))");
		IntervalTuple range = newRange(0, 2, -10, 10);
		IntervalFunctionSampler sampler =
				new IntervalFunctionSampler(tanX, range, 100);
		IntervalTupleList result = sampler.result();
		assertTrue(false);
	}

	private IntervalTuple newRange(double xMin, double xMax, int yMin, int yMax) {
		return new IntervalTuple(new Interval(xMin, xMax), new Interval(yMin, yMax));
	}
}