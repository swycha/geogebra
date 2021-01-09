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
		if (value(0).isWhole()) {
			connectFromLeft(0);
		}

		for (int i = 1; i < samples.count() - 1; i++) {
			fixAt(i);
		}

		if (value(samples.count() - 1).isWhole()) {
			connectFromRight(samples.count() - 2);
		}
	}

	private void connectFromLeft(int index) {
		Interval value = value(index);

		if (isDescending(index + 1)) {
			value.set(value.getHigh(), Double.POSITIVE_INFINITY);
		} else {
			value.set(Double.NEGATIVE_INFINITY, value.getLow());

		}
	}

	private boolean isDescending(int index) {
		if (samples.count() < index) {
			return false;
		}

		return value(index).getHigh() > value(index + 1).getHigh();
	}

	private void fixAt(int index) {
		Interval right = rightValue(index);
		Interval left = leftValue(index);
		if (isCutOffPoint(index) && !right.isWhole()) {
			connect(left, right);
			value(index).setEmpty();
		}
		if ((isCutOffPoint(index) && right.isWhole())) {
				connectFromRight(index);
		}
	}

	private boolean isCutOffPoint(int index) {
		return samples.get(index).y().isWhole();
	} 

	private void connectFromRight(int index) {
		Interval value = value(index);
		Interval left = leftValue(index);
		Interval right = rightValue(index);
		if (left.isWhole()) {
			return;
		}

		if (isAscending(index - 1)) {
			right.set(value.getHigh(), Double.POSITIVE_INFINITY);
		} else {
			right.set(Double.NEGATIVE_INFINITY, value.getLow());
		}
	}

	private boolean isAscending(int index) {
		if (index - 1 < 0) {
			return false;
		}
		return value(index - 1).getHigh() < value(index).getHigh();
	}

	private IntervalTuple point(int index) {
		return samples.get(index);
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


	private void log(String message) {
		Log.debug("[ASYM] " + message);
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
