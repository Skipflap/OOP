package unsw.tracks;

import unsw.utils.TrackType;

public class Track {
    private String trackId;
    private String fromStationId;
    private String toStationId;
    private TrackType type;
    private int durability;

    public Track(String trackId, String fromStationId, String toStationId) {
        if (trackId == null || fromStationId == null || toStationId == null) {
            throw new IllegalArgumentException("Track and station IDs must not be null.");
        }
        this.trackId = trackId;
        this.fromStationId = fromStationId;
        this.toStationId = toStationId;
        // For NORMAL tracks, type is always NORMAL and durability is fixed at 10.
        this.type = TrackType.NORMAL;
        this.durability = 10;
    }

    public String getTrackId() {
        return trackId;
    }

    public String getFromStationId() {
        return fromStationId;
    }

    public String getToStationId() {
        return toStationId;
    }

    public TrackType getType() {
        return type;
    }

    public int getDurability() {
        return durability;
    }

    // task c
    public void damageTrack(int damage) {
        // NORMAL tracks do not decrease in durability.
    }

    // task c
    public void repairTrack() {
        // NORMAL tracks remain at full durability.
    }
}
