package com.example.citizen_science

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.lang.reflect.TypeVariable

/**
 * Activity for creating a new question during the creation of a new Multiple Choice survey
 */
class CreateMCQuestionActivity : AppCompatActivity() {

    private lateinit var mTitleText: EditText
    private lateinit var mDescriptionText: EditText
    private lateinit var mOptionText: EditText

    private lateinit var mAddButton: Button
    private lateinit var mRemoveButton: Button
    private lateinit var mFinishButton: Button

    private lateinit var mRecyclerView: RecyclerView //USE survey_option_item FOR ITEMS INSERTED INTO THIS VIEW
    private lateinit var mAdapter : SelectableItemAdapter

    private lateinit var mQuestionData: MultiChoiceQuestion //Question object to edit and pass back to the creation activity to add to survey
    private var mQuestionPosition: Int = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_mcquestion)

        //Initialize Views
        mTitleText = findViewById(R.id.editTextTitle)
        mDescriptionText = findViewById(R.id.editTextDescription)
        mOptionText = findViewById(R.id.editTextOption)
        mAddButton = findViewById(R.id.addButton)
        mRemoveButton = findViewById(R.id.removeButton)
        mFinishButton = findViewById(R.id.finishButton)

        // RecyclerView Listeners
        mRecyclerView = findViewById(R.id.optionsRecycler)
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter = SelectableItemAdapter(this)
        mRecyclerView.adapter = mAdapter

        // Button Listeners
        mAddButton.setOnClickListener {
            if(mOptionText.text.length == 0) {
                Toast.makeText(this, "You must fill in text for this option!", Toast.LENGTH_SHORT).show()
            } else {
                addOption()
            }
        }

        mRemoveButton.setOnClickListener {
            mAdapter.removeSelected()

        }

        // if the user has at least 2 options added to the question, and the description and title contain text, run finishQuestion()
        // otherwise, inform the user that they must complete these prerequisites
        mFinishButton.setOnClickListener{
            if(mAdapter.getList().size >= 2)
            {
                finishQuestion()
            }
            else
            {
                Toast.makeText(this, "Question must have at least 2 response choices.", Toast.LENGTH_SHORT).show()
            }
        }

        val intent = this.getIntent()
        mQuestionData = intent.getSerializableExtra("QUESTION") as MultiChoiceQuestion
        mQuestionPosition = intent.getIntExtra("POSITION", -1)
        populateFromQuestion()
    }

    /**
     * Function for adding a new option to the RecyclerView using the text fields
     */
    fun addOption()
    {
        mAdapter.add(SelectableItem(mOptionText.text.toString(),"",false))
        mOptionText.setText("")
    }

    /**
     * Function that populates the recyclerview using the question passed to mQuestionData
     */
    fun populateFromQuestion()
    {
        mTitleText.setText(mQuestionData.title)
        mDescriptionText.setText(mQuestionData.description)

        mQuestionData.choices.forEach {
            mAdapter.add(SelectableItem(it, "", false))
        }
    }

    fun packageOptions() : List<String>
    {
        val items = mAdapter.getList()
        return List<String>(items.size) { items[it].title }
    }

    /**
     * Function that takes all input data from the views,
     * packs them into mQuestionData,
     * and passes this object back to the creation activity
     */
    fun finishQuestion()
    {
        if(mTitleText.text.length == 0)
        {
            Toast.makeText(this, "Please fill in a title.", Toast.LENGTH_SHORT).show()
        }
        else
        {
            mQuestionData.title = mTitleText.text.toString()
            mQuestionData.description = mDescriptionText.text.toString()
            mQuestionData.choices = packageOptions()
            val passback = Intent()
            passback.putExtra("QUESTION",mQuestionData)
            passback.putExtra("POSITION", mQuestionPosition)
            setResult(RESULT_OK, passback)
            finish()
        }
    }
}