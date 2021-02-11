package xyz.hardliner.beat.service.pipeline;

import xyz.hardliner.beat.domain.DataBatch;

import java.util.function.Supplier;

public interface DataSupplier extends Supplier<DataBatch> {

}
