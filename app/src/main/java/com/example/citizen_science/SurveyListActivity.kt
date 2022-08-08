package com.example.citizen_science

import android.app.Instrumentation
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.RecyclerView

class SurveyListActivity : AppCompatActivity() {

    // Views
    private lateinit var textSearch : EditText
    private lateinit var textTitle : TextView
    private lateinit var textDesc : TextView
    private lateinit var buttonStart : Button
    private lateinit var viewList : RecyclerView

    // Data
    private lateinit var searchID : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey_list)

        // Initialize views
        textSearch = findViewById(R.id.editTextSearchbar)
        textTitle = findViewById(R.id.titleTextView)
        textDesc = findViewById(R.id.descriptionTextView)
        buttonStart = findViewById(R.id.buttonStartSurvey)
        buttonStart.setOnClickListener {
            startSurvey()
        }
        viewList = findViewById(R.id.searchResultsRecycler)

        // Get search ID
        val sourceIntent = getIntent()
        if(sourceIntent.getStringExtra("ID") != null) {
            searchID = sourceIntent.getStringExtra("ID")!!
        } else {
            searchID = ""
        }

        // TODO: Populate viewList with surveys matching the search query

    }

    /**
     * Checks which survey is currently selected from the recycler view,
     * then sends intent to begin the survey in question.
     */
    private fun startSurvey() {

        // TODO: Get info from the recycler view on the selected survey

        // TODO: Determine the type of survey and start the corresponding activity

        // TODO: Package the survey ID so that the survey content can be retrieved

        // Placeholder:
        val intent = Intent(this@SurveyListActivity, SpotterSurvey::class.java)
        startActivityForResult(intent, CODE)
    }

    override fun onActivityResult(requestCode : Int, resultCode : Int, data : Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == CODE) {
            finish()
        }
    }

    // Odd design pattern that can be used instead of the deprecated "startForActivity"
    // Avoiding for now but leaving here if necessary later
    val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result : ActivityResult ->
            finish()
    }

    companion object {
        const val CODE = 259
    }
}