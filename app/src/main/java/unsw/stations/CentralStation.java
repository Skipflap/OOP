package unsw.stations;

import unsw.utils.Position;
import unsw.trains.Train;
import unsw.loads.Passenger;
import unsw.loads.Cargo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * CentralStation is designed to store both passengers and cargo.
 * It can accommodate a maximum of 8 trains.
 */
public class CentralStation extends Station {
    private static final int MAX_TRAINS = 8;
    private List<Train> trains;
    // Separate lists for passengers and cargo loads
    private List<Passenger> passengers;
    private List<Cargo> cargos;

    /**
     * Constructs a CentralStation with the given stationId and position.
     * @pre stationId != null && position != null
     * @post trains, passengers, and cargos are initialized as empty lists.
     */
    public CentralStation(String stationId, Position position) {
        super(stationId, position);
        this.trains = new ArrayList<>();
        this.passengers = new ArrayList<>();
        this.cargos = new ArrayList<>();
    }

    /**
     * Returns the maximum number of trains allowed at a CentralStation.
     * @return MAX_TRAINS (8)
     */
    public int getMaxTrains() {
        return MAX_TRAINS;
    }

    /**
     * Adds a train to the station if capacity allows.
     * @pre train != null
     * @post if trains.size() < MAX_TRAINS, the train is added; otherwise, an exception is thrown.
     */
    public void addTrain(Train train) {
        if (trains.size() >= MAX_TRAINS) {
            throw new IllegalStateException("CentralStation is full. Maximum allowed trains: " + MAX_TRAINS);
        }
        trains.add(train);
    }

    /**
     * Removes a train from the station.
     * @pre trains contains train
     * @post trains no longer contains train.
     */
    public void removeTrain(Train train) {
        trains.remove(train);
    }

    /**
     * Adds a passenger load to the station.
     * @pre passenger != null
     * @post passengers list contains the new passenger.
     */
    public void addPassenger(Passenger passenger) {
        if (passenger == null) {
            throw new IllegalArgumentException("Passenger cannot be null");
        }
        passengers.add(passenger);
    }

    /**
     * Removes a passenger load from the station.
     * @pre passengers contains passenger
     * @post passengers no longer contains passenger.
     */
    public void removePassenger(Passenger passenger) {
        passengers.remove(passenger);
    }

    /**
     * Returns a new list of waiting passengers sorted lexicographically by their load ID.
     * @post returned list is sorted in lexicographical order.
     */
    public List<Passenger> getWaitingPassengers() {
        List<Passenger> sorted = new ArrayList<>(passengers);
        Collections.sort(sorted, Comparator.comparing(Passenger::getLoad));
        return sorted;
    }

    /**
     * Clears all passengers from the station.
     * @post passengers list is empty.
     */
    public void clearPassengers() {
        passengers.clear();
    }

    /**
     * Adds a cargo load to the station.
     * @pre cargo != null
     * @post cargos list contains the cargo.
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
     * Returns a new list of waiting cargos sorted lexicographically by their load ID.
     * @post returned list is sorted in lexicographical order.
     */
    public List<Cargo> getWaitingCargo() {
        List<Cargo> sorted = new ArrayList<>(cargos);
        Collections.sort(sorted, Comparator.comparing(Cargo::getLoad));
        return sorted;
    }

    /**
     * Clears all cargos from the station.
     * @post cargos list is empty.
     */
    public void clearCargo() {
        cargos.clear();
    }
}
