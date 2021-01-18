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

		for (int index = 1; index < samples.count() - 1; index++) {
			Interval value = value(index);
			updateExtremum(value);
			 if (value.isWhole()) {
				fixGraph(leftValue(index), value, rightValue(index));
			}
		}

		if (isVerticalAsymptoteFromRight()) {
			value(samples.count() - 1).setEmpty();
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
		} else if (right.isWhole()) {
			value.set(extremum);
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
