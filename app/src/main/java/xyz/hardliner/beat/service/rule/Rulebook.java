package xyz.hardliner.beat.service.rule;

import xyz.hardliner.beat.domain.RideReport;
import xyz.hardliner.beat.domain.SegmentReport;

import java.math.BigDecimal;

public interface Rulebook {

    RideReport applyRulesOnRide(RideReport report);

    BigDecimal applyRulesOnSegment(SegmentReport report);

    boolean isValidSegment(SegmentReport report);
}
