package com.daivansh.androidmanagementmdm.jobs

import com.birbit.android.jobqueue.Job
import com.birbit.android.jobqueue.Params
import com.birbit.android.jobqueue.RetryConstraint
import com.google.api.services.androidmanagement.v1.model.Policy

import org.greenrobot.eventbus.EventBus

import com.daivansh.androidmanagementmdm.utils.MyManagementAgent

class GetPolicyJob(private val myManagementAgent: MyManagementAgent?) : Job(Params(1)) {
    private var policy: Policy? = null

    override fun onAdded() {

    }

    @Throws(Throwable::class)
    override fun onRun() {
        policy = myManagementAgent?.retrieveMyPolicy()
        EventBus.getDefault().post(policy)
    }

    override fun onCancel(cancelReason: Int, throwable: Throwable?) {

    }

    override fun shouldReRunOnThrowable(throwable: Throwable, runCount: Int, maxRunCount: Int): RetryConstraint? {
        return null
    }
}
