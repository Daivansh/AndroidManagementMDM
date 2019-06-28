package com.daivansh.androidmanagementmdm.ui.activity

import android.app.AlertDialog
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.daivansh.androidmanagementmdm.MyApplication
import com.daivansh.androidmanagementmdm.callbacks.ApplicationActionCallbacks
import com.daivansh.androidmanagementmdm.jobs.GetPolicyJob
import com.daivansh.androidmanagementmdm.jobs.UpdatePolicyJob
import com.daivansh.androidmanagementmdm.ui.adapter.ApplicationListAdapter
import com.daivansh.androidmanagementmdm.utils.MyManagementAgent
import com.daivansh.androidmanagementmdm.utils.ProgressDialogHelper
import com.daivansh.androidmanagementmdm.utils.StaticConstants
import com.birbit.android.jobqueue.JobManager
import com.birbit.android.jobqueue.config.Configuration
import com.daivansh.androidmanagementmdm.R
import com.google.api.services.androidmanagement.v1.model.ApplicationPolicy
import com.google.api.services.androidmanagement.v1.model.PersistentPreferredActivity
import com.google.api.services.androidmanagement.v1.model.Policy
import kotlinx.android.synthetic.main.application_settings_item.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList

class ApplicationPolicyActivity : AppCompatActivity(), ApplicationActionCallbacks, View.OnClickListener, AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {

    private val mContext = this@ApplicationPolicyActivity
    private var myManagementAgent: MyManagementAgent? = null
    lateinit internal var applicationActionCallbacks: ApplicationActionCallbacks
    private var mPersistentPreferredActivity: PersistentPreferredActivity? = null
    private var mPolicy: Policy? = null
    private var mApplicationPolicy: ApplicationPolicy? = null
    lateinit private var mApplicationListAdapter: ApplicationListAdapter
   /* lateinit internal var linearLayout: LinearLayout
    lateinit internal var linearLayoutCheckbox: LinearLayout
    lateinit private var buttonAddApplication: Button
    lateinit internal var tvNoApplicationsFound: TextView
    lateinit internal var etPackageName: EditText
    lateinit internal var spinnerInstallType: Spinner
    lateinit internal var spinnerDefaultPermission: Spinner
    lateinit internal var switchPersistent: Switch
    lateinit internal var switchDisable: Switch
    lateinit internal var switchLocktaskmode: Switch
    lateinit internal var cbCategoryHome: CheckBox
    lateinit internal var cbCategoryDefault: CheckBox
    lateinit internal var cbCategoryLauncher: CheckBox
    lateinit internal var cbActionMain: CheckBox
    lateinit internal var cbActionView: CheckBox*/

    lateinit internal var rvPolicySettings: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.application_settings_item)
        Objects.requireNonNull<ActionBar>(supportActionBar).setTitle(R.string.applications_activity_title)
        EventBus.getDefault().register(mContext)
        myManagementAgent = (application as MyApplication).myManagementAgent
        setupApplicationsList()
        setupSpinners()
        setListeners()
