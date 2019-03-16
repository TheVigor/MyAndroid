package com.noble.activity.myandroid.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.noble.activity.myandroid.models.DeviceInfo
import com.noble.activity.myandroid.utilities.LoadStatus

class AppsViewModel: ViewModel() {
    val apps = MutableLiveData<MutableList<DeviceInfo>>()
    val isAppsLoaded: MutableLiveData<LoadStatus> = MutableLiveData()
}
