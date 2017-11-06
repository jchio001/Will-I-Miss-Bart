package com.app.jonathan.willimissbart.API.Models.Etd;

import com.app.jonathan.willimissbart.API.Models.Generic.CDataSection;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class EtdRoot implements Serializable {
    public static final SimpleDateFormat format =
        new SimpleDateFormat("MM/dd/yyyy h:m:s aa z", Locale.ENGLISH);

    @SerializedName("@id")
    @Expose
    private String id;

    @SerializedName("uri")
    @Expose
    private CDataSection uri;

    @SerializedName("date")
    @Expose
    private String date;

    @SerializedName("time")
    @Expose
    private String time;

    @SerializedName("station")
    @Expose
    private List<EtdStation> stations;

    public String getId() {
        return id;
    }

    public CDataSection getUri() {
        return uri;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public List<EtdStation> getStations() {
        return stations;
    }

    public long getTimeAsEpochMs() {
        try {
            return format.parse(date + " " + time).getTime();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
