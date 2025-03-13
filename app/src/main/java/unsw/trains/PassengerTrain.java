package unsw.trains;

import unsw.utils.Position;
import unsw.routes.Route;
import unsw.stations.Station;
import unsw.tracks.Track;
import java.util.Map;

public class PassengerTrain extends Train {
    private static final double SPEED = 2.0;

    public PassengerTrain(String trainId, Position position, String currentLocationId, Route route) {
        super(trainId, position, currentLocationId, route);
    }

    @Override
    public double getSpeed() {
        return SPEED;
    }

    @Override
    public void moveOneTick(Map<String, Station> stations, Map<String, Track> tracks) {
        // 1) If the train is physically on a station at the start of the tick, do pre-departure load/unload.
        Station currentStation = stations.get(getCurrentLocationId());
        if (currentStation != null) {
            // Unload any loads whose destination is this station
            currentStation.unloadTrain(this);
            // Board any new loads if capacity and route allow
            currentStation.loadTrain(this);
        }

        // 2) Now figure out next station for partial movement or inbound arrival
        boolean forward = isMovingForward();
        String nextStationId = getRoute().getNextStation(getCurrentLocationId(), forward);
        Station nextStation = stations.get(nextStationId);

        double distance = getPosition().distance(nextStation.getPosition());
        double effectiveSpeed = getSpeed(); // e.g. 2.0 for PassengerTrain

        // 3) If we can reach the next station in this tick, do so, then load/unload again
        if (effectiveSpeed >= distance) {
            setPosition(nextStation.getPosition());
            setCurrentLocationId(nextStationId);

            // Arrival load/unload
            nextStation.unloadTrain(this);
            nextStation.loadTrain(this);

            // Potentially reverse if linear route endpoint
            updateDirectionIfNeeded(forward);
        } else {
            // partial movement
            Position newPos = getPosition().calculateNewPosition(nextStation.getPosition(), effectiveSpeed);
            setPosition(newPos);
        }
    }
}
