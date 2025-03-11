package unsw.stations;

import unsw.utils.Position;
import unsw.trains.Train;
import java.util.ArrayList;
import java.util.List;

/**
 * DepotStation is a stopping point for trains only.
 * No passengers or cargo are loaded or unloaded at a DepotStation.
 * It can accommodate a maximum of 8 trains.
 */
public class DepotStation extends Station {
    private static final int MAX_TRAINS = 8;
    private List<Train> trains;

    /**
     * Constructs a DepotStation with the given stationId and position.
     * @pre stationId != null && position != null
     * @post trains is initialized as an empty list.
     */
    public DepotStation(String stationId, Position position) {
        super(stationId, position);
        this.trains = new ArrayList<>();
    }

    /**
     * Returns the maximum number of trains allowed at a DepotStation.
     * @return MAX_TRAINS (8)
     */
    public int getMaxTrains() {
        return MAX_TRAINS;
    }

    /**
     * Adds a train to the depot if capacity allows.
     * @pre train != null
     * @post if trains.size() < MAX_TRAINS, the train is added; otherwise, an exception is thrown.
     */
    public void addTrain(Train train) {
        if (trains.size() >= MAX_TRAINS) {
            throw new IllegalStateException("DepotStation is full. Maximum allowed trains: " + MAX_TRAINS);
        }
        trains.add(train);
    }

    /**
     * Removes a train from the depot.
     * @pre trains contains train
     * @post trains no longer contains train.
     */
    public void removeTrain(Train train) {
        trains.remove(train);
    }

    // DepotStation does not allow any loading or unloading of passengers or cargo.
    // Therefore, no methods for handling loads are provided.
}
