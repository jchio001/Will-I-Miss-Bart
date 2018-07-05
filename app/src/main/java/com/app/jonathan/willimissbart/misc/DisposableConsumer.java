package com.app.jonathan.willimissbart.misc;

import android.support.annotation.NonNull;

import com.app.jonathan.willimissbart.api.Models.Etd.Estimate;
import com.app.jonathan.willimissbart.misc.EstimatesManager.EstimateConsumer;

import java.util.List;

/**
 * Wraps around EstimateConsumer. This wrapper allows caller to dispose the results of
 * EstimateManager's requestEstimates().
 */
public class DisposableConsumer {

    private EstimateConsumer estimateConsumer;

    public DisposableConsumer(@NonNull EstimateConsumer estimateConsumer) {
        this.estimateConsumer = estimateConsumer;
    }

    public synchronized void dispose() {
        this.estimateConsumer = null;
    }

    public synchronized boolean isDisposed() {
        return estimateConsumer == null;
    }

    public synchronized void onPendingEstimates() {
        if (estimateConsumer != null) {
            estimateConsumer.onPendingEstimates();
        }
    }

    public synchronized void consumeEstimates(List<Estimate> estimates) {
        if (estimateConsumer != null) {
            estimateConsumer.consumeEstimates(estimates);
        }
    }
}
