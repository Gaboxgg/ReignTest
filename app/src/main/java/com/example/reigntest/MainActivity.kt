package com.example.reigntest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity(){
    lateinit var fragment: Fragment
    lateinit var name: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        selectFragment("")
    }

    fun selectFragment(url:String){

        if(url.isEmpty()) {
            fragment = HitsFragment.newInstance()
            name = "hitList"
        }else {
            fragment = WebviewFragment.newInstance(url)
            name = url
        }
        supportFragmentManager
            .beginTransaction()
            .addToBackStack(name)
            .add(R.id.fragment, fragment, name)
            .commit()
    }
}
