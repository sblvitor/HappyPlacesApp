package com.lira.happyplaces.database

import android.app.Application

class HappyPlacesApp: Application() {
    val db by lazy {
        HappyPlaceDatabase.getInstance(this)
    }
}