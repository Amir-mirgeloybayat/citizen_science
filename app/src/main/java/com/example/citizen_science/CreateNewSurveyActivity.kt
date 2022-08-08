package com.example.citizen_science

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.widget.AppCompatImageButton

/**
 * Activity for the creation of a new survey of an unspecified type
 */
class CreateNewSurveyActivity : AppCompatActivity()
{
    private lateinit var mEditTitle:EditText
    private lateinit var mEditDescription:EditText

    private lateinit var mTypeGroup:RadioGroup

    private lateinit var mCreateButton:Button
    private lateinit var mInfoButton:AppCompatImageButton

    private var mSurveyType:String = "";

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_new_survey)

        mEditTitle = findViewById(R.id.titleEditText)
        mEditDescription = findViewById(R.id.descriptionEditText)

        mCreateButton = findViewById(R.id.button)
        mCreateButton.setOnClickListener {
            if(mEditTitle.text.length < 8) {
                Toast.makeText(this, "Please enter a name of length 8 or greater!", Toast.LENGTH_SHORT).show()
            } else {
                nextStep(mSurveyType)
            }
        }

        mInfoButton = findViewById(R.id.infoButton)
        mInfoButton.setOnClickListener{
            val info = Intent(this,InfoScreenActivity::class.java)
            startActivity(info)
        }

        mTypeGroup = findViewById(R.id.typeGroup)
        mTypeGroup.setOnCheckedChangeListener { _, i ->
            if(i == R.id.spotterRadioButton) {
                mSurveyType = Interpreter.SPOTTER_TYPE
            } else if(i == R.id.multichoiceRadioButton) {
                mSurveyType = Interpreter.MULTIPLE_TYPE
            } else {
                mSurveyType = ""
            }
        }

    }

    /**
     * Launches the appropriate activity for creation of the rest of the survey
     * Takes the survey type (if survey type is invalid, quit and notify user to select a valid type)
     * Builds new survey object of the appropriate type using the title, description, user info, and type
     * Passes new survey object via intent to the appropriate activity
     */
    fun nextStep(type:String)
    {
        if(type == Interpreter.SPOTTER_TYPE) {
            intent = Intent(this@CreateNewSurveyActivity, CreateSpotterActivity::class.java)
            intent.putExtra("TITLE", mEditTitle.text.toString())
            intent.putExtra("DESC", mEditDescription.text.toString())
            startActivityForResult(intent, CODE)
        } else if (type == Interpreter.MULTIPLE_TYPE) {
            intent = Intent(this@CreateNewSurveyActivity, CreateMultiChoiceActivity::class.java)
            intent.putExtra("TITLE", mEditTitle.text.toString())
            intent.putExtra("DESC", mEditDescription.text.toString())
            startActivityForResult(intent, CODE)
        } else {
            Toast.makeText(this, "Please select a survey type!", Toast.LENGTH_SHORT).show()
        }
    }

    // Skip activity when returning
    override fun onActivityResult(requestCode : Int, resultCode : Int, data : Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == CODE) {
            finish()
        }
    }

    companion object {
        const val CODE = 456
    }

}