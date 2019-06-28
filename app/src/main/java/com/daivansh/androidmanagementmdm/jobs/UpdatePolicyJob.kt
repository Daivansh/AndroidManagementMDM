package com.daivansh.androidmanagementmdm.jobs

import com.birbit.android.jobqueue.Job
import com.birbit.android.jobqueue.Params
import com.birbit.android.jobqueue.RetryConstraint
import com.google.api.services.androidmanagement.v1.model.Policy

import org.greenrobot.eventbus.EventBus

import com.daivansh.androidmanagementmdm.utils.MyManagementAgent

class UpdatePolicyJob(private val myManagementAgent: MyManagementAgent?, private val policy: Policy) : Job(Params(1)) {

    override fun onAdded() {

    }

    @Throws(Throwable::class)
    override fun onRun() {
        val updatedPolicy: Policy? = myManagementAgent?.updatePolicy(policy)
        EventBus.getDefault().post(updatedPolicy)
    }

    override fun onCancel(cancelReason: Int, throwable: Throwable?) {

    }

    override fun shouldReRunOnThrowable(throwable: Throwable, runCount: Int, maxRunCount: Int): RetryConstraint? {
        return null
    }
}
