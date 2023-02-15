package com.soft.zb.accidentwarning.fragment

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceFragment
import android.preference.SwitchPreference
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.soft.zb.accidentwarning.R

class SettingsFragment : PreferenceFragment(),SharedPreferences.OnSharedPreferenceChangeListener {

    companion object {
        val KEY_NAME = "pre_key_name"
        val KEY_SEX = "pre_key_sex"
        val KEY_AGE = "pre_key_age"
        val KEY_ILLNESS = "pre_key_illness"
        val KEY_VIBRATE = "pre_key_vibrate"
        val KEY_LIGHT = "pre_key_light"
    }

    private var userName: String = "我"
    private var userSex: String = ""
    private var userAge: String = ""
    private var userIllness: String = ""
    private var isVibrate : Boolean = false
    private var isLight : Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences)

        var sp = preferenceScreen.sharedPreferences

        val namePre = findPreference(KEY_NAME)
        namePre.summary = sp.getString(KEY_NAME, "请输入姓名")

        val sexPre = findPreference(KEY_SEX)
        sexPre.summary = sp.getString(KEY_SEX, "请选择性别")

        val agePre = findPreference(KEY_AGE)
        agePre.summary = sp.getString(KEY_AGE, "请输入年龄")

        val illnessPre = findPreference(KEY_ILLNESS)
        illnessPre.summary = sp.getString(KEY_ILLNESS, "请输入既往重大病史")

    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        Log.e("SettingsFragment", "Information have been modify :" + key)

        when (key) {
            KEY_NAME -> {
                val namePre = findPreference(key)
                namePre.summary = sharedPreferences.getString(key, "")
                userName = sharedPreferences.getString(key, "")
            }

            KEY_SEX -> {
                val sexPre = findPreference(key)
                sexPre.summary = sharedPreferences.getString(key, "")
                userSex = sharedPreferences.getString(key, "")
            }

            KEY_AGE -> {
                val agePre = findPreference(key)
                agePre.summary = sharedPreferences.getString(key, "")
                userAge = sharedPreferences.getString(key, "")
            }

            KEY_ILLNESS -> {
                val illnessPre = findPreference(key)
                illnessPre.summary = "病史将在发送求助短信时告知对方"
                userIllness = sharedPreferences.getString(key, "")
            }

            KEY_VIBRATE -> {
                var vibratePre = findPreference(key) as SwitchPreference
                isVibrate = vibratePre.isChecked
//                Log.e( "binbin", "vibrate = " +  isVibrate)
            }

            KEY_LIGHT-> {
                var vibratePre = findPreference(key) as SwitchPreference
                isLight = vibratePre.isChecked
            }

        }

        val editor = context.getSharedPreferences("settingsData", MODE_PRIVATE).edit()
//        val editor = SharedPreferences.getSharedPreferences("settingsData", MODE_PRIVATE).edit()
        editor.putString("name", userName)
        editor.putString("sex", userSex)
        editor.putString("age", userAge)
        editor.putString("illness", userIllness)
        editor.putBoolean("vibrate", isVibrate)
        editor.putBoolean("light", isLight)
        editor.apply()

    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onPause()
    }


}