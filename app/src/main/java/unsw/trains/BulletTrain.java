package unsw.trains;

import unsw.utils.LoadUtils;
import unsw.utils.Position;
import unsw.routes.RouteType;
import unsw.stations.Station;
import unsw.tracks.Track;
import java.util.List;
import java.util.Map;

public class BulletTrain extends Train {
    private static final double BASE_SPEED = 5.0;
    private static final double MAX_COMBINED_WEIGHT = 5000;

    // BulletTrain can carry both passengers and cargo, so we store them in a list of Loads.
    private List<unsw.loads.Load> loads;

    public BulletTrain(String trainId, Position position, String currentLocationId, unsw.routes.Route route) {
        super(trainId, position, currentLocationId, route);
        loads = new java.util.ArrayList<>();
    }

    @Override
    public double getSpeed() {
        double totalWeight = LoadUtils.getTotalWeight(loads);
        return LoadUtils.calculateEffectiveSpeed(BASE_SPEED, totalWeight);
    }

    public double getMaxCombinedWeight() {
        return MAX_COMBINED_WEIGHT;
    }

    // Adds a load (passenger or cargo) to the train.
    public void addLoad(unsw.loads.Load load) {
        if (getTotalLoadWeight() + load.getWeight() > MAX_COMBINED_WEIGHT) {
            throw new IllegalStateException("Adding this load would exceed maximum combined weight.");
        }
        loads.add(load);
    }

    // Sum the weight of all loads on the train.
    public double getTotalLoadWeight() {
        double total = 0;
        for (unsw.loads.Load l : loads) {
            total += l.getWeight();
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

            if (getRoute().getType() == RouteType.LINEAR) {
                updateDirectionIfNeeded(forward);
            }
            // For cyclical routes, the direction remains unchanged.
        } else {
            // Otherwise, move proportionally towards the next station.
            // The Position class's calculateNewPosition helper computes the new coordinates.
            Position newPos = getPosition().calculateNewPosition(nextStationPos, effectiveSpeed);
            setPosition(newPos);
        }
    }
}
