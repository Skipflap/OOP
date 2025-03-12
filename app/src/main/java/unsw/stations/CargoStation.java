package unsw.stations;

import unsw.utils.Position;
import unsw.trains.Train;
import unsw.loads.Cargo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CargoStation extends Station {
    private static final int MAX_TRAINS = 4;
    private List<Train> trains;
    private List<Cargo> cargos;

    public CargoStation(String stationId, Position position) {
        super(stationId, position);
        this.trains = new ArrayList<>();
        this.cargos = new ArrayList<>();
    }

    @Override
    public int getMaxTrains() {
        return MAX_TRAINS;
    }

    public void addTrain(Train train) {
        if (trains.size() >= MAX_TRAINS) {
            throw new IllegalStateException("CargoStation is full. Maximum allowed trains: " + MAX_TRAINS);
        }
        trains.add(train);
    }

    public void removeTrain(Train train) {
        trains.remove(train);
    }

    public void addCargo(Cargo cargo) {
        if (cargo == null) {
            throw new IllegalArgumentException("Cargo cannot be null");
        }
        cargos.add(cargo);
    }

    public void removeCargo(Cargo cargo) {
        cargos.remove(cargo);
    }

    public List<Cargo> getWaitingCargo() {
        List<Cargo> sorted = new ArrayList<>(cargos);
        Collections.sort(sorted, Comparator.comparing(Cargo::getLoad));
        return sorted;
    }

    public void clearCargo() {
        cargos.clear();
    }

    @Override
    public boolean canHoldPassengers() {
        return false;
    }

    @Override
    public boolean canHoldCargo() {
        return true;
    }

    //xtra methods for later
}
