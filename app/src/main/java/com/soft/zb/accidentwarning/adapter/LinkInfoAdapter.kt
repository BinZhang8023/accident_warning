package com.soft.zb.accidentwarning.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.soft.zb.accidentwarning.R
import com.soft.zb.accidentwarning.bean.UserBean
import org.litepal.crud.DataSupport

class LinkInfoAdapter(var context: Context, var list: ArrayList<UserBean>) : RecyclerView.Adapter<LinkInfoAdapter.MyHolder>() {

    lateinit var link_name: String
    lateinit var link_phone: String


    class MyHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView = view.findViewById(R.id.name)
        var phoneNum: TextView = view.findViewById(R.id.phoneNum)
        var itemDelete: ImageView = view.findViewById(R.id.link_delete)
        var linkView: View = view
        var link_user_item: LinearLayout = view.findViewById(R.id.link_user_item)
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): LinkInfoAdapter.MyHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_user_item, p0, false)

        val holder = MyHolder(view)
        holder.itemDelete.setOnClickListener(View.OnClickListener { v ->
            link_name = holder.name.text.toString()
            link_phone = holder.phoneNum.text.toString()

            var findAll = DataSupport.findAll(UserBean::class.java) as ArrayList<UserBean>

            if(findAll.size == 0){
                Toast.makeText(v.context, "请添加紧急联系人" , Toast.LENGTH_SHORT).show()

            }else{
                DataSupport.deleteAll(UserBean::class.java, "name=? and phoneNum=?",link_name, link_phone)
                list.remove(UserBean(link_name, link_phone))

                findAll = DataSupport.findAll(UserBean::class.java) as ArrayList<UserBean>
                if (findAll.size == 0) {
                    list.add(UserBean("姓名", "手机号"))
                }

                notifyDataSetChanged()
                Toast.makeText(v.context, "删除成功" , Toast.LENGTH_SHORT).show()
            }

        })


        return MyHolder(view)
    }

    override fun onBindViewHolder(p0: LinkInfoAdapter.MyHolder, p1: Int) {
        p0!!.name.text = list[p1].name
        p0.phoneNum.text = list[p1].phoneNum
    }

    override fun getItemCount(): Int {
        return list.size
    }


}