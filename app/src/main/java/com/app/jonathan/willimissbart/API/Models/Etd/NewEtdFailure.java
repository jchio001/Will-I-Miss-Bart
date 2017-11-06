package com.app.jonathan.willimissbart.API.Models.Etd;


public class NewEtdFailure {
    private boolean isReturnRoute = false;

    public NewEtdFailure(boolean isReturnRoute) {
        this.isReturnRoute = isReturnRoute;
    }

    public boolean isReturnRoute() {
        return isReturnRoute;
    }
}
