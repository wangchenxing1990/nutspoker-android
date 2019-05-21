package com.htgames.nutspoker.ui.activity.Game

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.htgames.nutspoker.R
import com.htgames.nutspoker.config.GameConfigData
import com.htgames.nutspoker.game.bean.BlindStuctureEntity
import com.htgames.nutspoker.game.mtt.adapter.BlindAdapter
import com.htgames.nutspoker.ui.base.BaseActivity
import com.netease.nim.uikit.constants.GameConstants
import kotlinx.android.synthetic.main.activity_ante_table.*
import android.support.v7.widget.LinearLayoutManager



/**
 * Created by 周智慧 on 2017/8/24.
 */
class AnteTableAC : BaseActivity() {
    lateinit var mAdapter: BlindStructureAdapter
    lateinit var recyclerViewNormal: RecyclerView
    lateinit var adapterNormal: BlindAdapter
    internal var dataNormal = java.util.ArrayList<BlindStuctureEntity>()
    lateinit var recyclerViewQuick: RecyclerView
    lateinit var adapterQuick: BlindAdapter
    internal var dataQuick = java.util.ArrayList<BlindStuctureEntity>()
    var ante_table_type = GameConstants.PINEAPPLE_MTT_ANTE_TABLE_NORMAL
    companion object {
        val KEY_ANTE_TABLE_TYPE = "key_ante_table_type"
        fun start(activity: Activity, type: Int) {
            val intent = Intent(activity, AnteTableAC::class.java)
            intent.putExtra(KEY_ANTE_TABLE_TYPE, type)
            activity.startActivityForResult(intent, GameCreateActivity.REQUEST_CODE_SELECT_PINEAPPLE_ANTE_TABLE)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ante_table_type = intent.getIntExtra(KEY_ANTE_TABLE_TYPE, GameConstants.PINEAPPLE_MTT_ANTE_TABLE_NORMAL)
        setContentView(R.layout.activity_ante_table)
        initHead()
        initViewPager()
    }

    fun initHead() {
        setHeadTitle("底注结构表")
        setHeadRightButton(R.string.finish) {
            val intent = Intent()
            intent.putExtra(KEY_ANTE_TABLE_TYPE, ante_table_type)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    fun initViewPager() {
        //普通
        for (i in GameConfigData.pineapple_mtt_ante.indices) {
            val entity = BlindStuctureEntity()
            entity.level = i + 1
            entity.ante = GameConfigData.pineapple_mtt_ante[i]
            dataNormal.add(entity)
        }
        recyclerViewNormal = RecyclerView(this)
        recyclerViewNormal.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapterNormal = BlindAdapter(this, dataNormal)
        adapterNormal.showBlind = false
        recyclerViewNormal.adapter = adapterNormal
        //快速
        for (i in GameConfigData.pineapple_mtt_ante_quick.indices) {
            val entity = BlindStuctureEntity()
            entity.level = i + 1
            entity.ante = GameConfigData.pineapple_mtt_ante_quick[i]
            dataQuick.add(entity)
        }
        recyclerViewQuick = RecyclerView(this)
        recyclerViewQuick.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapterQuick = BlindAdapter(this, dataQuick)
        adapterQuick.showBlind = false
        recyclerViewQuick.adapter = adapterQuick
        //viewpager
        mAdapter = BlindStructureAdapter(this)
        mAdapter.titleList = arrayListOf("底注表(普通)", "底注表(快速)")
        view_pager.adapter = mAdapter
        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }
            override fun onPageSelected(position: Int) {
                ante_table_type = position
            }
        })
        ante_table_tabs.setViewPager(view_pager)
        view_pager.currentItem = ante_table_type
    }

    inner class BlindStructureAdapter(activity: Activity) : PagerAdapter() {
        var titleList: List<String> = ArrayList()
        override fun getCount(): Int {
            return titleList.size
        }

        override fun getPageTitle(position: Int): CharSequence {
            return if (position < titleList.size) titleList.get(position) else "nil"
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            if (position == 0) {
                container.addView(recyclerViewNormal)
            } else if (position == 1) {
                container.addView(recyclerViewQuick)
            }
            return if (position == 0) recyclerViewNormal else recyclerViewQuick
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }
    }
}