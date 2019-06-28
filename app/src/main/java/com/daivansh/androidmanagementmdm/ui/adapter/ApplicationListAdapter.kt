package com.daivansh.androidmanagementmdm.ui.adapter

import android.app.AlertDialog
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.daivansh.androidmanagementmdm.R
import com.daivansh.androidmanagementmdm.callbacks.ApplicationActionCallbacks
import com.daivansh.androidmanagementmdm.utils.StaticConstants
import com.google.api.services.androidmanagement.v1.model.ApplicationPolicy
import kotlinx.android.synthetic.main.device_item.view.*
import java.util.*

class ApplicationListAdapter(private val mContext: Context, private var applicaitonPolicyList: MutableList<ApplicationPolicy>?
                             , private val applicationActionCallbacks: ApplicationActionCallbacks
)
    : RecyclerView.Adapter<ApplicationListAdapter.ApplicationListViewHolder>() {

    fun getApplicaitonPolicyList(): List<ApplicationPolicy>? {
        return applicaitonPolicyList
    }

    fun setApplicaitonPolicyList(applicaitonNameList: MutableList<ApplicationPolicy>?) {
        this.applicaitonPolicyList = applicaitonNameList
        notifyDataSetChanged()
    }

    /*public void addApplicaitonPolicyList(List<ApplicationPolicy> applicaitonList) {
        this.applicaitonPolicyList.addAll(applicaitonList);
        notifyDataSetChanged();
    }*/
    fun addApplicaitonPolicy(applicationPolicy: ApplicationPolicy) {
        if(!(applicaitonPolicyList?.add(applicationPolicy) ?: false )){
            applicaitonPolicyList = ArrayList()
            applicaitonPolicyList?.add(applicationPolicy)
        }

        if (applicaitonPolicyList?.size ?: 0 > 0) {
            applicationActionCallbacks.noAppicationsinList(false)
        }
        notifyDataSetChanged()
    }

    fun updateApplicationPolicyInList(applicationPolicy: ApplicationPolicy) {
        var counter = 0
        if(applicaitonPolicyList!=null) {
            for (index in applicaitonPolicyList!!.indices ) {
                val appPolicy: ApplicationPolicy = applicaitonPolicyList!!.get(index)
                if (appPolicy.packageName.equals(applicationPolicy.packageName
                                ?: StaticConstants.EMPTY_STRING, ignoreCase = true)) {
                    applicaitonPolicyList!!.remove(appPolicy)
                    applicaitonPolicyList!!.add(counter, applicationPolicy)
                    break
                }
                counter++
            }
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApplicationListViewHolder {
        val view = LayoutInflater.from(mContext)
                .inflate(R.layout.device_item, parent, false)
        return ApplicationListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ApplicationListViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return applicaitonPolicyList?.size ?: 0
    }

    inner class ApplicationListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
       /* var tvApplicationName: TextView
        init {
            tvApplicationName = itemView.findViewById(R.id.tv_devicename)
        }*/

        fun bind(position: Int) {
            val applicationPolicy: ApplicationPolicy? = applicaitonPolicyList?.get(position)
            if (applicationPolicy != null) {
                itemView.tvDeviceName.text = applicationPolicy.packageName
                itemView.setOnClickListener { applicationActionCallbacks.updateApplicationPolicy(applicationPolicy) }

                itemView.setOnLongClickListener {
                    AlertDialog.Builder(mContext)
                            .apply {
                                setMessage(R.string.delete_application_alert_message)
                                setCancelable(false)
                                setNegativeButton(R.string.delete_device_alert_no) { dialogInterface, _ -> dialogInterface.dismiss() }
                                setPositiveButton(R.string.delete_device_alert_yes) { _, _ ->
                                    applicaitonPolicyList?.remove(applicationPolicy)
                                    if (applicaitonPolicyList?.size ?: 0 <= 0) {
                                        applicationActionCallbacks.noAppicationsinList(true)
                                    } else {
                                        applicationActionCallbacks.deletedApplication(applicationPolicy.packageName)
                                    }
                                    notifyDataSetChanged()
                                }
                            }
                            .create()
                            .show()
                    true
                }
            }
        }
    }
}
