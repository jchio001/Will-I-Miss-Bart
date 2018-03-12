package com.app.jonathan.willimissbart.api.Models.Etd;

import com.app.jonathan.willimissbart.misc.NotGuava;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EtdRespWrapper {
    private Map<String, List<Estimate>> origDestToEstimates = NotGuava.newHashMap();
    private String orig;
    private long respTime = 0;

    public EtdRespWrapper(String orig, EtdRoot etdRoot, Set<String> destSet) {
        this.orig = orig;
        destSet = destSet != null ? destSet : new HashSet<>();

        if (etdRoot != null) {
            this.respTime = Math.min(etdRoot.getTimeAsEpochMs() / 1000,
                System.currentTimeMillis() / 1000);

            // filter out jank/irrelevant etds first. TODO: probably don't need this
            List<Etd> filtered = NotGuava.newArrayList();
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
    }

    public Map<String, List<Estimate>> getOrigDestToEstimates() {
        return origDestToEstimates;
    }

    public String getOrig() {
        return orig;
    }

    public long getRespTime() {
        return respTime;
    }
}