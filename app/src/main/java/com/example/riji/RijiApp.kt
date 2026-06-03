package com.example.riji

import android.app.Application
import com.example.riji.data.AppDatabase

class RijiApp : Application() {

    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }
}
