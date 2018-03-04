package com.app.jonathan.willimissbart.api.Models.Etd;


public class EtdFailure {
    private boolean isReturnRoute = false;

    public EtdFailure(boolean isReturnRoute) {
        this.isReturnRoute = isReturnRoute;
    }

    public boolean isReturnRoute() {
        return isReturnRoute;
    }
}
