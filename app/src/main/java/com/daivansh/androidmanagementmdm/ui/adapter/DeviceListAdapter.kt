package com.daivansh.androidmanagementmdm.ui.adapter

import android.app.AlertDialog
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.daivansh.androidmanagementmdm.MyApplication
import com.daivansh.androidmanagementmdm.jobs.DeleteDeviceJob
import com.daivansh.androidmanagementmdm.ui.activity.DeviceActivity
import com.daivansh.androidmanagementmdm.utils.ProgressDialogHelper
import com.daivansh.androidmanagementmdm.utils.StaticConstants
import com.birbit.android.jobqueue.JobManager
import com.birbit.android.jobqueue.config.Configuration
import com.daivansh.androidmanagementmdm.R
import com.google.api.services.androidmanagement.v1.model.Device
import kotlinx.android.synthetic.main.device_item.view.*
import java.util.*

class DeviceListAdapter(private val mContext: Context, private val deviceArrayList: ArrayList<Device>)
    : RecyclerView.Adapter<DeviceListAdapter.DeviceListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceListViewHolder {
        val view = LayoutInflater.from(mContext)
                .inflate(R.layout.device_item, parent, false)
        return DeviceListViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeviceListViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return deviceArrayList.size
    }

    inner class DeviceListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(position: Int){
            val device = deviceArrayList.get(position)
            itemView.tvDeviceName.text = device.name ?: StaticConstants.EMPTY_STRING
            itemView.setOnLongClickListener {
                val dialog = AlertDialog.Builder(mContext)
                        .apply {
                            setMessage(R.string.delete_device_alert_message)
                            setCancelable(false)
                            setNegativeButton(R.string.delete_device_alert_no) { dialogInterface, _ -> dialogInterface.dismiss() }
                            setPositiveButton(R.string.delete_device_alert_yes) { _, _ ->
                                    deleteDevice(deviceArrayList.get(layoutPosition).name ?: StaticConstants.EMPTY_STRING)
                            }
                        }.create()
                dialog.show()
                true
            }
        }
    }

    private fun deleteDevice(deviceName: String) {
        ProgressDialogHelper.showProgressDialog(mContext)
        val builder = Configuration.Builder(mContext)
        val manager = JobManager(builder.build())
        val myManagementAgent = ((mContext as DeviceActivity).application as MyApplication).myManagementAgent
        val wipeDataFlags = ArrayList<String>()
        wipeDataFlags.add(StaticConstants.FLAG_PRESERVE_RESET_PROTECTION_DATA)
        manager.addJobInBackground(DeleteDeviceJob(myManagementAgent, deviceName, wipeDataFlags))
    }
}
