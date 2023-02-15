package com.soft.zb.accidentwarning.bean

import org.litepal.crud.DataSupport

data class UserBean(var name: String, var phoneNum: String) : DataSupport()
