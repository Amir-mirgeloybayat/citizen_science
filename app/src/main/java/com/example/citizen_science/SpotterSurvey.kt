package com.example.citizen_science

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class SpotterSurvey : AppCompatActivity() {

    private companion object {
        const val TAG = "SpotterSurvey"
        const val SPOTTER_TYPE = "SP"   //use this for surveyID generator
    }


    private lateinit var mSpinner : Spinner //Spinner that contains the various items being counted by the survey
    private lateinit var mCounterText : TextView //Text that shows the count of the currently selected item (Defaulted to 0000 in the layout)
    private lateinit var mIncreaseButton : Button //Button that increases count by 1
    private lateinit var mDecreaseButton : Button //Button that decreases count by 1
    private lateinit var mSubmitButton : Button // Button that ends and submits survey
    private lateinit var mTitleText : TextView

    private lateinit var mItemCounts: MutableList<Int>
    private lateinit var mSurveyID : String
    private lateinit var mInterpreter : Interpreter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spotter_survey)

        // Initialize view variables
        mSpinner = findViewById(R.id.itemList)
        mTitleText = findViewById(R.id.surveyTitle)
        mCounterText = findViewById(R.id.spotCount)
        mIncreaseButton = findViewById(R.id.plusButton)
        mDecreaseButton = findViewById(R.id.minusButton)
        mSubmitButton = findViewById(R.id.submitButton)

        // Set button behaviors
        mIncreaseButton.setOnClickListener {
            updateCount(1)
        }
        mDecreaseButton.setOnClickListener {
            updateCount(-1)
        }
        mSubmitButton.setOnClickListener {
            endSurvey()
        }

        // Get survey ID from intent
        mSurveyID = getIntent().getStringExtra("ID")!!
        mInterpreter = Interpreter(mSurveyID)

        // Set title
        mInterpreter.title.get().addOnCompleteListener() {
            mTitleText.text = it.result.value as String
        }

        // Populate trackables
        mInterpreter.trackables!!.get().addOnCompleteListener() {
            populateLists(it.result.value as List<String>)
        }

        // Set up onItemSelected listener for the spinner to update the count whenever a new item is selected
        mSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                updateCount(0);
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }

    /**
     * Populates the dropdown menu with the items tracked by the survey and initializes the count
     * of each item to zero within a hash. Called once when the survey is started.
     */
    private fun populateLists(itemList : List<String>)
    {
        val adapter = ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, itemList)
        mSpinner.adapter = adapter
        mItemCounts = MutableList<Int>(itemList.size) { 0 }
    }

    /**
     * Adjusts the count of the currently selected item and updates the center text.
     * Pass with a parameter of zero to refresh the count of the currently selected item.
     */
    private fun updateCount(adjustment : Int)
    {
        if(mSpinner.selectedItem != null) {
            val index = mSpinner.getSelectedItemPosition()
            mItemCounts[index] += adjustment
            if(mItemCounts[index]!! < 0)
            {
                mItemCounts[index] = 0
            }
            mCounterText.text = mItemCounts[index].toString()
        }
    }

    /**
     * Function to save and submit data gathered within the survey
     */
    private fun endSurvey()
    {
        // Submit counts as List<Int>
        mInterpreter.addResponse(mItemCounts)

        // Return to search page
        Toast.makeText(this,"Survey submitted!",Toast.LENGTH_SHORT).show()
        finish()
    }

}

