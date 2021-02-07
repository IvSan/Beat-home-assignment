package xyz.hardliner.beat.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.hardliner.beat.concurrency.ConcurrentOrderedExecutor;
import xyz.hardliner.beat.domain.DataEntry;
import xyz.hardliner.beat.domain.Ride;
import xyz.hardliner.beat.utils.TimezonesHelper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.String.format;

public class RidesHandler {

    private static final Logger log = LoggerFactory.getLogger(RidesHandler.class);
    private static final boolean IS_DEBUG_LOGGING_ENABLED = log.isDebugEnabled();

    private final TimezonesHelper timezonesHelper;
    private final SegmentCalculator segmentCalculator;
    private final ConcurrentOrderedExecutor<Long> executor;
    private final Map<Long, Ride> rides;

    public RidesHandler(TimezonesHelper timezonesHelper,
                        SegmentCalculator segmentCalculator) {
        this.timezonesHelper = timezonesHelper;
        this.segmentCalculator = segmentCalculator;
        executor = new ConcurrentOrderedExecutor<>();
        rides = new ConcurrentHashMap<>();
    }

    public Map<Long, Ride> getRides() {
        return rides;
    }

    public void process(DataEntry data, String lineToLog) {
        executor.planTask(data.rideId, () -> {
            if (!rides.containsKey(data.rideId)) {
                createNewRide(data, lineToLog);
                return;
            }
            updateExistingRide(data, lineToLog);
        });
    }

    public void endProcessing() {
        executor.close();
    }

    private void createNewRide(DataEntry data, String lineToLog) {
        var ride = new Ride(data, timezonesHelper.retrieveTimeZone(data.position.latLong));
        if (IS_DEBUG_LOGGING_ENABLED)
            log.debug(format(
                "Processing line: '%s'. New ride found, ride id '%d', ride timezone '%s'",
                lineToLog, ride.rideId, ride.timezone));
        rides.put(data.rideId, ride);
    }

    private void updateExistingRide(DataEntry data, String lineToLog) {
        var ride = rides.get(data.rideId);
        var calculation = segmentCalculator.calculateSegmentCost(ride.lastData.position, data.position, ride.timezone, lineToLog);
        if (calculation.isValid) {
            ride.updateRide(calculation.cost, data);
        }
    }
}
