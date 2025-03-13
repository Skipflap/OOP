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

    /**
     * TEST 2: Single piece of cargo, cargo train, partial route checks
     *  - Cargo train's base speed is 3km/min
     *  - Ensure that the cargo is loaded at the starting station
     *  - Check that partial movement after 1 tick is correct
     *  - Then confirm cargo is removed upon arrival at station
     */
    @Test
    public void testSingleCargoShortRoute() throws InvalidRouteException {
        TrainsController controller = new TrainsController();
        // Stations 9km apart
        controller.createStation("cargoStart", "CargoStation", 0.0, 0.0);
        controller.createStation("cargoEnd", "CargoStation", 9.0, 0.0);
        controller.createTrack("tCargo", "cargoStart", "cargoEnd");

        // Create cargo at cargoStart -> cargoEnd
        controller.createCargo("cargoStart", "cargoEnd", "cargoSingle", 100);

        // Create cargo train (3km/min) at cargoStart
        controller.createTrain("trainCargo", "CargoTrain", "cargoStart", List.of("cargoStart", "cargoEnd"));

        // TICK 1: load cargo, move ~3 km
        controller.simulate();
        TrainInfoResponse infoAfterTick1 = controller.getTrainInfo("trainCargo");
        // position => x=3
        assertEquals(3.0, infoAfterTick1.getPosition().getX(), 0.01);
        // cargo is on the train
        assertEquals(1, infoAfterTick1.getLoads().size());

        // TICK 2 => from x=3 to x=6
        controller.simulate();
        assertEquals(6.0, controller.getTrainInfo("trainCargo").getPosition().getX(), 0.01);

        // TICK 3 => from x=6 to x=9 => arrives at cargoEnd => cargo unloaded
        controller.simulate();
        TrainInfoResponse finalInfo = controller.getTrainInfo("trainCargo");
        assertEquals("cargoEnd", finalInfo.getLocation());
        assertEquals(0, finalInfo.getLoads().size(), "Cargo should be unloaded upon arrival at its destination.");
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
     *  - We'll fill the next station while the train is en-route
     *  - The train must pause inbound if the station is still full by the time it tries to arrive.
     */
    @Test
    public void testStationBecomesFullMidRoute() throws InvalidRouteException {
        TrainsController controller = new TrainsController();
        // stations ~ 10km apart
        controller.createStation("startPS", "PassengerStation", 0, 0);
        controller.createStation("busyPS", "PassengerStation", 10, 0);
        controller.createTrack("track1", "startPS", "busyPS");

        // Fill the busyPS with 2 trains so capacity=2 is reached
        controller.createTrain("fullTrain1", "PassengerTrain", "busyPS", List.of("startPS", "busyPS"));
        controller.createTrain("fullTrain2", "PassengerTrain", "busyPS", List.of("startPS", "busyPS"));

        // Another train that starts at startPS, wants to move to busyPS
        controller.createTrain("incoming", "PassengerTrain", "startPS", List.of("startPS", "busyPS"));

        // Move 'incoming' partial distance. After 4 ticks => x=8 (speed=2km/min)
        // Next tick => inbound if station is full => must remain in place
        controller.simulate(4);
        TrainInfoResponse incomingInfo1 = controller.getTrainInfo("incoming");
        assertEquals(8.0, incomingInfo1.getPosition().getX(), 0.01);

        // Attempt 5th tick => station is still full => incoming doesn't move
        controller.simulate();
        TrainInfoResponse incomingInfo2 = controller.getTrainInfo("incoming");
        assertEquals(8.0, incomingInfo2.getPosition().getX(), 0.01, "Should remain at x=8 because the station is full");

        // Remove one train from busyPS (simulating a departure or re-route)
        // For test’s sake, we can forcibly remove it from the station list
        controller.getStationInfo("busyPS").getTrains().clear();
        // That’s not normal usage, but helps isolate the scenario in a unit test

        // Next tick => now station is no longer full => incoming can arrive
        controller.simulate();
        assertEquals("busyPS", controller.getTrainInfo("incoming").getLocation());
    }
}
