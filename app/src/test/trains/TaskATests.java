package trains;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import unsw.trains.TrainsController;
import unsw.response.models.StationInfoResponse;
import unsw.response.models.TrackInfoResponse;
import unsw.response.models.TrainInfoResponse;
import unsw.utils.Position;
import unsw.exceptions.InvalidRouteException;

public class TaskATests {

    // Test creating stations with edge coordinate values and checking their info.
    @Test
    public void testCreateStationsEdgeCoordinates() {
        TrainsController controller = new TrainsController();
        controller.createStation("s_min", "PassengerStation", 0.0, 0.0);
        controller.createStation("s_max", "DepotStation", 200.0, 200.0);

        StationInfoResponse stationMin = controller.getStationInfo("s_min");
        StationInfoResponse stationMax = controller.getStationInfo("s_max");

        assertEquals(new Position(0.0, 0.0), stationMin.getPosition());
        assertEquals(new Position(200.0, 200.0), stationMax.getPosition());
        assertEquals("PassengerStation", stationMin.getType());
        assertEquals("DepotStation", stationMax.getType());
    }

    // Test that a cyclical route is rejected for a PassengerTrain.
    @Test
    public void testInvalidTrainRouteForPassengerTrain() {
        TrainsController controller = new TrainsController();
        // Create three stations.
        controller.createStation("A", "PassengerStation", 0.0, 0.0);
        controller.createStation("B", "PassengerStation", 10.0, 0.0);
        controller.createStation("C", "PassengerStation", 20.0, 0.0);

        // Create tracks that form a cycle: A->B, B->C, and C->A.
        controller.createTrack("tA-B", "A", "B");
        controller.createTrack("tB-C", "B", "C");
        controller.createTrack("tC-A", "C", "A");

        // A cyclical route (route includes a track from C back to A) should be invalid for a PassengerTrain.
        List<String> route = List.of("A", "B", "C");
        assertThrows(InvalidRouteException.class, () -> {
            controller.createTrain("trainInvalid", "PassengerTrain", "A", route);
        });
    }

    // Test that a BulletTrain can be created with a cyclical route.
    @Test
    public void testBulletTrainValidCyclicalRoute() {
        TrainsController controller = new TrainsController();
        // Create stations (using CentralStation so they can accept all loads).
        controller.createStation("A", "CentralStation", 0.0, 0.0);
        controller.createStation("B", "CentralStation", 10.0, 0.0);
        controller.createStation("C", "CentralStation", 5.0, 8.66); // forming an equilateral triangle

        // Create tracks to form a cycle: A->B, B->C, and C->A.
        controller.createTrack("tA-B", "A", "B");
        controller.createTrack("tB-C", "B", "C");
        controller.createTrack("tC-A", "C", "A");

        List<String> route = List.of("A", "B", "C");
        // For BulletTrain, a cyclical route is allowed.
        assertDoesNotThrow(() -> {
            controller.createTrain("trainBullet", "BulletTrain", "A", route);
        });

        TrainInfoResponse trainInfo = controller.getTrainInfo("trainBullet");
        // The initial position should match that of station A.
        assertEquals(new Position(0.0, 0.0), trainInfo.getPosition());
        assertEquals("BulletTrain", trainInfo.getType());
    }

    // Test creating multiple tracks and verifying the list of track IDs.
    @Test
    public void testMultipleTracksAndListing() {
        TrainsController controller = new TrainsController();
        // Create stations for track connections.
        controller.createStation("s1", "DepotStation", 1.0, 1.0);
        controller.createStation("s2", "CargoStation", 10.0, 10.0);
        controller.createStation("s3", "PassengerStation", 20.0, 20.0);
        controller.createStation("s4", "CentralStation", 30.0, 30.0);

        controller.createTrack("t1", "s1", "s2");
        controller.createTrack("t2", "s2", "s3");
        controller.createTrack("t3", "s3", "s4");
        controller.createTrack("t4", "s4", "s1");

        List<String> expectedTracks = List.of("t1", "t2", "t3", "t4");
        List<String> actualTracks = controller.listTrackIds();

        // As order is not guaranteed, check that all expected track IDs are present.
        assertEquals(expectedTracks.size(), actualTracks.size());
        assertTrue(actualTracks.containsAll(expectedTracks));
    }

