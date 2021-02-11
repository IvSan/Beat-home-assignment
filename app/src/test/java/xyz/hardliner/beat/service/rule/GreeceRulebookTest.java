package xyz.hardliner.beat.service.rule;

import org.junit.jupiter.api.Test;
import xyz.hardliner.beat.domain.RideReport;
import xyz.hardliner.beat.domain.SegmentReport;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GreeceRulebookTest {

    private final GreeceRulebook underTest = new GreeceRulebook();

    @Test
    void shouldApplyRulesOnRide() {
        assertEquals(GreeceRulebook.MIN_FARE,
            underTest.applyRulesOnRide(new RideReport(1L, BigDecimal.ZERO)).cost);
        assertEquals(BigDecimal.TEN.add(GreeceRulebook.FLAG_FARE),
            underTest.applyRulesOnRide(new RideReport(1L, BigDecimal.TEN)).cost);
    }

    @Test
    void shouldApplyRulesOnSegment() {
        assertEquals(GreeceRulebook.IDLE_FARE_PER_SEC.multiply(BigDecimal.valueOf(60)), underTest.applyRulesOnSegment(
            new SegmentReport(0.1, 60, GreeceRulebook.DAY_NIGHT_CHANGING_MINUTE - 10)
        ));
        assertEquals(0,
            GreeceRulebook.NIGHT_TIME_FARE_PER_KM.compareTo(underTest.applyRulesOnSegment(
                new SegmentReport(1, 60, GreeceRulebook.DAY_NIGHT_CHANGING_MINUTE - 10))
            ));
        assertEquals(0,
            GreeceRulebook.DAY_TIME_FARE_PER_KM.compareTo(underTest.applyRulesOnSegment(
                new SegmentReport(1, 60, GreeceRulebook.DAY_NIGHT_CHANGING_MINUTE + 10))
            ));
    }

    @Test
    void shouldJudgeSegmentValidity() {
        assertTrue(underTest.isValidSegment(new SegmentReport(1, 60, 500)));
        assertFalse(underTest.isValidSegment(new SegmentReport(1, 10, 500)));
    }

}