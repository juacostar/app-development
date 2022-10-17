package com.example.challenge10

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.lifecycle.map
import com.example.challenge10.databinding.ActivityMainBinding
import com.example.challenge10.model.Active
import com.example.challenge10.viewModel.ActiveViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val activeViewModel = ActiveViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)

        activeViewModel.onCreate()
        val names = mutableListOf<String>()
        var arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, names)

        val mActivesList:ListView = findViewById(R.id.activesList)
        activeViewModel.actives.observe(this) {
            println("funciono" + activeViewModel.actives.value?.get(0))

            var ac = 0
            for(active in activeViewModel.actives.value!!){
                ac++
                names.add(active.name)
                if(ac == 30) break
            }
            arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, names)
            mActivesList.adapter = arrayAdapter
        }

         mActivesList.setOnItemClickListener{ parent, view, position, id ->
             val activeSelected = activeViewModel.actives.value?.get(position)
             val intent = Intent(this, MainActivityDetail::class.java).apply {
                 putExtra("period", activeSelected?.period)
                 putExtra("name", activeSelected?.name)
                 putExtra("gender", activeSelected?.gender)
                 putExtra("school", activeSelected?.school)
                 putExtra("program", activeSelected?.program)
                 putExtra("methodology", activeSelected?.methodology)
             }
             startActivity(intent)
         }


//        val users = arrayOf(
//            "Virat Kohli", "Rohit Sharma", "Steve Smith",
//            "Kane Williamson", "Ross Taylor"
//        )

        // access the listView from xml file
//        var mListView = findViewById<ListView>(R.id.activesList)
//        val arrayAdapter2: ArrayAdapter<*>
//        arrayAdapter2 = ArrayAdapter(this,
//            android.R.layout.simple_list_item_1, users)
//        mListView.adapter = arrayAdapter2
    }

}



