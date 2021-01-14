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
		for (int index = 0; index < samples.count() - 1; index++) {
			if (value(index).isWhole()) {
				fixGraph(leftValue(index), value(index), rightValue(index));
			}
		}
	}

	private void fixGraph(Interval left, Interval value, Interval right) {
		if (isVerticalAsymptote(left, right)) {
			fixVerticalAsymptote(left, value, right);
		}
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


	private void fixAt(int index) {
		Interval right = rightValue(index);
		Interval left = leftValue(index);
		if (!left.isWhole() && isCutOffPoint(index) && !right.isWhole()) {
			if (left.isEmpty() && !right.isEmpty())  {
				connectFromRight(index, right);
				value(index).setEmpty();
			} else if (!left.isEmpty() && right.isEmpty())  {
				connectFromLeft(index, left);
				value(index).setEmpty();
			} else {
				if (isVerticalAsymptote(right, left)) {
					connect(left, right);
					value(index).setEmpty();
				} else {
					value(index).set(left);
				}
			}
		}
	}

	private boolean isVerticalAsymptote(Interval right, Interval left) {
		if (left.isEmpty() || right.isEmpty()) {
			return true;
		}

		return Math.abs(left.getLow() - right.getLow()) >= range.y().getLow();
	}

	private void connectFromLeft(int index, Interval left) {
		if (value(index -2 ).getLow() < left.getLow()) {
			left.setHigh(range.y().getHigh());
		}  else {
			left.setLow(range.y().getLow());
		}
	}

	private void connectFromRight(int index, Interval right) {
		if (right.getLow() > value(index + 2).getLow()) {
			right.setHigh(range.y().getHigh());
		} else {
			right.setLow(range.y().getLow());
		}
	}

	private boolean isCutOffPoint(int index) {
		return samples.get(index).y().isWhole();
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
