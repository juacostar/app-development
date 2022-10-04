package com.example.challenge8

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView

class CompanyList : AppCompatActivity() {

    private var listView: ListView? = null
    private var mydb: DBHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_company_list)

        mydb = DBHelper(this)
        val arrayList = mydb!!.getAllCompanies()
        val arrayAdapter: ArrayAdapter<*> =
            ArrayAdapter<Any?>(this,android.R.layout.simple_list_item_1, arrayList as List<Any?>)

        listView = findViewById(R.id.listView1)
        listView!!.adapter = arrayAdapter
        listView!!.onItemClickListener = AdapterView.OnItemClickListener { _, _, arg2, _ ->
            val idToSearch = arg2 + 1
            val dataBundle = Bundle()
            dataBundle.putInt("id", idToSearch)
            val intent = Intent(applicationContext, MainActivity2::class.java)
            intent.putExtras(dataBundle)
            startActivity(intent)
        }

    }
}