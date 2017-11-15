package com.app.jonathan.willimissbart.Misc;

import com.app.jonathan.willimissbart.API.Models.Etd.Estimate;
import com.app.jonathan.willimissbart.API.Models.Etd.EtdRespWrapper;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Map;
import java.util.Set;

// Singleton class for different components of the app to pull estimates from
public class EstimatesManager {
    private static EstimatesManager instance = null;

    private Map<String, List<Estimate>> origDestToEstimates = Maps.newHashMap();
    private Map<String, Long> stationToRespTime = Maps.newHashMap();
    private Set<EstimatesListener> subscribers = Sets.newHashSet();

    private EstimatesManager() {
    }

    public synchronized static EstimatesManager getInstance() {
        if (instance == null) {
            instance = new EstimatesManager();
        }

        return instance;
    }

    public synchronized static void register(EstimatesListener subscriber) {
        getInstance().subscribers.add(subscriber);
    }

    public synchronized static void unregister(EstimatesListener subscriber) {
        getInstance().subscribers.remove(subscriber);
    }

    public synchronized static void persistThenPost(EtdRespWrapper etdRespWrapper) {
        getInstance().stationToRespTime.put(etdRespWrapper.getOrig(), etdRespWrapper.getRespTime());
        getInstance().origDestToEstimates.putAll(etdRespWrapper.getOrigDestToEstimates());
        for (EstimatesListener subscriber : getInstance().subscribers) {
            subscriber.onReceiveEstimates(etdRespWrapper);
        }
    }

    public synchronized static void clear() {
        getInstance().origDestToEstimates.clear();
    }

    public synchronized static boolean containsKey(String origDest) {
        return getInstance().origDestToEstimates.containsKey(origDest);
    }

    public synchronized static List<Estimate> getEstimates(String origDest) {
        return getInstance().origDestToEstimates.get(origDest);
    }

    public synchronized static long getEstimatesRespTime(String abbr) {
        Map<String, Long> stationToRespTime = getInstance().stationToRespTime;
        if (stationToRespTime.containsKey(abbr)) {
            return stationToRespTime.get(abbr);
        } else {
            return 0;
        }
    }

    public synchronized static void updateEstimates(int elapsedTime) {
        int elapsedMinutes = elapsedTime / 60;
        int elapsedSeconds = elapsedTime % 60;

        Map<String, List<Estimate>> origDestToEstimates = getInstance().origDestToEstimates;
        Map<String, Long> stationToRespTime = getInstance().stationToRespTime;

        for (Map.Entry<String, List<Estimate>> entry : origDestToEstimates.entrySet()) {
            List<Estimate> estimates = entry.getValue();

            for (int i = 0; i < estimates.size(); ++i) {
                Estimate estimate = estimates.get(i);
                if (estimate.getMinutes().equals("Leaving")) {
                    estimates.remove(i);
                    --i;
                } else {
                    int updatedMinutes = Integer.valueOf(estimate.getMinutes()) - elapsedMinutes;
                    if (updatedMinutes < 0) {
                        estimates.remove(i);
                        --i;
                    } else if (updatedMinutes == 0) {
                        estimate.setMinutes("Leaving");
                        --i;
                    } else {
                        estimate.setMinutes(String.valueOf(updatedMinutes));
                    }
                }
            }
        }

        // TODO: Fix this logic
        for (Map.Entry<String, Long> entry : stationToRespTime.entrySet()) {
            stationToRespTime.put(entry.getKey(), entry.getValue() - elapsedSeconds);
        }
    }

    public synchronized static List<Estimate> getEstimatesElseRegister(EstimatesListener listener,
                                                                       String origDest) {
        Map<String, List<Estimate>> origDestToEstimates = getInstance().origDestToEstimates;
        if (origDestToEstimates.containsKey(origDest)) {
            return origDestToEstimates.get(origDest);
        } else {
            EstimatesManager.register(listener);
            return null;
        }
    }
 }
