package com.example.citizen_science

import android.content.Intent
import android.icu.text.CaseMap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Activity for the creation of a new Spotter-type survey
 */
class CreateSpotterActivity : AppCompatActivity() {

    private lateinit var mCreateButton:Button
    private lateinit var mAddItemButton:Button //button that add
    private lateinit var mDeleteButton:Button
    private lateinit var mItemEditText:EditText

    private lateinit var mRecyclerView:RecyclerView
    private lateinit var mAdapter : SelectableItemAdapter

    private lateinit var mSurveyTitle : String
    private lateinit var mSurveyDesc : String

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_spotter)

        // Initialize Views
        mCreateButton = findViewById(R.id.button4)
        mAddItemButton = findViewById(R.id.addButton)
        mDeleteButton = findViewById(R.id.deleteButton)
        mItemEditText = findViewById(R.id.editTextOption)

        // Get basic survey info from previous activity
        mSurveyTitle = intent.getStringExtra("TITLE")!!
        mSurveyDesc = intent.getStringExtra("DESC")!!

        // Button listeners
        mCreateButton.setOnClickListener {
            if(mRecyclerView.childCount < 1) {
                Toast.makeText(this, "You must add at least one trackable item!", Toast.LENGTH_SHORT).show()
            } else {
                completeCreation()
            }
        }

        mAddItemButton.setOnClickListener {
           if(mItemEditText.text.length == 0) {
               Toast.makeText(this, "Trackable must have a name!", Toast.LENGTH_SHORT).show()
           } else {
               addTrackable()
           }
        }

        mDeleteButton.setOnClickListener {
            mAdapter.removeSelected()
        }

        // RecyclerView Listeners + adapter
        mRecyclerView = findViewById(R.id.spotterCreateRecycler)
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter = SelectableItemAdapter(this)
        mRecyclerView.adapter = mAdapter

    }

    fun addTrackable() {
        mAdapter.add(SelectableItem(mItemEditText.text.toString(), "", false))
        mItemEditText.setText("")
    }

    /**
     * Passes the data from the views into the mSurveyData object
     * Generates unique ID for the new survey
     * Pushes the survey object to the database
     * Put ID as extra on intent
     * Sends user to CompletedCreationActivity using intent
     * Finish
     */
    fun completeCreation()
    {
        // Get trackables from adapter
        val items = mAdapter.getList()
        val trackables = List<String>(items.size) { items[it].title }

        // Create survey
        val id = Interpreter.createSpotterSurvey(mSurveyTitle, mSurveyDesc, trackables)
        Toast.makeText(this, "Survey created!", Toast.LENGTH_SHORT)

        // Go to completion activity
        val intent = Intent(this, CompletedCreationActivity::class.java)
        intent.putExtra("ID", id)
        startActivityForResult(intent, CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == CODE)
            finish()
    }

    companion object {
        const val CODE = 654
    }
}