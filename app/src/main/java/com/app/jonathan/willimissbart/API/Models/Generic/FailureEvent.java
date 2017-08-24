package com.app.jonathan.willimissbart.API.Models.Generic;

public class FailureEvent {
    public String tag;
    public int code;

    public FailureEvent(String tag, int code) {
        this.tag = tag;
        this.code = code;
    }
}
