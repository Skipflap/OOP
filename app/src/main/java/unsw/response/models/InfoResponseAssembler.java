package unsw.response.models;

import java.util.ArrayList;
import java.util.List;

import unsw.loads.Load;
import unsw.stations.Station;
import unsw.tracks.Track;
import unsw.trains.Train;

public class InfoResponseAssembler {
    public static TrainInfoResponse toTrainInfoResponse(Train train) {
        if (train == null) {
            throw new IllegalArgumentException("No such train.");
        }
        List<LoadInfoResponse> loadInfos = new ArrayList<>();
        for (Load load : train.getLoads()) {
            loadInfos.add(new LoadInfoResponse(load.getLoad(), load.getType()));
        }
        return new TrainInfoResponse(train.getTrainId(), train.getCurrentLocationId(), train.getClass().getSimpleName(),
                train.getPosition(), loadInfos);
    }

    public static StationInfoResponse toStationInfoResponse(Station station) {
        if (station == null) {
            throw new IllegalArgumentException("No such station.");
        }
        List<TrainInfoResponse> trainInfos = new ArrayList<>();
        for (Train train : station.getTrains()) {
            trainInfos.add(toTrainInfoResponse(train));
        }
        List<LoadInfoResponse> loadInfos = new ArrayList<>();
        for (Load load : station.getLoads()) {
            loadInfos.add(new LoadInfoResponse(load.getLoad(), load.getType()));
        }
        return new StationInfoResponse(station.getStationId(), station.getClass().getSimpleName(),
                station.getPosition(), loadInfos, trainInfos);
    }

    public static TrackInfoResponse toTrackInfoResponse(Track track) {
        if (track == null) {
            throw new IllegalArgumentException("No such track.");
        }
        return new TrackInfoResponse(track.getTrackId(), track.getFromStationId(), track.getToStationId(),
                track.getType(), track.getDurability());
    }
}
