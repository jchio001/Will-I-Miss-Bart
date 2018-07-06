package com.app.jonathan.willimissbart.fragment

import android.os.Bundle
import com.app.jonathan.willimissbart.misc.Constants
import com.app.jonathan.willimissbart.persistence.SPManager
import com.app.jonathan.willimissbart.persistence.models.UserStationData

class UserDataManager {

    val spManager : SPManager
    // A pair of station data. First one is origin
    var userData : java.util.ArrayList<UserStationData>

    constructor(spManager: SPManager, bundle : Bundle?) {
        this.spManager = spManager
        userData = bundle?.getParcelableArrayList<UserStationData>(Constants.USER_DATA)
                ?: spManager.fetchUserData()
    }

    fun getOriginStationData() : UserStationData {
        return userData.get(0)
    }

    fun getDestinationStationData() : UserStationData {
        return userData.get(1)
    }

    fun getUserDataCopy() : ArrayList<UserStationData> {
        return java.util.ArrayList(userData)
    }

    fun includeReturnRoute() : Boolean {
        return spManager.fetchIncludeReturnRoute()
    }
}