package com.daivansh.androidmanagementmdm.ui.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.daivansh.androidmanagementmdm.MyApplication
import com.daivansh.androidmanagementmdm.jobs.ListDeviceJob
import com.daivansh.androidmanagementmdm.ui.adapter.DeviceListAdapter
import com.daivansh.androidmanagementmdm.utils.MyManagementAgent
import com.daivansh.androidmanagementmdm.utils.ProgressDialogHelper
import com.daivansh.androidmanagementmdm.utils.StaticConstants
import com.birbit.android.jobqueue.JobManager
import com.birbit.android.jobqueue.config.Configuration
import com.daivansh.androidmanagementmdm.R
import com.google.api.services.androidmanagement.v1.model.Device
import com.ui.activity.EnrollmentTokenActivity
import kotlinx.android.synthetic.main.activity_device.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

class DeviceActivity : AppCompatActivity(), View.OnClickListener {

    /*lateinit var mRecyclerView: RecyclerView
    lateinit var mTxtNoDeviceFound: TextView*/
    private var myManagementAgent: MyManagementAgent? = null
    private val mContext = this@DeviceActivity


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device)
        Objects.requireNonNull<ActionBar>(supportActionBar).setTitle(StaticConstants.ENTERPRISE)

        EventBus.getDefault().register(mContext)

       /* mTxtNoDeviceFound = findViewById(R.id.tv_no_device_found)
        mRecyclerView = findViewById(R.id.rv_device)*/
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mBtnListDevices.setOnClickListener(this)

        myManagementAgent = (application as MyApplication).myManagementAgent
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_token -> startActivity(Intent(mContext, EnrollmentTokenActivity::class.java))
            R.id.item_policy -> startActivity(Intent(mContext, PolicyActivity::class.java))
            R.id.item_application_policy -> startActivity(Intent(mContext, ApplicationPolicyActivity::class.java))
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.dashboard_menu, menu)
        return true
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.mBtnListDevices -> listDevices()
        }
    }

    private fun listDevices() {
        ProgressDialogHelper.showProgressDialog(mContext)
        val builder = Configuration.Builder(mContext)
        val manager = JobManager(builder.build())
        manager.addJobInBackground(ListDeviceJob(myManagementAgent))
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(list: List<Device>?) {
        ProgressDialogHelper.hideProgressDialog()
        if (list != null) {
            mRecyclerView.visibility = View.VISIBLE
            mTxtNoDeviceFound.visibility = View.GONE
            val mDeviceListAdapter = DeviceListAdapter(mContext, list as ArrayList<Device>)
            mRecyclerView.adapter = mDeviceListAdapter
        } else {
            mRecyclerView.visibility = View.GONE
            mTxtNoDeviceFound.visibility = View.VISIBLE
            Toast.makeText(this@DeviceActivity, getString(R.string.no_device_found), Toast.LENGTH_SHORT).show()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(deletionStatus: Boolean) {
        ProgressDialogHelper.hideProgressDialog()
        if (deletionStatus) {
            Toast.makeText(mContext, getString(R.string.delete_device_success), Toast.LENGTH_SHORT).show()
            listDevices()
        } else {
            Toast.makeText(mContext, getString(R.string.delete_device_failed), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(mContext)
    }
}
