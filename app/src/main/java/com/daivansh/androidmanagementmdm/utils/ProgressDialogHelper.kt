package com.daivansh.androidmanagementmdm.utils

import android.app.ProgressDialog
import android.content.Context
import com.daivansh.androidmanagementmdm.R


object ProgressDialogHelper {
    private var progressDialog: ProgressDialog? = null

    fun showProgressDialog(context: Context) {
        try {
            progressDialog = ProgressDialog(context)
            if (!progressDialog!!.isShowing) {
                progressDialog!!.setMessage(context.getString(R.string.loading))
                progressDialog!!.setCancelable(false)
                progressDialog!!.show()
            }
        } catch (ignore: Throwable) {
        }
    }

    fun hideProgressDialog() {
        try {
            if (progressDialog != null && progressDialog!!.isShowing) {
                progressDialog!!.dismiss()
            }
        } catch (ignore: Throwable) {
        }
    }
}
