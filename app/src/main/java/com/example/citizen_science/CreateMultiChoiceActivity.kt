package com.example.citizen_science

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Activity allowing the user to create a Multiple-Choice type survey
 */
class CreateMultiChoiceActivity : AppCompatActivity(), MCRecyclerListener  {

    companion object {
        const val FINISH_CODE = 987
        const val NEW_CODE = 789
        const val EDIT_CODE = 879
        const val TAG = "CreateMultiChoiceActivity"
    }

    private lateinit var mAddButton: Button
    private lateinit var mRemoveButton: Button
    private lateinit var mCreateButton: Button

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter : MCQuestionAdapter

    private lateinit var mSurveyTitle : String
    private lateinit var mSurveyDesc : String


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_multi_choice)

        // Initialize Views
        mAddButton = findViewById(R.id.addButton)
        mRemoveButton = findViewById(R.id.removeButton)
        mCreateButton = findViewById(R.id.completeButton)


        // Get basic survey info from intent
        mSurveyTitle = intent.getStringExtra("TITLE")!!
        mSurveyDesc = intent.getStringExtra("DESC")!!

        // Button Listeners
        mAddButton.setOnClickListener {
            val question = MultiChoiceQuestion("","", List<String>(0) { "" })
            val intent = Intent(this,CreateMCQuestionActivity::class.java)
            intent.putExtra("QUESTION",question)
            startActivityForResult(intent,NEW_CODE)
        }

        mRemoveButton.setOnClickListener {
            mAdapter.removeSelected()
        }

        mCreateButton.setOnClickListener {
            if(mRecyclerView.childCount < 1) {
                Toast.makeText(this, "You must have at least one question!", Toast.LENGTH_SHORT).show()
            } else {
                completeCreation()
            }
        }

        // RecyclerView
        mRecyclerView = findViewById(R.id.questionsRecycler)
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter = MCQuestionAdapter(this, this)
        mRecyclerView.adapter = mAdapter
    }

    // Called from within the recycler view when a question is selected (listener pattern)
    override fun edit(item: MCQuestionItem, position: Int) {
        val question = MultiChoiceQuestion(item.title, item.description, item.choices)
        val intent = Intent(this, CreateMCQuestionActivity::class.java)
        intent.putExtra("QUESTION", question)
        intent.putExtra("POSITION", position)
        startActivityForResult(intent, EDIT_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == NEW_CODE && resultCode == Activity.RESULT_OK) {
            Log.i(TAG,"Got new question, adding to view.")
            val newQuestion = data!!.getSerializableExtra("QUESTION")
            mAdapter.add(newQuestion as MultiChoiceQuestion)
        } else if (requestCode == EDIT_CODE && resultCode == Activity.RESULT_OK) {
            val updatedQuestion = data!!.getSerializableExtra("QUESTION") as MultiChoiceQuestion
            val position = data!!.getIntExtra("POSITION", -1)
            mAdapter.clearSelections()
            mAdapter.edit(updatedQuestion, position)
        } else if (requestCode == FINISH_CODE) {
            finish()
        }
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
        // Pull questions in and collate into proper format
        val items = mAdapter.getList()
        val questions = List<List<String>> (items.size) { a ->
            List<String>(items[a].choices.size + 2) {
                when(it) {
                    0 -> items[a].title
                    1 -> items[a].description
                    else -> items[a].choices[it - 2]
                }
            }
        }

        // Create and push survey
        val id = Interpreter.createMultiChoiceSurvey(mSurveyTitle, mSurveyDesc, questions)
        Toast.makeText(this, "Survey created!", Toast.LENGTH_SHORT)
        val intent = Intent(this, CompletedCreationActivity::class.java)
        intent.putExtra("ID", id)
        startActivityForResult(intent, FINISH_CODE)
    }

}