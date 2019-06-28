package com.daivansh.androidmanagementmdm.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.daivansh.androidmanagementmdm.R
import com.daivansh.androidmanagementmdm.pojos.PolicyConfig
import com.daivansh.androidmanagementmdm.pojos.PolicyConfigItem
import com.daivansh.androidmanagementmdm.utils.StaticConstants
import com.google.api.services.androidmanagement.v1.model.Policy
import com.google.api.services.androidmanagement.v1.model.SystemUpdate
import com.google.api.services.androidmanagement.v1.model.UserFacingMessage
import com.google.gson.Gson
import kotlinx.android.synthetic.main.policy_settings_spinner_item.view.*
import kotlinx.android.synthetic.main.policy_settings_switch_item.view.*
import kotlinx.android.synthetic.main.policy_settings_textinput_item.view.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.reflect.Field

class PolicySettingsAdapter(private val mContext: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mPolicyConfigItemList: List<PolicyConfigItem<*>>? = null
    internal var policy: Policy

    init {
        policy = Policy()
        setupPolicyConfigList()
    }

    private fun setupPolicyConfigList() {
        val raw = mContext.resources.openRawResource(R.raw.mypolicyconfigjson)
        val rd = BufferedReader(InputStreamReader(raw))
        val gson = Gson()
        val config: PolicyConfig? = gson.fromJson(rd, PolicyConfig::class.java)

        mPolicyConfigItemList = config?.policyConfigItem
    }

    fun setPolicy(policy: Policy) {
        this.policy = policy
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v: View
        when (viewType) {
            R.layout.policy_settings_switch_item -> {
                v = LayoutInflater.from(parent.context).inflate(R.layout.policy_settings_switch_item, parent, false)
                return PolicySettingsSwitchViewHolder(v)
            }

            R.layout.policy_settings_spinner_item -> {
                v = LayoutInflater.from(parent.context).inflate(R.layout.policy_settings_spinner_item, parent, false)
                return PolicySettingsSpinnerViewHolder(v)
            }

            R.layout.policy_settings_textinput_item -> {
                v = LayoutInflater.from(parent.context).inflate(R.layout.policy_settings_textinput_item, parent, false)
                return PolicySettingsInputViewHolder(v)
            }
            else -> {
                v = LayoutInflater.from(parent.context).inflate(R.layout.policy_settings_textinput_item, parent, false)
                return PolicySettingsInputViewHolder(v)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {

            R.layout.policy_settings_switch_item -> (holder as PolicySettingsSwitchViewHolder).bindSwitchHolder(position)
            R.layout.policy_settings_spinner_item -> (holder as PolicySettingsSpinnerViewHolder).bindSpinnerHolder(position)
            R.layout.policy_settings_textinput_item -> (holder as PolicySettingsInputViewHolder).bindTextHolder(position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        when (mPolicyConfigItemList?.get(position)?.type ?: 0) {
            PolicyConfigItem.VALUE_TYPE_BOOLEAN -> return R.layout.policy_settings_switch_item

            PolicyConfigItem.VALUE_TYPE_LIBRARY_SPECIFIC, PolicyConfigItem.VALUE_TYPE_LIST -> return R.layout.policy_settings_spinner_item

            PolicyConfigItem.VALUE_TYPE_LONG, PolicyConfigItem.VALUE_TYPE_STRING_MESSAGE -> return R.layout.policy_settings_textinput_item
        }
        return 0
    }

    override fun getItemCount(): Int {
        return mPolicyConfigItemList?.size ?: 0
    }

    inner class PolicySettingsSwitchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), CompoundButton.OnCheckedChangeListener {
     /*   var tvSettingsName: TextView
        var switchSettings: Switch*/

        init {
            this.setIsRecyclable(false)
          /*  tvSettingsName = itemView.findViewById(R.id.tv_settings_name)
            switchSettings = itemView.findViewById(R.id.switch_settings)*/
        }

        fun bindSwitchHolder(position: Int) {
            val policyConfigItem: PolicyConfigItem<*>? = mPolicyConfigItemList?.get(position)
            itemView.tvSwitchSettingsName.text = policyConfigItem?.name ?: StaticConstants.EMPTY_STRING
            itemView.switchSettings.setOnCheckedChangeListener(this)
            val fieldName = policyConfigItem?.fieldName ?: StaticConstants.EMPTY_STRING
            itemView.switchSettings.tag = fieldName
            updateSwitch(fieldName)
        }

        private fun updateSwitch(fieldName: String) {
            try {
                val field: Field? = policy.javaClass.getDeclaredField(fieldName)
                field?.isAccessible = true
                val value: Boolean? = field?.get(policy) as? Boolean?
                itemView.switchSettings.isChecked = value ?: false
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }

        }

        override fun onCheckedChanged(compoundButton: CompoundButton, b: Boolean) {
            val fieldName = compoundButton.tag as String
            try {
                val field: Field? = policy.javaClass.getDeclaredField(fieldName)
                field?.isAccessible = true
                field?.set(policy, b)
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }

    }


    internal inner class PolicySettingsSpinnerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), AdapterView.OnItemSelectedListener {
       /* var tvSettingsName: TextView
        var spinnerSettings: Spinner*/

        init {
            this.setIsRecyclable(false)
          /*  tvSettingsName = itemView.findViewById(R.id.tv_spinner_settings_name)
            spinnerSettings = itemView.findViewById(R.id.spinner_settings)*/
        }

        fun bindSpinnerHolder(position: Int) {
            val policyConfigItem: PolicyConfigItem<*>? = mPolicyConfigItemList?.get(position)

            itemView.tvSpinnerSettingsName.text = policyConfigItem?.name ?: StaticConstants.EMPTY_STRING

            val fieldName = policyConfigItem?.fieldName ?: StaticConstants.EMPTY_STRING
            val spinnerList: List<String> = policyConfigItem?.subObject as? List<String>? ?: emptyList<String>()

            val mySpinnerAdapter: ArrayAdapter<String> = ArrayAdapter(mContext, android.R.layout.simple_spinner_item, spinnerList)
            mySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            itemView.spinnerSettings.adapter = mySpinnerAdapter
            itemView.spinnerSettings.tag = fieldName
            itemView.spinnerSettings.onItemSelectedListener = this
            updateSpinner(fieldName, spinnerList, policyConfigItem?.type ?: 0)
        }

        private fun updateSpinner(fieldName: String, spinnerList: List<*>, type: Int) {
            try {
                val field: Field? = policy.javaClass.getDeclaredField(fieldName)
                field?.isAccessible = true
                if (type == PolicyConfigItem.VALUE_TYPE_LIBRARY_SPECIFIC) {
                    if (fieldName.equals(StaticConstants.LS_FIELD_SYSTEM_UPDATE, ignoreCase = true)) {
                        val systemUpdate: SystemUpdate?= field?.get(policy) as? SystemUpdate?
                            val systemUpdateType: String = systemUpdate?.type ?: StaticConstants.EMPTY_STRING
                            for (i in spinnerList.indices) {
                                if ((spinnerList.get(i) as String).equals(systemUpdateType, ignoreCase = true)) {
                                    itemView.spinnerSettings.setSelection(i)
                                    break
                                }
                        }
                    }
                } else {
                    val value: String = field?.get(policy) as? String? ?: StaticConstants.EMPTY_STRING
                        for (i in spinnerList.indices) {
                            if ((spinnerList.get(i) as String).equals(value, ignoreCase = true)) {
                                itemView.spinnerSettings.setSelection(i)
                                break
                            }
                        }
                    }
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
            }

        }

        override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
            val fieldName = adapterView.tag as String
            try {
                    val field: Field? = policy.javaClass.getDeclaredField(fieldName)
                        field?.isAccessible = true
                        val value = adapterView.getItemAtPosition(i) as String
                        if (fieldName.equals(StaticConstants.LS_FIELD_SYSTEM_UPDATE, ignoreCase = true)) {
                            var update: SystemUpdate? = null
                            if (i != 0) {
                                update = SystemUpdate()
                                update.type = value
                            }
                            field?.set(policy, update)
                        } else {
                            field?.set(policy, value)
                        }
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }

        override fun onNothingSelected(adapterView: AdapterView<*>) {
            // TODO Auto-generated method
        }
    }


    internal inner class PolicySettingsInputViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), TextWatcher {
       /* var tvSettingsName: TextView
        var editTextSettings: EditText*/

        init {
            this.setIsRecyclable(false)
            /*tvSettingsName = itemView.findViewById(R.id.tv_text_settings_name)
            editTextSettings = itemView.findViewById(R.id.edit_text_settings)*/

        }

        fun bindTextHolder(position: Int) {
            val policyConfigItem: PolicyConfigItem<*>? = mPolicyConfigItemList?.get(position)
            itemView.tvTextSettingsName.text = policyConfigItem?.name ?: StaticConstants.EMPTY_STRING
            itemView.editTextSettings.addTextChangedListener(this)
            val fieldName = policyConfigItem?.fieldName ?: StaticConstants.EMPTY_STRING
            updateEditText(fieldName, policyConfigItem?.type ?: 0)
        }

        private fun updateEditText(fieldName: String, type: Int) {
            try {
                val field: Field? = policy.javaClass.getDeclaredField(fieldName)
                field?.isAccessible = true
                if (type == PolicyConfigItem.VALUE_TYPE_STRING_MESSAGE) {
                    itemView.editTextSettings.hint = mContext.getString(R.string.edit_text_message)
                    val message: UserFacingMessage? = field?.get(policy) as? UserFacingMessage?
                    itemView.editTextSettings.setText(message?.defaultMessage ?: StaticConstants.EMPTY_STRING)

                } else if (type == PolicyConfigItem.VALUE_TYPE_LONG) {
                    val number: Long? = field?.get(policy) as? Long?
                    itemView.editTextSettings.hint = mContext.getString(R.string.edit_text_number)
                    itemView.editTextSettings.inputType = InputType.TYPE_CLASS_NUMBER
                    itemView.editTextSettings.setText(number?.toString() ?: mContext.getString(R.string.default_long_value))
                }
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }

        }

        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

        }

        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            try {
                val policyConfigItem: PolicyConfigItem<*>? = mPolicyConfigItemList?.get(adapterPosition)
                val fieldName = policyConfigItem?.fieldName ?: StaticConstants.EMPTY_STRING
                val type = policyConfigItem?.type ?: 0
                val field: Field? = policy.javaClass.getDeclaredField(fieldName)
                field?.isAccessible = true
                if (type == PolicyConfigItem.VALUE_TYPE_STRING_MESSAGE) {
                    if (!TextUtils.isEmpty(charSequence)) {
                        val message = UserFacingMessage()
                        message.defaultMessage = charSequence.toString()
                        field?.set(policy, message)
                    } else {
                        field?.set(policy, null)
                    }

                } else if (type == PolicyConfigItem.VALUE_TYPE_LONG) {
                    if (!TextUtils.isEmpty(charSequence)) {
                        val number = java.lang.Long.valueOf(charSequence.toString())
                        field?.set(policy, number)
                    } else {
                        field?.set(policy, null)
                    }

                }
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }

        }

        override fun afterTextChanged(editable: Editable) {

        }
    }

}

