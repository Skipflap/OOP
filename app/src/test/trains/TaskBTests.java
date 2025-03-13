package trains;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import unsw.trains.TrainsController;
import unsw.response.models.LoadInfoResponse;
import unsw.response.models.TrainInfoResponse;
import unsw.response.models.StationInfoResponse;
import unsw.exceptions.InvalidRouteException;

public class TaskBTests {
    /**
     * TEST 1: A passenger train picks up multiple passengers if capacity allows.
     * Ensures the station's passenger loads are cleared once boarding is done.
     */
    @Test
    public void testMultiplePassengersBoardSamePassengerTrain() throws InvalidRouteException {
        TrainsController controller = new TrainsController();

        // Create two passenger stations and connect them.
        controller.createStation("ps1", "PassengerStation", 0, 0);
        controller.createStation("ps2", "PassengerStation", 10, 0);
        controller.createTrack("track-p1p2", "ps1", "ps2");

        // Create 3 passengers at ps1, all traveling to ps2.
        controller.createPassenger("ps1", "ps2", "p1");
        controller.createPassenger("ps1", "ps2", "p2");
        controller.createPassenger("ps1", "ps2", "p3");

        // Create a passenger train at ps1 with enough capacity for all passengers.
        controller.createTrain("trainP", "PassengerTrain", "ps1", List.of("ps1", "ps2"));

        // After the first simulate tick, all 3 passengers should be on board the train.
        controller.simulate();
        TrainInfoResponse trainInfo = controller.getTrainInfo("trainP");
        assertEquals(3, trainInfo.getLoads().size());

        StationInfoResponse stationInfo = controller.getStationInfo("ps1");
        assertEquals(0, stationInfo.getLoads().size());
    }

    /**
     * TEST 2: A cargo train is slowed proportionally by the weight of cargo it carries.
     * After two ticks, we check if its position matches the slower speed calculations.
     * (Note: the actual user must ensure the slow-down logic is coded in simulate().)
     */
    @Test
    public void testCargoTrainSlowedByHeavyCargo() throws InvalidRouteException {
        TrainsController controller = new TrainsController();

        controller.createStation("cs1", "CargoStation", 0, 0);
        controller.createStation("cs2", "CargoStation", 30, 0); // 30 km away
        controller.createTrack("track-c1c2", "cs1", "cs2");

        // Create cargo at cs1, 1000 kg
        controller.createCargo("cs1", "cs2", "cargo1", 1000);

        // Create cargo train. Base speed = 3 km/min
        controller.createTrain("trainC", "CargoTrain", "cs1", List.of("cs1", "cs2"));

        // Tick 1: The cargo should load, then train starts moving.
        controller.simulate();

        // The cargo train now weighs 1000 kg of cargo => speed slowed by 1000 * 0.01% = 10%
        // So new speed = 3 km/min * (1 - 0.10) = 2.7 km/min
        // After 1 tick of movement, the train should have traveled about 2.7 km
        TrainInfoResponse infoAfterFirstTick = controller.getTrainInfo("trainC");
        // Check X coordinate is ~2.7 (± small floating error)
        assertEquals(2.7, infoAfterFirstTick.getPosition().getX(), 0.01);

        // Tick 2: Another minute of movement => 2.7 more km => ~5.4 total distance
        controller.simulate();
        double xAfterSecondTick = controller.getTrainInfo("trainC").getPosition().getX();
        assertEquals(5.4, xAfterSecondTick, 0.01);
    }

