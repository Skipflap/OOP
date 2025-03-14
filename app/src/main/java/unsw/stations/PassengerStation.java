package unsw.stations;

import unsw.utils.Position;
import unsw.trains.Train;
import unsw.loads.Passenger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PassengerStation extends Station {
    private static final int MAX_TRAINS = 2;
    private List<Passenger> passengers;

    public PassengerStation(String stationId, Position positionId) {
        super(stationId, positionId);
        this.passengers = new ArrayList<>();
    }

    @Override
    public void addTrain(Train train) {
        super.addTrain(train);
    }

    @Override
    public int getMaxTrains() {
        return MAX_TRAINS;
    }
    @Override
    public void removeTrain(Train train) {
        super.removeTrain(train);
    }

    public void addPassenger(Passenger passenger) {
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

    @Override
    public boolean canHoldPassengers() {
        return true;
    }

    @Override
    public boolean canHoldCargo() {
        return false;
    }

    // Add later loading & unloading etc
}
