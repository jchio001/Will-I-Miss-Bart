package com.app.jonathan.willimissbart.Misc;

// Wrapper used to track the state of API calls
public class RespWrapper<T> {
    private boolean fetched;
    private T resp;

    private RespWrapper(boolean fetched, T resp) {
        this.fetched = fetched;
        this.resp = resp;
    }

    public RespWrapper<T> clear() {
        this.fetched = false;
        this.resp = null;
        return this;
    }

    public boolean fetched() {
        return fetched;
    }

    public T getResp() {
        return resp;
    }

    public static <T> RespWrapper<T> of(T resp) {
        return new RespWrapper<>(true, resp);
    }

    public static <T> RespWrapper<T> empty() {
        return new RespWrapper<>(false, null);
    }
}
