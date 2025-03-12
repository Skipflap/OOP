package unsw.stations;

import unsw.utils.Position;
import unsw.trains.Train;
import java.util.ArrayList;
import java.util.List;
import unsw.loads.Load;

public abstract class Station {
    private String stationId;
    private Position positionId;
    private List<Train> trains = new ArrayList<>();
    private List<Load> loads = new ArrayList<>();

    public Station(String stationId, Position positionId) {
        this.stationId = stationId;
        this.positionId = positionId;
    }

    protected abstract int getMaxTrains();

    public void addTrain(Train train) {
        if (trains.size() >= getMaxTrains()) {
            throw new IllegalStateException(getClass().getSimpleName() + " is full. Max = " + getMaxTrains());
        }
        trains.add(train);
    }

    public void removeTrain(Train train) {
        trains.remove(train);
    }

    public List<Train> getTrains() {
        return trains;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public Position getPosition() {
        return positionId;
    }

    public void setPosition(Position positionId) {
        this.positionId = positionId;
    }

    public List<Load> getLoads() {
        return loads;
    }

    public void addLoad(Load load) {
        loads.add(load);
        System.out.println("DEBUG: Station " + stationId + " added load: " + load.getLoad() + " (type: "
                + load.getType() + "). Total loads now: " + loads.size());
    }

    public abstract boolean canHoldPassengers();

    public abstract boolean canHoldCargo();
}
