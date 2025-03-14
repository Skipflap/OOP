package unsw.trains;

import unsw.utils.LoadUtils;
import unsw.utils.Position;
import unsw.routes.RouteType;
import unsw.stations.Station;
import unsw.tracks.Track;
import unsw.loads.Load;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BulletTrain extends Train {
    private static final double BASE_SPEED = 5.0;
    private static final double MAX_COMBINED_WEIGHT = 5000.0;

    // The bullet train’s current loads
    private List<Load> loads;

    public BulletTrain(String trainId, Position position, String currentLocationId, unsw.routes.Route route) {
        super(trainId, position, currentLocationId, route);
        this.loads = new ArrayList<>();
    }

    @Override
    public double getSpeed() {
        double totalWeight = LoadUtils.getTotalWeight(loads);
        // E.g. for bullet train, we might reduce speed by 0.01% per kg, or something similar.
        // If you want to keep it simpler, you can just do BASE_SPEED (the spec hints at slowdown, though).
        return LoadUtils.calculateEffectiveSpeed(BASE_SPEED, totalWeight);
    }

    public double getMaxCombinedWeight() {
        return MAX_COMBINED_WEIGHT;
    }

    @Override
    public List<Load> getLoads() {
        // So stations can call train.getLoads().remove(...)
        return this.loads;
    }

    public void addLoad(Load load) {
        double newTotal = getTotalLoadWeight() + load.getWeight();
        if (newTotal > MAX_COMBINED_WEIGHT) {
            throw new IllegalStateException(
                    "Cannot add load. Exceeds BulletTrain’s max combined load of " + MAX_COMBINED_WEIGHT);
        }
        this.loads.add(load);
    }

    public double getTotalLoadWeight() {
        return loads.stream().mapToDouble(Load::getWeight).sum();
    }

    /**
     * The main movement method, updated to do pre-departure + arrival load/unload.
     */
    @Override
    public void moveOneTick(Map<String, Station> stations, Map<String, Track> tracks) {
        // (1) If physically on a station at start of tick, do pre-departure load/unload
        Station currentStation = stations.get(getCurrentLocationId());
        if (currentStation != null) {
            currentStation.unloadTrain(this);
            currentStation.loadTrain(this);
        }

        // (2) Decide partial movement vs arrival
        boolean forward = isMovingForward();
        String nextStationId = getRoute().getNextStation(getCurrentLocationId(), forward);
        Station nextStation = stations.get(nextStationId);

        double distance = getPosition().distance(nextStation.getPosition());
        double effectiveSpeed = getSpeed();

        if (effectiveSpeed >= distance) {
            // We arrive this tick
            setPosition(nextStation.getPosition());
            setCurrentLocationId(nextStationId);

            // (3) ARRIVAL => load/unload again
            nextStation.unloadTrain(this);
            nextStation.loadTrain(this);

            // If linear route, we might reverse
            if (getRoute().getType() == RouteType.LINEAR) {
                updateDirectionIfNeeded(forward);
            }
            // cyc route => direction remains
        } else {
            // partial
            Position newPos = getPosition().calculateNewPosition(nextStation.getPosition(), effectiveSpeed);
            setPosition(newPos);
        }
    }
}