    // Test that a train's initial position is the same as its starting station.
    @Test
    public void testTrainInitialPositionMatchesStation() {
        TrainsController controller = new TrainsController();
        controller.createStation("s1", "CargoStation", 15.0, 15.0);
        controller.createStation("s2", "CargoStation", 25.0, 25.0);
        controller.createTrack("t1", "s1", "s2");

        List<String> route = List.of("s1", "s2");
        assertDoesNotThrow(() -> {
            controller.createTrain("trainCargo", "CargoTrain", "s1", route);
        });

        TrainInfoResponse trainInfo = controller.getTrainInfo("trainCargo");
        // The initial position should equal that of station s1.
        assertEquals(new Position(15.0, 15.0), trainInfo.getPosition());
    }

    // Test that when a train is created at a station, the station's train list is updated accordingly.
    @Test
    public void testStationTrainListUpdated() {
        TrainsController controller = new TrainsController();
        controller.createStation("s1", "PassengerStation", 0.0, 0.0);
        controller.createStation("s2", "PassengerStation", 10.0, 0.0);
        controller.createTrack("t1", "s1", "s2");

        List<String> route = List.of("s1", "s2");
        // Create two PassengerTrains at station s1.
        assertDoesNotThrow(() -> {
            controller.createTrain("train1", "PassengerTrain", "s1", route);
            controller.createTrain("train2", "PassengerTrain", "s1", route);
        });

        StationInfoResponse stationInfo = controller.getStationInfo("s1");
        List<TrainInfoResponse> trainsAtStation = stationInfo.getTrains();

        // Verify that the train list includes both train1 and train2.
        assertTrue(trainsAtStation.stream().anyMatch(t -> t.getTrainId().equals("train1")));
        assertTrue(trainsAtStation.stream().anyMatch(t -> t.getTrainId().equals("train2")));
    }

    // **TEST: Creating a station and retrieving its info**
    @Test
    public void testCreateAndRetrieveStation() {
        TrainsController controller = new TrainsController();
        controller.createStation("central", "CentralStation", 50.0, 50.0);

        StationInfoResponse stationInfo = controller.getStationInfo("central");

        assertEquals("central", stationInfo.getStationId());
        assertEquals("CentralStation", stationInfo.getType());
        assertEquals(new Position(50.0, 50.0), stationInfo.getPosition());
    }

    // **TEST: Creating a track and retrieving its info**
    @Test
    public void testCreateAndRetrieveTrack() {
        TrainsController controller = new TrainsController();
        controller.createStation("s1", "PassengerStation", 0.0, 0.0);
        controller.createStation("s2", "PassengerStation", 20.0, 0.0);
        controller.createTrack("t1", "s1", "s2");

        TrackInfoResponse trackInfo = controller.getTrackInfo("t1");

        assertEquals("t1", trackInfo.getTrackId());
        assertEquals("s1", trackInfo.getFromStationId());
        assertEquals("s2", trackInfo.getToStationId());
    }

    // **TEST: Creating a train with a valid linear route**
    @Test
    public void testCreateValidPassengerTrain() {
        TrainsController controller = new TrainsController();
        controller.createStation("A", "PassengerStation", 0.0, 0.0);
        controller.createStation("B", "PassengerStation", 10.0, 0.0);
        controller.createTrack("track1", "A", "B");

        List<String> route = List.of("A", "B");

        assertDoesNotThrow(() -> {
            controller.createTrain("train1", "PassengerTrain", "A", route);
        });

        TrainInfoResponse trainInfo = controller.getTrainInfo("train1");
        assertEquals("train1", trainInfo.getTrainId());
        assertEquals("PassengerTrain", trainInfo.getType());
        assertEquals(new Position(0.0, 0.0), trainInfo.getPosition());
    }