    /**
     * TEST 3: A bullet train that can carry both passengers and cargo. Ensures
     * each load is properly loaded at the station if the train will pass the
     * load's destination.
     */
    @Test
    public void testBulletTrainCarriesPassengersAndCargo() throws InvalidRouteException {
        TrainsController controller = new TrainsController();

        // Create stations
        controller.createStation("central", "CentralStation", 0, 0);
        controller.createStation("cargoS", "CargoStation", 10, 0);
        controller.createStation("passengerS", "PassengerStation", 20, 0);

        // Tracks
        controller.createTrack("t1", "central", "cargoS");
        controller.createTrack("t2", "cargoS", "passengerS");

        // Create a bullet train at 'central' with route [central, cargoS, passengerS]
        controller.createTrain("bTrain", "BulletTrain", "central", List.of("central", "cargoS", "passengerS"));

        // Create cargo at cargoS -> passengerS
        controller.createCargo("cargoS", "passengerS", "c1", 200);

        // Create passenger at central -> passengerS
        controller.createPassenger("central", "passengerS", "p1");

        // Tick 1: The bullet train loads the passenger at central
        // and moves some distance toward cargoS
        controller.simulate();

        // Ensure passenger is now on the train
        assertEquals(1, controller.getTrainInfo("bTrain").getLoads().size());
        // No cargo yet (the cargo is at cargoS station)
        assertEquals("p1", controller.getTrainInfo("bTrain").getLoads().get(0).getLoadId());

        // Tick enough times until the train arrives at cargoS (the distance is 10 km
        // and bullet train has base speed 5 km/min, with negligible cargo weight so far).
        controller.simulate(); // minute 2 -> total distance traveled: ~10 km => arrives cargoS

        // Once arrived at cargoS, the cargo c1 should load on the train
        controller.simulate(); // minute 3 - loads cargo, train sets off for passengerS

        // Check that the train is carrying both p1 and c1
        List<LoadInfoResponse> loadsOnTrain = controller.getTrainInfo("bTrain").getLoads();
        assertEquals(2, loadsOnTrain.size());
        // Check that cargoS station is now empty
        assertTrue(controller.getStationInfo("cargoS").getLoads().isEmpty());
    }

    /**
     * TEST 4: Multiple trains arrive at the same station; loads should be assigned
     * by the specified priority: trains are sorted lexicographically by ID, loads
     * sorted lexicographically by ID, then assigned if feasible.
     */
    @Test
    public void testLexicographicalLoadingOrderAtStation() throws InvalidRouteException {
        TrainsController controller = new TrainsController();

        // Create station that can hold cargo and passengers
        controller.createStation("hub", "CentralStation", 0, 0);
        controller.createStation("dest1", "CentralStation", 50, 0); // final stop for all loads
        controller.createTrack("t-hub-1", "hub", "dest1");

        // Create loads at the station
        controller.createPassenger("hub", "dest1", "aPassenger"); // ID 'aPassenger'
        controller.createPassenger("hub", "dest1", "zPassenger");
        controller.createCargo("hub", "dest1", "bCargo", 500);
        controller.createCargo("hub", "dest1", "xCargo", 200);

        // Create two trains, with IDs 'TT1' and 'TT2' so that TT1 < TT2 lex order
        // Both start at 'hub' with route [hub, dest1].
        // Both can carry passengers or cargo => use 'BulletTrain' for simplicity
        controller.createTrain("TT1", "BulletTrain", "hub", List.of("hub", "dest1"));
        controller.createTrain("TT2", "BulletTrain", "hub", List.of("hub", "dest1"));

        // On the first tick, both trains are at the station, so we expect the loads
        // to be assigned in lexicographical order of train IDs, then load IDs:
        // 1) TT1 tries to load 'aPassenger', 'bCargo', 'xCargo', 'zPassenger'
        // 2) TT2 then tries to load any leftover loads in lex order
        controller.simulate();

        TrainInfoResponse trainTT1 = controller.getTrainInfo("TT1");
        TrainInfoResponse trainTT2 = controller.getTrainInfo("TT2");

        // Because there's enough capacity, TT1 picks up all loads before TT2 tries.
        // We expect TT1 to have all 4 loads, TT2 to have 0 loads.
        assertEquals(4, trainTT1.getLoads().size());
        assertEquals(0, trainTT2.getLoads().size());
    }

    /**
     * TEST 5: Passenger and cargo unloading upon reaching their destination,
     * verifying that they are removed from the system once delivered.
     */
    @Test
    public void testLoadsAreRemovedUponArrival() throws InvalidRouteException {
        TrainsController controller = new TrainsController();
        controller.createStation("start", "CentralStation", 0, 0);
        controller.createStation("dest", "CentralStation", 30, 0);
        controller.createTrack("sdTrack", "start", "dest");

        // Create cargo and passenger at start => both want to go to dest
        controller.createCargo("start", "dest", "c1", 500);
        controller.createPassenger("start", "dest", "p1");

        // Create a bullet train that can carry both, route is linear [start, dest].
        // distance is 30km, bullet train speed 5km/min => 6 ticks to arrive
        controller.createTrain("bT", "BulletTrain", "start", List.of("start", "dest"));

        // TICK 1 => train picks up c1 + p1
        controller.simulate();
        assertEquals(0, controller.getStationInfo("start").getLoads().size());
        assertEquals(2, controller.getTrainInfo("bT").getLoads().size());

        // Simulate 5 more ticks => train now inbound to 'dest'
        // Next tick => arrival at 'dest' => loads are delivered => removed from the system
        controller.simulate(5);
        // Right after the 6th tick, the train is inbound or has arrived at 'dest'
        // The cargo/passenger should automatically be removed from the train once it arrives
        assertEquals(0, controller.getTrainInfo("bT").getLoads().size());
    }

