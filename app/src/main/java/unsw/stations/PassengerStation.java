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
    private List<Train> trains;
    private List<Passenger> passengers;

    public PassengerStation(String stationId, Position positionId) {
        super(stationId, positionId);
        this.trains = new ArrayList<>();
        this.passengers = new ArrayList<>();
    }

    public void addTrain(Train train) {
        if (trains.size() >= MAX_TRAINS) {
            throw new IllegalStateException("Passenger Station is full. Max = 2");
        }
    }

    public void removeTrain(Train train) {
        trains.remove(train);
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

    // Add later loading & unloading etc
}
