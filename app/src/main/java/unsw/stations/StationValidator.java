package unsw.stations;

public class StationValidator {
    public static void validateCanHoldPassengers(Station station, String stationId) {
        if (!station.canHoldPassengers()) {
            throw new IllegalArgumentException("Station " + stationId + " cannot hold passengers");
        }
    }

    public static void validateCanHoldCargo(Station station, String stationId) {
        if (!station.canHoldCargo()) {
            throw new IllegalArgumentException("Station " + stationId + " cannot store cargo.");
        }
    }
}
