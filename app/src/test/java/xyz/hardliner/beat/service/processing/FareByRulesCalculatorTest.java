package xyz.hardliner.beat.service.processing;

import org.junit.jupiter.api.Test;
import xyz.hardliner.beat.domain.DataBatch;
import xyz.hardliner.beat.domain.LatLong;
import xyz.hardliner.beat.domain.Position;
import xyz.hardliner.beat.domain.RideReport;
import xyz.hardliner.beat.domain.SegmentReport;
import xyz.hardliner.beat.service.rule.Rulebook;
import xyz.hardliner.beat.utils.TimezonesManager;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FareByRulesCalculatorTest {

    private final FareByRulesCalculator underTest = new FareByRulesCalculator(
        new TimezonesHelperMock(),
        Map.of(ZoneId.of("Europe/Athens"), new RulebookMock())
    );

    @Test
    void shouldCalculateRide() {
        var positions = new ArrayList<Position>();
        positions.add(new Position(new LatLong(38, 24), 1405595000));
        positions.add(new Position(new LatLong(38.02, 24.02), 1405595200));
        positions.add(new Position(new LatLong(38.04, 24.04), 1405595400));
        positions.add(new Position(new LatLong(38.06, 24.02), 1405595600));
        positions.add(new Position(new LatLong(38.08, 24), 1405595800));
        var batch = new DataBatch(1L, ZoneId.of("Europe/Athens"), positions);

        var actualReport = underTest.calculateRide(batch);

        assertEquals(1L, actualReport.rideId);
        assertEquals(BigDecimal.valueOf(40), actualReport.cost);
    }

    private static class TimezonesHelperMock implements TimezonesManager {

        @Override
        public ZoneId retrieveTimeZone(LatLong latLong) {
            return ZoneId.of("Europe/Athens");
        }

        @Override
        public int getLocalMinutesOfDay(long timestamp, ZoneId zoneId) {
            return 500;
        }
    }

    private static class RulebookMock implements Rulebook {

        @Override
        public RideReport applyRulesOnRide(RideReport report) {
            return report;
        }

        @Override
        public BigDecimal applyRulesOnSegment(SegmentReport report) {
            return BigDecimal.TEN;
        }

        @Override
        public boolean isValidSegment(SegmentReport report) {
            return true;
        }
    }

}