package com.soft.zb.accidentwarning.activities

import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View

import com.amap.api.maps.AMap
import com.amap.api.maps.AMapOptions
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.MyLocationStyle
import com.amap.api.navi.AmapNaviPage
import com.amap.api.navi.AmapNaviParams
import com.amap.api.navi.INaviInfoCallback
import com.amap.api.navi.model.AMapNaviLocation

import com.soft.zb.accidentwarning.R
import kotlinx.android.synthetic.main.activity_map.*

class MapActivity : AppCompatActivity() {
    lateinit var toolbar: Toolbar
    lateinit var myLocationStyle: MyLocationStyle
    lateinit var aMap: AMap
//    private var longitude: Double = 0.toDouble()    //经度
//    private var latitude:  Double = 0.toDouble()    // 纬度
//    internal var latLng = LatLng(latitude, longitude) // 前纬后经



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

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

        //在activity执行onCreate时执行MapView.onCreate(savedInstanceState)，创建地图
        mapView.onCreate(savedInstanceState)    // 必须重写此方法,实现地图生命周期管理
        aMap = mapView.map  //初始化地图控制器对象

        // 蓝点定位
        myLocationStyle = MyLocationStyle()
        // 定位样式
        // 连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。如果不设置myLocationType，默认也会执行此种模式。
        // 连续定位并移动视角至地图中心，以保证第一次打开软件获得定位权限后能自动移动视角至当前定位位置
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
        myLocationStyle.interval(1000)  // 1s定位1次
        // 定位图标
        aMap.myLocationStyle = myLocationStyle                  //设置定位蓝点的Style
        aMap.uiSettings.isMyLocationButtonEnabled = true        //设置默认定位按钮是否显示，非必需设置
        aMap.isMyLocationEnabled = true                        // 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false
        aMap.uiSettings.logoPosition = AMapOptions.LOGO_POSITION_BOTTOM_CENTER  // 高德logo位置，必须
        aMap.moveCamera(CameraUpdateFactory.zoomTo(16f))        // 设置缩放比例

        var notChange = true // 是否还未切换定位模式为LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER
        val pM = packageManager
        var locPermission = false // 是否拥有定位权限
        aMap.setOnMyLocationChangeListener {
            if(notChange){
                //判断是否开启了定位权限
                locPermission = PackageManager.PERMISSION_GRANTED === pM.checkPermission("android.permission.ACCESS_FINE_LOCATION", "com.soft.zb.accidentwarning")
            }

            if(locPermission && notChange){
                notChange = false
                Handler().postDelayed({ }, 1000)    // 延时1s 以保证开启定位权限后成功将地图视角移至当前定位位置
                //开始定位一次 然后更改定位模式为一直定位 该定位针对定位图标的定位
                myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER)//连续定位
                myLocationStyle.interval(2000)          //设置连续定位模式下的定位间隔，只在连续定位模式下生效（单位为毫秒）
                aMap.myLocationStyle = myLocationStyle
            }
        }


        btn_navi.setOnClickListener {
            //不传入起点、途径点、终点，启动导航组件
            AmapNaviPage.getInstance().showRouteActivity(this, AmapNaviParams(null),  object : INaviInfoCallback {
                /**
                 * 导航播报信息回调函数。
                 * @param text 语音播报文字
                 **/
                override fun onGetNavigationText(p0: String?) {

                }

                /**
                 * 算路成功回调
                 * @param routeIds 路线id数组
                 */
                override fun onCalculateRouteSuccess(p0: IntArray?) {
                }

                /**
                 * 导航初始化失败时的回调函数
                 **/
                override fun onInitNaviFailure() {
                    Log.e("hg导航失败", "shibaile")
                }

                override fun onStrategyChanged(p0: Int) {
                }

                override fun onReCalculateRoute(p0: Int) {
                }

                override fun getCustomNaviView(): View? {
                    return null
                }

                /**
                 * 步行或者驾车路径规划失败后的回调函数
                 **/
                override fun onCalculateRouteFailure(p0: Int) {
                }

                /**
                 * 当GPS位置有更新时的回调函数。
                 *@param location 当前自车坐标位置
                 **/
                override fun onLocationChange(p0: AMapNaviLocation?) {
                }

                override fun getCustomNaviBottomView(): View? {
                    return null
                }

                override fun onArrivedWayPoint(p0: Int) {
                }

                /**
                 * 到达目的地后回调函数。
                 **/
                override fun onArriveDestination(p0: Boolean) {
                }

                /**
                 * 启动导航后的回调函数
                 **/
                override fun onStartNavi(p0: Int) {
                }

                /**
                 * 停止语音回调，收到此回调后用户可以停止播放语音
                 **/
                override fun onStopSpeaking() {
                }

                override fun onExitPage(p0: Int) {
                }

                override fun onMapTypeChanged(p0: Int) {
                }
            })
        }


//        btn_contact.setOnClickListener({
//            val intent = Intent(this, SecondViewActivity::class.java)
//            startActivity(intent)
//        })

//        startService(Intent(this, LocalService::class.java))
//
//        startService(Intent(this, RemoteService::class.java))
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

//    override fun onBackPressed() {
//        val home = Intent(Intent.ACTION_MAIN)
//        home.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//        home.addCategory(Intent.CATEGORY_HOME)
//        startActivity(home)
//    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        mapView.onSaveInstanceState(outState)
    }

}