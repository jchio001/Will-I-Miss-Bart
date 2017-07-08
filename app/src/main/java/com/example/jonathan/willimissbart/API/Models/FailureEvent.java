package com.example.jonathan.willimissbart.API.Models;

public class FailureEvent {
    public String tag;
    public int code;

    public FailureEvent(String tag, int code) {
        this.tag = tag;
        this.code = code;
    }
}
