package org.geogebra.common.kernel.interval;

import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.debug.Log;

public class IntervalAsymptotes {
	private final IntervalTupleList samples;
	private final IntervalTuple range;

	public IntervalAsymptotes(IntervalTupleList samples, IntervalTuple range) {
		this.samples = samples;
		this.range = range;
	}

	public void process() {
		if (isVerticalAsymptoteFromLeft()) {
			value(0).setEmpty();
		}

		for (int index = 1; index < samples.count() - 1; index++) {
			if (value(index).isWhole()) {
				fixGraph(leftValue(index), value(index), rightValue(index));
			}
		}

		if (isVerticalAsymptoteFromRight()) {
			value(samples.count() - 1).setEmpty();
		}
	}

	private boolean isVerticalAsymptoteFromLeft() {
		Interval value = value(0);
		if (!value.isWhole() || samples.count() < 2) {
			return false;
		}
		Interval right = value(1);
		return right.getLow() < range.y().getLow() || right.getHigh() > range.y().getHigh();
	}

	private boolean isVerticalAsymptoteFromRight() {
		Interval value = value(samples.count() - 1);
		if (!value.isWhole() || samples.count() < 2) {
			return false;
		}
		Interval left = value(samples.count() - 2);
		return left.getLow() < range.y().getLow() || left.getHigh() > range.y().getHigh();
	}

	private void fixGraph(Interval left, Interval value, Interval right) {
		if (isCloseTo(left, right)) {
			connect(left, value, right);
		} else if (isVerticalAsymptote(left, right)) {
			fixVerticalAsymptote(left, value, right);
		}
	}

	private void debug(String message) {
		Log.debug("[ASYM] " + message);
	}

	private void connect(Interval left, Interval value, Interval right) {
		double diffLow = right.getLow() - left.getLow();
		double diffHigh = right.getHigh() - left.getHigh();
		value.set(left.getLow() + diffLow /2, left.getHigh() + diffHigh / 2);
	}

	private boolean isCloseTo(Interval left, Interval right) {
		double diffLow = Math.abs(right.getLow() - left.getLow());
		double diffHigh = Math.abs(right.getHigh() - left.getHigh());
		if (diffLow == Double.POSITIVE_INFINITY || diffHigh == Double.POSITIVE_INFINITY) {
			return false;
		}
		return left.isFinite() && right.isFinite()
				&& (diffHigh < 2 && diffLow < 2);
	}

	private boolean isZero(Interval left, Interval right) {
		if (isHalfConvergesToZero(left, right) || isHalfConvergesToZero(right, left)) {
			return true;
		}

		return DoubleUtil.isEqual(Math.abs(right.getLow() - left.getHigh())
				, 0, 1E-1);
	}

	private boolean isHalfConvergesToZero(Interval interval1, Interval interval2) {
		return interval1.isEmpty() && DoubleUtil.isEqual(interval2.getLow(), 0, 1);
	}

	private void fixVerticalAsymptote(Interval left, Interval value, Interval right) {
		extendToInfinite(left);
		extendToInfinite(right);
		value.setEmpty();
	}

	private void extendToInfinite(Interval value) {
		if (value.getHigh() > 0) {
			value.setHigh(Double.POSITIVE_INFINITY);
		} else if (value.getLow() < 0){
			value.setLow(Double.NEGATIVE_INFINITY);
		}
	}

	private boolean isVerticalAsymptote(Interval left, Interval right) {
		if (left.isEmpty() || right.isEmpty()) {
			return true;
		}

		return Math.abs(left.getLow() - right.getLow()) >= range.y().getLow();
	}

	private Interval value(int index) {
		IntervalTuple tuple = samples.get(index);
		return tuple != null ? tuple.y(): IntervalConstants.empty();
	}

	private Interval leftValue(int index) {
		IntervalTuple prev = prev(index);
		return prev != null ? prev.y(): IntervalConstants.empty();
	}

	private Interval rightValue(int index) {
		IntervalTuple next = next(index);
		return next != null ? next.y() : IntervalConstants.empty();
	}

	private IntervalTuple prev(int index) {
		return samples.get(index - 1);
	}

	private IntervalTuple next(int index) {
		return samples.get(index + 1);
	}
}
