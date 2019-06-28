package com.daivansh.androidmanagementmdm

import android.app.Application

import java.io.IOException
import java.security.GeneralSecurityException

import com.daivansh.androidmanagementmdm.utils.MyManagementAgent


class MyApplication : Application() {

    var myManagementAgent: MyManagementAgent?= null
        private set

    override fun onCreate() {
        super.onCreate()
        try {
            myManagementAgent = MyManagementAgent(MyManagementAgent.getAndroidManagementClient(applicationContext))
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: GeneralSecurityException) {
            e.printStackTrace()
        }

    }
}
