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
    private List<Cargo> cargos;

    public CargoStation(String stationId, Position position) {
        super(stationId, position);
        this.cargos = new ArrayList<>();
    }

    @Override
    public int getMaxTrains() {
        return MAX_TRAINS;
    }
    @Override
    public void addTrain(Train train) {
        if (super.getTrains().size() >= MAX_TRAINS) {
            throw new IllegalStateException("CargoStation is full. Maximum allowed trains: " + MAX_TRAINS);
        }
        super.addTrain(train);
    }

    public void removeTrain(Train train) {
        super.removeTrain(train);
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
