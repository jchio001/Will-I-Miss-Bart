package com.app.jonathan.willimissbart.API.Models.Routes;


public class RoutesFailure {
    private boolean isReturnRoute;

    public RoutesFailure(boolean isReturnRoute) {
        this.isReturnRoute = isReturnRoute;
    }

    public boolean isReturnRoute() {
        return isReturnRoute;
    }
}
