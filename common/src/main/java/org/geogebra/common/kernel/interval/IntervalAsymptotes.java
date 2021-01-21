package org.geogebra.common.kernel.interval;

public class IntervalAsymptotes {
	private final IntervalTupleList samples;
	private final IntervalTuple range;
	private final Interval extremum;

	public IntervalAsymptotes(IntervalTupleList samples, IntervalTuple range) {
		this.samples = samples;
		this.range = range;
		extremum = IntervalConstants.empty();
	}

	public void process() {
		if (isVerticalAsymptoteFromLeft()) {
			value(0).setEmpty();
		}
		int lastIndex = samples.count() - 1;

		for (int index = 1; index < lastIndex; index++) {
			Interval value = value(index);
			updateExtremum(value);
			 if (value.isWhole()) {
				fixGraph(index);
			}
		}

		if (isVerticalAsymptoteFromRight()) {
			extendToInfinite(value(lastIndex -2));
			value(lastIndex - 1).setEmpty();
		}

	}

	private void updateExtremum(Interval value) {
		if (!value.isFinite()) {
			return;
		}

		if (value.getLow() < extremum.getLow()) {
			extremum.setLow(value.getLow());
		}

		if (extremum.getHigh() < value.getHigh()) {
			extremum.setHigh(value.getHigh());
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
		int lastIndex = samples.count() - 1;
		return value(lastIndex).isEmpty() && value(lastIndex -1).isWhole();
	}

	private void fixGraph(int index) {
		Interval left = leftValue(index);
		Interval right = rightValue(index);
		if (right.isEmpty()) {
			return;
		}
		if (isCloseTo(left, right)) {
			connect(left, value(index), right);
		} else if (isVerticalAsymptote(left, right)) {
			fixVerticalAsymptote(index);
		} else if (right.isWhole()) {
			value(index).set(extremum);
		}
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

	private void fixVerticalAsymptote(int index) {
		if (index <samples.count() - 2 && !next(index+1).isEmpty()) {
			extendToInfinite(leftValue(index));
		}

		extendToInfinite(rightValue(index));
		value(index).setEmpty();
	}

	private void extendToInfinite(Interval value) {
		if (value.isEmpty()) {
			return;
		}

		if (value.getHigh() > 0) {
			value.setHigh(Double.POSITIVE_INFINITY);
		} else if (value.getLow() < 0){
			value.setLow(Double.NEGATIVE_INFINITY);
		}
	}

	private void doExtendToInfinite(Interval value) {
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

		if (left.isWhole()||right.isWhole()) {
			return false;
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
