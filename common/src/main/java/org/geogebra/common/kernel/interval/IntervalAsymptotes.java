package org.geogebra.common.kernel.interval;

import org.geogebra.common.util.debug.Log;

/**
 * Class to detect and fix asymptotes and cut off points
 *
 * Note: whole interval [-INFINITY, INFINITY] in already evaluated samples means,
 * that data should be fixed there.
 */
public class IntervalAsymptotes {
	private final IntervalFunction function;
	private final IntervalTupleList samples;
	private final IntervalTuple range;
	private final Interval extremum;

	/**
	 * Constructor
	 *
	 * @param function the original interval function.
	 * @param samples the evaluated data
	 * @param range the visible range of x and y
	 */
	public IntervalAsymptotes(IntervalFunction function, IntervalTupleList samples,
			IntervalTuple range) {
		this.function = function;
		this.samples = samples;
		this.range = range;
		extremum = IntervalConstants.empty();
	}

	/**
	 * Check samples for cut-off points and fix them.
	 */
	public void process() {
//		if (isVerticalAsymptoteFromLeft()) {
//			value(0).setEmpty();
//		}
		int lastIndex = samples.count() - 1;

		for (int index = 1; index < lastIndex; index++) {
			Interval value = value(index);
			updateExtremum(value);
			 if (value.isUndefined()) {
				fixGraph(index);
			}
		}

//		if (isVerticalAsymptoteFromRight()) {
////			extendToLimit(value(lastIndex -2));
//			value(lastIndex - 1).setEmpty();
//		} else if (value(lastIndex).isWhole()) {
//			value(lastIndex).setEmpty();
//		}
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
		if (value.isFinite() || samples.count() < 2) {
			return false;
		}
		Interval right = value(1);
		return right.getLow() < range.y().getLow() || right.getHigh() > range.y().getHigh();
	}

	private boolean isVerticalAsymptoteFromRight() {
		int lastIndex = samples.count() - 1;
		return value(lastIndex).isEmpty() && value(lastIndex - 1).isWhole();
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
		value.set(left.getLow() + diffLow / 2, left.getHigh() + diffHigh / 2);
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
		try {
		Interval x = samples.get(index).x();
		Interval leftLimit;
		Interval rightLimit;
			leftLimit = function.evaluate(new Interval(x.getLow()));
			rightLimit = function.evaluate(new Interval(x.getHigh()));
			Log.debug("leftLimit: " + leftLimit + " " + "rightLimit: " + rightLimit);
			if (leftValue(index).isEmpty()) {
				extendToLimit(value(index), rightValue(index), rightLimit);
			}
			if (rightValue(index).isEmpty()) {
				extendToLimit(value(index), leftValue(index), leftLimit);
			} else if (!rightValue(index).isWhole()) {
				extendToLimit(leftValue(index), leftValue(index), leftLimit);
				extendToLimit(rightValue(index), rightValue(index), rightLimit);
				value(index).setEmpty();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void extendToLimit(Interval toExtend, Interval neighbour, Interval limit) {
		if (neighbour.getHigh() > 0) {
			toExtend.set(neighbour.getHigh(),
					limit.isEmpty() ? Double.POSITIVE_INFINITY : limit.getHigh());
		} else {
			toExtend.set(limit.isEmpty() ? Double.NEGATIVE_INFINITY : limit.getLow(),
					neighbour.getHigh());
		}
 		}

	private Interval value(int index) {
		IntervalTuple tuple = samples.get(index);
		return tuple != null ? tuple.y() : IntervalConstants.empty();
	}

	private Interval leftValue(int index) {
		IntervalTuple prev = prev(index);
		return prev != null ? prev.y() : IntervalConstants.empty();
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
