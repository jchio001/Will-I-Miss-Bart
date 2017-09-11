package com.app.jonathan.willimissbart.API.Models.DeparturesFeedModels;

import com.app.jonathan.willimissbart.API.Models.EtdModels.Estimate;
import com.app.jonathan.willimissbart.API.Models.EtdModels.Etd;
import com.app.jonathan.willimissbart.API.Models.EtdModels.EtdStation;
import com.app.jonathan.willimissbart.Misc.MyApplication;
import com.app.jonathan.willimissbart.Misc.Utils;
import com.app.jonathan.willimissbart.Persistence.Models.UserBartData;
import com.app.jonathan.willimissbart.R;

// Wraps around estimate and gives origin & destination information as well
// This is used so that it's easier to generate a departure feed element
public class FlattenedEstimate {
    private String originAbbr;
    private String originName;
    private String destAbbr;
    private String destName;
    private String title;
    private Estimate estimate;

    private long respTime;

    public FlattenedEstimate(EtdStation etdStation, Etd etd, long respTime, Estimate estimate) {
        this.originAbbr = etdStation.getAbbr();
        this.originName = etdStation.getName();
        this.destAbbr = etd.getAbbreviation();
        this.destName = etd.getDestination();
        this.estimate = estimate;
        this.respTime = respTime;

        if (etd != null) {
            if (estimate != null) {
                this.title = getEstimateText(getMinutes());
            } else {
                this.title = MyApplication.getContext()
                    .getString(R.string.departures_title_format, originAbbr, destAbbr);
            }
        } else {
            this.title = "No departures for " + originAbbr + " " + destAbbr;
        }
    }

    public FlattenedEstimate(UserBartData userBartData) {
        this.originAbbr = userBartData.getAbbr();
        this.originName = userBartData.getStation();
        this.title = "Failure to get departures for " + this.originAbbr;
    }

    private String getEstimateText(String minutes) {
        return "Leaving " + (minutes.equals("Leaving") ? "now!": "in " + minutes + "m");
    }

    public String getOriginAbbr() {
        return originAbbr;
    }

    public String getOriginName() {
        return originName;
    }

    public String getDestAbbr() {
        return destAbbr;
    }

    public String getDestName() {
        return destName;
    }

    public Estimate getEstimate() {
        return estimate;
    }

    public long getRespTime() {
        return respTime;
    }

    public String getTitle() {
        return title;
    }

    public String getHexColor() {
        return estimate.getHexcolor();
    }

    public String getMinutes() {
        return estimate.getMinutes();
    }

    // Returns # of seconds before the train leaves;
    public int getRealTimeEstimate() {
        return Utils.getTimerDuration(estimate.getMinutes(), respTime);
    }

    public String getFormattedRealTimeEstimate() {
        int currentEstimate = getRealTimeEstimate();
        if (currentEstimate > 0) {
            return "is leaving in " + Utils.secondsToFormattedString(currentEstimate);
        } else if (-60 <= currentEstimate && currentEstimate <= 0) {
            return "is leaving now!";
        } else {
            return "probably already left...";
        }
    }
}
