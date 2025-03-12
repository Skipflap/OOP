package unsw.trains;

import unsw.utils.Position;
import unsw.routes.Route;

public class BulletTrain extends Train {
    private static final double BASE_SPEED = 5.0;
    private static final double MAX_COMBINED_WEIGHT = 5000;

    public BulletTrain(String trainId, Position position, String currentLocationId, Route route) {
        super(trainId, position, currentLocationId, route);
    }

    @Override
    public double getSpeed() {
        return BASE_SPEED;
    }

    public double getMaxCombinedWeight() {
        return MAX_COMBINED_WEIGHT;
    }
}
