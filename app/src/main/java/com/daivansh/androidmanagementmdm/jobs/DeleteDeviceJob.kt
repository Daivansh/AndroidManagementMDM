package com.daivansh.androidmanagementmdm.jobs

import com.birbit.android.jobqueue.Job
import com.birbit.android.jobqueue.Params
import com.birbit.android.jobqueue.RetryConstraint
import com.google.api.services.androidmanagement.v1.model.Empty

import org.greenrobot.eventbus.EventBus

import com.daivansh.androidmanagementmdm.utils.MyManagementAgent

class DeleteDeviceJob(private val myManagementAgent: MyManagementAgent?, private val mDeviceName: String, private val mWipeDataFlags: List<String>) : Job(Params(1)) {

    override fun onAdded() {

    }

    @Throws(Throwable::class)
    override fun onRun() {
        val empty: Empty? = myManagementAgent?.deleteDevice(mDeviceName, mWipeDataFlags)
        EventBus.getDefault().post(empty?.keys?.size ?: 1 <= 0)
    }

    override fun onCancel(cancelReason: Int, throwable: Throwable?) {

    }

    override fun shouldReRunOnThrowable(throwable: Throwable, runCount: Int, maxRunCount: Int): RetryConstraint? {
        return null
    }
}
