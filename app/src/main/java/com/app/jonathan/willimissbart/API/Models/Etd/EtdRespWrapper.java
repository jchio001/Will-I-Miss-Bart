package com.app.jonathan.willimissbart.API.Models.Etd;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class EtdRespWrapper {
    private Map<String, List<Estimate>> origDestToEstimates = Maps.newHashMap();

    private long respTime = 0;
    private boolean isReturnRoute = false;

    public EtdRespWrapper(EtdRoot etdRoot, Set<String> destSet, boolean isReturnRoute) {
        this.isReturnRoute = isReturnRoute;

        if (etdRoot != null) {
            this.respTime = etdRoot.getTimeAsEpochMs() / 1000;

            // filter out jank/irrelavant etds first
            List<Etd> filtered = Lists.newArrayList();
            EtdStation etdStation = etdRoot.getStations().get(0);
            for (Etd etd : etdStation.getEtds()) {
                if (destSet.contains(etd.getAbbreviation())) {
                    filtered.add(etd);
                }
            }
            etdStation.setEtds(filtered);

            for (Etd etd : etdStation.getEtds()) {
                origDestToEstimates.put(
                    etdStation.getAbbr() + etd.getAbbreviation(), etd.getEstimates());
            }
        } else {
            respTime = System.currentTimeMillis() / 1000;
        }

        for (String dest : destSet) {
            if (!origDestToEstimates.containsKey(dest)) {
                origDestToEstimates.put(dest, Lists.<Estimate>newArrayList());
            }
        }
    }

    public Map<String, List<Estimate>> getOrigDestToEstimates() {
        return origDestToEstimates;
    }

    public long getRespTime() {
        return respTime;
    }

    public boolean isReturnRoute() {
        return isReturnRoute;
    }
}
