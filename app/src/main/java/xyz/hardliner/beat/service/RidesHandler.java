package xyz.hardliner.beat.service;

import xyz.hardliner.beat.domain.Ride;

import java.util.HashMap;

public class RidesHandler {

    public final HashMap<Long, Ride> rides;

    public RidesHandler() {
        rides = new HashMap<>();
    }
}
