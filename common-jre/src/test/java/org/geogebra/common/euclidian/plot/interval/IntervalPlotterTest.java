package org.geogebra.common.euclidian.plot.interval;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.junit.Ignore;
import org.junit.Test;

public class IntervalPlotterTest extends BaseUnitTest {

	public static final String EMPTY_PATH = "R";

	@Ignore
	@Test
	public void testDisconnetivity() {
		EuclidianView view = getApp().getActiveEuclidianView();
		view.setRealWorldCoordSystem(Math.PI - 0.0001, 2 * Math.PI + 0.0001, -2, 2);
		IntervalPathPlotterMock gp = new IntervalPathPlotterMock();
		IntervalPlotter plotter = new IntervalPlotter(view, gp);
		GeoFunction function = add("sqrt(sin(x))");
		plotter.enableFor(function);
		assertEquals(EMPTY_PATH, gp.getLog());
	}
}
