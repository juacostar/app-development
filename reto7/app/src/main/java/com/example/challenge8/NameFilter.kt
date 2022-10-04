package com.example.challenge8

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView

class NameFilter : AppCompatActivity() {

    private var listView: ListView? = null
    private var mydb: DBHelper? = null
    private var nameCompany: EditText? = null
    private var searchForCategory: Boolean? = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_name_filter)
        mydb = DBHelper(this)

        nameCompany = findViewById(R.id.nameFilter)
        listView = findViewById(R.id.listView1)

        val extras = intent.extras
        if(extras != null){
            searchForCategory = true
        }
    }

    fun searchCompanyByName(view: View){

        if(searchForCategory==true){
            searchCompanyByCategory()
            return
        }
        val arrayListName = mydb!!.getByName(nameCompany!!.text)
        val arrayListNameIds = mydb!!.getByNameIds(nameCompany!!.text)

        val arrayAdapterName: ArrayAdapter<*> =
            ArrayAdapter<Any?>(this,android.R.layout.simple_list_item_1, arrayListName as List<Any?>)

        listView!!.adapter = arrayAdapterName
        listView!!.onItemClickListener = AdapterView.OnItemClickListener { _, _, arg2, _ ->
            val idToSearch = arrayListNameIds[arg2]
            val dataBundle = Bundle()
            dataBundle.putInt("id", idToSearch.toInt())
            val intent = Intent(applicationContext, MainActivity2::class.java)
            intent.putExtras(dataBundle)
            startActivity(intent)
        }
    }

    fun searchCompanyByCategory(){
        val arrayList = mydb!!.getByClassifications(nameCompany!!.text.toString())
        val arrayListIds = mydb!!.getByClassificationsIds(nameCompany!!.text.toString())

        val arrayAdapterName: ArrayAdapter<*> =
            ArrayAdapter<Any?>(this,android.R.layout.simple_list_item_1, arrayList as List<Any?>)

        listView!!.adapter = arrayAdapterName
        listView!!.onItemClickListener = AdapterView.OnItemClickListener { _, _, arg2, _ ->
            val idToSearch = arrayListIds[arg2]
            val dataBundle = Bundle()
            dataBundle.putInt("id", idToSearch.toInt())
            val intent = Intent(applicationContext, MainActivity2::class.java)
            intent.putExtras(dataBundle)
            startActivity(intent)
        }
    }
}