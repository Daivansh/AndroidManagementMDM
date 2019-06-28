package com.daivansh.androidmanagementmdm.jobs

import com.daivansh.androidmanagementmdm.utils.MyManagementAgent
import com.daivansh.androidmanagementmdm.utils.StaticConstants
import com.birbit.android.jobqueue.Job
import com.birbit.android.jobqueue.Params
import com.birbit.android.jobqueue.RetryConstraint
import com.google.api.services.androidmanagement.v1.model.EnrollmentToken
import org.greenrobot.eventbus.EventBus

class CreateEnrollmentTokenJob(private val myManagementAgent: MyManagementAgent?) : Job(Params(1)) {
    private var enrollmentToken: EnrollmentToken? = null

    override fun onAdded() {

    }

    @Throws(Throwable::class)
    override fun onRun() {
        enrollmentToken = myManagementAgent?.createEnrollmentToken(StaticConstants.DURATION)
        EventBus.getDefault().post(enrollmentToken)
    }

    override fun onCancel(cancelReason: Int, throwable: Throwable?) {

    }

    override fun shouldReRunOnThrowable(throwable: Throwable, runCount: Int, maxRunCount: Int): RetryConstraint? {
        return null
    }
}
