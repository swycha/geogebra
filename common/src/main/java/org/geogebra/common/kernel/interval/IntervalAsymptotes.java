package org.geogebra.common.kernel.interval;

import org.geogebra.common.kernel.geos.GeoFunction;

public class IntervalAsymptotes {
	private GeoFunction function;
	private final IntervalTupleList samples;
	private final IntervalTuple range;
	private final Interval extremum;

	public IntervalAsymptotes(GeoFunction function, IntervalTupleList samples, IntervalTuple range) {
		this.function = function;
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
//			extendToLimit(value(lastIndex -2));
			value(lastIndex - 1).setEmpty();
		} else if (value(lastIndex).isWhole()) {
			value(lastIndex).setEmpty();
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


		if (isCloseTo(left, right)) {
			connect(left, value(index), right);
		} else if (isVerticalAsymptote(left, right)) {
			fixVerticalAsymptote(index);
		} if (right.isWhole()) {
			value(index).set(extremum);
		}
	}

	private boolean isVerticalAsymptote(Interval left, Interval right) {
		if ((right.isEmpty() && left.isFinite())
				|| (left.isEmpty() && right.isFinite())) {
			return true;
		}

		return Math.abs(left.getLow() - right.getLow()) >= range.y().getLow();
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
		Interval x = samples.get(index).x();
		double leftLimit = function.evaluate(x.getLow() + 1E-15, 0);
		double rightLimit = function.evaluate(x.getHigh() - 1E-15, 0);
		if (leftValue(index).isEmpty()) {
			extendToLimit(value(index), rightValue(index), rightLimit);
		} else if (rightValue(index).isEmpty()) {
			extendToLimit(value(index), leftValue(index), leftLimit);
		} else {
			extendToLimit(leftValue(index), leftValue(index), Double.NaN);
			extendToLimit(rightValue(index), rightValue(index), Double.NaN);
			value(index).setEmpty();
//
		}
	}

	private void extendToLimit(Interval toExtend, Interval neighbour, double limit) {
		if (neighbour.getHigh() > 0) {
			toExtend.set(neighbour.getHigh(), Double.isNaN(limit) ? Double.POSITIVE_INFINITY : limit);
		} else {
			toExtend.set(Double.isNaN(limit) ? Double.NEGATIVE_INFINITY : limit,
					neighbour.getHigh());
		}
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
