package unsw.tracks;

import java.util.Map;
import unsw.stations.Station;

public class TrackFactory {
    public static Track createTrack(String trackId, String fromStationId, String toStationId) {
        return new Track(trackId, fromStationId, toStationId);
    }

    public static void validateTrackCreation(String trackId, String fromStationId, String toStationId,
            Map<String, Track> tracks, Map<String, Station> stations) {
        if (tracks.containsKey(trackId)) {
            throw new IllegalArgumentException("Track ID already exists: " + trackId);
        }
        if (!stations.containsKey(fromStationId) || !stations.containsKey(toStationId)) {
            throw new IllegalArgumentException(
                    "One or both station IDs do not exist: " + fromStationId + ", " + toStationId);
        }
    }
}
