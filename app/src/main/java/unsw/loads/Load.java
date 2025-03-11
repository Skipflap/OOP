package unsw.loads;

import unsw.utils.Position;

public abstract class Load {
    private String loadId;
    private String destinationId;
    private double weight;
    private Position currentPosition;

    public Load(String loadId, String destinationId, double weight, Position currentPosition) {
        if (weight <= 0) {
            throw new IllegalArgumentException("Weight must be more than 0");
        }

        this.loadId = loadId;
        this.destinationId = destinationId;
        this.weight = weight;
        this.currentPosition = currentPosition;
    }

    public String getLoad() {
        return loadId;
    }

    public String getDestination() {
        return destinationId;
    }

    public double getWeight() {
        return weight;
    }

    public Position getCurrentPosition() {
        return currentPosition;
    }

    public abstract String getType();
}
