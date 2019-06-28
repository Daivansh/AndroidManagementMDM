package com.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.daivansh.androidmanagementmdm.MyApplication
import com.daivansh.androidmanagementmdm.jobs.CreateEnrollmentTokenJob
import com.daivansh.androidmanagementmdm.utils.MyManagementAgent
import com.daivansh.androidmanagementmdm.utils.ProgressDialogHelper
import com.daivansh.androidmanagementmdm.utils.SharedPreferenceHelper
import com.daivansh.androidmanagementmdm.utils.StaticConstants
import com.birbit.android.jobqueue.JobManager
import com.birbit.android.jobqueue.config.Configuration
import com.daivansh.androidmanagementmdm.R
import com.google.api.services.androidmanagement.v1.model.EnrollmentToken
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_enrollment_token.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class EnrollmentTokenActivity : AppCompatActivity(), View.OnClickListener {

    private val mContext = this@EnrollmentTokenActivity
    private var myManagementAgent: MyManagementAgent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enrollment_token)
        Objects.requireNonNull<ActionBar>(supportActionBar).setTitle(R.string.enrollment_activity_title)
        EventBus.getDefault().register(mContext)
        myManagementAgent = (application as MyApplication).myManagementAgent
        mBtnRefreshToken.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        checkIfATokenIsPresent()
    }

    /*  private void checkIfATokenIsPresent() {
        String tokenvalue=SharedPreferenceHelper.getSharedPreferenceString(mContext, StaticConstants.PREF_FILE_TOKEN,
                StaticConstants.TOKEN_VALUE,StaticConstants.EMPTY_STRING);
        if(tokenvalue==null || tokenvalue.equalsIgnoreCase(StaticConstants.EMPTY_STRING)){
            createNewToken();
        }
        else{
            String expiryTime=SharedPreferenceHelper.getSharedPreferenceString(mContext, StaticConstants.PREF_FILE_TOKEN,
                    StaticConstants.TOKEN_EXPIRY_TIME,StaticConstants.EMPTY_STRING);
            if(expiryTime==null || expiryTime.equalsIgnoreCase(StaticConstants.EMPTY_STRING)){
                createNewToken();
            }
            else{
                if(checkTokenExpiryDate(expiryTime)){
                    createNewToken();
                }
                else{
                    tvToken.setText(String.format("%s \n %s",getString(R.string.emrollment_display),tokenvalue));
                }
            }
        }
    }*/

    private fun checkIfATokenIsPresent() {
        val enrollmentTokenJSON: String = SharedPreferenceHelper.getSharedPreferenceString(mContext, StaticConstants.PREF_FILE_TOKEN,
                StaticConstants.ENROLLMENT_OBJECT, StaticConstants.EMPTY_STRING)
        if (enrollmentTokenJSON.equals(StaticConstants.EMPTY_STRING)) {
            return  createNewToken()
        }
        val gson = Gson()
        val token: EnrollmentToken? = gson.fromJson(enrollmentTokenJSON, EnrollmentToken::class.java)
        if(token?.value?.equals(StaticConstants.EMPTY_STRING) ?: return createNewToken()){
            return createNewToken()
        }
        if( token.expirationTimestamp?.equals(StaticConstants.EMPTY_STRING) ?: return createNewToken()){
            return createNewToken()
        }
        if (checkTokenExpiryDate(token.expirationTimestamp)) {
            return createNewToken()
        }
            tvToken.text = String.format("%s \n %s", getString(R.string.emrollment_display), token.value)
    }


    private fun checkTokenExpiryDate(expiryTime: String): Boolean {
        @SuppressLint("SimpleDateFormat")
        val sdf = SimpleDateFormat(StaticConstants.UTC_ZULU_FORMAT)
        var date: Date? = null
        try {
            date = sdf.parse(expiryTime)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return date?.before(Date()) ?: false
    }

    private fun createNewToken() {
        ProgressDialogHelper.showProgressDialog(mContext)
        tvToken.text = StaticConstants.EMPTY_STRING
        val builder = Configuration.Builder(mContext)
        val manager = JobManager(builder.build())
        manager.addJobInBackground(CreateEnrollmentTokenJob(myManagementAgent))
    }


    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(mContext)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(token: EnrollmentToken?) {
        ProgressDialogHelper.hideProgressDialog()
        if (token != null) {
            tvToken.text = String.format("%s \n %s", getString(R.string.emrollment_display), token.value)
            saveTokenInPreferences(token)
            /* SharedPreferenceHelper.setSharedPreferenceString(mContext, StaticConstants.PREF_FILE_TOKEN,
                    StaticConstants.TOKEN_VALUE,token.getValue());
            SharedPreferenceHelper.setSharedPreferenceString(mContext, StaticConstants.PREF_FILE_TOKEN,
                    StaticConstants.TOKEN_EXPIRY_TIME,token.getExpirationTimestamp());*/
        } else {
            tvToken.text = getString(R.string.no_token)
            Toast.makeText(mContext, getString(R.string.token_creation_failed), Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveTokenInPreferences(token: EnrollmentToken) {
        val gson = Gson()
        val json: String? = gson.toJson(token)
        SharedPreferenceHelper.setSharedPreferenceString(mContext, StaticConstants.PREF_FILE_TOKEN,
                StaticConstants.ENROLLMENT_OBJECT, json)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.mBtnRefreshToken -> checkIfATokenIsPresent()
        }
    }
}
