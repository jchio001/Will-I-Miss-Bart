package com.app.jonathan.willimissbart.persistence

import com.app.jonathan.willimissbart.api.Models.Station.Station
import com.app.jonathan.willimissbart.api.RetrofitClient
import com.app.jonathan.willimissbart.misc.NotGuava
import com.google.gson.Gson
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class StationsManager private constructor() {

    private var stations = NotGuava.newArrayList<Station>()

    fun getStations(): List<Station> {
        return get().stations
    }

    // Moderately ugly (from the API perspective). Maybe fix this later.
    fun getStations(spManager: SPManager,
                    retrofitClient: RetrofitClient): Single<ArrayList<Station>> {
        return spManager.fetchStations()
                .flatMap {
                    if (it.isEmpty()) {
                        return@flatMap retrofitClient.stations
                                .doOnSuccess {
                                    for (i in 0 until it.size) {
                                        it[i].index = i;
                                    }

                                    spManager.persistStations(Gson().toJson(it))
                                    stations = it
                                }
                    } else {
                        return@flatMap Single.just(it)
                    }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun setStations(stations: List<Station>) {
        get().stations.addAll(stations)
    }

    companion object {
        private val instance : StationsManager by lazy { StationsManager() }

        // Need the @JvmStatic tag to tell Java that this is a static method!
        @JvmStatic
        fun get() : StationsManager {
            return instance
        }
    }


}