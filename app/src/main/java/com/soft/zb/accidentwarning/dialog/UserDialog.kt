package com.soft.zb.accidentwarning.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.soft.zb.accidentwarning.R
import com.soft.zb.accidentwarning.adapter.LinkInfoAdapter
import com.soft.zb.accidentwarning.bean.UserBean
import kotlinx.android.synthetic.main.layout_user_info.*
import org.litepal.crud.DataSupport
import android.util.DisplayMetrics
import kotlinx.android.synthetic.main.layout_user_item.*


class UserDialog(mycontext:Context): Dialog(mycontext, R.style.MyDialog), View.OnClickListener {

    private var mycontext: Context = context

    override fun onClick(p0: View?) {

        when (p0!!.id) {
            R.id.btn_add -> {
//                第一种    gone         表示不可见并且不占用空间
//                第二种    visible      表示可见
//                第三种    invisible    表示不可见但是占用空间
                link_list_view.visibility = View.GONE
                link_add_view.visibility = View.VISIBLE
                tv_user_title.text = "添加联系人"
                et_name.setText("")
                et_phone.setText("")
            }

            R.id.btn_user_confirm -> {
                val name = et_name.text.toString()
                val phone = et_phone.text.toString()

                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone)) {
                    Toast.makeText(context, "输入内容不能为空", Toast.LENGTH_SHORT).show()
                    return
                }

                val userBean = UserBean(name, phone)
                val findAll = DataSupport.where("phoneNum = ?", phone).find(UserBean::class.java) as ArrayList<UserBean>

                if(findAll.size == 0) {
                    userBean.save()
                    Toast.makeText(context, "添加成功", Toast.LENGTH_SHORT).show()
                    resetView()

                }else{
                    Toast.makeText(context, "该联系方式已存在，请重新添加", Toast.LENGTH_SHORT).show()
                    return
                }
            }

            R.id.btn_cancle -> resetView()
        }
    }

    private fun resetView() {
        link_list_view.visibility = View.VISIBLE
        link_add_view.visibility = View.GONE
        tv_user_title.text = "紧急联系人"

        reLoadData()
        adapter.notifyDataSetChanged()
    }

    private var list: ArrayList<UserBean> = ArrayList()
    private lateinit var adapter: LinkInfoAdapter

    private fun reLoadData() {
        list.clear()
        val findAll = DataSupport.findAll(UserBean::class.java) as ArrayList<UserBean>
        //如果没数据，则默认添加admin
        if (findAll.size == 0) {
            list.add(UserBean("姓名", "手机号")) // 并未存入数据库
        } else {
            for (i in findAll) {
                list.add(i)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_user_info)
        setViewSize()
        // 点击空白处取消动画
        setCanceledOnTouchOutside(true)
        initView()
    }

    private fun initView() {
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager

        reLoadData()
        //设置Adapter
        adapter = LinkInfoAdapter(context, list)
        recyclerView.adapter = adapter

        //设置增加或删除条目的动画
        recyclerView.itemAnimator = DefaultItemAnimator()
        //添加Android自带的分割线
        recyclerView.addItemDecoration(DividerItemDecoration(mycontext, DividerItemDecoration.HORIZONTAL))

        btn_add.setOnClickListener(this)
        btn_user_confirm.setOnClickListener(this)
        btn_cancle.setOnClickListener(this)
    }

    private fun setViewSize() {
        val displayMetrics = DisplayMetrics()

        val manager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        manager.defaultDisplay.getMetrics(displayMetrics)   // 获取屏幕宽、高
        val cur_dailog = this.window!!.attributes  // 获取对话框当前的参数值

        cur_dailog.height = (displayMetrics.heightPixels * 0.6).toInt()   // 高度设置为屏幕的0.6
        cur_dailog.width = (displayMetrics.widthPixels * 0.9).toInt()     // 宽度设置为屏幕的0.9
        this.window!!.attributes = cur_dailog     // 设置生效

    }
}
