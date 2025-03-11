package unsw.trains;

import unsw.utils.Position;

public class CargoTrain extends Train {
    private static final double BASE_SPEED = 3.0;
    private static final double MAX_CARGO_WEIGHT = 5000;

    public CargoTrain(String trainId, Position position, String currentLocationId) {
        super(trainId, position, currentLocationId);
    }

    @Override
    public double getSpeed() {
        return BASE_SPEED;
    }

    public double getMaxCargoWeight() {
        return MAX_CARGO_WEIGHT;
    }
}
