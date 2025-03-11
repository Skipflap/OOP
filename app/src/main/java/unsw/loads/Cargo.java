package unsw.loads;

import unsw.utils.Position;

public class Cargo extends Load {
    public Cargo(String loadId, String destinationId, double weight, Position currentPosition) {
        super(loadId, destinationId, weight, currentPosition);
    }

    @Override
    public String getType() {
        return "Cargo";
    }
}
