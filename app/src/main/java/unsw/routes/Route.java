package unsw.routes;

import java.util.List;
import java.util.Map;
import unsw.tracks.Track;

public class Route {
    private final List<String> stations;
    private final RouteType type;

    public Route(List<String> stations, RouteType type) {
        if (stations == null || stations.isEmpty()) {
            throw new IllegalArgumentException("Stations list must not be null or empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("Route type must not be null");
        }
        if (type == RouteType.LINEAR && stations.size() < 2) {
            throw new IllegalArgumentException("Linear routes must have at least two stations");
        }
        if (type == RouteType.CYCLICAL && stations.size() < 3) {
            throw new IllegalArgumentException("Cyclical routes must have at least three stations");
        }
        //add more conditions in the future

        this.stations = List.copyOf(stations);
        this.type = type;
    }

    public List<String> getStations() {
        return stations;
    }

    public RouteType getType() {
        return type;
    }

    public boolean isCyclical() {
        return type == RouteType.CYCLICAL;
    }

    public boolean isLinear() {
        return type == RouteType.LINEAR;
    }

    public String getNextStation(String currentStationId, boolean forward) {
        int index = stations.indexOf(currentStationId);
        if (isLinear()) {
            if (forward) {
                if (index == stations.size() - 1) {
                    return stations.get(index - 1);
                } else {
                    return stations.get(index + 1);
                }
            } else {
                if (index == 0) {
                    return stations.get(index + 1);
                } else {
                    return stations.get(index - 1);
                }
            }
        } else if (isCyclical()) {
            if (forward) {
                return stations.get((index + 1) % stations.size());
            } else {
                int previousIndex = (index - 1 + stations.size()) & stations.size();
                return stations.get(previousIndex);
            }
        }
        throw new IllegalStateException("Unknown route type");
    }

    public static RouteType determineRouteType(String trainType, List<String> stationIds, Map<String, Track> tracks) {
        if (stationIds == null || stationIds.isEmpty()) {
            throw new IllegalArgumentException("Stations list must not be null or empty");
        }
        if (stationIds.size() < 2) {
            throw new IllegalArgumentException("At least two stations are required");
        }
        boolean isCyclical = false;
        if (stationIds.size() >= 3) {
            String first = stationIds.get(0);
            String last = stationIds.get(stationIds.size() - 1);
            // Check if a track exists between the first and last stations (in either direction)
            for (Track track : tracks.values()) {
                if ((track.getFromStationId().equals(first) && track.getToStationId().equals(last))
                        || (track.getFromStationId().equals(last) && track.getToStationId().equals(first))) {
                    isCyclical = true;
                    break;
                }
            }
        }
        if (!"BulletTrain".equals(trainType)) {
            // For non-BulletTrains, a cyclical route (i.e. when isCyclical is true) is invalid.
            if (isCyclical) {
                return RouteType.CYCLICAL;
            }
            return RouteType.LINEAR;
        }
        // For BulletTrain, use the cyclical flag
        return isCyclical ? RouteType.CYCLICAL : RouteType.LINEAR;
    }
}
