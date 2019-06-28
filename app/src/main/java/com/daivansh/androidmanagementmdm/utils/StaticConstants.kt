package com.daivansh.androidmanagementmdm.utils

interface StaticConstants {
    companion object {
        /** The id of the Google Cloud Platform project.  */
//        TODO: Update Project ID Here
        val PROJECT_ID = "PROJECT ID HERE"

//        TODO: Update Enterprise ID Here
        /**  The enterprise id which you have created from your google account in which you have to assign devices and set policies       */
        val ENTERPRISE = "Enterprise ID"


        // TODO: Update POLICY_ID,POLICY_ABSOLUTE_PATH and POLICY_RELATIVE_PATH
        /** The id of the policy for the COSU device.  */
        val POLICY_ID = "POLICY ID"
        val POLICY_ABSOLUTE_PATH = "POLICY ABSOLUTE PATH"
        val POLICY_RELATIVE_PATH = "POLICY RELATIVE PATH"

        /** The package name of the COSU app.  */
        val COSU_APP_PACKAGE_NAME = "com.google.android.apps.youtube.gaming"


        val COSU_APP_PACKAGE_NAMES = arrayOf("com.android.chrome","com.google.android.apps.youtube.gaming")

        /** The OAuth scope for the Android Management API.  */
        val OAUTH_SCOPE = "https://www.googleapis.com/auth/androidmanagement"

        /** The name of this app.  */
        val APP_NAME = "Android Management"


        //Shared Prefernces files
        val PREF_FILE_TOKEN = "TOKENFILE"
        val PREF_FILE_POLICY = "POLICYFILE"


        //Enrollment Token preference keys
        val TOKEN_VALUE = "token_value"
        val TOKEN_EXPIRY_TIME = "expiry_time"
        val ENROLLMENT_OBJECT = "enrollment_object"
        val POLICY_OBJECT = "policy_object"


        //Token Time
        val EMPTY_STRING = ""
        val UTC_ZULU_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.sss'Z'"
        val DURATION = "2592000s"

        //Policy Apps Intents
        val INTENT_CATEGORY_HOME = "android.intent.category.HOME"
        val INTENT_CATEGORY_DEFAULT = "android.intent.category.DEFAULT"
        val INTENT_CATEGORY_LAUNCHER = "android.intent.category.LAUNCHER"
        val INTENT_ACTION_VIEW = "android.intent.action.VIEW"
        val INTENT_ACTION_MAIN = "android.intent.action.MAIN"


        //Policy Parameters
        val FORCE_INSTALLED = "FORCE_INSTALLED"
        val GRANT = "GRANT"
        val ALWAYS = "ALWAYS"
        val MAX_INTEGER_VALUE = 2147484000L

        val FLAG_PRESERVE_RESET_PROTECTION_DATA = "PRESERVE_RESET_PROTECTION_DATA"

        //Library Specific Type Fields
        val LS_FIELD_SYSTEM_UPDATE = "systemUpdate"

        val INITIAL_INDEX = 0
    }

}
