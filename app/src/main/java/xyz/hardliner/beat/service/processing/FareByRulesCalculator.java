package xyz.hardliner.beat.service.processing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.hardliner.beat.domain.DataBatch;
import xyz.hardliner.beat.domain.Position;
import xyz.hardliner.beat.domain.RideReport;
import xyz.hardliner.beat.domain.SegmentReport;
import xyz.hardliner.beat.service.rule.Rulebook;
import xyz.hardliner.beat.utils.TimezonesManager;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;

public class FareByRulesCalculator implements FareCalculator {

    private static final Logger log = LoggerFactory.getLogger(FareByRulesCalculator.class);

    private final TimezonesManager tzManager;
    public final Map<ZoneId, Rulebook> localRules;

    public FareByRulesCalculator(TimezonesManager timezonesHelper,
                                 Map<ZoneId, Rulebook> localRules) {
        this.tzManager = timezonesHelper;
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
                var segmentCost = rulebook.applyRulesOnSegment(report);
                cost = cost.add(segmentCost);
                log.debug(format("New segment calculated: start at '%s', stop at '%s', result '%s', cost '%s'",
                    lastValidPosition, position, report, segmentCost));
                lastValidPosition = position;
            }
        }

        var report = new RideReport(dataBatch.rideId, cost);
        return rulebook.applyRulesOnRide(report);
    }

    private SegmentReport calculateSegment(Position start, Position stop, ZoneId timezone) {
        var segmentDistance = start.latLong.distanceTo(stop.latLong);
        var segmentTime = stop.timestamp - start.timestamp;
        var localMinutesOfDay = tzManager.getLocalMinutesOfDay(stop.timestamp, timezone);

        return new SegmentReport(segmentDistance, segmentTime, localMinutesOfDay);
    }

}
