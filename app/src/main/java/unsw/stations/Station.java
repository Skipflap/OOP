package unsw.stations;

import unsw.utils.Position;

public class Station {
    private String stationId;
    private Position positionId;

    public Station(String stationId, Position positionId) {
        this.stationId = stationId;
        this.positionId = positionId;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public Position getPosition() {
        return positionId;
    }

    public void setPosition(Position positionId) {
        this.positionId = positionId;
    }
}
