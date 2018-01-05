package com.app.jonathan.willimissbart.Misc;

import android.util.Log;

import com.app.jonathan.willimissbart.API.Models.Etd.Estimate;
import com.app.jonathan.willimissbart.API.Models.Etd.EtdRespWrapper;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Map;
import java.util.Set;

// Singleton class for different components of the app to pull estimates from
public class EstimatesManager {
    public interface EstimatesListener {
        void onReceiveEstimates(EtdRespWrapper etdRespWrapper);
        void onEstimatesUpdated();
    }

    private static EstimatesManager instance = null;

    private Map<String, List<Estimate>> origDestToEstimates = Maps.newHashMap();
    // keeps track of when I fetched real time estimates for a specific seconds
    private Map<String, Long> stationToRespTime = Maps.newHashMap();
    // keeps track of seconds remainder when the user wants to update their estimates
    private Map<String, Integer> stationToRemainderSeconds = Maps.newHashMap();
    // keeps track of subscribers for this manager
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

    public synchronized static void updateEstimates(long refreshTime) {
        Map<String, List<Estimate>> origDestToEstimates = getInstance().origDestToEstimates;
        Map<String, Integer> stationToRemainderSeconds = getInstance().stationToRemainderSeconds;
        Map<String, Long> stationToRespTime = getInstance().stationToRespTime;

        Map<String, Integer> stationToUpdatedRemainderSeconds = Maps.newHashMap();
        boolean estimatesUpdated = false;
        for (Map.Entry<String, List<Estimate>> entry : origDestToEstimates.entrySet()) {
            List<Estimate> estimates = entry.getValue();
            String originStation = entry.getKey().substring(0, 4);
            long lastRespTime = stationToRespTime.get(originStation);

            long elapsedTime = refreshTime - lastRespTime;
            if (elapsedTime >= 60) {
                estimatesUpdated = true;

                if (stationToRemainderSeconds.containsKey(originStation)) {
                    elapsedTime += stationToRemainderSeconds.get(originStation);
                }

                int elapsedMinutes = (int) elapsedTime / 60;
                int elapsedSeconds = (int) elapsedTime % 60;

                if (!stationToUpdatedRemainderSeconds.containsKey(originStation)) {
                    stationToUpdatedRemainderSeconds.put(originStation, elapsedSeconds);
                }

                stationToRespTime.put(originStation, refreshTime);

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
        }

        stationToRemainderSeconds.putAll(stationToUpdatedRemainderSeconds);

        if (estimatesUpdated) {
            Log.i("EstimatesManager", "Estimates updated!");
            for (EstimatesListener subscriber : getInstance().subscribers) {
                subscriber.onEstimatesUpdated();
            }
        }
    }
 }