    /**
     * TEST 1: Ensure that a passenger train does NOT exceed its max passenger capacity
     * (3500 kg or effectively 50 passengers).
     * The extra passenger should remain at the station if capacity is reached.
     */
    @Test
    public void testPassengerTrainCapacityLimit() throws InvalidRouteException {
        TrainsController controller = new TrainsController();
        controller.createStation("ps1", "PassengerStation", 0.0, 0.0);
        controller.createStation("ps2", "PassengerStation", 10.0, 0.0);
        controller.createTrack("t-ps1-ps2", "ps1", "ps2");

        // Create a passenger train from ps1 -> ps2
        controller.createTrain("trainP", "PassengerTrain", "ps1", List.of("ps1", "ps2"));

        // Add 51 passengers to ps1, each going to ps2
        for (int i = 1; i <= 51; i++) {
            controller.createPassenger("ps1", "ps2", "passenger" + i);
        }

        // 1st simulate tick => train boards as many passengers as capacity allows
        // (50 max). The 51st passenger remains at the station.
        controller.simulate();

        // Check how many loads are on the train
        TrainInfoResponse trainInfo = controller.getTrainInfo("trainP");
        assertEquals(50, trainInfo.getLoads().size());

        // Check that 1 passenger remains at the station
        StationInfoResponse stationInfo = controller.getStationInfo("ps1");
        assertEquals(1, stationInfo.getLoads().size());
    }

    /**
     * TEST 2: Ensure that a cargo train respects the 5000 kg capacity limit.
     * Attempt to load multiple cargo items that exceed 5000 kg in total.
     */
    @Test
    public void testCargoTrainCapacityLimit() throws InvalidRouteException {
        TrainsController controller = new TrainsController();
        controller.createStation("cs1", "CargoStation", 0.0, 0.0);
        controller.createStation("cs2", "CargoStation", 15.0, 0.0);
        controller.createTrack("t-cs1-cs2", "cs1", "cs2");

        // Create cargo train from cs1 -> cs2
        controller.createTrain("cargoTrain", "CargoTrain", "cs1", List.of("cs1", "cs2"));

        // Add multiple cargo items (exceeding 5000 kg)
        controller.createCargo("cs1", "cs2", "cargo1", 3000);
        controller.createCargo("cs1", "cs2", "cargo2", 2000);
        controller.createCargo("cs1", "cs2", "cargo3", 1000); // This pushes total to 6000

        // On the first simulate tick:
        // cargoTrain tries to load cargo1 & cargo2 first => total 5000 kg => capacity hit.
        // cargo3 remains at station.
        controller.simulate();

        // Verify cargo on the train
        TrainInfoResponse trainInfo = controller.getTrainInfo("cargoTrain");
        assertEquals(2, trainInfo.getLoads().size(), "Train should only load first 2 cargo items (3000 & 2000).");
        // Next cargo remains behind
        StationInfoResponse stationInfo = controller.getStationInfo("cs1");
        assertEquals(1, stationInfo.getLoads().size(), "One cargo item should remain at station.");
        assertEquals("cargo3", stationInfo.getLoads().get(0).getLoadId());
    }

