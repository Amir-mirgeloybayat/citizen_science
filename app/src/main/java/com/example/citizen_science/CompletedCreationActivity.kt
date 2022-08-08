package com.example.citizen_science

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*

class CompletedCreationActivity : AppCompatActivity() {

    private lateinit var mDoneButton:Button //the button that will return the user back to the main search page
    private lateinit var mIDText:TextView //the field for the ID of the newly generated survey

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_completed_creation)

        mIDText = findViewById(R.id.surveyIDText)
        mDoneButton = findViewById(R.id.doneButton)

        mIDText.text = "ID: " + intent.getStringExtra("ID")

        // Button listener
        mDoneButton.setOnClickListener{
            finish()
        }
    }

}