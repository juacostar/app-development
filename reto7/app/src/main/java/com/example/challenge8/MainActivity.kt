package com.example.challenge8

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun passToCreateCompany(view: View){
        val intent = Intent(this, MainActivity2::class.java)
        startActivity(intent)
    }

    fun passToListCompany(view: View){
        val intent = Intent(this, CompanyList::class.java)
        startActivity(intent)
    }

    fun passToFilterByNameCompany(view: View){
        val intent = Intent(this, NameFilter::class.java)
        startActivity(intent)
    }

    fun passToFilterByCategory(view: View){
        val dataBundle = Bundle()
        dataBundle.putBoolean("searchForCategory", true)
        val intent = Intent(this, NameFilter::class.java)
        intent.putExtras(dataBundle)
        startActivity(intent)
    }

}