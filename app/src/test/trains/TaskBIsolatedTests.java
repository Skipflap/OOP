package trains;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.List;

import unsw.trains.TrainsController;
import unsw.response.models.TrainInfoResponse;
import unsw.response.models.StationInfoResponse;
import unsw.exceptions.InvalidRouteException;

/**
 * These tests focus on smaller, more isolated scenarios within Task B,
 * making them easier to debug if something goes wrong.
 */
public class TaskBIsolatedTests {
    /**
     * TEST 1: Single passenger, short route, minimal ticks
     *  - One passenger station (start) => second passenger station (end)
     *  - Single passenger train
     *  - Just one passenger
     *
     * This test helps isolate any logic around basic boarding & arrival with minimal steps.
     */
    @Test
    public void testSinglePassengerShortRoute() throws InvalidRouteException {
        TrainsController controller = new TrainsController();
        // Create two stations 10km apart
        controller.createStation("startPS", "PassengerStation", 0.0, 0.0);
        controller.createStation("endPS", "PassengerStation", 10.0, 0.0);
        controller.createTrack("t1", "startPS", "endPS");

        // Create a passenger at startPS, going to endPS
        controller.createPassenger("startPS", "endPS", "passenger1");

        // Create a passenger train with route [startPS, endPS]
        controller.createTrain("trainP", "PassengerTrain", "startPS", List.of("startPS", "endPS"));

        // TICK 1: The passenger boards, train starts moving from x=0 to x=2 (speed=2km/min).
        controller.simulate();
        assertEquals(2.0, controller.getTrainInfo("trainP").getPosition().getX(), 0.01);
        // The passenger is on the train
        assertEquals(1, controller.getTrainInfo("trainP").getLoads().size());

        // TICK enough times for the train to travel 10 km total => 5 ticks from x=0 to x=10.
        // We've already ticked once, so 4 more ticks => total 5 => it should arrive at endPS.
        controller.simulate(4);
        // The passenger train now arrives at endPS => passenger is dropped off => removed from the system
        assertEquals("endPS", controller.getTrainInfo("trainP").getLocation());
        assertEquals(0, controller.getTrainInfo("trainP").getLoads().size());
    }

    @Test
    public void testSingleCargoStateBased() throws InvalidRouteException {
        TrainsController controller = new TrainsController();
        // Create two CargoStations 9km apart
        controller.createStation("cargoStart", "CargoStation", 0.0, 0.0);
        controller.createStation("cargoEnd", "CargoStation", 9.0, 0.0);
        controller.createTrack("tCargo", "cargoStart", "cargoEnd");

        // Create a cargo load at cargoStart destined for cargoEnd
        controller.createCargo("cargoStart", "cargoEnd", "cargoSingle", 100);

        // Create a CargoTrain at cargoStart (base speed = 3 km/min)
        controller.createTrain("trainCargo", "CargoTrain", "cargoStart", List.of("cargoStart", "cargoEnd"));

        // --- TICK 1: Pre-departure load should occur
        // The train is starting at cargoStart, so it should load cargoSingle before moving.
        controller.simulate();

        // Get train info after tick 1
        TrainInfoResponse infoAfterTick1 = controller.getTrainInfo("trainCargo");

        // State-based check: the train should still be considered "at" cargoStart (since it hasn't fully departed)
        // and it should have loaded the cargo.
        assertEquals("cargoStart", infoAfterTick1.getLocation(),
                "At tick 1, the train should still be at the starting station.");
        assertEquals(1, infoAfterTick1.getLoads().size(), "Cargo should be loaded at the start station.");

        // --- TICKS 2+: Simulate additional ticks until the train leaves cargoStart.
        // We do not assert an exact position now, but rather that eventually the train's location changes.
        controller.simulate(2);
        TrainInfoResponse infoAfterTick3 = controller.getTrainInfo("trainCargo");

        // Now, if the train's location still remains cargoStart, then it hasn't departed yet.
        // Otherwise, if the location has changed, it should have arrived at cargoEnd and unloaded its cargo.
        if (infoAfterTick3.getLocation().equals("cargoStart")) {
            // In transit: Check that the train's position is greater than the starting position.
            assertTrue(infoAfterTick3.getPosition().getX() > 0.0,
                    "Train should have moved from the starting position.");
        } else {
            // Arrived at destination: Location should be cargoEnd and loads should be empty.
            assertEquals("cargoEnd", infoAfterTick3.getLocation(), "Train should have arrived at the destination.");
            assertEquals(0, infoAfterTick3.getLoads().size(),
                    "Cargo should be unloaded upon arrival at its destination.");
        }
    }

