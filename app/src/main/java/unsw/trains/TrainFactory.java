package unsw.trains;

import java.util.List;
import java.util.Map;

import unsw.exceptions.InvalidRouteException;
import unsw.routes.Route;
import unsw.routes.RouteType;
import unsw.stations.Station;
import unsw.tracks.Track;

public class TrainFactory {
    public static Train createTrain(String trainId, String type, Station startStation, List<String> route,
            Map<String, Track> tracks) throws InvalidRouteException {
        if (!route.contains(startStation.getStationId())) {
            throw new InvalidRouteException("Starting station is not in the route: " + startStation.getStationId());
        }
        RouteType routeType = Route.determineRouteType(type, route, tracks);
        Route trainRoute = new Route(route, routeType);

        switch (type) {
        case "PassengerTrain":
            return new PassengerTrain(trainId, startStation.getPosition(), startStation.getStationId(), trainRoute);
        case "CargoTrain":
            return new CargoTrain(trainId, startStation.getPosition(), startStation.getStationId(), trainRoute);
        case "BulletTrain":
            return new BulletTrain(trainId, startStation.getPosition(), startStation.getStationId(), trainRoute);
        default:
            throw new IllegalArgumentException("Invalid train type: " + type);
        }
    }

    public static void validateTrainCreation(String trainId, Map<String, Train> trains) {
        if (trains.containsKey(trainId)) {
            throw new IllegalArgumentException("Train ID already exists: " + trainId);

        }
    }
}
