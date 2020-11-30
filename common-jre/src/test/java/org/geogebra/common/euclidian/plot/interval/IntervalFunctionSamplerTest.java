package org.geogebra.common.euclidian.plot.interval;

import static org.geogebra.common.euclidian.plot.interval.PlotterUtils.createRange;
import static org.geogebra.common.euclidian.plot.interval.PlotterUtils.newSampler;
import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.interval.IntervalFunctionSampler;
import org.geogebra.common.kernel.interval.IntervalTuple;
import org.geogebra.common.kernel.interval.IntervalTupleList;
import org.junit.Test;

public class IntervalFunctionSamplerTest extends BaseUnitTest {

	@Test
	public void testExtendTo() {
		IntervalTuple rangeActual = createRange(0, 10, 0, 100);
		IntervalTuple rangeExpected = createRange(20, 30, 0, 100);
		GeoFunction xDoubled = add("2x");
		IntervalFunctionSampler sampler = newSampler(xDoubled, rangeActual, 10);
		sampler.extendMax(30);

		IntervalFunctionSampler samplerExpected = newSampler(xDoubled,	rangeExpected,	10);

		IntervalTupleList expected = samplerExpected.result();
		assertEquals(expected, sampler.result());
	}

	@Test
	public void testShrinkTo() {
		IntervalTuple rangeActual = createRange(0, 10, 0, 100);
		IntervalTuple rangeExpected = createRange(-2, 8, 0, 100);
		GeoFunction xDoubled = add("2x");
		IntervalFunctionSampler sampler = newSampler(xDoubled, rangeActual, 10);
		IntervalTupleList points = sampler.result();
		IntervalTupleList newPoints = sampler.extendMin(-2);

		if (newPoints != null) {
			points.prependKeepingSize(newPoints);
		}

		IntervalFunctionSampler samplerExpected = newSampler(xDoubled,	rangeExpected,	10);

		IntervalTupleList expected = samplerExpected.result();
		assertEquals(expected, points);
	}
}
