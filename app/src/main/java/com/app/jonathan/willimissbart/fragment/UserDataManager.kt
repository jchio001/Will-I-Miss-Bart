package com.app.jonathan.willimissbart.fragment

import android.os.Bundle
import com.app.jonathan.willimissbart.misc.Constants
import com.app.jonathan.willimissbart.persistence.SPManager
import com.app.jonathan.willimissbart.persistence.models.UserStationData
import java.util.ArrayList

/**
 * Manages user data (the stations they're travelling to and from and whether or not they want a
 * return route).
 */
class UserDataManager {

    interface UserDataSubscriber {
        fun onUserDataChanged(updatedUserData: ArrayList<UserStationData>, includeReturnRoute: Boolean)
    }

    private val spManager : SPManager

    // A pair of station data. First one is origin
    private var userData : ArrayList<UserStationData>

    private var subscriber: UserDataSubscriber? = null

    private var includeReturnRoute : Boolean;

    constructor(spManager: SPManager, bundle : Bundle?) {
        this.spManager = spManager
        userData = bundle?.getParcelableArrayList<UserStationData>(Constants.USER_DATA)
                ?: spManager.fetchUserData()
        includeReturnRoute = spManager.fetchIncludeReturnRoute();
    }

    fun subscribe(subscriber : UserDataSubscriber) {
        this.subscriber = subscriber;
    }

    fun getOriginStationData() : UserStationData {
        return userData.get(0)
    }

    fun getDestinationStationData() : UserStationData {
        return userData.get(1)
    }

    fun getUserDataCopy() : ArrayList<UserStationData> {
        return ArrayList(userData)
    }

    fun includeReturnRoute() : Boolean {
        return includeReturnRoute
    }

    fun updateUserData(updatedUserData: ArrayList<UserStationData>, includeReturnRoute: Boolean) {
        this.userData = ArrayList(updatedUserData)
        this.includeReturnRoute = includeReturnRoute

        spManager.persistUserData(updatedUserData);
        spManager.persistIncludeReturnRoute(includeReturnRoute)

        subscriber?.onUserDataChanged(updatedUserData, includeReturnRoute)
    }
}