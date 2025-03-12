package unsw.trains;

import unsw.utils.Position;
import unsw.routes.Route;

public abstract class Train {
    private String trainId;
    private Position position;
    private String currentLocationId; //Represents the station or track ID that the train is currently at
    private Route route;

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

    public abstract double getSpeed();
}
