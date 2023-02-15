package com.soft.zb.accidentwarning.activities

import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.Toast
import com.soft.zb.accidentwarning.R
import com.soft.zb.accidentwarning.bean.SensorBean
import com.soft.zb.accidentwarning.utils.FileUtil
import com.soft.zb.accidentwarning.utils.SensorUtil
import kotlinx.android.synthetic.main.activity_show_data.*
import java.lang.StringBuilder

class ShowDataActivity: AppCompatActivity(), View.OnClickListener {

    var list: MutableList<String> = ArrayList<String>()
    lateinit var stringBuilder: StringBuilder
    lateinit var timer:CountDownTimer
    lateinit var toolbar: Toolbar


    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.btn_data_save -> {
                FileUtil.writeSensorDataToFile(tv_sensorData.text.toString(), "MySensorData", "Data${FileUtil.getNowTime()}.xml")
                Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show()
            }

            R.id.btn_data_show -> {
                timer.start()
                Toast.makeText(this, "开始获取数据", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_data)

        // 为map_activity窗口设置活动栏
        toolbar = findViewById(R.id.toolbar) as Toolbar
        toolbar.setTitle(R.string.navigation_map)
        setSupportActionBar(toolbar)

        // 设置返回图标
        val actionBar = supportActionBar
        actionBar!!.setHomeAsUpIndicator(0)
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener(View.OnClickListener {
            onBackPressed()
        })

        btn_data_show.setOnClickListener(this)
        btn_data_save.setOnClickListener(this)

        timer = object : CountDownTimer(10000, 50){ // 20Hz  0.05s
            override fun onFinish() {
                tv_sensorData.text = ""
                timer.start()
            }

            override fun onTick(p0: Long) {
                val data = SensorUtil.getSensorData() as SensorBean
                val showData = "${FileUtil.getNowTime()}\n" +
                        "加速度:x=${data.accArray[0]},y=${data.accArray[1]},z=${data.accArray[2]}\n" +
                        "陀螺仪:x=${data.gyrArray[0]},y=${data.gyrArray[1]},z=${data.gyrArray[2]}\n"

                if(list.size < 100) {
                    list.add(showData)
                }else {
                    list.removeAt(0)
                    list.add(showData)
                }

                stringBuilder = StringBuilder()
                for(item in list) {
                    stringBuilder.append(item)
                }
                tv_sensorData.text = stringBuilder.toString()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }
}