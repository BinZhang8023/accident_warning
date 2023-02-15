package com.soft.zb.accidentwarning.activities

import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.soft.zb.accidentwarning.R
import com.soft.zb.accidentwarning.bean.UserBean
import com.soft.zb.accidentwarning.service.LocalService
import com.soft.zb.accidentwarning.utils.SmsUtil
import com.soft.zb.accidentwarning.utils.SoundUtil
import kotlinx.android.synthetic.main.activity_warn.*
import org.litepal.crud.DataSupport
import android.widget.Button
import com.soft.zb.accidentwarning.utils.FlashlightUtil
import com.soft.zb.accidentwarning.utils.VibrateUtil


class WarnActivity: AppCompatActivity(){
    lateinit var timer: CountDownTimer
    lateinit var soundUtil: SoundUtil
    lateinit var vibrateUtil: VibrateUtil
    lateinit var flashlightUtil: FlashlightUtil

    var isVibrate: Boolean = false
    var isLight: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_warn)

        val show_time = findViewById(R.id.show_time) as TextView
        val btn_over = findViewById(R.id.btn_close) as Button

        soundUtil = SoundUtil(this)
        vibrateUtil = VibrateUtil(this)
        flashlightUtil = FlashlightUtil()

        val pref = getSharedPreferences("settingsData", MODE_PRIVATE)

        var userName = pref.getString("name", "我")
        if(userName != "我"){
            userName = "姓名：${userName}，"
        }

        var userSex = pref.getString("sex", "")
        if (userSex != ""){
            userSex = "性别：${userSex}，"
        }

        var userAge = pref.getString("age", "")
        if (userAge != ""){
            userAge = "年龄：${userAge}，"
        }

        var userIllness = pref.getString("illness", "")
        if (userIllness != ""){
            userIllness = "病史：${userIllness}，"
        }

        var userInfo = userName + userSex + userAge + userIllness

        isVibrate = pref.getBoolean("vibrate", false)
        isLight = pref.getBoolean("light", false)

        if(!flashlightUtil.hasFlashlight(this)){
            isLight = false
        }


        timer = object : CountDownTimer(10000, 1000) {
            override fun onFinish() {
                Log.e("Warn", "发送短信")

                val sendMsg = SmsUtil()
                val list = DataSupport.findAll(UserBean::class.java)
                for (userBean in list) {
                    sendMsg.sendMsgToPeople(
                        userBean.phoneNum,
                        "【事故预防】自动短信求助：${userInfo}在(${LocalService.getLocalData().longitude}, ${LocalService.getLocalData().latitude})" +
                                "（地址：${LocalService.getLocalData().location}）可能发生意外，急需救助"
                    )
                }

//                soundUtil.stop()      //发送短信关闭继续报警
                if(list.isEmpty()){
                    Toast.makeText(this@WarnActivity, "未发送短信", Toast.LENGTH_SHORT).show()
                    show_time.text = "未添加联系人"
                }else {
                    Toast.makeText(this@WarnActivity, "短信发送成功", Toast.LENGTH_SHORT).show()
                    show_time.text = "短信发送成功"
                }

                btn_over.text = "返回"
            }

            override fun onTick(p0: Long) {
                var t = p0/1000 + 1
                show_time.text = t.toString() + "秒后自动发送短信报警求助"
            }
        }

        timer.start()

        soundUtil.playSound()

        if(isVibrate){
            vibrateUtil.startVibrate()
        }

        if(isLight){
            flashlightUtil.sosOn(this)
        }


        btn_close.setOnClickListener {
            timer.cancel()
            soundUtil.stopSound()
            if(isVibrate){
                vibrateUtil.stopVibrate()
            }
            if(isLight){
                flashlightUtil.sosOff()
            }

            finish()
        }
    }


    override fun onBackPressed() {
        timer.cancel()
        soundUtil.stopSound()
        if(isVibrate){
            vibrateUtil.stopVibrate()
        }
        if(isLight){
            flashlightUtil.sosOff()
        }

        super.onBackPressed()
    }

}
