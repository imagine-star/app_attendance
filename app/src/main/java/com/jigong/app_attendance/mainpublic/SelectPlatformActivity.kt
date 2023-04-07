package com.jigong.app_attendance.mainpublic

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jigong.app_attendance.databinding.ActivitySelectPlatformBinding
import com.jigong.app_attendance.databinding.SelectPlatformItemBinding
import com.jigong.app_attendance.info.User

class SelectPlatformActivity : BaseActivity() {

    private lateinit var binding: ActivitySelectPlatformBinding

    private val platforemList: MutableList<Pair<String, String>> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectPlatformBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        platforemList.add(Pair("佛山", "283"))
//        platforemList.add(Pair("湖南", "300"))
//        platforemList.add(Pair("陇明公", "301"))

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = RecyclerView.VERTICAL
        binding.recyclerView.layoutManager = layoutManager
        val adapter = SelectPlatformAdapter(platforemList, this)
        binding.recyclerView.adapter = adapter

//        MyApplication.getApplication().daoSession.attendanceInfoDao.deleteAll()
//        val attendanceList = MyApplication.getApplication().daoSession.attendanceInfoDao.queryBuilder().list()
//        User.getInstance().rowId = "0"
//        User.getInstance().signDate = ""

        if (User.getInstance().login) {
            startActivity(Intent(this, InfoManageActivity::class.java))
            finish()
        }

//        startService(Intent(this, FoShanService::class.java))

    }

    private class SelectPlatformAdapter(val dataList: MutableList<Pair<String, String>>, val context: Context) : RecyclerView.Adapter<SelectPlatformAdapter.MyHolder>() {

        private class MyHolder(itemView: SelectPlatformItemBinding) : RecyclerView.ViewHolder(itemView.root) {
            var itemBinding: SelectPlatformItemBinding

            init {
                itemBinding = itemView
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
            val binding = SelectPlatformItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return MyHolder(binding)
        }

        override fun onBindViewHolder(holder: MyHolder, position: Int) {
            holder.itemBinding.platformName.text = dataList[position].first
            holder.itemBinding.platformName.setOnClickListener {
                User.getInstance().joinCity = dataList[position].second
                context.startActivity(Intent(context, MainActivity::class.java))
                (context as SelectPlatformActivity).finish()
            }
        }

        override fun getItemCount(): Int {
            return if (dataList.isEmpty()) 0 else dataList.size
        }

    }

}