package com.app.jonathan.willimissbart.activity.onboarding

import com.app.jonathan.willimissbart.api.Models.Station.Station
import com.app.jonathan.willimissbart.api.RetrofitClient
import com.app.jonathan.willimissbart.misc.Utils
import com.app.jonathan.willimissbart.persistence.SPManager
import com.app.jonathan.willimissbart.persistence.StationsManager
import com.google.gson.Gson
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class OnboardingStationsManager(val retrofitClient: RetrofitClient,
                                val spManager: SPManager,
                                val stationsManager: StationsManager) {

    fun persistStations() {
        spManager.persistStations(Gson().toJson(stationsManager.getStations()))
    }

    fun getStations() : Single<List<Station>> {
        return spManager.fetchStationsJson()
                .flatMap { stationsJson ->
                    if (stationsJson.isEmpty()) {
                        return@flatMap retrofitClient.stations
                                .doOnSuccess {
                                    for (i in 0..it.size) {
                                        it[i].index = i;
                                    }

                                    spManager.persistStations(
                                            Gson().toJson(stationsManager.getStations()))
                                    stationsManager.setStations(it)
                                }
                    } else {
                        return@flatMap Single.just(Utils.stationsJsonToList(stationsJson))
                    }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}