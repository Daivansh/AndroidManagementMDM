package com.daivansh.androidmanagementmdm.pojos

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PolicyConfig {

    @SerializedName("policyConfigItem")
    @Expose
    var policyConfigItem: List<PolicyConfigItem<*>>? = null
}