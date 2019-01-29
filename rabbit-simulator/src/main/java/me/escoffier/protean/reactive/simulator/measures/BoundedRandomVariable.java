package me.escoffier.protean.reactive.simulator.measures;

import java.util.DoubleSummaryStatistics;
import java.util.Random;

import static me.escoffier.protean.reactive.simulator.Checks.checkArgument;

/**
 * A random variable that models normally-distributed real values. Returned values will fall between the specified bounds.
 */
public class BoundedRandomVariable {

  private static final Random random = new Random();

  private final double variance;
  private final double standardDeviation;
  private final double minimumValue;
  private final double maximumValue;

  private final DoubleSummaryStatistics statistics;

  public BoundedRandomVariable(double standardDeviation, double minimumValue, double maximumValue) {
    checkArgument(minimumValue, min -> min <= maximumValue);
    checkArgument(standardDeviation, dev -> dev >= 0);
    this.standardDeviation = standardDeviation;
    this.variance = Math.pow(standardDeviation, 2.0);
    this.minimumValue = minimumValue;
    this.maximumValue = maximumValue;

    statistics = new DoubleSummaryStatistics();
    statistics.accept(maximumValue);
    statistics.accept(minimumValue);
  }

  public double variance() {
    return variance;
  }

  public double standardDeviation() {
    return standardDeviation;
  }

  public double min() {
    return minimumValue;
  }

  public double max() {
    return maximumValue;
  }

  public double next() {
    double nextValue;
    do {
      nextValue = random.nextGaussian() * standardDeviation + statistics.getAverage();
      if ((nextValue >= minimumValue) &&
        (nextValue <= maximumValue)) {
        break;
      }
    }
    while (true);
    statistics.accept(nextValue);
    return nextValue;
  }
}
