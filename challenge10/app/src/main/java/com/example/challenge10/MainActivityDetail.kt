package com.example.challenge10

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivityDetail : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_detail)

        var tvPeriod = findViewById<TextView>(R.id.period)
        var tvName = findViewById<TextView>(R.id.name)
        var tvGender = findViewById<TextView>(R.id.gender)
        var tvSchool = findViewById<TextView>(R.id.school)
        var tvProgram = findViewById<TextView>(R.id.program)
        var tvMethodology = findViewById<TextView>(R.id.methodology)

        val period = intent.getStringExtra("period")
        val name = intent.getStringExtra("name")
        val gender = intent.getStringExtra("gender")
        val school = intent.getStringExtra("school")
        val program = intent.getStringExtra("program")
        val methodology = intent.getStringExtra("methodology")

        tvPeriod.text = period
        tvName.text = name
        tvGender.text = gender
        tvSchool.text = school
        tvProgram.text = program
        tvMethodology.text = methodology
    }
}