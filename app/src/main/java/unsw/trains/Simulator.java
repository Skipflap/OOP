package unsw.trains;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import unsw.stations.Station;
import unsw.tracks.Track;

public class Simulator {
    public static void simulateOneTick(Map<String, Train> trains, Map<String, Station> stations, Map<String, Track> tracks) {
        List<Train> trainList = new ArrayList<>(trains.values());
        Collections.sort(trainList, (t1, t2) -> t1.getTrainId().compareTo(t2.getTrainId()));

        for (Train train : trainList) {
            train.moveOneTick(stations, tracks);
        }
    }
}
