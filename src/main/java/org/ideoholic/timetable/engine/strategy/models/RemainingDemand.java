package org.ideoholic.timetable.engine.strategy.models;

public class RemainingDemand {

    private final int requiredPeriods;

    private final int allocatedPeriods;

    private final int remainingPeriods;

    public RemainingDemand(
            int requiredPeriods,
            int allocatedPeriods) {

        this.requiredPeriods = Math.max(0, requiredPeriods);
        this.allocatedPeriods = Math.max(0, allocatedPeriods);
        this.remainingPeriods = Math.max(0, this.requiredPeriods - this.allocatedPeriods);
    }

    public int getRequiredPeriods() {
        return requiredPeriods;
    }

    public int getAllocatedPeriods() {
        return allocatedPeriods;
    }

    public int getRemainingPeriods() {
        return remainingPeriods;
    }
}
