package unsw.stations;

import unsw.utils.Position;
import unsw.trains.Train;

public class DepotStation extends Station {
    private static final int MAX_TRAINS = 8;

    public DepotStation(String stationId, Position position) {
        super(stationId, position);
    }

    public int getMaxTrains() {
        return MAX_TRAINS;
    }

    public void addTrain(Train train) {
        if (super.getTrains().size() >= MAX_TRAINS) {
            throw new IllegalStateException("DepotStation is full. Maximum allowed trains: " + MAX_TRAINS);
        }
        super.addTrain(train);
    }

    public void removeTrain(Train train) {
        super.removeTrain(train);
    }

    @Override
    public boolean canHoldPassengers() {
        return false;
    }

    @Override
    public boolean canHoldCargo() {
        return false;
    }

    //no methods for handling loads
}