    /**
     * TEST 3: Linear route reversing after reaching the end station.
     * This test specifically focuses on the train reversing direction
     * after reaching the final station in a linear route of length > 2.
     */
    @Test
    public void testLinearRouteReversal() throws InvalidRouteException {
        TrainsController controller = new TrainsController();
        // Stations: A -> B -> C -> D
        controller.createStation("A", "PassengerStation", 0, 0);
        controller.createStation("B", "PassengerStation", 10, 0);
        controller.createStation("C", "PassengerStation", 20, 0);
        controller.createStation("D", "PassengerStation", 30, 0);

        // Tracks connecting them in a chain
        controller.createTrack("tA-B", "A", "B");
        controller.createTrack("tB-C", "B", "C");
        controller.createTrack("tC-D", "C", "D");
        // No track from A -> D to keep route linear

        // Train route: [A, B, C, D]
        // The train starts at station B, so it visits C, D, then reverses to C, B, A, etc.
        controller.createTrain("trainL", "PassengerTrain", "B", List.of("A", "B", "C", "D"));

        // We'll move the train step by step. Speed=2km/min, distance between stations=10 km => 5 ticks per link
        // The train is currently at B => next is C => 5 ticks => arrives at C => next is D => 5 more => arrives at D
        // Then it reverses => next station is C => then B => then A, etc.
        controller.simulate(5); // after 5 ticks => at station C
        assertEquals("C", controller.getTrainInfo("trainL").getLocation());

        controller.simulate(5); // after 5 more => at station D
        assertEquals("D", controller.getTrainInfo("trainL").getLocation());

        // Next step => the route reverses => heading back to C
        controller.simulate(5);
        assertEquals("C", controller.getTrainInfo("trainL").getLocation());

        // Then B
        controller.simulate(5);
        assertEquals("B", controller.getTrainInfo("trainL").getLocation());
    }

    /**
     * TEST 4: 'Inbound' behavior check for a passenger train that is near the next station.
     * If speed >= the distance to next station, the train is considered inbound
     * and should arrive exactly at the station in the next tick, not overshoot.
     */
    @Test
    public void testInboundBehavior() throws InvalidRouteException {
        TrainsController controller = new TrainsController();
        controller.createStation("sA", "PassengerStation", 0, 0);
        controller.createStation("sB", "PassengerStation", 3, 0); // 3km away
        controller.createTrack("tAB", "sA", "sB");
        // Passenger train speed = 2km/min

        // The train is at sA, route = [sA, sB]
        controller.createTrain("pTrain", "PassengerTrain", "sA", List.of("sA", "sB"));

        // If we simulate 1 tick, the train moves from x=0 to x=2
        controller.simulate();
        assertEquals(2.0, controller.getTrainInfo("pTrain").getPosition().getX(), 0.01);

        // The next tick => distance to next station is 1 km, train speed is 2 km/min => inbound
        // so it should arrive exactly at x=3, sB, not overshoot.
        controller.simulate();
        // Confirm location is the station 'sB'
        TrainInfoResponse info = controller.getTrainInfo("pTrain");
        assertEquals("sB", info.getLocation());
        assertEquals(3.0, info.getPosition().getX(), 0.01);
    }

    /**
     * TEST 5: If a station becomes full *just* before an inbound train arrives,
     * the inbound train should not move that tick. 
     * (Task B spec 2.5.5: 'If a station becomes full before an inbound train arrives, the inbound train will not move that tick.')
     */
    @Test
    public void testInboundTrainBlockedByFullStation() throws InvalidRouteException {
        TrainsController controller = new TrainsController();
        // We'll use a PassengerStation with capacity=2
        controller.createStation("psA", "PassengerStation", 0, 0);
        controller.createStation("psB", "PassengerStation", 10, 0);
        controller.createTrack("pAB", "psA", "psB");

        // Create 2 trains that remain at psB, filling it up
        controller.createTrain("trainFull1", "PassengerTrain", "psB", List.of("psA", "psB"));
        controller.createTrain("trainFull2", "PassengerTrain", "psB", List.of("psA", "psB"));

        // Now create a 3rd train at psA that wants to move to psB
        controller.createTrain("trainInbound", "PassengerTrain", "psA", List.of("psA", "psB"));

        // Let's move trainInbound until it is inbound to psB
        // Distance=10, speed=2 => 5 ticks from psA -> psB.
        controller.simulate(4);
        // after 4 ticks, position ~ x=8 (2km/min *4).
        // next tick => the train is inbound. 
        // BUT psB is still full (2 trains).
        // So the inbound train should NOT move on the 5th tick.

        controller.simulate();
        TrainInfoResponse inboundInfo = controller.getTrainInfo("trainInbound");
        // The position should remain ~ x=8, not 10, because station is full
        // (the spec says the inbound train won't move this tick).
        assertEquals(8.0, inboundInfo.getPosition().getX(), 0.01);
        // The location is still track-based, not psB
        assertNotEquals("psB", inboundInfo.getLocation());
    }
}


