package io.dhruv.weatherwise

import android.location.Location

interface LocationTracker {
    suspend fun getCurrentLocation(): Location?
}