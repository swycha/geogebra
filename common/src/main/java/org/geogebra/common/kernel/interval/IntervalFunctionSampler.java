package org.geogebra.common.kernel.interval;

import java.util.List;

import org.geogebra.common.kernel.geos.GeoFunction;

/**
 * Class to provide samples of the given function as a
 * list of (x, y) pairs, where both x and y are intervals.
 *
 * @author Laszlo
 */
public class IntervalFunctionSampler {

	private final IntervalFunction function;
	private final int numberOfSamples;
	private final LinearSpace space;
	private IntervalTuple range;

	/**
	 *
	 * @param geo function to get sampled
	 * @param range (x, y) range.
	 * @param numberOfSamples the sample rate.
	 */
	public IntervalFunctionSampler(GeoFunction geo, IntervalTuple range,
			int numberOfSamples) {
		this.function = new IntervalFunction(geo);
		this.numberOfSamples = numberOfSamples;
		this.range = range;
		space = new LinearSpace();
		update(range);
	}

	/**
	 * Gets the samples with the predefined range and sample rate
	 *
	 * @return the sample list
	 */
	public IntervalTupleList result() {
		try {
			return evaluateOnSpace(space);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new IntervalTupleList();
	}

	/**
	 * Gets the samples with the predefined range and sample rate
	 *
	 * @return the sample list
	 */
	public IntervalTupleList result(LinearSpace space) {
		try {
			return evaluateOnSpace(space);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new IntervalTupleList();
	}

	private IntervalTupleList evaluateOnSpace(LinearSpace space) throws Exception {
		List<Double> xCoords = space.values();
		IntervalTupleList samples = new IntervalTupleList();
		boolean addEmpty = true;
		for (int i = 0; i < xCoords.size() - 1; i += 1) {
			Interval x = new Interval(xCoords.get(i), xCoords.get(i + 1));
			Interval y = function.evaluate(x);
			if (!y.isEmpty() || addEmpty) {
				samples.add(new IntervalTuple(x, y));
			}

			addEmpty = !y.isEmpty();
		}

		IntervalAsymptotes asymtotes = new IntervalAsymptotes(samples, range);
		asymtotes.process();
		return samples;
	}


	/**
	 * Updates the range on which sampler has to run.
	 *
	 * @param range the new (x, y) range
	 */
	public void update(IntervalTuple range) {
		space.update(range.x(), numberOfSamples);
		this.range = range;
	}

	public IntervalTupleList extendMax(double max) {
		return evaluateAtDomain(space.extendMax(max));
	}

	private IntervalTupleList evaluateAtDomain(LinearSpace domain) {
		try {
			return evaluateOnSpace(domain);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new IntervalTupleList();
	}

	public IntervalTupleList extendMin(double min) {
		return evaluateAtDomain(space.extendMin(min));
	}

	public int shrinkMax(double max) {
		return space.shrinkMax(max);
	}

	public int shrinkMin(double min) {
		return space.shrinkMin(min);
	}
}
