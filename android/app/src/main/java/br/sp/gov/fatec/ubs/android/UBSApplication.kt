package br.sp.gov.fatec.ubs.android

import android.app.Application

class UBSApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        instance = this
    }
    
    companion object {
        lateinit var instance: UBSApplication
            private set
    }
}
