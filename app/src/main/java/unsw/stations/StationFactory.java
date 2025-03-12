package unsw.stations;

import unsw.utils.Position;

public class StationFactory {
    public static Station createStation(String stationId, String type, Position pos) {
        switch (type) {
        case "PassengerStation":
            return new PassengerStation(stationId, pos);
        case "CargoStation":
            return new CargoStation(stationId, pos);
        case "CentralStation":
            return new CentralStation(stationId, pos);
        case "DepotStation":
            return new DepotStation(stationId, pos);
        default:
            throw new IllegalArgumentException("Invalid station type" + type);
        }
    }
}
