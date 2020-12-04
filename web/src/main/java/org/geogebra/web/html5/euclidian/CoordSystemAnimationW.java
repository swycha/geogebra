package org.geogebra.web.html5.euclidian;

import org.geogebra.common.euclidian.CoordSystemAnimation;
import org.geogebra.common.euclidian.CoordSystemInfo;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.util.GTimerListener;
import org.geogebra.web.html5.sound.GTimerW;

public class CoordSystemAnimationW extends CoordSystemAnimation implements GTimerListener {
	protected GTimerW timer; // for animation

	/**
	 * @param view
	 *            zoomed / panned Euclidian view
	 * @param coordSystemInfo {@link CoordSystemInfo}
	 */
	public CoordSystemAnimationW(EuclidianView view,
			CoordSystemInfo coordSystemInfo) {
		super(view, coordSystemInfo);
		timer = new GTimerW(this, DELAY);
	}

	@Override
	protected void stopTimer() {
		timer.stop();
	}

	@Override
	protected boolean hasTimer() {
		return timer != null;
	}

	@Override
	public synchronized void onRun() {
		step();
	}

	@Override
	protected void startTimer() {
		timer.startRepeat();
	}
}
