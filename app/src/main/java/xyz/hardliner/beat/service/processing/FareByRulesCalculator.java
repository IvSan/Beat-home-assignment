package xyz.hardliner.beat.service.processing;

import xyz.hardliner.beat.domain.DataBatch;
import xyz.hardliner.beat.domain.Position;
import xyz.hardliner.beat.domain.RideReport;
import xyz.hardliner.beat.domain.SegmentReport;
import xyz.hardliner.beat.service.rule.Rulebook;
import xyz.hardliner.beat.utils.TimezonesHelper;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.Map;
import java.util.Optional;

public class FareByRulesCalculator implements FareCalculator {

    private final TimezonesHelper timezonesHelper;
    public final Map<ZoneId, Rulebook> localRules;

    public FareByRulesCalculator(TimezonesHelper timezonesHelper,
                                 Map<ZoneId, Rulebook> localRules) {
        this.timezonesHelper = timezonesHelper;
        this.localRules = localRules;
    }

    @Override
    public RideReport calculateRide(DataBatch dataBatch) {
        var rulebook = Optional.of(localRules.get(dataBatch.city))
            .orElseThrow(() -> new IllegalArgumentException("No rulebook for " + dataBatch.city));

        var lastValidPosition = dataBatch.positions.remove(0);
        var cost = BigDecimal.ZERO;

        for (Position position : dataBatch.positions) {
            var report = calculateSegment(lastValidPosition, position, dataBatch.city);
            if (rulebook.isValidSegment(report)) {
                lastValidPosition = position;
                var segmentCost = rulebook.applyRulesOnSegment(report);
                cost = cost.add(segmentCost);
            } else {
                System.out.println("");
            }
        }

        var report = new RideReport(dataBatch.rideId, cost);
        return rulebook.applyRulesOnRide(report);
    }

    private SegmentReport calculateSegment(Position start, Position stop, ZoneId timezone) {
        var segmentDistance = start.latLong.distanceTo(stop.latLong);
        var segmentTime = stop.timestamp - start.timestamp;
        var localMinutesOfDay = timezonesHelper.getLocalMinutesOfDay(stop.timestamp, timezone);

        return new SegmentReport(segmentDistance, segmentTime, localMinutesOfDay);
    }

}
