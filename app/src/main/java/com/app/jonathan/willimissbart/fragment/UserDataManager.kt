package com.app.jonathan.willimissbart.fragment

import android.os.Bundle
import com.app.jonathan.willimissbart.misc.Constants
import com.app.jonathan.willimissbart.misc.NotGuava
import com.app.jonathan.willimissbart.persistence.SPManager
import com.app.jonathan.willimissbart.persistence.models.UserStationData
import java.util.*

/**
 * Manages user data (the stations they're traveling to and from and whether or not they want a
 * return route).
 *
 * There's a lot of coupling with specific functionality in this manager and I'm leaving that as it
 * is for now because this thing is ~100ish lines of code and hasn't become complete spaghetti yet.
 *
 * TODO: includeReturnRoute shouldn't be coupled in this data manager!
 */
class UserDataManager {

    interface UserDataSubscriber {
        fun onUserDataChanged()
    }

    private val spManager : SPManager

    // A pair of station data. First one is origin
    private var userData : ArrayList<UserStationData>

    private val subscribers : LinkedList<UserDataSubscriber> = LinkedList();
    private val ignoringSubscribers : LinkedList<UserDataSubscriber> = LinkedList();

    private var includeReturnRoute : Boolean = false

    constructor(spManager: SPManager, bundle: Bundle?) {
        this.spManager = spManager
        userData = bundle?.getParcelableArrayList<UserStationData>(Constants.USER_DATA)
                ?: spManager.fetchUserData()
        includeReturnRoute = spManager.fetchIncludeReturnRoute()
    }

    constructor(spManager: SPManager) {
        this.spManager = spManager
        this.userData = NotGuava.newArrayList(
                UserStationData.UNSELECTED_DATA,
                UserStationData.UNSELECTED_DATA)
    }

    fun subscribe(subscriber : UserDataSubscriber) {
        for (existingSubscriber in subscribers) {
            if (existingSubscriber == subscriber) {
                return
            }
        }

        subscribers.add(subscriber);
    }

    fun unsubscribe(subscriber : UserDataSubscriber) {
        subscribers.remove(subscriber)
    }

    /**
     * Sometimes we're publishing an event from an entity that contains a subscriber. This entity
     * obviously doesn't need to subscriber to tell it that it needs to update itself since it is
     * the one sending out the broadcast, so it already knows it needs to update itself.
     */
    fun ignoreNextBroadcast(subscriber : UserDataSubscriber) {
        ignoringSubscribers.add(subscriber)
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

    fun isDataFullyInitialized() : Boolean {
        for (userStationData in userData) {
            if (userStationData == UserStationData.UNSELECTED_DATA) {
                return false
            }
        }

        return true
    }

    fun includeReturnRoute() : Boolean {
        return includeReturnRoute
    }

    fun update(updatedUserData: ArrayList<UserStationData>, includeReturnRoute: Boolean) {
        this.userData = ArrayList(updatedUserData)
        this.includeReturnRoute = includeReturnRoute

        for (subscriber in subscribers) {
            if (!ignoringSubscribers.contains(subscriber)) {
                subscriber.onUserDataChanged()
            }
        }

        ignoringSubscribers.clear()
    }

    fun commit(updatedUserData: ArrayList<UserStationData>, includeReturnRoute: Boolean) {
        spManager.persistUserData(updatedUserData);
        spManager.persistIncludeReturnRoute(includeReturnRoute)
    }

    fun commitAndUpdate(updatedUserData: ArrayList<UserStationData>, includeReturnRoute: Boolean) {
        commit(updatedUserData, includeReturnRoute)
        update(updatedUserData, includeReturnRoute)
    }
}