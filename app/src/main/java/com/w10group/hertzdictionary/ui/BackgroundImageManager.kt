package com.w10group.hertzdictionary.ui

import android.content.Context
import android.content.SharedPreferences
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.w10group.hertzdictionary.util.GetImageURLService
import com.w10group.hertzdictionary.util.NetworkUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.design.snackbar
import java.util.*

/**
 * Created by Administrator on 2018/6/15.
 * 背景图片加载管理类
 */

object BackgroundImageManager {

    private const val KEY_TODAY = "today"
    private const val KEY_URL = "URL"
    private const val DEFAULT_VALUE = "null"
    private const val GET_URL = "http://guolin.tech/api/bing_pic"

    fun show(context: Context, imageView: ImageView) {
        getURLOnLocal(context, imageView)
    }

    private fun getURLOnLocal(context: Context, imageView: ImageView) {
        val sharedPreferences = context.getSharedPreferences("BGImageInfo", Context.MODE_PRIVATE)
        val calendar = Calendar.getInstance()
        val today = "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.DAY_OF_MONTH)}"
        val date = sharedPreferences.getString(KEY_TODAY, DEFAULT_VALUE)
        if (today == date) {
            val url = sharedPreferences.getString(KEY_URL, DEFAULT_VALUE)
            if (url != url) {
                Glide.with(context).load(url).into(imageView)
                return
            }
        }
        getURLOnInternet(context, imageView, today, sharedPreferences)
    }

    private fun getURLOnInternet(context: Context, imageView: ImageView, today: String, sharedPreferences: SharedPreferences) {
        if (!NetworkUtil.checkNetwork(context)) {
            snackbar(imageView, "当前无网络连接")
            return
        }
        NetworkUtil.create<GetImageURLService>()
                .get(GET_URL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy {
                    val url = it.charStream().readText()
                    Glide.with(context).load(url).into(imageView)
                    val edit = sharedPreferences.edit()
                    edit.putString(KEY_TODAY, today)
                    edit.putString(KEY_URL, url)
                    edit.apply()
                }
    }

}