//        setupViewIDs()
    }

    /*private fun setupViewIDs() {
        linearLayout = findViewById(R.id.linear_layout_below)
        linearLayoutCheckbox = findViewById(R.id.linear_layout_checkbox)
        tvNoApplicationsFound = findViewById(R.id.tv_no_applications)
        buttonAddApplication = findViewById(R.id.btn_add_application)
        etPackageName = findViewById(R.id.et_app_package_name)
        spinnerDefaultPermission = findViewById(R.id.spinner_app_permission)
        spinnerInstallType = findViewById(R.id.spinner_app_install_type)
        switchDisable = findViewById(R.id.switch_app_disabled)
        switchLocktaskmode = findViewById(R.id.switch_app_locktask)
        switchPersistent = findViewById(R.id.switch_persistent)
        cbCategoryHome = findViewById(R.id.check_category_home)
        cbActionView = findViewById(R.id.check_action_view)
        cbCategoryDefault = findViewById(R.id.check_category_default)
        cbCategoryLauncher = findViewById(R.id.check_category_launcher)
        cbActionMain = findViewById(R.id.check_action_main)

    }*/

    private fun setupSpinners() {
        val permissionSpinnerList = mContext.resources.getStringArray(R.array.application_setting_permission_spinner)
        val installTypeSpinnerList = mContext.resources.getStringArray(R.array.application_setting_installtype_spinner)

        val myPermissionSpinnerAdapter = ArrayAdapter(mContext, android.R.layout.simple_spinner_item, permissionSpinnerList)
        val myInstallTypeSpinnerAdapter = ArrayAdapter(mContext, android.R.layout.simple_spinner_item, installTypeSpinnerList)

        myInstallTypeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        myPermissionSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerInstallType.adapter = myInstallTypeSpinnerAdapter
        spinnerDefaultPermission.adapter = myPermissionSpinnerAdapter
    }

    private fun setListeners() {
        buttonAddApplication.setOnClickListener(this)
        //        etPackageName.addTextChangedListener(packageTextWatcher);
        spinnerDefaultPermission.onItemSelectedListener = this
        spinnerInstallType.onItemSelectedListener = this
        switchDisable.setOnCheckedChangeListener(this)
        switchLocktaskmode.setOnCheckedChangeListener(this)
        switchPersistent.setOnCheckedChangeListener(this)
        updateApplicationPolicy(mApplicationPolicy)
    }

    override fun onResume() {
        super.onResume()
        getCurrentPolicy()
    }


    private fun setupApplicationsList() {
        applicationActionCallbacks = this
        rvPolicySettings = findViewById(R.id.rv_application_list)
        mApplicationListAdapter = ApplicationListAdapter(mContext, null, applicationActionCallbacks)
        rvPolicySettings.layoutManager = LinearLayoutManager(mContext, LinearLayout.HORIZONTAL, false)
        rvPolicySettings.adapter = mApplicationListAdapter
    }

    private fun getCurrentPolicy() {
        ProgressDialogHelper.showProgressDialog(mContext)
        val builder = Configuration.Builder(mContext)
        val manager = JobManager(builder.build())
        manager.addJobInBackground(GetPolicyJob(myManagementAgent))
    }

    private fun updatePolicy() {
        val applicationPolicies: List<ApplicationPolicy>? = mApplicationListAdapter.getApplicaitonPolicyList()
        mPolicy?.let {
            it.applications = applicationPolicies
            updatePersistentPreferredActivityInPolicy()
            sCheckUpdatedPolicy = true
            ProgressDialogHelper.showProgressDialog(mContext)
            val builder = Configuration.Builder(mContext)
            val manager = JobManager(builder.build())
            manager.addJobInBackground(UpdatePolicyJob(myManagementAgent, it))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_update_policy -> updatePolicy()
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.policy_menu, menu)
        return true
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(policy: Policy?) {
        ProgressDialogHelper.hideProgressDialog()
        if (policy != null) {
            this.mPolicy = policy
            val applicationPolicyList: MutableList<ApplicationPolicy>? = policy.applications
            if (applicationPolicyList != null) {
                /* rvPolicySettings.visibility = View.VISIBLE
                 tvNoApplicationsFound.visibility = View.GONE*/
                mApplicationListAdapter.setApplicaitonPolicyList(applicationPolicyList)
                mApplicationPolicy = applicationPolicyList.get(0)
                updatePersistentPreferredActivityVariable()
                updateApplicationPolicy(mApplicationPolicy)
                noAppicationsinList(false)
            } else {
                noAppicationsinList(true)
                mApplicationListAdapter.setApplicaitonPolicyList(null)
                mApplicationPolicy = null
                mPersistentPreferredActivity = null
            }
            if (sCheckUpdatedPolicy) {
                Toast.makeText(mContext, getString(R.string.applications_updated_successfully), Toast.LENGTH_SHORT).show()
                sCheckUpdatedPolicy = false
            }
        } else if (sCheckUpdatedPolicy) {
            Toast.makeText(mContext, getString(R.string.policy_not_applied), Toast.LENGTH_SHORT).show()
            sCheckUpdatedPolicy = false
        }
    }

    private fun updatePersistentPreferredActivityVariable() {
        val persistentPreferredList: MutableList<PersistentPreferredActivity>? = mPolicy?.persistentPreferredActivities
        if (persistentPreferredList != null && mApplicationPolicy != null) {
            var check = true
            for (persistentPreferred in persistentPreferredList) {
                val recieverActivity: String = persistentPreferred.receiverActivity ?: StaticConstants.EMPTY_STRING
                if (mApplicationPolicy?.packageName.equals(recieverActivity, ignoreCase = true)) {
                    mPersistentPreferredActivity = persistentPreferred
                    check = false
                    break
                }
            }
            if (check) {
                mPersistentPreferredActivity = null
            }
        }
        checkPersistency()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(mContext)
    }

    override fun updateApplicationPolicy(application: ApplicationPolicy?) {
        updatePersistentPreferredActivityInPolicy()
        mApplicationPolicy?.let { mApplicationListAdapter.updateApplicationPolicyInList(it) }
        mApplicationPolicy = application
        setupData()
    }

    private fun setupData() {
        mApplicationPolicy?.let {
            updatePersistentPreferredActivityVariable()
            switchPersistent.tag = it.packageName ?: StaticConstants.EMPTY_STRING
            updateSpinners(it)
            updateSwitches(it)
            updateEditText(it)
        }
    }

    override fun noAppicationsinList(b: Boolean) {
        if (b) {
            rvPolicySettings.visibility = View.GONE
            tvNoApplicationsFound.visibility = View.VISIBLE
            linearLayout.visibility = View.GONE
            enableCheckBoxes(false)
            mPolicy?.persistentPreferredActivities = null
            mPersistentPreferredActivity = null
        } else {
            if(mPersistentPreferredActivity!=null) enableCheckBoxes(true)
            rvPolicySettings.visibility = View.VISIBLE
            tvNoApplicationsFound.visibility = View.GONE
            linearLayout.visibility = View.VISIBLE
        }
    }

    override fun deletedApplication(deletedApplicationPackage: String) {
        if (deletedApplicationPackage.equals(mApplicationPolicy?.packageName ?: StaticConstants.EMPTY_STRING , ignoreCase = true)) {
            mApplicationPolicy = mApplicationListAdapter.getApplicaitonPolicyList()?.get(StaticConstants.INITIAL_INDEX)
            setupData()
        }
        val persistentPreferredActivities: MutableList<PersistentPreferredActivity>? = mPolicy?.persistentPreferredActivities
        persistentPreferredActivities?.let {
            for (i in it.indices) {
                val preferredActivity: PersistentPreferredActivity = it.get(i)
                if (preferredActivity.receiverActivity.equals(deletedApplicationPackage, ignoreCase = true)) {
                    mPolicy?.persistentPreferredActivities?.remove(preferredActivity)
                }
            }
        }
    }


    private fun updatePersistentPreferredActivityInPolicy() {
        var preferredActivities: MutableList<PersistentPreferredActivity>? = mPolicy?.persistentPreferredActivities
        if (preferredActivities != null) {
            for (i in preferredActivities.indices) {
                val preferredActivity = preferredActivities[i]
                mApplicationPolicy.let {

                }
                if(mApplicationPolicy!=null){
                    if (preferredActivity.receiverActivity.equals(mApplicationPolicy?.packageName, ignoreCase = true)) {
                        preferredActivities.remove(preferredActivity)
                        break
                    }
                }
            }
            if(mPersistentPreferredActivity!=null){
                preferredActivities.add(mPersistentPreferredActivity!!)
            }
        } else {
            if(mPersistentPreferredActivity!=null){
                preferredActivities=ArrayList()
                preferredActivities.add(mPersistentPreferredActivity!!)
            }

        }
        mPolicy?.persistentPreferredActivities = preferredActivities
    }

    private fun checkPersistency() {
        if (mPersistentPreferredActivity != null && mApplicationPolicy != null) {
            switchPersistent.isChecked = true
            enableCheckBoxes(true)
            var intents: List<String>? = mPersistentPreferredActivity!!.actions
            if (intents != null) {
                for (intent in intents) {
                    when (intent) {
                        StaticConstants.INTENT_ACTION_MAIN -> cbActionMain.isChecked = true
                        StaticConstants.INTENT_ACTION_VIEW -> cbActionView.isChecked = true
                    }
                }
            }
            intents = mPersistentPreferredActivity!!.categories
            if (intents != null) {
                for (intent in intents) {
                    when (intent) {
                        StaticConstants.INTENT_CATEGORY_DEFAULT -> cbCategoryDefault.isChecked = true
                        StaticConstants.INTENT_CATEGORY_HOME -> cbCategoryHome.isChecked = true
                        StaticConstants.INTENT_CATEGORY_LAUNCHER -> cbCategoryLauncher.isChecked = true
                    }
                }
            }
        } else {
            switchPersistent.isChecked = false
            enableCheckBoxes(false)
        }
    }

    private fun enableCheckBoxes(b: Boolean) {
        if (b) {
            linearLayoutCheckbox.visibility = View.VISIBLE
            cbActionView.setOnCheckedChangeListener(this)
            cbActionMain.setOnCheckedChangeListener(this)
            cbCategoryDefault.setOnCheckedChangeListener(this)
            cbCategoryHome.setOnCheckedChangeListener(this)
            cbCategoryLauncher.setOnCheckedChangeListener(this)
        } else {
            linearLayoutCheckbox.visibility = View.GONE
            cbActionView.setOnCheckedChangeListener(null)
            cbActionMain.setOnCheckedChangeListener(null)
            cbCategoryDefault.setOnCheckedChangeListener(null)
            cbCategoryHome.setOnCheckedChangeListener(null)
            cbCategoryLauncher.setOnCheckedChangeListener(null)
            cbActionMain.isChecked = b
            cbActionView.isChecked = b
            cbCategoryDefault.isChecked = b
            cbCategoryHome.isChecked = b
            cbCategoryLauncher.isChecked = b
        }
    }

    private fun updateEditText(appPolicy: ApplicationPolicy) {
        etPackageName.setText(appPolicy.packageName ?: StaticConstants.EMPTY_STRING)
    }

    private fun updateSwitches(appPolicy: ApplicationPolicy) {
        switchDisable.isChecked = appPolicy.disabled ?: false
        switchLocktaskmode.isChecked = appPolicy.lockTaskAllowed ?: false
    }

    private fun updateSpinners(appPolicy: ApplicationPolicy) {
        val permissionSpinnerList = mContext.resources.getStringArray(R.array.application_setting_permission_spinner)
        val installTypeSpinnerList = mContext.resources.getStringArray(R.array.application_setting_installtype_spinner)

        var value: String? = appPolicy.defaultPermissionPolicy
        if (value != null) {
            for (i in permissionSpinnerList.indices) {
                if (value.equals(permissionSpinnerList[i], ignoreCase = true)) {
                    spinnerDefaultPermission.setSelection(i)
                }
            }
        } else {
            spinnerDefaultPermission.setSelection(0)
        }
        value = appPolicy.installType
        if (value != null) {
            for (i in installTypeSpinnerList.indices) {
                if (value.equals(installTypeSpinnerList[i], ignoreCase = true)) {
                    spinnerInstallType.setSelection(i)
                }
            }
        } else {
            spinnerInstallType.setSelection(0)
        }
    }

    override fun onClick(view: View) {
        if (view.id == R.id.buttonAddApplication) {
            val builder = AlertDialog.Builder(mContext)

            val input = EditText(mContext)
            val lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT)
            input.layoutParams = lp

            builder.setView(input).apply {
                setMessage(R.string.add_application_alert_message)
                setCancelable(false)
                setNegativeButton(R.string.add_application_alert_cancel) { dialogInterface, _ -> dialogInterface.dismiss() }
                setPositiveButton(R.string.add_application_alert_add) { _ , _ ->
                    val packageName = input.text.toString().trim()
                    if (validatePackageName(packageName)) {
                        val applicationPolicy = ApplicationPolicy()
                        applicationPolicy.packageName = packageName
                        mApplicationListAdapter.addApplicaitonPolicy(applicationPolicy)
                        mApplicationPolicy = applicationPolicy
                        setupData()

                    } else {
                        input.error = getString(R.string.wrong_package_name)
                    }
                }
                create()
                show()
            }
        }
    }

    private fun validatePackageName(packageName: String): Boolean {
        val pattern: Pattern = Pattern.compile("^[a-zA-Z][a-zA-Z0-9_.]*$")
        if(!packageName.equals(StaticConstants.EMPTY_STRING,ignoreCase = true)){
            return pattern.matcher(packageName).matches()
        }
        return false
    }

    /*TextWatcher packageTextWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // TODO Auto-generated method
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if(TextUtils.isEmpty(charSequence)){
                mApplicationPolicy.setPackageName(StaticConstants.EMPTY_STRING);
            }
            else{
                mApplicationPolicy.setPackageName(String.valueOf(charSequence));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // TODO Auto-generated method
        }
    };*/

    override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
        val permissionSpinnerList = mContext.resources.getStringArray(R.array.application_setting_permission_spinner)
        val installTypeSpinnerList = mContext.resources.getStringArray(R.array.application_setting_installtype_spinner)
        when (adapterView.id) {
            R.id.spinnerInstallType -> mApplicationPolicy?.installType = installTypeSpinnerList[i]
            R.id.spinnerDefaultPermission -> mApplicationPolicy?.defaultPermissionPolicy = permissionSpinnerList[i]
        }
    }

    override fun onNothingSelected(adapterView: AdapterView<*>) {
        // TODO Auto-generated method
    }

    override fun onCheckedChanged(compoundButton: CompoundButton, b: Boolean) {
        when (compoundButton.id) {

            R.id.switchPersistent -> {
                if (b) {
                    if (mApplicationPolicy != null && mPersistentPreferredActivity == null) {
                        mPersistentPreferredActivity = PersistentPreferredActivity()
                        mPersistentPreferredActivity!!.receiverActivity = mApplicationPolicy!!.packageName
                    }
                } else {
                    mPersistentPreferredActivity = null
                }
                enableCheckBoxes(b)
            }
            R.id.switchDisable ->  mApplicationPolicy?.disabled = b

            R.id.switchLocktaskmode -> mApplicationPolicy?.lockTaskAllowed = b


            R.id.cbActionMain -> if (mPersistentPreferredActivity != null) {
                var actions: MutableList<String>? = mPersistentPreferredActivity!!.actions
                if (b) {
                    if(!((actions?.contains(StaticConstants.INTENT_ACTION_MAIN) ?: false)
                                    || (actions?.add(StaticConstants.INTENT_ACTION_MAIN) ?: false))){
                        actions=ArrayList()
                        actions.add(StaticConstants.INTENT_ACTION_MAIN)
                        mPersistentPreferredActivity!!.actions = actions
                    }
                } else {
                    if(actions?.remove(StaticConstants.INTENT_ACTION_MAIN) ?: false){
                        mPersistentPreferredActivity!!.actions = actions
                    }
                }
            }

            R.id.cbActionView -> if (mPersistentPreferredActivity != null) {
                var actions: MutableList<String>? = mPersistentPreferredActivity!!.actions
                if (b) {
                    if(!((actions?.contains(StaticConstants.INTENT_ACTION_VIEW) ?: false)
                                    || (actions?.add(StaticConstants.INTENT_ACTION_VIEW) ?: false))){
                        actions=ArrayList()
                        actions.add(StaticConstants.INTENT_ACTION_VIEW)
                        mPersistentPreferredActivity!!.actions = actions
                    }
                } else {
                    if(actions?.remove(StaticConstants.INTENT_ACTION_VIEW) ?: false){
                        mPersistentPreferredActivity!!.actions = actions
                    }
                }
            }

            R.id.cbCategoryDefault -> if (mPersistentPreferredActivity != null) {
                var categories: MutableList<String>? = mPersistentPreferredActivity!!.categories
                if (b) {
                    if(!((categories?.contains(StaticConstants.INTENT_CATEGORY_DEFAULT) ?: false)
                                    || (categories?.add(StaticConstants.INTENT_CATEGORY_DEFAULT) ?: false))){
                        categories=ArrayList()
                        categories.add(StaticConstants.INTENT_CATEGORY_DEFAULT)
                        mPersistentPreferredActivity!!.categories = categories
                    }
                } else {
                    if(categories?.remove(StaticConstants.INTENT_CATEGORY_DEFAULT) ?: false){
                        mPersistentPreferredActivity!!.categories = categories
                    }
                }
            }


            R.id.cbCategoryHome -> if (mPersistentPreferredActivity != null) {
                var categories: MutableList<String>? = mPersistentPreferredActivity!!.categories
                if (b) {
                    if(!((categories?.contains(StaticConstants.INTENT_CATEGORY_HOME) ?: false)
                                    || (categories?.add(StaticConstants.INTENT_CATEGORY_HOME) ?: false))){
                        categories=ArrayList()
                        categories.add(StaticConstants.INTENT_CATEGORY_HOME)
                        mPersistentPreferredActivity!!.categories = categories
                    }
                } else {
                    if(categories?.remove(StaticConstants.INTENT_CATEGORY_HOME) ?: false){
                        mPersistentPreferredActivity!!.categories = categories
                    }
                }
            }

            R.id.cbCategoryLauncher ->if (mPersistentPreferredActivity != null) {
                var categories: MutableList<String>? = mPersistentPreferredActivity!!.categories
                if (b) {
                    if(!((categories?.contains(StaticConstants.INTENT_CATEGORY_LAUNCHER) ?: false)
                                    || (categories?.add(StaticConstants.INTENT_CATEGORY_LAUNCHER) ?: false))){
                        categories=ArrayList()
                        categories.add(StaticConstants.INTENT_CATEGORY_LAUNCHER)
                        mPersistentPreferredActivity!!.categories = categories
                    }
                } else {
                    if(categories?.remove(StaticConstants.INTENT_CATEGORY_LAUNCHER) ?: false){
                        mPersistentPreferredActivity!!.categories = categories
                    }
                }
            }

        }
    }

    companion object {
        private var sCheckUpdatedPolicy = false
    }
}
