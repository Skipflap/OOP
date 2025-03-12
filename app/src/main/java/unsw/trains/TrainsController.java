package unsw.trains;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import unsw.exceptions.InvalidRouteException;
import unsw.response.models.StationInfoResponse;
import unsw.response.models.TrackInfoResponse;
import unsw.response.models.TrainInfoResponse;
import unsw.routes.RouteType;
import unsw.stations.Station;
import unsw.stations.StationFactory;
import unsw.utils.Position;
import unsw.tracks.Track;
import unsw.routes.Route;

/*
 * import unsw.trains.Train; import unsw.trains.BulletTrain; import
 * unsw.trains.PassengerTrain; import unsw.trains.CargoTrain; import
 * unsw.routes.Route; import unsw.stations.Station; import
 * unsw.stations.PassengerStation; import unsw.stations.CargoStation; import
 * unsw.stations.CentralStation; import unsw.stations.DepotStation;
 */

/**
 * The controller for the Trains system.
 *
 * The method signatures here are provided for you. Do NOT change the method signatures.
 */
@SuppressWarnings("unused")
public class TrainsController {
    // Add any fields here if necessary
    private Map<String, Station> stations = new HashMap<>();
    private Map<String, Track> tracks = new HashMap<>();
    private Map<String, Train> trains = new HashMap<>();

    public void createStation(String stationId, String type, double x, double y) {
        Position pos = new Position(x, y);
        Station station = StationFactory.createStation(stationId, type, pos);

        if (stations.containsKey(stationId)) {
            throw new IllegalArgumentException("Station ID already exists" + stationId);
        }

        stations.put(stationId, station);
    }

    public void createTrack(String trackId, String fromStationId, String toStationId) {
        if (tracks.containsKey(trackId)) {
            throw new IllegalArgumentException("Track ID already exists:" + trackId);
        }
        if (!stations.containsKey(fromStationId) || !stations.containsKey(toStationId)) {
            throw new IllegalArgumentException(
                    "One or both station IDs do not exist: " + fromStationId + ", " + toStationId);
        }
        Track track = new Track(trackId, fromStationId, toStationId);
        tracks.put(trackId, track);
    }

    public void createTrain(String trainId, String type, String stationId, List<String> route)
            throws InvalidRouteException {
        if (!stations.containsKey(stationId)) {
            throw new IllegalArgumentException("Station does not exist: " + stationId);
        }
        // Validate that the starting station is part of the route.
        if (!route.contains(stationId)) {
            throw new InvalidRouteException("Starting station is not in the route: " + stationId);
        }

        // Determine the route type using the helper function in the Route class.
        RouteType routeType = Route.determineRouteType(type, route, tracks);
        // Create the train's route using the determined type.
        unsw.routes.Route trainRoute = new unsw.routes.Route(route, routeType);

        // Create train of the appropriate type.
        Train train;
        Station startStation = stations.get(stationId);
        switch (type) {
        case "PassengerTrain":
            train = new PassengerTrain(trainId, startStation.getPosition(), stationId, trainRoute);
            break;
        case "CargoTrain":
            train = new CargoTrain(trainId, startStation.getPosition(), stationId, trainRoute);
            break;
        case "BulletTrain":
            train = new BulletTrain(trainId, startStation.getPosition(), stationId, trainRoute);
            break;
        default:
            throw new IllegalArgumentException("Invalid train type: " + type);
        }

        startStation.addTrain(train);

        if (trains.containsKey(trainId)) {
            throw new IllegalArgumentException("Train ID already exists: " + trainId);
        }
        trains.put(trainId, train);
    }

    public List<String> listStationIds() {
        return new ArrayList<>(stations.keySet());
    }

    public List<String> listTrackIds() {
        return new ArrayList<>(tracks.keySet());
    }

    public List<String> listTrainIds() {
        return new ArrayList<>(trains.keySet());
    }

    public TrainInfoResponse getTrainInfo(String trainId) {
        Train train = trains.get(trainId);
        if (train == null) {
            throw new IllegalArgumentException("No such train: " + trainId);
        }
        return new TrainInfoResponse(train.getTrainId(), train.getCurrentLocationId(), train.getClass().getSimpleName(),
                train.getPosition());
    }

    public StationInfoResponse getStationInfo(String stationId) {
        Station station = stations.get(stationId);
        if (station == null) {
            throw new IllegalArgumentException("No such station: " + stationId);
        }
        List<TrainInfoResponse> trainInfos = new ArrayList<>();
        for (Train train : station.getTrains()) {
            trainInfos.add(new TrainInfoResponse(train.getTrainId(), train.getCurrentLocationId(),
                    train.getClass().getSimpleName(), train.getPosition()));
        }
        // Loads are not implemented, so pass an empty list for loads.
        return new StationInfoResponse(station.getStationId(), station.getClass().getSimpleName(),
                station.getPosition(), new ArrayList<>(), trainInfos);
    }

    public TrackInfoResponse getTrackInfo(String trackId) {
        Track track = tracks.get(trackId);
        if (track == null) {
            throw new IllegalArgumentException("No such track: " + trackId);
        }
        return new TrackInfoResponse(track.getTrackId(), track.getFromStationId(), track.getToStationId(),
                track.getType(), track.getDurability());
    }

    public void simulate() {
        // Todo: Task bi
    }

    /**
     * Simulate for the specified number of minutes. You should NOT modify
     * this function.
     */
    public void simulate(int numberOfMinutes) {
        for (int i = 0; i < numberOfMinutes; i++) {
            simulate();
        }
    }

    public void createPassenger(String startStationId, String destStationId, String passengerId) {
        // Todo: Task bii
    }

    public void createCargo(String startStationId, String destStationId, String cargoId, int weight) {
        // Todo: Task bii
    }

    public void createPerishableCargo(String startStationId, String destStationId, String cargoId, int weight,
            int minsTillPerish) {
        // Todo: Task biii
    }

    public void createTrack(String trackId, String fromStationId, String toStationId, boolean isBreakable) {
        // Todo: Task ci
    }

    public void createPassenger(String startStationId, String destStationID, String passengerId, boolean isMechanic) {
        // Todo: Task cii
    }
}
