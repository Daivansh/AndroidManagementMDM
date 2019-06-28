package com.daivansh.androidmanagementmdm.ui.activity

import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.daivansh.androidmanagementmdm.MyApplication
import com.daivansh.androidmanagementmdm.jobs.GetPolicyJob
import com.daivansh.androidmanagementmdm.jobs.UpdatePolicyJob
import com.daivansh.androidmanagementmdm.ui.adapter.PolicySettingsAdapter
import com.daivansh.androidmanagementmdm.utils.MyManagementAgent
import com.daivansh.androidmanagementmdm.utils.ProgressDialogHelper
import com.birbit.android.jobqueue.JobManager
import com.birbit.android.jobqueue.config.Configuration
import com.daivansh.androidmanagementmdm.R
import com.google.api.services.androidmanagement.v1.model.Policy
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

class PolicyActivity : AppCompatActivity() {

    private val mContext = this@PolicyActivity
    private var myManagementAgent: MyManagementAgent? = null
    private lateinit var mPolicySettingsAdapter: PolicySettingsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_policy)
        Objects.requireNonNull<ActionBar>(supportActionBar).setTitle(R.string.policy_activity_title)
        EventBus.getDefault().register(mContext)
        myManagementAgent = (application as MyApplication).myManagementAgent
        setupPolicySettingsList()
        //        findViewById(R.id.btn_set_policy).setOnClickListener(this);
    }

    override fun onResume() {
        super.onResume()
        getCurrentPolicy()
        //        getPolicyConfigFromPref();
    }

    private fun setupPolicySettingsList() {
        val rvPolicySettings = findViewById<RecyclerView>(R.id.rv_policy_settings)
        mPolicySettingsAdapter = PolicySettingsAdapter(mContext)
        rvPolicySettings.layoutManager = LinearLayoutManager(mContext)
        rvPolicySettings.adapter = mPolicySettingsAdapter
    }

    private fun getCurrentPolicy() {
        ProgressDialogHelper.showProgressDialog(mContext)
        val builder = Configuration.Builder(mContext)
        val manager = JobManager(builder.build())
        manager.addJobInBackground(GetPolicyJob(myManagementAgent))
    }

    private fun updatePolicy() {
        val policy: Policy = mPolicySettingsAdapter.policy
            sCheckUpdatedPolicy = true
            ProgressDialogHelper.showProgressDialog(mContext)
            val builder = Configuration.Builder(mContext)
            val manager = JobManager(builder.build())
            manager.addJobInBackground(UpdatePolicyJob(myManagementAgent, policy))
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
            mPolicySettingsAdapter.policy = policy
            if (sCheckUpdatedPolicy) {
                Toast.makeText(mContext, getString(R.string.policy_updated_successfully), Toast.LENGTH_SHORT).show()
                sCheckUpdatedPolicy = false
            }
            //            savePolicyInPreferences(policy);
        } else if (sCheckUpdatedPolicy) {
            Toast.makeText(mContext, getString(R.string.policy_not_applied), Toast.LENGTH_SHORT).show()
            sCheckUpdatedPolicy = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(mContext)
    }

    companion object {
        private var sCheckUpdatedPolicy = false
    }


    /*   private void savePolicyInPreferences(Policy policy){
        ObjectMapper objectMapper = new ObjectMapper();
        String json=null;
        policy.setVersion(policy.getVersion()+StaticConstants.MAX_INTEGER_VALUE);
        try {
            json = objectMapper.writeValueAsString(policy);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
//        Gson gson = new Gson();
//        String json = gson.toJson(policy,Policy.class);
        if(json!=null) {
            SharedPreferenceHelper.setSharedPreferenceString(mContext, StaticConstants.PREF_FILE_POLICY,
                    StaticConstants.POLICY_OBJECT, json);
            updateUI();
        }
    }*/

    /*  private void getPolicyConfigFromPref()  {
        String policyConfigJSON = SharedPreferenceHelper.getSharedPreferenceString(mContext, StaticConstants.PREF_FILE_POLICY,
                StaticConstants.POLICY_OBJECT, StaticConstants.EMPTY_STRING);
        if (policyConfigJSON == null || policyConfigJSON.equalsIgnoreCase(StaticConstants.EMPTY_STRING)) {
            setDefaultPolicyConfig();
        } else {
//            policyConfigJSON=convertVersionToLong(policyConfigJSON);
//            Gson gson = new Gson();
//            policy=gson.fromJson(policyConfigJSON,Policy.class);
            try {
                ObjectMapper mapper=new ObjectMapper();
                policy=mapper.readValue(policyConfigJSON,Policy.class);
                if(policy.getVersion()!=null){
                    policy.setVersion(policy.getVersion()-StaticConstants.MAX_INTEGER_VALUE);
                }

                updateUI();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }*/

    /*  private void setDefaultPolicyConfig() {
        policy.setInstallUnknownSourcesAllowed(false);
        policy.setModifyAccountsDisabled(false);
        policy.setStatusBarDisabled(false);
        policy.setBluetoothConfigDisabled(false);
        updateUI();
//        savePolicyInPreferences(policy);
    }*/

    /*private String convertVersionToLong(String policyConfigJSON) {
        JSONObject jsonObject=null;
        try{
            jsonObject=new JSONObject(policyConfigJSON);
            if(jsonObject.has("version")) {
                long version = jsonObject.getInt("version");
                jsonObject.remove("version");
                jsonObject.put("version", version + 1L);

            }
        }
        catch (JSONException ex){
            ex.printStackTrace();
        }
        return jsonObject.toString();
    }*/

    /*  private void createPolicyAndApply() {
        ProgressDialogHelper.showProgressDialog(mContext);
        Configuration.Builder builder=new Configuration.Builder(mContext);
        JobManager manager=new JobManager(builder.build());
        if(manager!=null)
            manager.addJobInBackground(new UpdatePolicyJob(myManagementAgent,createSingleTaskModePolicy()));
    }*/


    /* private Policy createSingleTaskModePolicy() {
        List<String> categories = new ArrayList<>();
        categories.add(StaticConstants.INTENT_CATEGORY_HOME);
        categories.add(StaticConstants.INTENT_CATEGORY_DEFAULT);
        categories.add(StaticConstants.INTENT_CATEGORY_LAUNCHER);

        List<String> actions= new ArrayList<>();
        actions.add(StaticConstants.INTENT_ACTION_VIEW);
        actions.add(StaticConstants.INTENT_ACTION_MAIN);


        List<ApplicationPolicy> applicationPolicies=new ArrayList<>();
        for(String app : StaticConstants.COSU_APP_PACKAGE_NAMES){
            applicationPolicies.add( new ApplicationPolicy()
                    .setPackageName(app)
                    .setInstallType(StaticConstants.FORCE_INSTALLED)
                    .setDefaultPermissionPolicy("")
                    .setLockTaskAllowed(true));
        }

        return new Policy()
                .setApplications(
                      applicationPolicies)
                .setPersistentPreferredActivities(
                        Collections.singletonList(
                                new PersistentPreferredActivity()
                                        .setReceiverActivity(StaticConstants.COSU_APP_PACKAGE_NAMES[0])
                                        .setActions(actions)
                                        .setCategories(categories)))
                .setCameraDisabled(true)
                .setDefaultPermissionPolicy(StaticConstants.GRANT)
                .setInstallUnknownSourcesAllowed(true)
                .setDebuggingFeaturesAllowed(true)
                .setAppAutoUpdatePolicy(StaticConstants.ALWAYS)
                .setStatusBarDisabled(true);
    }
*/
    /* private Policy createSimplePolicy() {
        return new Policy()
                .setCameraDisabled(true);
    }*/

    /*  @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_set_policy:
                createPolicyAndApply();
                break;

        }
    }*/

}
