package com.example.aifa

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.preference.PreferenceManager
import com.example.aifa.database.Fund
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

fun notice(title: String, text: String, context: Context) {
    val CHANNEL_ID = "FUND"
    //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    val name = "send"
    val importance = NotificationManager.IMPORTANCE_DEFAULT
    val channel = NotificationChannel(CHANNEL_ID, name, importance)
    val notificationManager =
        context.getSystemService(NotificationManager::class.java)
    notificationManager?.createNotificationChannel(channel)
    //}
    val builder = NotificationCompat
        .Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_background)
        .setContentTitle(title)
        .setContentText(text)
        .setAutoCancel(true)
        .build()
    NotificationManagerCompat.from(context).notify(1024, builder)
}

fun loadFund(context: Context?, id: String): Fund? {
    //var waveUrl: String
    //var valueUrl: String
    //todo:检测url可访问，页面结构变化
    try {
        val doc = Jsoup.connect("http://fund.eastmoney.com/$id.html")
            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:71.0) Gecko/20100101 Firefox/71.0")
            .get()
        val name = doc.select(".funCur-FundName").text()
        val month1 =
            doc.select(".dataItem01>dd:nth-child(3)>span:nth-child(2)").text()
                .split("%")[0].toFloat()
        val month3 =
            doc.select(".dataItem02>dd:nth-child(3)>span:nth-child(2)").text()
                .split("%")[0].toFloat()
        val month6 =
            doc.select(".dataItem03>dd:nth-child(3)>span:nth-child(2)").text()
                .split("%")[0].toFloat()
        val wave = month1 + 2.0 / 3.0 * month3 + 0.5 * month6
        // get NAV
        var page = 1
        val list = mutableListOf<Float>()
        while (page <= 6) {
            val vurl =
                "http://fund.eastmoney.com/f10/F10DataApi.aspx?type=lsjz&code=$id&page=$page&per=30"
            val time = (5..30).random().toLong()
            lateinit var vdoc: Document
            GlobalScope.launch {
                delay(time * 1000)
                vdoc = Jsoup.connect(vurl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:71.0) Gecko/20100101 Firefox/71.0")
                    .get()//?:break，如此一来就要检测list是否为空，则不用catch
                val nav = vdoc.select(".w782 > tbody:nth-child(2) > tr > td:nth-child(2)")
                for (e in nav) {
                    list.add(e.text().toFloat())
                }
            }
            runBlocking { delay((time + 1) * 1000) }
            page += 1
        }
        val slist = list.sorted()
        val past = slist.last() - slist.first()
        val present = slist.last() - list.first()
        val weight = present / past
        return Fund(
            id,
            name,
            month1,
            month3,
            month6,
            list.first(),
            slist.last(),
            slist.first(),
            wave,
            past,
            present,
            weight,1
        )
    } catch (e:Exception) {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = pref.edit()
        editor.putString("loadFund exception",e.message).apply()
        return null
    }

}
/*fun thing(
    urls: Map<String, String>,
    selectors: Map<String, Map<String, String>>,
    delay: IntArray
): MutableMap<String, MutableMap<String, Elements>> {
    lateinit var docs: MutableMap<String, Document>
    lateinit var doc: Document
    urls.forEach { (t, u) ->
        val time = (delay[0]..delay[1]).random().toLong()
        GlobalScope.launch {
            delay(time * 1000)
            doc = Jsoup.connect(u)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:71.0) Gecko/20100101 Firefox/71.0")
                .get()
            docs[t] = doc
        }
        runBlocking { delay((time + 1) * 1000) }
    }
    lateinit var attrs: MutableMap<String, Elements>
    lateinit var pages: MutableMap<String, MutableMap<String, Elements>>
    docs.forEach { (docname, page) ->//多网页
        selectors.forEach { (dn, selectorlist) ->//单网页多选择器
            selectorlist.forEach { (selectorname, selector) ->//单选择器
                val node = page.select(selector)
                attrs[selectorname] = node
            }
            pages[dn] = attrs//一个网页
        }
    }
    return pages
}
*/