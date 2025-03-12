package unsw.stations;

import unsw.utils.Position;
import unsw.trains.Train;
import java.util.ArrayList;
import java.util.List;

public class DepotStation extends Station {
    private static final int MAX_TRAINS = 8;
    private List<Train> trains;

    public DepotStation(String stationId, Position position) {
        super(stationId, position);
        this.trains = new ArrayList<>();
    }

    public int getMaxTrains() {
        return MAX_TRAINS;
    }

    public void addTrain(Train train) {
        if (trains.size() >= MAX_TRAINS) {
            throw new IllegalStateException("DepotStation is full. Maximum allowed trains: " + MAX_TRAINS);
        }
        trains.add(train);
    }

    public void removeTrain(Train train) {
        trains.remove(train);
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
