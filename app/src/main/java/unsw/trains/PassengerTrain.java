package unsw.trains;

import unsw.utils.Position;
import unsw.routes.Route;


public class PassengerTrain extends Train {
    private static final double SPEED = 2.0;
    private static final double MAX_PASSENGER_WEIGHT = 3500;

    public PassengerTrain(String trainId, Position position, String currentLocationId, Route route) {
        super(trainId, position, currentLocationId, route);
    }

    @Override
    public double getSpeed() {
        return SPEED;
    }

    public double getMaxPassengerWeight() {
        return MAX_PASSENGER_WEIGHT;
    }
}
