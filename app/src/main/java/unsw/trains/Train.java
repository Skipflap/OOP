package unsw.trains;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import unsw.utils.Position;
import unsw.routes.Route;
import unsw.stations.Station;
import unsw.tracks.Track;
import unsw.loads.Load;

public abstract class Train {
    private String trainId;
    private Position position;
    private String currentLocationId;
    private Route route;
    private Boolean movingForward = true;

    private List<Load> loads = new ArrayList<>();

    public Train(String trainId, Position position, String currentLocationId, Route route) {
        this.trainId = trainId;
        this.position = position;
        this.currentLocationId = currentLocationId;
        this.route = route;
    }

    public String getTrainId() {
        return trainId;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public String getCurrentLocationId() {
        return currentLocationId;
    }

    public void setCurrentLocationId(String currentLocationId) {
        this.currentLocationId = currentLocationId;
    }

    public Route getRoute() {
        return route;
    }

    public boolean isMovingForward() {
        return movingForward;
    }

    public void setMovingForward(boolean movingForward) {
        this.movingForward = movingForward;
    }

    public List<Load> getLoads() {
        return loads;
    }

    public void addLoad(Load load) {
        loads.add(load);
    }

    public abstract double getSpeed();

    public abstract void moveOneTick(Map<String, Station> stations, Map<String, Track> tracks);

    protected void updateDirectionIfNeeded(boolean forward) {
        List<String> routeStations = getRoute().getStations();
        String firstStation = routeStations.get(0);
        String lastStation = routeStations.get(routeStations.size() - 1);
        if (getCurrentLocationId().equals(firstStation) && !forward) {
            setMovingForward(true);
        } else if (getCurrentLocationId().equals(lastStation) && forward) {
            setMovingForward(false);
        }
    }
}
