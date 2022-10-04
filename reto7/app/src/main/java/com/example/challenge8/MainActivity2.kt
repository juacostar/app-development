package com.example.challenge8

import android.app.AlertDialog
import android.content.Intent
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import androidx.core.text.set
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Visibility
import kotlin.collections.ArrayList

class MainActivity2 : AppCompatActivity() {

    private var companyName: EditText? = null
    private var companyUrl: EditText? = null
    private var companyPhone: EditText? = null
    private var companyEmail: EditText? = null
    private var companyProducts: EditText? = null
    private var companyCategory: EditText? = null
    private var clasification: ArrayList<String> = ArrayList()
    private var  recyclerCategories: RecyclerView? = null
    private var linearLayout: LinearLayout? = null
    private var createCompanyButton: Button? = null

    private var mydb: DBHelper? = null
    private var idToUpdate: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        companyName = findViewById<EditText>(R.id.companyName)
        companyUrl = findViewById<EditText>(R.id.companyUrl)
        companyPhone = findViewById<EditText>(R.id.companyPhone)
        companyEmail = findViewById<EditText>(R.id.companyEmail)
        companyProducts = findViewById<EditText>(R.id.companyProducts)
        companyCategory = findViewById<EditText>(R.id.companyCategory)
        createCompanyButton = findViewById(R.id.createCompanyButton)
//        recyclerCategories = findViewById(R.id.recyclerCategories)
        linearLayout = findViewById(R.id.linearLayout)

        clasification.add("Consultoria")
        clasification.add("Desarrollo a medida")
        clasification.add("Fabrica de software")


        mydb = DBHelper(this)
        val extras = intent.extras
        if(extras != null){
            val value = extras.getInt("id")
            idToUpdate = value
            val rs: Cursor? = mydb!!.getData(value)
            rs?.moveToFirst()
            val nam: String? = rs?.getString(rs.getColumnIndexOrThrow(DBHelper.COMPANIES_COLUMN_NAME))
            val url: String? = rs?.getString(rs.getColumnIndexOrThrow(DBHelper.COMPANIES_COLUMN_URL))
            val phon: String? =
                rs?.getString(rs.getColumnIndexOrThrow(DBHelper.COMPANIES_COLUMN_PHONE))
            val emai: String? =
                rs?.getString(rs.getColumnIndexOrThrow(DBHelper.COMPANIES_COLUMN_EMAIL))
            val prod: String? =
                rs?.getString(rs.getColumnIndexOrThrow(DBHelper.COMPANIES_COLUMN_PRODUCTS_SERVICES))
            val clas: String? =
                rs?.getString(rs.getColumnIndexOrThrow(DBHelper.COMPANIES_COLUMN_CLASSIFICATION))

            if (rs != null) {
                if (!rs.isClosed) {
                    rs.close()
                }
            }

            companyName!!.setText(nam)
            companyUrl!!.setText(url)
            companyPhone!!.setText(phon)
            companyEmail!!.setText(emai)
            companyProducts!!.setText(prod)
            companyCategory!!.setText(clas)

            createCompanyButton!!.visibility = GONE
            linearLayout!!.visibility = VISIBLE


        }



    }

    fun setAdapter(){

    }


    fun createCompany(view:View){
        if (mydb?.insertCompany(
                companyName!!.text.toString(), companyUrl!!.text.toString(), companyPhone!!.text.toString(),
                companyEmail!!.text.toString(), companyProducts!!.text.toString(),
                companyCategory!!.text.toString()
            ) == true
        ) {
            Toast.makeText(
                applicationContext, "done",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                applicationContext, "not done",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun UpdateCompany(view:View){
        if (mydb?.updateCompany(
                idToUpdate, companyName!!.text.toString(), companyUrl!!.text.toString(), companyPhone!!.text.toString(),
                companyEmail!!.text.toString(), companyProducts!!.text.toString(),
                companyCategory!!.text.toString()
            ) == true
        ) {
            Toast.makeText(applicationContext, "Updated", Toast.LENGTH_SHORT).show()
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        } else {
            Toast.makeText(applicationContext, "not Updated", Toast.LENGTH_SHORT).show()
        }
    }

    fun deleteCompany(view: View){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage("Eliminar empresa")
            .setPositiveButton("Si") { _, _ ->
                mydb?.deleteCompany(idToUpdate)
                Toast.makeText(
                    applicationContext, "Deleted Successfully",
                    Toast.LENGTH_SHORT
                ).show()
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
            }
            .setNegativeButton("No") { _, _ ->
                // User cancelled the dialog
            }
        val d: AlertDialog = builder.create()
        d.setTitle("Are you sure")
        d.show()
    }

}