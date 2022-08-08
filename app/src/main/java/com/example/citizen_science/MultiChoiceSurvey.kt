package com.example.citizen_science

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.view.isVisible
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MultiChoiceSurvey : AppCompatActivity()
{
    private companion object {
        const val TAG = "MultiChoiceSurvey"
        const val MULTIPLE_QUESTION_TYPE = "MQ" // use this for surveyID generator
    }

    private lateinit var mQuestionTitle : TextView
    private lateinit var mQuestionText : TextView
    private lateinit var mBackButton : Button //when pressed, changeQuestion(false) should be called
    private lateinit var mNextButton : Button //when pressed, changeQuestion(true) should be called
    private lateinit var mAnswerGroup : RadioGroup

    private var mQuestionIndex : Int = -1 //pointer variable to track what question the user is currently viewing in the survey
    private var mNumQuestions : Int = Int.MAX_VALUE
    private var mAnswersMap : HashMap<Int,Int> = HashMap<Int,Int>()  //list to track the option picked for each question

    private lateinit var mSurveyID : String
    private lateinit var mInterpreter : Interpreter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multi_choice_survey)

        // Initialize view variables
        mQuestionTitle = findViewById(R.id.questionTitle)
        mQuestionText = findViewById(R.id.questionText)
        mAnswerGroup = findViewById(R.id.answersGroup)
        mNextButton = findViewById(R.id.buttonNext)
        mBackButton = findViewById(R.id.buttonBack)

        // Set up button callbacks
        mNextButton.setOnClickListener() {
            if(mQuestionIndex < mNumQuestions - 1)
                changeQuestion(true)
            else
                endSurvey()
        }
        mBackButton.setOnClickListener() {
            changeQuestion(false)
        }

        // Set up radio group behavior
        mAnswerGroup.setOnCheckedChangeListener() { _, i ->
            if(i >= 0) {
                val checkIndex = mAnswerGroup.indexOfChild(findViewById(i))
                mAnswersMap.put(mQuestionIndex,checkIndex)
                Log.i(TAG, "Updated answers map: " + mAnswersMap.toString())
            }
        }

        // Get survey ID from intent and set up interpreter
        mSurveyID = getIntent().getStringExtra("ID")!!
        mInterpreter = Interpreter(mSurveyID)

        // Set up first question
        // Index starts as -1 so that we jump to 0 here
        mInterpreter.questions!!.get().addOnCompleteListener() {
            mNumQuestions = (it.result.value as List<List<String>>).size
            changeQuestion(true)
        }

    }

    /**
     *  Populates the layout with the appropriate data for the current question
     */
    fun populateQuestion()
    {
        // Set up question
        mInterpreter.questions!!.get().addOnCompleteListener() {

            // Use weird mapping programming stuff to split things out
            val q = it.result.value as List<List<String>>
            val questions = List<String>(q.size) { a ->
                q[a][0]
            }
            val descriptions = List<String>(q.size) { a ->
                q[a][1]
            }
            val answers = List<List<String>>(q.size) { a ->
                List<String>(q[a].size - 2) { b ->
                    q[a][b + 2]
                }
            }

            // Record number of questions
            mNumQuestions = questions.size

            // Set title (and desc?)
            mQuestionTitle.text = questions[mQuestionIndex]
            mQuestionText.text = descriptions[mQuestionIndex]

            // Reset radio group
            mAnswerGroup.removeAllViews()

            // Add radio buttons for each possible answer of the current question
            answers[mQuestionIndex].forEachIndexed { index, i ->
                val newChoice : RadioButton = RadioButton(this)
                newChoice.text = i
                mAnswerGroup.addView(newChoice)
            }

            // Check if answer is recorded for this question
            if(mAnswersMap.containsKey(mQuestionIndex)) {
                val index = mAnswersMap.get(mQuestionIndex)!!
                Log.i(TAG,"Saved answer " + index + " exists for question " + mQuestionIndex)
                mAnswerGroup.check(mAnswerGroup.getChildAt(index).id)
            }
        }
    }

    /**
     * Changes the information to reflect the current survey question
     * @param forward -> if true, increment question number. if false, decrement
     */
    fun changeQuestion(forward : Boolean)
    {
        if(forward)
        {
            if(mQuestionIndex < mNumQuestions - 1)
            {
                mQuestionIndex++
            }
        }
        else
        {
            if(mQuestionIndex > 0)
            {
                mQuestionIndex--
            }
        }

        populateQuestion()

        //Checks if the user can progress forward/back in the survey and enables/disables the appropriate buttons
        // Have the buttons "grey-out" when they are made unclickable and return to normal color otherwise
        if(mQuestionIndex == mNumQuestions - 1) {
            mNextButton.setText("SUBMIT")
        } else {
            mNextButton.setText("NEXT")
        }

        if(mQuestionIndex == 0) {
            mBackButton.isClickable = false
            mBackButton.visibility = View.INVISIBLE
        } else {
            mBackButton.isClickable = true
            mBackButton.visibility = View.VISIBLE
        }
    }

    /**
     * Function to save and submit data gathered within the survey
     */
    fun endSurvey()
    {
        // Abort if user has not answer every question
        if(mAnswersMap.size < mNumQuestions) {
            Toast.makeText(this,"All questions must be answered before submitting.", Toast.LENGTH_SHORT).show()
            return
        }

        // Submit results to database
        val answersList = List<Int>(mAnswersMap.size) { mAnswersMap.get(it)!! }
        mInterpreter.addResponse(answersList)

        // Return to search page
        Toast.makeText(this,"Survey submitted!",Toast.LENGTH_SHORT).show()
        finish()
    }
}