package com.noble.activity.myandroid.utilities

object KeyUtil {
    val datePattern = "dd MMM yyyy HH:mm:ss z"
    val KEY_MODE = "key_mode"
    var SELECTED_LANG = 0

    /*** Sensor  */
    val KEY_SENSOR_NAME = "key_sensor_name"
    val KEY_SENSOR_TYPE = "key_sensor_type"
    val KEY_SENSOR_ICON = "key_sensor_icon"

    var KEY_LAST_KNOWN_HUMIDITY = 0f

    val KEY_CAMERA_CODE = 101
    val KEY_CALL_PERMISSION = 102
    val KEY_READ_PHONE_STATE = 103
    val READ_EXTERNAL_STORAGE = 104
    val IS_USER_COME_FROM_SYSTEM_APPS = 1
    val IS_USER_COME_FROM_USER_APPS = 2
    var IS_USER_COME_FROM_DASHBOARD = "IS_USER_COME_FROM_DASHBOARD"
    val REQUEST_ENABLE_BT = 105
}