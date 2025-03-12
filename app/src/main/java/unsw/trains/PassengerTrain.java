package unsw.trains;

import unsw.utils.Position;

import java.util.Map;

import unsw.routes.Route;
import unsw.stations.Station;
import unsw.tracks.Track;

public class PassengerTrain extends Train {
    private static final double SPEED = 2.0;
    private static final double MAX_PASSENGER_WEIGHT = 3500;

    public PassengerTrain(String trainId, Position position, String currentLocationId, Route route) {
        super(trainId, position, currentLocationId, route);
    }

    @Override
    public double getSpeed() {
        return SPEED;
    }

    public double getMaxPassengerWeight() {
        return MAX_PASSENGER_WEIGHT;
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
        double distance = getPosition().distance(nextStationPos);
        double effectiveSpeed = getSpeed();

        if (effectiveSpeed >= distance) {
            // Arrive at the station.
            setPosition(nextStationPos);
            setCurrentLocationId(nextStationId);

            updateDirectionIfNeeded(forward);

        } else {
            // Otherwise, move proportionally towards the next station.
            // The Position class should provide a helper method to calculate a new position.
            Position newPos = getPosition().calculateNewPosition(nextStationPos, effectiveSpeed);
            setPosition(newPos);
        }
    }
}
