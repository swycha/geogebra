package org.geogebra.common.kernel.interval;

import org.geogebra.common.util.debug.Log;

public class IntervalAsymptotes {
	private final IntervalTupleList samples;
	private final IntervalTuple range;

	public IntervalAsymptotes(IntervalTupleList samples, IntervalTuple range) {
		this.samples = samples;
		this.range = range;
	}

	public void process() {
		for (int i = 1; i < samples.count() - 1; i++) {
			fixAt(i);
		}
	}

	private void fixAt(int index) {
		Interval right = rightValue(index);
		Interval left = leftValue(index);
		if (isCutOffPoint(index) && !right.isWhole()) {
			connect(left, right);
			value(index).setEmpty();
		}
	}

	private boolean isCutOffPoint(int index) {
		return samples.get(index).y().isWhole();
	}

	private boolean hasBothNeighbours(int index) {
		return prev(index) != null && next(index) != null;
	}

	private void connect(Interval left, Interval right) {
		if (!left.isOverlap(right)) {
			if (left.getLow() > right.getHigh()) {
				left.setHigh(Math.max(left.getHigh(), range.y().getHigh()));
				right.setLow(Math.min(range.y().getLow(), right.getLow()));
			}

			if (left.getHigh() < right.getLow()) {
				left.setLow(Math.min(left.getLow(), range.y().getLow()));
				right.setHigh(Math.max(range.y().getHigh(), right.getHigh()));
			}
		}
	}
	private boolean isLeftCutOff(int index) {
		return leftValue(index).isEmpty()  &&
				!rightValue(index).isWhole()
				&& !rightValue(index).isEmpty();
	}

	private boolean isValueInfinite(int index) {
		return point(index) != null
				&& value(index).hasInfinity();
	}

	private boolean isLeftValueEmpty(int index) {
		Interval left = leftValue(index);
		return left == null || left.isEmpty();
	}

	private boolean isRightCutOff(int index) {
		return isRightValueEmpty(index) && isValueInfinite(index);
	}

	private boolean isRightValueEmpty(int index) {
		Interval right = rightValue(index);
		return right == null || right.isEmpty();
	}

	private Interval value(int index) {
		IntervalTuple point = point(index);
		if (point == null) {
			return IntervalConstants.empty();
		}

 		return point.y();
	}

	private IntervalTuple point(int index) {
		return samples.get(index);
	}

	private void log(String message) {
		Log.debug("[ASYM] " + message);
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
