package unsw.loads;

import unsw.utils.Position;

public class Passenger extends Load {
    private static final double PASSENGER_WEIGHT = 70;

    public Passenger(String loadId, String destinationId, Position currentPosition) {
        super(loadId, destinationId, PASSENGER_WEIGHT, currentPosition);
    }

    @Override
    public String getType() {
        return "Passenger";
    }
}
