package unsw.trains;

import unsw.utils.Position;

public class Train {
    private String trainId;
    private Position position;
    private String currentLocationId; //Represents the station or track ID that the train is currently at

    public Train(String trainId, Position position, String currentLocationId) {
        this.trainId = trainId;
        this.position = position;
        this.currentLocationId = currentLocationId;
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
}
