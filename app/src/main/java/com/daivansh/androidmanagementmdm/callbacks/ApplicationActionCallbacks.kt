package com.daivansh.androidmanagementmdm.callbacks

import com.google.api.services.androidmanagement.v1.model.ApplicationPolicy

interface ApplicationActionCallbacks {

    fun updateApplicationPolicy(application: ApplicationPolicy?)
    fun noAppicationsinList(b: Boolean)
    fun deletedApplication(deletedApplicationPackage: String)
}
