package com.daivansh.androidmanagementmdm.pojos

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PolicyConfigItem<T> {

    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("fieldName")
    @Expose
    var fieldName: String? = null
    @SerializedName("type")
    @Expose
    var type: Int = 0
    @SerializedName("subObject")
    @Expose
    var subObject: List<T>? = null
        get() {
            return if (type == VALUE_TYPE_LIST || type == VALUE_TYPE_LIBRARY_SPECIFIC)
                field
            else
                null
        }
        set(value) {
            if (type == VALUE_TYPE_LIST || type == VALUE_TYPE_LIBRARY_SPECIFIC)
                this.subObject = value
        }

    fun getType(): Int? {
        return type
    }

    fun setType(type: Int?) {
        this.type = type!!
    }

    /*fun getSubObject(): List<T>? {
        return if (type == VALUE_TYPE_LIST || type == VALUE_TYPE_LIBRARY_SPECIFIC)
            subObject
        else
            null
    }

    fun setSubObject(subObject: List<T>) {
        if (type == VALUE_TYPE_LIST || type == VALUE_TYPE_LIBRARY_SPECIFIC)
            this.subObject = subObject
    }*/

    companion object {

        val VALUE_TYPE_BOOLEAN = 1
        val VALUE_TYPE_LIST = 2
        val VALUE_TYPE_LONG = 3
        val VALUE_TYPE_STRING_MESSAGE = 4
        val VALUE_TYPE_LIBRARY_SPECIFIC = 5
    }

}