    // **TEST: Invalid - Creating a cyclical route for a PassengerTrain should fail**
    @Test
    public void testPassengerTrainInvalidCyclicalRoute() {
        TrainsController controller = new TrainsController();
        controller.createStation("A", "PassengerStation", 0.0, 0.0);
        controller.createStation("B", "PassengerStation", 10.0, 0.0);
        controller.createStation("C", "PassengerStation", 20.0, 0.0);

        controller.createTrack("t1", "A", "B");
        controller.createTrack("t2", "B", "C");
        controller.createTrack("t3", "C", "A");

        List<String> cyclicalRoute = List.of("A", "B", "C");

        assertThrows(InvalidRouteException.class, () -> {
            controller.createTrain("invalidTrain", "PassengerTrain", "A", cyclicalRoute);
        });
    }

    // **TEST: Creating multiple stations and listing them**
    @Test
    public void testListStations() {
        TrainsController controller = new TrainsController();
        controller.createStation("s1", "DepotStation", 5.0, 5.0);
        controller.createStation("s2", "PassengerStation", 10.0, 10.0);
        controller.createStation("s3", "CargoStation", 20.0, 20.0);

        List<String> stations = controller.listStationIds();

        assertEquals(3, stations.size());
        assertTrue(stations.contains("s1"));
        assertTrue(stations.contains("s2"));
        assertTrue(stations.contains("s3"));
    }

    // **TEST: Creating multiple tracks and listing them**
    @Test
    public void testListTracks() {
        TrainsController controller = new TrainsController();
        controller.createStation("s1", "DepotStation", 0.0, 0.0);
        controller.createStation("s2", "PassengerStation", 10.0, 10.0);
        controller.createStation("s3", "CargoStation", 20.0, 20.0);

        controller.createTrack("track1", "s1", "s2");
        controller.createTrack("track2", "s2", "s3");

        List<String> tracks = controller.listTrackIds();

        assertEquals(2, tracks.size());
        assertTrue(tracks.contains("track1"));
        assertTrue(tracks.contains("track2"));
    }

    // **TEST: A train should not be created at a full station**
    @Test
    public void testFullStationPreventsTrainCreation() {
        TrainsController controller = new TrainsController();
        controller.createStation("passengerStation", "PassengerStation", 0.0, 0.0);
        controller.createStation("nextStation", "PassengerStation", 10.0, 10.0);
        controller.createTrack("t1", "passengerStation", "nextStation");

        List<String> route = List.of("passengerStation", "nextStation");

        // Fill the station (PassengerStation max is 2 trains)
        assertDoesNotThrow(() -> controller.createTrain("train1", "PassengerTrain", "passengerStation", route));
        assertDoesNotThrow(() -> controller.createTrain("train2", "PassengerTrain", "passengerStation", route));

        // Third train should fail
        assertThrows(IllegalStateException.class, () -> {
            controller.createTrain("train3", "PassengerTrain", "passengerStation", route);
        });
    }

    // **TEST: BulletTrain should allow cyclical routes**
    @Test
    public void testBulletTrainAllowsCyclicalRoute() {
        TrainsController controller = new TrainsController();
        controller.createStation("A", "CentralStation", 0.0, 0.0);
        controller.createStation("B", "CentralStation", 10.0, 0.0);
        controller.createStation("C", "CentralStation", 5.0, 8.66);

        controller.createTrack("t1", "A", "B");
        controller.createTrack("t2", "B", "C");
        controller.createTrack("t3", "C", "A");

        List<String> cyclicalRoute = List.of("A", "B", "C");

        assertDoesNotThrow(() -> {
            controller.createTrain("bulletTrain", "BulletTrain", "A", cyclicalRoute);
        });

        TrainInfoResponse trainInfo = controller.getTrainInfo("bulletTrain");
        assertEquals("BulletTrain", trainInfo.getType());
        assertEquals(new Position(0.0, 0.0), trainInfo.getPosition());
    }

}
