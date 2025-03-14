package unsw.stations;

import unsw.utils.Position;
import unsw.trains.BulletTrain;
import unsw.trains.CargoTrain;
import unsw.trains.PassengerTrain;
import unsw.trains.Train;
import java.util.ArrayList;
import java.util.List;
import unsw.loads.Load;
import unsw.loads.Passenger;

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

    public void unloadTrain(Train train) {
        // Remove from the train any loads whose destination is this station
        List<Load> toRemove = new ArrayList<>();
        for (Load l : train.getLoads()) {
            if (this.stationId.equals(l.getDestination())) {
                toRemove.add(l);
            }
        }

        if (!toRemove.isEmpty()) {
            System.out.println("DEBUG: Unloading cargo at " + stationId + " => " + toRemove.size() + " items.");
        }

        // Actually remove them
        train.getLoads().removeAll(toRemove);
        System.out.println("DEBUG: trainCargo now has " + train.getLoads().size() + " cargo items after unloading.");

    }

    public void loadTrain(Train train) {
        // Move loads from the station to the train if capacity and route checks pass
        // Also check that the train’s route eventually visits the load’s destination.
        // We'll do a stable sort for the load IDs so we handle them in lex order
        List<Load> sortedLoads = new ArrayList<>(this.loads);
        sortedLoads.sort((l1, l2) -> l1.getLoad().compareTo(l2.getLoad()));

        for (Load ld : sortedLoads) {
            if (!trainWillVisitDestination(train, ld.getDestination())) {
                // if the train won't pass the load's destination, skip
                System.out.println(
                        "DEBUG: Skipping cargo " + ld.getLoad() + " - Train does not reach " + ld.getDestination());
                continue;
            }
            if (!trainCanAcceptLoad(train, ld)) {
                // skip if capacity won't permit
                System.out.println(
                        "DEBUG: Skipping cargo " + ld.getLoad() + " - Train cannot accept due to weight limit.");
                continue;
            }
            // Passed all checks => transfer from station to train
            this.loads.remove(ld);
            train.addLoad(ld);
            System.out.println("DEBUG: Cargo " + ld.getLoad() + " loaded onto train " + train.getTrainId());
        }
    }

    /**
     * Checks if train’s route includes `dest`.
     * For a linear route, it eventually comes back (like a shuttle).
     * For cyclical, it loops. So if `dest` is in route’s station list, we consider it reachable.
     */
    private boolean trainWillVisitDestination(Train train, String dest) {
        return train.getRoute().getStations().contains(dest);
    }

    /**
     * Checks capacity depending on train type (PassengerTrain, CargoTrain, BulletTrain).
     */
    private boolean trainCanAcceptLoad(Train train, Load load) {
        // e.g. check instance types
        if (train instanceof PassengerTrain) {
            if (!(load instanceof Passenger))
                return false;
            return (getCurrentPassengerWeight(train) + 70 <= 3500);
        } else if (train instanceof CargoTrain) {
            if (!(load instanceof unsw.loads.Cargo))
                return false;
            double current = getCurrentCargoWeight(train);
            double toAdd = ((unsw.loads.Cargo) load).getWeight();
            boolean canLoad = (current + toAdd <= 5000);
            System.out.println("DEBUG: Checking cargo " + load.getLoad() + " (weight " + toAdd + ")");
            System.out.println("DEBUG: Train current weight: " + current + " | Max: 5000 | Can load? " + canLoad);
            return canLoad;
        } else if (train instanceof BulletTrain) {
            // bullet can carry passenger or cargo, up to 5000 total.
            double current = getMixedLoadWeight(train);
            double extra = (load instanceof Passenger) ? 70 : ((unsw.loads.Cargo) load).getWeight();
            return (current + extra <= 5000);
        }
        return false;
    }

    private double getCurrentPassengerWeight(Train train) {
        double total = 0;
        for (Load l : train.getLoads()) {
            if (l instanceof Passenger) {
                total += 70;
            }
        }
        return total;
    }

    private double getCurrentCargoWeight(Train train) {
        double total = 0;
        for (Load l : train.getLoads()) {
            if (l instanceof unsw.loads.Cargo) {
                total += ((unsw.loads.Cargo) l).getWeight();
            }
        }
        return total;
    }

    private double getMixedLoadWeight(Train train) {
        double total = 0;
        for (Load l : train.getLoads()) {
            if (l instanceof Passenger) {
                total += 70;
            } else if (l instanceof unsw.loads.Cargo) {
                total += ((unsw.loads.Cargo) l).getWeight();
            }
        }
        return total;
    }
}
