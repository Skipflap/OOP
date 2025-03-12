package unsw.stations;

import unsw.utils.Position;
import unsw.trains.Train;
import unsw.loads.Passenger;
import unsw.loads.Cargo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CentralStation extends Station {
    private static final int MAX_TRAINS = 8;
    private List<Train> trains;
    private List<Passenger> passengers;
    private List<Cargo> cargos;

    public CentralStation(String stationId, Position position) {
        super(stationId, position);
        this.trains = new ArrayList<>();
        this.passengers = new ArrayList<>();
        this.cargos = new ArrayList<>();
    }

    public int getMaxTrains() {
        return MAX_TRAINS;
    }

    public void addTrain(Train train) {
        if (trains.size() >= MAX_TRAINS) {
            throw new IllegalStateException("CentralStation is full. Maximum allowed trains: " + MAX_TRAINS);
        }
        trains.add(train);
    }

    public void removeTrain(Train train) {
        trains.remove(train);
    }

    public void addPassenger(Passenger passenger) {
        if (passenger == null) {
            throw new IllegalArgumentException("Passenger cannot be null");
        }
        passengers.add(passenger);
    }

    public void removePassenger(Passenger passenger) {
        passengers.remove(passenger);
    }

    public List<Passenger> getWaitingPassengers() {
        List<Passenger> sorted = new ArrayList<>(passengers);
        Collections.sort(sorted, Comparator.comparing(Passenger::getLoad));
        return sorted;
    }

    public void clearPassengers() {
        passengers.clear();
    }

    public void addCargo(Cargo cargo) {
        if (cargo == null) {
            throw new IllegalArgumentException("Cargo cannot be null");
        }
        cargos.add(cargo);
    }

    public void removeCargo(Cargo cargo) {
        cargos.remove(cargo);
    }

    public List<Cargo> getWaitingCargo() {
        List<Cargo> sorted = new ArrayList<>(cargos);
        Collections.sort(sorted, Comparator.comparing(Cargo::getLoad));
        return sorted;
    }

    public void clearCargo() {
        cargos.clear();
    }

    @Override
    public boolean canHoldCargo() {
        return true;
    }

    @Override
    public boolean canHoldPassengers() {
        return true;
    }
}
