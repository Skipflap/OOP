package unsw.stations;

import unsw.utils.Position;
import unsw.trains.Train;
import java.util.ArrayList;
import java.util.List;

public abstract class Station {
    private String stationId;
    private Position positionId;
    private List<Train> trains = new ArrayList<>();

    public Station(String stationId, Position positionId) {
        this.stationId = stationId;
        this.positionId = positionId;
    }

    // Let each subclass define its capacity.
    protected abstract int getMaxTrains();

    /**
     * A single, reusable addTrain that checks capacity
     * and throws if it is full.
     */
    public void addTrain(Train train) {
        if (trains.size() >= getMaxTrains()) {
            throw new IllegalStateException(getClass().getSimpleName() + " is full. Max = " + getMaxTrains());
        }
        trains.add(train);
    }

    /**
     * A single, reusable removeTrain
     */
    public void removeTrain(Train train) {
        trains.remove(train);
    }

    // Accessor if you ever need to inspect which trains are docked
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
}
