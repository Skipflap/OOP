package unsw.utils;

import java.util.List;
import unsw.loads.Load;

public class LoadUtils {
    /**
     * Sums up the total weight of a list of loads.
     * @param loads The list of loads.
     * @return The total weight.
     */
    public static double getTotalWeight(List<? extends Load> loads) {
        double total = 0;
        for (Load load : loads) {
            total += load.getWeight();
        }
        return total;
    }

    /**
     * Calculates the effective speed based on the base speed and total load weight.
     * Each kg of load reduces the speed by 0.01% (i.e., 0.0001 per kg).
     * @param baseSpeed The base speed of the train.
     * @param totalWeight The total weight of the loads.
     * @return The effective speed.
     */
    public static double calculateEffectiveSpeed(double baseSpeed, double totalWeight) {
        double reductionFactor = totalWeight * 0.0001;
        double effectiveSpeed = baseSpeed * (1 - reductionFactor);
        return Math.max(effectiveSpeed, 0);
    }
}
