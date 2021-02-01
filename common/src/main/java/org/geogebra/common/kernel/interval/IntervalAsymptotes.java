package org.geogebra.common.kernel.interval;

/**
 * Class to detect and fix asymptotes and cut off points
 *
 * Note: whole interval [-INFINITY, INFINITY] in already evaluated samples means,
 * that data should be fixed there.
 */
public class IntervalAsymptotes {
	private final IntervalTupleList samples;
	private IntervalFunction function;

	/**
	 * Constructor
	 *  @param samples the evaluated data
	 *
	 */
	public IntervalAsymptotes(IntervalTupleList samples, IntervalFunction function) {
		this.samples = samples;
		this.function = function;
	}

	/**
	 * Check samples for cut-off points and fix them.
	 */
	public void process() {
		for (int index = 0; index < samples.count(); index++) {
			if (value(index).isUndefined()) {
				try {
					fixVerticalAsymptote(index);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (value(index).isWhole()) {
				value(index).setEmpty();
			}
		}
	}

	private void fixVerticalAsymptote(int index) throws Exception {
		Interval result = new Interval(0);
		LinearSpace space = new LinearSpace();
		Interval x1 = samples.get(index).x();
		Interval refine = new Interval(x1.getLow() - 1E-7, x1.getHigh() + 1E-7);
		space.update(refine, 100);
		for (int i = 0; i < space.values.size() - 1; i += 1) {
			Interval x = new Interval(space.values.get(i), space.values.get(i + 1));
			Interval y = function.evaluate(x);
			if (!y.isUndefined() && (y.getLength() > result.getLength())) {
				result.set(y);
			}
		}
		if (!result.isEmpty()) {
			samples.get(index).y().set(result);
			samples.get(index).y().set(result);
		}
	}

	private void fixGraph(int index) {
		Interval left = leftValue(index);
		Interval right = rightValue(index);

		if (isCloseTo(left, right)) {
			connect(left, value(index), right);
		}
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
