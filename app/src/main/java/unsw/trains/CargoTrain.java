package unsw.trains;

import unsw.utils.LoadUtils;
import unsw.utils.Position;
import unsw.loads.Cargo;
import unsw.routes.Route;
import unsw.stations.Station;
import unsw.tracks.Track;
import unsw.loads.Load;

import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class CargoTrain extends Train {
    private static final double BASE_SPEED = 3.0;
    private static final double MAX_CARGO_WEIGHT = 5000.0;

    // Keep a list of cargo loads on the train
    private List<Cargo> cargoes;

    public CargoTrain(String trainId, Position position, String currentLocationId, Route route) {
        super(trainId, position, currentLocationId, route);
        this.cargoes = new ArrayList<>();
    }

    @Override
    public double getSpeed() {
        // e.g. slowdown formula: baseSpeed * (1 - 0.0001 * totalCargoWeight)
        // or use a helper like LoadUtils:
        double totalWeight = LoadUtils.getTotalWeight(cargoes);
        return LoadUtils.calculateEffectiveSpeed(BASE_SPEED, totalWeight);
    }

    public double getMaxCargoWeight() {
        return MAX_CARGO_WEIGHT;
    }

    /**
     * Called by station.loadTrain(...) if capacity is okay.
     */
    public void addCargo(Cargo cargo) {
        System.out.println("DEBUG: Attempting to add cargo " + cargo.getLoad() + " to train " + getTrainId());
        double currentWeight = getTotalCargoWeight();
        double newWeight = currentWeight + cargo.getWeight();

        if (newWeight > MAX_CARGO_WEIGHT) {
            System.out.println("DEBUG: Cargo too heavy! " + newWeight + " exceeds max weight " + MAX_CARGO_WEIGHT);
            throw new IllegalStateException("Adding cargo would exceed max weight.");
        }
        cargoes.add(cargo);
        System.out.println(
                "DEBUG: Cargo " + cargo.getLoad() + " successfully added. New total weight: " + getTotalCargoWeight());
    }

    public double getTotalCargoWeight() {
        double total = 0;
        for (Cargo c : cargoes) {
            total += c.getWeight();
        }
        return total;
    }

    @Override
    public void addLoad(Load load) {
        if (!(load instanceof Cargo)) {
            throw new IllegalStateException("CargoTrain cannot add non-cargo loads!");
        }
        // Reuse existing addCargo method
        addCargo((Cargo) load);
    }

    /**
     * The main movement method: we do pre-departure loading if we’re on a station,
     * partial movement or arrival, and arrival loading if we do arrive.
     */
    @Override
    public void moveOneTick(Map<String, Station> stations, Map<String, Track> tracks) {
        // (1) If physically on a station at the start of the tick, do load/unload.
        Station currentStation = stations.get(getCurrentLocationId());
        System.out.println("DEBUG: CargoTrain#moveOneTick start => currentLocationId=" + getCurrentLocationId());

        if (currentStation != null) {
            System.out.println("DEBUG: Pre-departure load/unload on station" + currentStation.getStationId());
            currentStation.unloadTrain(this); // Usually no effect for cargo trains unless something is wrongly loaded
            currentStation.loadTrain(this); // ensures we pick up cargo at the start station
            // Ensure movement is triggered
            if (!currentStation.getTrains().contains(this)) {
                return; // Prevent moving if still registered at the station
            }
        }

        // (2) Next, do partial movement or arrival
        boolean forward = isMovingForward();
        String nextStationId = getRoute().getNextStation(getCurrentLocationId(), forward);
        Station nextStation = stations.get(nextStationId);

        double distance = getPosition().distance(nextStation.getPosition());
        double speed = getSpeed();
        double tolerance = 0.01;
        if (speed >= distance - tolerance) {
            // We arrive this tick
            setPosition(nextStation.getPosition());
            setCurrentLocationId(nextStationId);

            // On arrival, unload & load again
            System.out.println("DEBUG: CargoTrain arrived at " + nextStationId + " and unloading cargo.");

            nextStation.unloadTrain(this);
            nextStation.loadTrain(this);
            // Ensure that when arriving, all unloading happens correctly
            System.out.println("DEBUG: CargoTrain arrived at " + nextStationId + " and unloading cargo.");

            // If linear route, we might reverse direction
            updateDirectionIfNeeded(forward);
        } else {
            // partial movement
            Position newPos = getPosition().calculateNewPosition(nextStation.getPosition(), speed);
            setPosition(newPos);
        }
    }

    /**
     * Return all loads this train is carrying. Station uses this to remove cargo
     * that is delivered or to check capacity.
     */
    @Override
    public List<Load> getLoads() {
        System.out.println("DEBUG: trainCargo currently has " + cargoes.size() + " cargo items.");
        return new ArrayList<>(cargoes);
    }
}
