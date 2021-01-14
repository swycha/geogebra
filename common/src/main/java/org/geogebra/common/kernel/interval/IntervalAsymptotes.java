package org.geogebra.common.kernel.interval;

import org.geogebra.common.util.DoubleUtil;

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
		if (isZero(left, right)) {
			value.setZero();
		} else if (isVerticalAsymptote(left, right)) {
			fixVerticalAsymptote(left, value, right);
		}
	}

	private boolean isZero(Interval left, Interval right) {
		return DoubleUtil.isEqual(Math.abs(right.getLow() - left.getHigh())
				, 0, 1E-1);
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
