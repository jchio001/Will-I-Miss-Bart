package com.app.jonathan.willimissbart.fragment

import com.app.jonathan.willimissbart.api.Models.Routes.Trip
import com.app.jonathan.willimissbart.api.RetrofitClient
import com.app.jonathan.willimissbart.misc.NotGuava
import com.app.jonathan.willimissbart.misc.RouteBundle
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import java.util.*

class TripManager(val userDataManager: UserDataManager,
                  val retrofitClient: RetrofitClient) {

    protected var routeFirstLegHead: String? = null
    protected var returnFirstLegHead: String? = null

    fun getTrips() : Single<ArrayList<Trip?>> {
        val origin = userDataManager.getOriginStationData()
        val destination = userDataManager.getDestinationStationData()

        val departuresSingle = retrofitClient.getTrips(
                origin.getAbbr(),
                destination.getAbbr())

        var returnDeparturesSingle: Single<ArrayList<Trip?>>? = null
        if (userDataManager.includeReturnRoute()) {
            returnDeparturesSingle = retrofitClient.getTrips(
                    destination.getAbbr(),
                    origin.getAbbr())
        }

        return mergeTripRequests(departuresSingle, returnDeparturesSingle)
                .observeOn(AndroidSchedulers.mainThread())
    }

    private fun mergeTripRequests(departuresSingle: Single<ArrayList<Trip?>>,
                                  returnDeparturesSingle: Single<ArrayList<Trip?>>?)
            : Single<ArrayList<Trip?>> {
        return returnDeparturesSingle?.let {
            Single.zip(departuresSingle, it,
                    BiFunction { trips: List<Trip?>, returnTrips: List<Trip?> ->
                        val mergedTrips = NotGuava.newArrayList<Trip>();
                        mergedTrips.addAll(trips)
                        trips.get(0)?.let {
                            routeFirstLegHead = it.legList[0].trainHeadStation
                        }

                        mergedTrips.addAll(returnTrips)
                        trips.get(0)?.let {
                            returnFirstLegHead = it.legList[0].trainHeadStation
                        }

                        return@BiFunction mergedTrips
                    })
        } ?: departuresSingle.doOnSuccess { trips ->
            routeFirstLegHead = trips[0]?.legList?.get(0)?.trainHeadStation
        }
    }

    // Note: trip can be null. See RetrofitClient's getTrips() method.
    fun getRouteBundles(mergedTrips: ArrayList<Trip?>): Pair<RouteBundle?, RouteBundle?> {
        val origToDestsMapping = NotGuava.newHashMap<String, HashSet<String>>()
        for (trip in mergedTrips) {
            trip?.let {
                if (!origToDestsMapping.containsKey(it.origin)) {
                    origToDestsMapping[it.origin] =
                            NotGuava.newHashSet(it.legList[0].trainHeadStation)
                } else {
                    val destSet = origToDestsMapping[it.origin]
                    destSet!!.add(it.legList[0].trainHeadStation)
                }
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