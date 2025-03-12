package unsw.trains;

import unsw.utils.LoadUtils;
import unsw.utils.Position;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import unsw.loads.Cargo;
import unsw.routes.Route;
import unsw.stations.Station;
import unsw.tracks.Track;

public class CargoTrain extends Train {
    private static final double BASE_SPEED = 3.0;
    private static final double MAX_CARGO_WEIGHT = 5000;
    private List<Cargo> cargoes;

    public CargoTrain(String trainId, Position position, String currentLocationId, Route route) {
        super(trainId, position, currentLocationId, route);
        cargoes = new ArrayList<>();
    }

    @Override
    public double getSpeed() {
        double totalWeight = LoadUtils.getTotalWeight(cargoes);
        return LoadUtils.calculateEffectiveSpeed(BASE_SPEED, totalWeight);
    }

    public double getMaxCargoWeight() {
        return MAX_CARGO_WEIGHT;
    }

    public void addCargo(Cargo cargo) {
        if (getTotalCargoWeight() + cargo.getWeight() > MAX_CARGO_WEIGHT) {
            throw new IllegalStateException("Adding this cargo would exceed maximum cargo weight.");
        }
        cargoes.add(cargo);
    }

    public double getTotalCargoWeight() {
        double total = 0;
        for (Cargo c : cargoes) {
            total += c.getWeight();
        }
        return total;
    }

    @Override
    public void moveOneTick(Map<String, Station> stations, Map<String, Track> tracks) {
        assert getRoute().getStations().contains(getCurrentLocationId())
                : "Current location " + getCurrentLocationId() + " must be in the route.";

        boolean forward = isMovingForward();
        String nextStationId = getRoute().getNextStation(getCurrentLocationId(), forward);
        Station nextStation = stations.get(nextStationId);
        assert nextStation != null : "Next station " + nextStationId + " must exist.";

        Position nextStationPos = nextStation.getPosition();
        double effectiveSpeed = getSpeed();
        double distance = getPosition().distance(nextStationPos);

        if (effectiveSpeed >= distance) {
            setPosition(nextStationPos);
            setCurrentLocationId(nextStationId);
            updateDirectionIfNeeded(forward);
        } else {
            // Otherwise, move proportionally toward the next station.
            Position newPos = getPosition().calculateNewPosition(nextStationPos, effectiveSpeed);
            setPosition(newPos);
        }
    }
}
