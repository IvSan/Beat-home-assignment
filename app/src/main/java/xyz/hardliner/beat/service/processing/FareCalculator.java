package xyz.hardliner.beat.service.processing;

import xyz.hardliner.beat.domain.DataBatch;
import xyz.hardliner.beat.domain.RideReport;

public interface FareCalculator {

    RideReport calculateRide(DataBatch dataBatch);

}
