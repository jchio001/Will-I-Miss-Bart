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
