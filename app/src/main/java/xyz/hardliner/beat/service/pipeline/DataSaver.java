package xyz.hardliner.beat.service.pipeline;

import xyz.hardliner.beat.domain.RideReport;

public interface DataSaver {

    void save(RideReport report);

}