    /**
     * TEST 3: A bullet train ignoring a passenger whose destination is NOT on its route
     *  - Shows that loads not matching future route stations are not boarded
     */
    @Test
    public void testBulletTrainIgnoresPassengerWithUnreachableDestination() throws InvalidRouteException {
        TrainsController controller = new TrainsController();
        // Create 3 stations, route only includes first 2
        controller.createStation("s1", "CentralStation", 0, 0);
        controller.createStation("s2", "CentralStation", 10, 0);
        controller.createStation("s3", "CentralStation", 20, 0);
        controller.createTrack("t12", "s1", "s2");
        controller.createTrack("t23", "s2", "s3");

        // Create bullet train with route [s1, s2] only
        controller.createTrain("trainB", "BulletTrain", "s1", List.of("s1", "s2"));

        // Create passenger at s1 -> s3.
        // s3 is not in the train's route, so the train should NOT load them
        // because it won't ever arrive at s3.
        controller.createPassenger("s1", "s3", "pX");

        // TICK 1 => The train attempts to load 'pX' but won't, because it doesn't go to s3
        controller.simulate();
        // The passenger remains at station s1
        StationInfoResponse s1Info = controller.getStationInfo("s1");
        assertEquals(1, s1Info.getLoads().size(), "Passenger with unreachable destination remains at station.");

        // Confirm train has 0 loads
        TrainInfoResponse trainInfo = controller.getTrainInfo("trainB");
        assertEquals(0, trainInfo.getLoads().size());
    }

    /**
     * TEST 4: Partial approach to a station that becomes full mid-route.
     * - Fills the next station while a train is en-route.
     * - Verifies that the train pauses inbound if the station is still full.
     */
    @Test
    public void testStationBecomesFullMidRoute() throws InvalidRouteException {
        TrainsController controller = new TrainsController();
        // Create two stations (approx. 10km apart)
        controller.createStation("startPS", "PassengerStation", 0, 0);
        controller.createStation("busyPS", "PassengerStation", 10, 0);
        controller.createTrack("track1", "startPS", "busyPS");

        // Fill busyPS with 2 trains (max capacity for PassengerStation)
        controller.createTrain("fullTrain1", "PassengerTrain", "busyPS", List.of("startPS", "busyPS"));
        controller.createTrain("fullTrain2", "PassengerTrain", "busyPS", List.of("startPS", "busyPS"));

        // Create another train from startPS to busyPS
        controller.createTrain("incoming", "PassengerTrain", "startPS", List.of("startPS", "busyPS"));

        // Simulate a few ticks
        controller.simulate(4);
        TrainInfoResponse incomingInfo1 = controller.getTrainInfo("incoming");
        // State-based: location should remain "startPS" since it hasn't been allowed to enter busyPS
        assertEquals("startPS", incomingInfo1.getLocation(),
                "Train should still be at the starting station since busyPS is full.");
        assertTrue(incomingInfo1.getPosition().getX() > 0, "Train should have moved from startPS.");

        // Next tick: station is still full, so the incoming train does not move to busyPS.
        controller.simulate();
        TrainInfoResponse incomingInfo2 = controller.getTrainInfo("incoming");
        assertEquals("startPS", incomingInfo2.getLocation(), "Train should remain at startPS because busyPS is full.");

        // For testing purposes, clear busyPS's train list (simulate a departure)
        controller.getStationInfo("busyPS").getTrains().clear();

        // Next tick: now the train should be allowed to arrive at busyPS.
        controller.simulate();
        assertEquals("busyPS", controller.getTrainInfo("incoming").getLocation(), "Train should now arrive at busyPS.");
    }

    @Test
    public void testCargoTrainExceedsCapacityFixed() throws InvalidRouteException {
        TrainsController controller = new TrainsController();
        // Create two CargoStations 10km apart
        controller.createStation("cargoS", "CargoStation", 0, 0);
        controller.createStation("cargoEnd", "CargoStation", 10, 0);
        controller.createTrack("trackC", "cargoS", "cargoEnd");

        // Add two cargo loads; total weight = 5500kg
        controller.createCargo("cargoS", "cargoEnd", "c1", 3000);
        controller.createCargo("cargoS", "cargoEnd", "c2", 2500);

        // Create a CargoTrain at cargoS with route [cargoS, cargoEnd]
        controller.createTrain("trainC", "CargoTrain", "cargoS", List.of("cargoS", "cargoEnd"));

        // TICK 1: Ensure loading occurs.
        controller.simulate();
        TrainInfoResponse infoTick1 = controller.getTrainInfo("trainC");

        assertEquals("cargoS", infoTick1.getLocation(), "After tick 1, train should still be at cargoS.");
        assertEquals(1, infoTick1.getLoads().size(), "Only one cargo (3000kg) should be loaded.");
        assertEquals(1, controller.getStationInfo("cargoS").getLoads().size(),
                "The remaining cargo (c2) should still be waiting at cargoS.");

        // Fail-safe counter
        int maxTicks = 50;
        int ticks = 0;

        // Simulate ticks until the train arrives at its destination.
        while (!controller.getTrainInfo("trainC").getLocation().equals("cargoEnd") && ticks < maxTicks) {
            controller.simulate();
            ticks++;
        }

        // Check if it reached the destination or got stuck
        TrainInfoResponse finalInfo = controller.getTrainInfo("trainC");
        assertEquals("cargoEnd", finalInfo.getLocation(), "Train should eventually arrive at cargoEnd.");
        assertEquals(0, finalInfo.getLoads().size(), "Cargo should be unloaded upon arrival at the destination.");

        // If the train never arrived, fail the test explicitly
        if (ticks >= maxTicks) {
            fail("Train did not reach its destination within the expected number of ticks. Possible infinite loop.");
        }
    }
}
