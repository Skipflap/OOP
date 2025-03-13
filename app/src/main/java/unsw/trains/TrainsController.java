package unsw.trains;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import unsw.exceptions.InvalidRouteException;
import unsw.response.models.InfoResponseAssembler;
import unsw.response.models.StationInfoResponse;
import unsw.response.models.TrackInfoResponse;
import unsw.response.models.TrainInfoResponse;
import unsw.stations.Station;
import unsw.stations.StationFactory;
import unsw.stations.StationValidator;
import unsw.tracks.Track;
import unsw.tracks.TrackFactory;
import unsw.utils.Position;
import unsw.loads.Passenger;
import unsw.loads.Cargo;

/*
 * import unsw.trains.Train; import unsw.trains.BulletTrain; importls
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
        StationFactory.validateStationCreation(stationId, stations);
        Position pos = new Position(x, y);
        Station station = StationFactory.createStation(stationId, type, pos);
        stations.put(stationId, station);
    }

    public void createTrack(String trackId, String fromStationId, String toStationId) {
        TrackFactory.validateTrackCreation(trackId, fromStationId, toStationId, tracks, stations);
        Track track = TrackFactory.createTrack(trackId, fromStationId, toStationId);
        tracks.put(trackId, track);
    }

    public void createTrain(String trainId, String type, String stationId, List<String> route)
            throws InvalidRouteException {
        StationFactory.validateStationExists(stationId, stations);

        TrainFactory.validateTrainCreation(trainId, trains);

        Station startStation = stations.get(stationId);

        Train train = TrainFactory.createTrain(trainId, type, startStation, route, tracks);

        startStation.addTrain(train);

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
        return InfoResponseAssembler.toTrainInfoResponse(train);
    }

    public StationInfoResponse getStationInfo(String stationId) {
        Station station = stations.get(stationId);
        return InfoResponseAssembler.toStationInfoResponse(station);
    }

    public TrackInfoResponse getTrackInfo(String trackId) {
        Track track = tracks.get(trackId);
        return InfoResponseAssembler.toTrackInfoResponse(track);
    }

    public void simulate() {
        Simulator.simulateOneTick(trains, stations, tracks);
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
        Station station = stations.get(startStationId);
        StationFactory.validateStationExists(startStationId, stations);
        StationValidator.validateCanHoldPassengers(station, startStationId);

        Passenger passenger = new Passenger(passengerId, destStationId, station.getPosition());
        station.addLoad(passenger);

        // Debug: print the current loads at the station
        System.out.println("DEBUG: createPassenger: Station " + startStationId + " loads after addition: "
                + station.getLoads().stream().map(l -> l.getLoad() + "(" + l.getType() + ")").toList());
    }

    public void createCargo(String startStationId, String destStationId, String cargoId, int weight) {
        Station station = stations.get(startStationId);
        StationFactory.validateStationExists(startStationId, stations);

        StationValidator.validateCanHoldCargo(station, startStationId);

        Cargo cargo = new Cargo(cargoId, destStationId, weight, station.getPosition());

        station.addLoad(cargo);
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
