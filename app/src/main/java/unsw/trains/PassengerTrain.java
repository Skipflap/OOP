package unsw.trains;

import unsw.utils.Position;

public class PassengerTrain extends Train {
    private static final double SPEED = 2.0;
    private static final double MAX_PASSENGER_WEIGHT = 3500;

    public PassengerTrain(String trainId, Position position, String currentLocationId) {
        super(trainId, position, currentLocationId);
    }

    @Override
    public double getSpeed() {
        return SPEED;
    }

    public double getMaxPassengerWeight() {
        return MAX_PASSENGER_WEIGHT;
    }
}
