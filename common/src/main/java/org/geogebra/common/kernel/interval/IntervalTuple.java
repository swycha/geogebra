package org.geogebra.common.kernel.interval;

import org.geogebra.common.util.DoubleUtil;

/**
 * Tuple of (x, y) intervals
 *
 * @author laszlo
 */
public class IntervalTuple {
	private final Interval x;
	private final Interval y;

	/**
	 *
	 * @param x interval of x coordinates.
	 * @param y interval of y coordinates.
	 */
	public IntervalTuple(Interval x, Interval y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Sets the tuple as (x, y) interval pair.
	 *
	 * @param x interval of x coordinates
	 * @param y interval of y coordinates
	 */
	public void set(Interval x, Interval y) {
		this.x.set(x);
		this.y.set(y);
	}

	/**
	 * Constructs an empty tuple.
	 */
	public IntervalTuple() {
		this(new Interval(), new Interval());
	}

	/**
	 *
	 * @return the interval of x coordinates
	 */
	public Interval x() {
		return x;
	}

	/**
	 *
	 * @return the interval of y coordinates
	 */
	public Interval y() {
		return y;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IntervalTuple) {
			IntervalTuple other = (IntervalTuple) obj;
			return x.equals(other.x) && y.equals(other.y);
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return DoubleUtil.hashCode(x.getLength() + y.getLength());
	}

	@Override
	public String toString() {
		return "{x: " + x().toShortString() + ": " + y().toShortString() + "}";
	}

	public boolean isYNaN() {
		return y == null || y.isEmpty();
	}

	/**
	 *
	 * @return true if value (y) interval is increasing
	 */
	public boolean isValueIncreasing() {
		return y.getLow() < y.getHigh();
	}

	public boolean follows(IntervalTuple last) {
		return x.getLow() == last.x.getHigh();
	}

	/**
	 *
	 * @return if tuple is an empty one
	 */
	public boolean isEmpty() {
		return x.isEmpty() && y.isEmpty();
	}
}
