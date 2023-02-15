package com.soft.zb.accidentwarning.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.FragmentTransaction
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.media.Image
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.PermissionChecker
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebView
import android.widget.ImageView
import android.widget.Toast
import com.soft.zb.accidentwarning.fragment.AboutFragment
import com.soft.zb.accidentwarning.fragment.HomeFragment
import com.soft.zb.accidentwarning.R
import com.soft.zb.accidentwarning.dialog.UserDialog
import com.soft.zb.accidentwarning.fragment.SettingsFragment
import com.soft.zb.accidentwarning.service.LocalService
import com.soft.zb.accidentwarning.service.RemoteService
import com.soft.zb.accidentwarning.utils.ImageUtil
import com.soft.zb.accidentwarning.utils.MiuiUtil
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    // 第一次运行获取权限清单
    private val START_PERMISSIONS = arrayOf(
        Manifest.permission.SEND_SMS,
        Manifest.permission.READ_SMS,
        Manifest.permission.RECEIVE_SMS,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    val REQUEST_CODE = 1

    lateinit var mainImage: ImageView
    lateinit var imageUtil: ImageUtil
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView
    lateinit var toolbar: Toolbar
    private var homeFragment: HomeFragment? = null
    private var settingsFragment: SettingsFragment? = null
    private var aboutFragment: AboutFragment? = null

    private val HOME = 1
    private val SETTINGS = 2
    private val ABOUT = 3
    private val DATA = 4

    override fun onCreate(savedInstanceState: Bundle?) {

        requestPermission(this)  // 第一次运行获取权限

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 调整图片自适应手机屏幕
//        mainImage = findViewById(R.id.main_bacground)
//        imageUtil = ImageUtil();
//        imageUtil.changeView(this, mainImage)


        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        toolbar = findViewById(R.id.toolbar)

        // 为activity窗口设置活动栏
        setSupportActionBar(toolbar)

        //设置导航图标
        val actionBar = supportActionBar
        actionBar!!.setHomeAsUpIndicator(R.drawable.ic_menu)
        actionBar.setDisplayHomeAsUpEnabled(true)

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)

        if (navigationView != null) {
            setDrawerContent(navigationView)
        }

        startService(Intent(this, LocalService::class.java))
        startService(Intent(this, RemoteService::class.java))

        changeFragment(HOME)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
            return true
        }

        if (item!!.getItemId() == R.id.id_menu_map) {
            goToMap()
        }

        if (item!!.getItemId() == R.id.id_menu_phone) {
            goToPhone()
        }

        return super.onOptionsItemSelected(item)
    }

    fun setDrawerContent(navigationView: NavigationView){
        //监听navigationView的项目选择
        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_item_home -> {
                    changeFragment(HOME)
                    toolbar.setTitle(R.string.app_name)
                }

                R.id.navigation_item_map -> goToMap()

                R.id.navigation_item_settings -> {
                    changeFragment(SETTINGS)
                    toolbar.setTitle(R.string.navigation_settings)
                }

                R.id.navigation_item_about -> {
                    changeFragment(ABOUT)
                    toolbar.setTitle(R.string.navigation_about)
                }

                R.id.get_data -> {
                    val intent = Intent(this,ShowDataActivity::class.java)
                    startActivity(intent)
                }
            }

            item.isChecked = true

            drawerLayout.closeDrawers()
            true
        }
    }


    fun goToMap() {
        toolbar.setTitle(R.string.app_name)
        val intent = Intent(this, MapActivity::class.java)
        startActivity(intent)
    }

    fun goToPhone(){
        val userDialog = UserDialog(this)
        userDialog.show()
    }


    fun changeFragment(index: Int) {
        val fm = fragmentManager.beginTransaction()
        // 先隐藏所有fragment，再显示一个fragment，防止重叠
        hideFragment(fm)

        when (index) {

            HOME -> if (homeFragment != null) {
                fm.show(homeFragment)
            } else {
                homeFragment = HomeFragment()
                fm.add(R.id.frame_content, homeFragment)
            }

            SETTINGS -> if (settingsFragment != null) {
                fm.show(settingsFragment)
            } else {
                settingsFragment = SettingsFragment()
                fm.add(R.id.frame_content, settingsFragment)
            }

            ABOUT -> if (aboutFragment != null) {
                fm.show(aboutFragment)
            } else {
                aboutFragment = AboutFragment()
                fm.add(R.id.frame_content, aboutFragment)
            }
        }
        fm.commit()
    }


    //当fragment已被实例化，就隐藏起来
    fun hideFragment(ft: FragmentTransaction) {
        if (homeFragment != null) {
            ft.hide(homeFragment)
        }

        if (settingsFragment != null) {
            ft.hide(settingsFragment)
        }

        if (aboutFragment != null) {
            ft.hide(aboutFragment)
        }
    }

    override fun onBackPressed() {
        if(!homeFragment!!.isVisible) {
            changeFragment(HOME)
            toolbar.setTitle(R.string.app_name)

        }else{
            super.onBackPressed()
        }
    }



    var miuiUtil : MiuiUtil =  MiuiUtil()

    /**
     * 请求权限
     *
     * @param permissions 请求的权限
     * @param requestCode 请求权限的请求码
     */
    fun requestPermission(activity: MainActivity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {// Android 6.0 以下不需要动态获取权限
            Log.e("Permission", "获取权限成功（requestPermission）")
            return

        } else {
            if(miuiUtil.isMIUI){
                Log.e("Xiaomi", "小米获取短信权限")
                if (PermissionChecker.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    showDialog(this)
                }
            }

            val needPermissions = getPermissions()
            var to_array = arrayOfNulls<String>(needPermissions.size)
            if(!needPermissions.isEmpty()) {
                ActivityCompat.requestPermissions(activity, needPermissions.toArray(to_array), REQUEST_CODE)
            }

        }
    }


    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions
     * @return
     */
    private fun getPermissions(): ArrayList<String> {
        var needRequestPermissionList: ArrayList<String> = ArrayList()

        for (permission in START_PERMISSIONS) {
            if (PermissionChecker.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
                ) {

                if(miuiUtil.isMIUI && permission == Manifest.permission.SEND_SMS){
                    continue
                }
                needRequestPermissionList.add(permission)
            }
        }
        return needRequestPermissionList
    }


    /**
     * 系统请求权限回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (verifyPermissions(grantResults)) {
                Toast.makeText(this, "You agreed the permission", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "必须同意本权限才能正常使用本程序", Toast.LENGTH_SHORT).show()
            }
        }
    }



    /**
     * 确认所有的权限是否都已授权
     *
     * @param grantResults
     * @return
     */
    private fun verifyPermissions(grantResults: IntArray): Boolean {
        for (grantResult in grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }


    /**
     * 显示对话框，提示用户允许权限
     *
     * @param name
     */
    fun showDialog(context: Activity) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("短信权限申请提示")
            builder.setMessage("当前缺少发送短信权限，\n是否跳转至设置打开权限？")

//            builder.setNegativeButton("取消", null)
        builder.setNegativeButton("取消", object : DialogInterface.OnClickListener{
            override fun onClick(p0: DialogInterface?, p1: Int) {
                Toast.makeText(context, "必须同意本权限才能正常使用本程序", Toast.LENGTH_SHORT).show()
            }
        })

        builder.setPositiveButton("确定", object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    miuiUtil.goPermissionSettings(context)
                }
            })
            builder.create().show()
        }

}
