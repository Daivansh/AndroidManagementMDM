package com.daivansh.androidmanagementmdm.utils

import android.content.Context

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.androidmanagement.v1.AndroidManagement
import com.google.api.services.androidmanagement.v1.model.Command
import com.google.api.services.androidmanagement.v1.model.Device
import com.google.api.services.androidmanagement.v1.model.Empty
import com.google.api.services.androidmanagement.v1.model.EnrollmentToken
import com.google.api.services.androidmanagement.v1.model.Enterprise
import com.google.api.services.androidmanagement.v1.model.Policy

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.security.GeneralSecurityException
import java.util.ArrayList

class MyManagementAgent(
        /** The Android Management API client.  */
        private val androidManagementClient: AndroidManagement) {

    /** Creates a new enterprise. Returns the enterprise name.  */
    @Throws(IOException::class)
    private fun createEnterprise(): String {
        // Initiate signup process.
        println("Creating signup URL...")
        val signupUrl = androidManagementClient
                .signupUrls()
                .create()
                .setProjectId(StaticConstants.PROJECT_ID)
                .setCallbackUrl("https://localhost:9999")
                .execute()
        print(
                "To sign up for a new enterprise, open this URL in your browser: ")
        println(signupUrl.url)
        println(
                "After signup, you will see an error page in the browser.")
        print(
                "Paste the enterpriseToken value from the error page URL here: ")
        val enterpriseToken = BufferedReader(InputStreamReader(System.`in`)).readLine()

        // Create the enterprise.
        println("Creating enterprise...")
        return androidManagementClient
                .enterprises()
                .create(Enterprise())
                .setProjectId(StaticConstants.PROJECT_ID)
                .setSignupUrlName(signupUrl.name)
                .setEnterpriseToken(enterpriseToken)
                .execute()
                .name
    }

    /** Sets the policy of the given id to the given value.  */
    @Throws(IOException::class)
    fun updatePolicy(policy: Policy): Policy {
        println("Setting policy...")
        val name = StaticConstants.ENTERPRISE + "/policies/" + StaticConstants.POLICY_ID
        return androidManagementClient
                .enterprises()
                .policies()
                .patch(name, policy)
                .execute()
    }

    @Throws(IOException::class)
    fun retrieveMyPolicy(): Policy {
        println("Getting policy...")
        return androidManagementClient
                .enterprises()
                .policies()
                .get(StaticConstants.POLICY_ABSOLUTE_PATH)
                .execute()
    }

    /** Creates an enrollment token.  */
    @Throws(IOException::class)
    fun createEnrollmentToken(duration: String): EnrollmentToken {
        println("Creating enrollment token...")
        val token = EnrollmentToken().setPolicyName(StaticConstants.POLICY_ID).setDuration(duration)
        return androidManagementClient
                .enterprises()
                .enrollmentTokens()
                .create(StaticConstants.ENTERPRISE, token)
                .execute()
                .setOneTimeOnly(false)
    }

    /** Lists the first page of devices for an enterprise.  */
    @Throws(IOException::class)
    fun listDevices(): List<Device> {
        println("Listing devices...")
        val response = androidManagementClient
                .enterprises()
                .devices()
                .list(StaticConstants.ENTERPRISE)
                .execute()
        return if (response.devices == null)
            ArrayList()
        else
            response.devices
    }

    fun deleteDevice(deviceName: String, wipeDataFlags: List<String>): Empty? {
        println("Deleting Device$deviceName")
        try {
            return androidManagementClient
                    .enterprises()
                    .devices()
                    .delete(deviceName).execute()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

    /** Reboots a device. Note that reboot only works on Android N+.  */
    @Throws(IOException::class)
    private fun rebootDevice(device: Device) {
        println(
                "Sending reboot command to " + device.name + "...")
        val command = Command().setType("REBOOT")
        androidManagementClient
                .enterprises()
                .devices()
                .issueCommand(device.name, command)
                .execute()
    }

    companion object {

        /** Builds an Android Management API client.  */
        @Throws(IOException::class, GeneralSecurityException::class)
        fun getAndroidManagementClient(context: Context): AndroidManagement {
            val inputStream = context.assets.open("AndroidManagement1.json")
            val credential = GoogleCredential.fromStream(inputStream)
                    .createScoped(setOf(StaticConstants.OAUTH_SCOPE))
            return AndroidManagement.Builder(
                    com.google.api.client.http.javanet.NetHttpTransport(),
                    JacksonFactory.getDefaultInstance(),
                    credential)
                    .setApplicationName(StaticConstants.APP_NAME)
                    .build()
        }
    }
}
