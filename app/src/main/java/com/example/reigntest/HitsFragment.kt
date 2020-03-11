package com.example.reigntest

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_hits.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class HitsFragment : Fragment() {

    lateinit var hitList:List<HitPojo>
    lateinit var adapter: HitsAdapter
    lateinit var mContext: Context

    companion object {
        fun newInstance(): HitsFragment {
            return HitsFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext= this!!.context!!


    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view:View = inflater.inflate(R.layout.fragment_hits, container, false)
        if(checkInternet())searchHits()
        else checkDatabase()

        return view
    }

    private fun checkDatabase() {
        doAsync {
                val hits = checkRecord()
                uiThread {
                    if(hits.isNotEmpty()) {
                        hitList=hits
                        initTemplate(hitList)
                    }else{
                        showErrorDialog()
                    }
                }
        }
    }

    private fun checkRecord(): List<HitPojo> {
        val dbHandler = DBHelper(mContext, null)
        val cursor = dbHandler.getAllHit()
        cursor!!.moveToFirst()
        var returnList:MutableList<HitPojo> = mutableListOf(HitPojo())
        while (cursor.moveToNext()) {
            var pojo:HitPojo = HitPojo()
            pojo.author=((cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_AUTHOR))))
            pojo.url=((cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_URL))))
            pojo.created_at=((cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CREATED))))
            pojo.title=((cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TITLE))))
            returnList.add(pojo)
        }
        cursor.close()
        if(returnList.count()==1) {
            return emptyList()
        }
        else{
            return returnList.subList(1,returnList.count()-1)
        }
    }

    @SuppressLint("ServiceCast")
    fun checkInternet():Boolean{
        val connectivityManager = mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
        return isConnected
    }
    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://hn.algolia.com/api/v1/search_by_date/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun searchHits() {
        doAsync {
            try {
                val call = getRetrofit().create(APIService::class.java).getHits().execute()
                val hits = call.body() as HitsPojo
                uiThread {
                    if(hits.hits.isNotEmpty()) {
                        hitList=hits.hits
                        refreshDatabase(hitList)
                        initTemplate(checkRecord())
                    }else{
                        showErrorDialog()
                    }
                }
            }catch (e:Exception){
                e.printStackTrace()
            }


        }
    }

    private fun refreshDatabase(hitList: List<HitPojo>) {
        val dbHandler = DBHelper(mContext, null)
        dbHandler.deleteAllHit()
        for(hit in hitList){
            dbHandler.addHit(hit)
        }
    }

    private fun initTemplate(hitList: List<HitPojo>) {
        if(hitList.isNotEmpty()) {
            adapter = HitsAdapter(hitList) { hit ->
                val url: String = if (hit.url != null) hit.url else hit.story_url
                if (url == null)
                    Toast.makeText(activity, "URL is not aviable", Toast.LENGTH_LONG).show()
                else {
                    if(checkInternet()) {
                        val fragment: Fragment = WebviewFragment.newInstance(url)
                        activity?.supportFragmentManager
                            ?.beginTransaction()
                            ?.addToBackStack(url)
                            ?.add(R.id.fragment, fragment, url)
                            ?.commit()
                    }else{
                        Toast.makeText(mContext,"You need an Internet conecction",
                            Toast.LENGTH_LONG).show()
                    }
                }
            }
            rvHits.setHasFixedSize(true)
            rvHits.layoutManager = LinearLayoutManager(mContext)
            rvHits.adapter = adapter

            enableSwipeToDelete()

            if(swipeRefreshLayout.isRefreshing)swipeRefreshLayout.isRefreshing = false

            swipeRefreshLayout.setOnRefreshListener {
                if(checkInternet()){
                    searchHits()
                }else{
                    Toast.makeText(mContext,"You need Internet for refresh",
                        Toast.LENGTH_LONG).show()
                    swipeRefreshLayout.isRefreshing = false
                }
            }
        }else{
            Toast.makeText(mContext,"No record aviable",Toast.LENGTH_LONG).show()
        }
    }

    private fun showErrorDialog() {
        Toast.makeText(activity, "Error,try again later...", Toast.LENGTH_LONG).show()
    }

    private fun enableSwipeToDelete() {
        val swipeToDeleteCallback = object : SwipeToDeleteCallback(context!!) {
            override fun onSwiped(@NonNull viewHolder: RecyclerView.ViewHolder, i: Int) {


                val position = viewHolder.adapterPosition
                val item = adapter!!.getData().get(position)

                adapter!!.removeItem(position)

                val dbHandler = DBHelper(mContext, null)
                dbHandler.addDeleteHit(item)

                val snack = Snackbar.make(constraintLayout, "Hit was removed from the list.", Snackbar.LENGTH_LONG)


                snack.setActionTextColor(Color.YELLOW)
                snack.show()

            }
        }

        val itemTouchhelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchhelper.attachToRecyclerView(rvHits)
    }

}