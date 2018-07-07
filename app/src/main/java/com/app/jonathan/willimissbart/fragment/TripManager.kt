package com.app.jonathan.willimissbart.fragment

import com.app.jonathan.willimissbart.api.Models.Routes.Trip
import com.app.jonathan.willimissbart.api.RetrofitClient
import com.app.jonathan.willimissbart.misc.NotGuava
import com.app.jonathan.willimissbart.misc.RouteBundle
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers

import java.util.ArrayList
import java.util.HashSet

class TripManager(val userDataManager: UserDataManager,
                  val retrofitClient: RetrofitClient) {

    protected var routeFirstLegHead: String? = null
    protected var returnFirstLegHead: String? = null

    fun getTrips() : Single<ArrayList<Trip>> {
        val origin = userDataManager.getOriginStationData()
        val destination = userDataManager.getDestinationStationData()

        val departuresSingle = retrofitClient.getTrips(
                origin.getAbbr(),
                destination.getAbbr())

        var returnDeparturesSingle: Single<ArrayList<Trip>>? = null
        if (userDataManager.includeReturnRoute()) {
            returnDeparturesSingle = retrofitClient.getTrips(
                    destination.getAbbr(),
                    origin.getAbbr())
        }

        return mergeTripRequests(departuresSingle, returnDeparturesSingle)
    }

    private fun mergeTripRequests(departuresSingle: Single<ArrayList<Trip>>,
                                  returnDeparturesSingle: Single<ArrayList<Trip>>?)
            : Single<ArrayList<Trip>> {
        return returnDeparturesSingle?.let{
            Single.zip(departuresSingle, it, BiFunction{ trips: List<Trip>, returnTrips: List<Trip> ->
                val mergedTrips = NotGuava.newArrayList<Trip>();
                mergedTrips.addAll(trips)
                routeFirstLegHead = trips.get(0).getLegList().get(0).getTrainHeadStation()

                mergedTrips.addAll(returnTrips)
                returnFirstLegHead = trips.get(0).getLegList().get(0).getTrainHeadStation()

                return@BiFunction mergedTrips
            })
        } ?: departuresSingle.doOnSuccess{ trips ->
            routeFirstLegHead = trips.get(0).getLegList().get(0).getTrainHeadStation()
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    fun getRouteBundles(mergedTrips: ArrayList<Trip>): Pair<RouteBundle?, RouteBundle?> {
        val origToDestsMapping = NotGuava.newHashMap<String, HashSet<String>>()
        for (trip in mergedTrips) {
            if (!origToDestsMapping.containsKey(trip.origin)) {
                origToDestsMapping[trip.origin] =
                        NotGuava.newHashSet(trip.legList[0].trainHeadStation)
            } else {
                val destSet = origToDestsMapping[trip.origin]
                destSet!!.add(trip.legList[0].trainHeadStation)
            }
        }

        var routeBundle: RouteBundle? = null
        if (routeFirstLegHead != null) {
            val originAbbr = userDataManager.getOriginStationData().getAbbr()
            routeBundle = RouteBundle(originAbbr, origToDestsMapping[originAbbr]!!)
        }

        var returnRouteBundle: RouteBundle? = null
        if (userDataManager.includeReturnRoute() && routeFirstLegHead != null) {
            val destAbbr = userDataManager.getDestinationStationData().getAbbr()
            returnRouteBundle = RouteBundle(destAbbr, origToDestsMapping[destAbbr]!!)
        }

        return Pair(routeBundle, returnRouteBundle)
    }
}