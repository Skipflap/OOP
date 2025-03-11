package unsw.stations;

import unsw.utils.Position;
import unsw.trains.Train;
import unsw.loads.Cargo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CargoStation extends Station {
    private static final int MAX_TRAINS = 4;
    // Collection of trains currently docked at the station.
    private List<Train> trains;
    // Collection of cargo loads waiting at the station.
    private List<Cargo> cargos;

    /**
     * Constructs a CargoStation with the specified stationId and position.
     * @pre stationId != null && position != null
     * @post this.stationId == stationId && this.position == position; trains and cargos are empty.
     */
    public CargoStation(String stationId, Position position) {
        super(stationId, position);
        this.trains = new ArrayList<>();
        this.cargos = new ArrayList<>();
    }

    /**
     * Returns the maximum number of trains allowed at a CargoStation.
     * @post returns MAX_TRAINS, which is 4.
     */
    public int getMaxTrains() {
        return MAX_TRAINS;
    }

    /**
     * Adds a train to the CargoStation if capacity allows.
     * @pre train != null
     * @post if trains.size() < MAX_TRAINS then the train is added; otherwise, an exception is thrown.
     */
    public void addTrain(Train train) {
        if (trains.size() >= MAX_TRAINS) {
            throw new IllegalStateException("CargoStation is full. Maximum allowed trains: " + MAX_TRAINS);
        }
        trains.add(train);
    }

    /**
     * Removes a train from the CargoStation.
     * @pre trains contains train
     * @post trains no longer contains train.
     */
    public void removeTrain(Train train) {
        trains.remove(train);
    }

    /**
     * Adds a cargo load to the station.
     * @pre cargo != null
     * @post cargos contains the new cargo load.
     */
    public void addCargo(Cargo cargo) {
        if (cargo == null) {
            throw new IllegalArgumentException("Cargo cannot be null");
        }
        cargos.add(cargo);
    }

    /**
     * Removes a cargo load from the station.
     * @pre cargos contains cargo
     * @post cargos no longer contains cargo.
     */
    public void removeCargo(Cargo cargo) {
        cargos.remove(cargo);
    }

    /**
     * Returns a new list of waiting cargo loads sorted lexicographically by their load ID.
     * This ordering is used when boarding cargo onto trains.
     * @post the returned list is sorted by cargo load IDs.
     */
    public List<Cargo> getWaitingCargo() {
        List<Cargo> sorted = new ArrayList<>(cargos);
        Collections.sort(sorted, Comparator.comparing(Cargo::getLoad));
        return sorted;
    }

    /**
     * Clears all cargo loads from the station.
     * @post cargos is empty.
     */
    public void clearCargo() {
        cargos.clear();
    }


    //xtra methods for later
}
