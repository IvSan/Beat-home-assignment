package xyz.hardliner.beat.domain;

import java.math.BigDecimal;

public class CalculationReport {

    public final boolean isValid;
    public final BigDecimal cost;

    public CalculationReport(BigDecimal cost) {
        this.isValid = true;
        this.cost = cost;
    }

    public CalculationReport(boolean isValid) {
        this.isValid = isValid;
        this.cost = BigDecimal.ZERO;
    }
}
