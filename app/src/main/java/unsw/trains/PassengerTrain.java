package unsw.trains;

import unsw.utils.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import unsw.loads.Passenger;
import unsw.routes.Route;
import unsw.stations.Station;
import unsw.tracks.Track;
import unsw.loads.Load;

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

    private void boardPassengers(Station currentStation) {
        List<Passenger> boardingPassengers = new ArrayList<>();
        for (Load load : currentStation.getLoads()) {
            if (load instanceof Passenger) {
                boardingPassengers.add((Passenger) load);
            }
        }
        for (Passenger p : boardingPassengers) {
            currentStation.getLoads().remove(p);
            this.addLoad(p);
            System.out.println("DEBUG: Passenger " + p.getLoad() + " boarded train " + getTrainId());
        }
    }

    @Override
    public void moveOneTick(Map<String, Station> stations, Map<String, Track> tracks) {
        assert getRoute().getStations().contains(getCurrentLocationId())
                : "Current location " + getCurrentLocationId() + " must be in the route.";

        Station currentStation = stations.get(getCurrentLocationId());
        if (currentStation != null) {
            boardPassengers(currentStation);
        }

        boolean forward = isMovingForward();
        String nextStationId = getRoute().getNextStation(getCurrentLocationId(), forward);
        Station nextStation = stations.get(nextStationId);
        assert nextStation != null : "Next station " + nextStationId + " must exist.";

        Position nextStationPos = nextStation.getPosition();
        double distance = getPosition().distance(nextStationPos);
        double effectiveSpeed = getSpeed();

        if (effectiveSpeed >= distance) {
            setPosition(nextStationPos);
            setCurrentLocationId(nextStationId);
            updateDirectionIfNeeded(forward);
        } else {
            Position newPos = getPosition().calculateNewPosition(nextStationPos, effectiveSpeed);
            setPosition(newPos);
        }
    }
}
