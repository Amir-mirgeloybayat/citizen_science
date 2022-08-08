package com.example.citizen_science

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.*

class ResultListActivity : AppCompatActivity()
{

    private companion object {
        const val TAG = "ResultListActivity"
    }

    private lateinit var mSurveyIDText: TextView
    private lateinit var mCompileButton: Button

    private lateinit var mScrollListView: LinearLayout

    private lateinit var mResultsMap: Map<String,List<Int>>

    private lateinit var surveyID:String

    private lateinit var mSurvey:Interpreter

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result_list)

        //mRecyclerView = findViewById(R.id.resultRecycler)
        mSurveyIDText = findViewById(R.id.surveyIDText)
        mCompileButton = findViewById(R.id.compileResultsButton)

        mScrollListView = findViewById(R.id.resultListLayout)

        surveyID = this.intent.getStringExtra("ID")!!

        mSurveyIDText.text = surveyID

        mResultsMap = this.intent.getSerializableExtra("RESULTS") as Map<String,List<Int>>

        mSurvey = Interpreter(surveyID)

        populateResults(mResultsMap.keys)

//        mScrollListView.setOnItemClickListener{ parent, view, position, id ->
//        val item = mScrollListView.getChildAt(position)
//                val uID = item.findViewById<TextView>(R.id.resultLabel)
//
//                showIndividualResponse(uID.text.toString())
//        }

        mCompileButton.setOnClickListener{
            showCompiledData()
        }

    }

    /**
     * Function to populate the result list
     */
    fun populateResults(keys:Set<String>)
    {
        val inflater = LayoutInflater.from(this)

        for(userID in keys)
        {
            var v = inflater.inflate(R.layout.result_item,null)

            var mLabel:TextView = v.findViewById(R.id.resultLabel)
            mLabel.text = userID

            mLabel.setOnClickListener{
                this.showIndividualResponse(mLabel.text.toString())
            }

            mScrollListView.addView(v,0)
        }

    }

    /**
     * Function that will create a string of results to display based on an individual user's response
     * to the survey. Launches ShowResultActivity.
     */
    fun showIndividualResponse(userID:String)
    {
        val launcher = Intent(this,ShowResultsActivity::class.java)

        launcher.putExtra("RESPONDANT",userID)
        var resultString:String = "User Response:\n\n"

        var responseData = mResultsMap[userID] as List<Int>

        //TODO String generation
        var pointer:Int = 0

        if(surveyID[0] == 'S')
        {
            Log.i("CHECKER","reached spotter "+responseData.size.toString())

            mSurvey.trackables?.get()?.addOnCompleteListener{
                val qdata = it.result.value as List<String>
                //Log.i("CHECKER","reached onCompleteListener")
                while(pointer < responseData.size)
                {
                    //Log.i("CHECKER","Running loop #" + (pointer+1))
                    val trackable = qdata[pointer]
                    val count = responseData[pointer]
                    resultString += "\t$trackable: $count\n"
                   //Log.i("CHECKER", "String is now: $resultString")
                    pointer++
                }
                //Log.i("CHECKER","Loop exited, result string is now: $resultString")
                launcher.putExtra("RESULTS",resultString)
                startActivity(launcher)
            }
        }
        else
        {
            Log.i(TAG,"reached multi "+responseData.size.toString())

            mSurvey.questions?.get()?.addOnCompleteListener{
                val qdata = it.result.value as List<List<String>>

                Log.i(TAG,responseData.size.toString())

                while(pointer < responseData.size) {
                    val question = qdata[pointer][0]
                    val option = qdata[pointer][responseData[pointer]+2]
                    resultString += "\t${pointer+1}) $question: $option\n"
                    pointer++
                }
                //Log.i(TAG,"Loop exited, result string is now: $resultString")
                launcher.putExtra("RESULTS",resultString)
                startActivity(launcher)
            }
        }


    }

    /**
     * Shows a compiled summary data for all responses
     */
    fun showCompiledData()
    {
        val launcher = Intent(this,ShowResultsActivity::class.java)
        launcher.putExtra("RESPONDANT","Compiled Summary")
        var resultString:String = ""

        //TODO String generation

        if(mResultsMap.isEmpty())
        {
            resultString = "No responses to compile."
        }
        else
        {
            if(surveyID[0] == 'S')
            {
                resultString+= "Total Counts For:\n"
                val responseData = Interpreter.collateSpotterResponses(mResultsMap)
                var pointer:Int = 0
                mSurvey.trackables?.get()?.addOnCompleteListener{
                    val qdata = it.result.value as List<String>
                    //Log.i("CHECKER","reached onCompleteListener")
                    while(pointer < responseData!!.size)
                    {
                        //Log.i("CHECKER","Running loop #" + (pointer+1))
                        val trackable = qdata[pointer]
                        val count = responseData!![pointer]
                        resultString += "\t$trackable: $count\n"
                        //Log.i("CHECKER", "String is now: $resultString")
                        pointer++
                    }
                    //Log.i("CHECKER","Loop exited, result string is now: $resultString")
                    launcher.putExtra("RESULTS",resultString)
                    startActivity(launcher)
                }
            }
            else {
                // we want to be this way:
                /*
                    Question1:
                        choice 1: count
                        choice 2: count
                        ...
                    Question2:
                        choice 1: count
                        choice 2: count
                        ...
                * */
                mSurvey.questions!!.get().addOnCompleteListener {
                    val eachQeuestionsEachChoiceCount = Interpreter.collateMultiResponses(mResultsMap)

                    Log.i(TAG, "Interpreter.collateMultiResponses(mResultsMap)")
                    Log.i(TAG, eachQeuestionsEachChoiceCount.toString())

                    val questions = it.result.value              // List<List<String>>
                    val questionsLength = (questions as List<List<String>>).size

                    for (i in 0 until questionsLength) {
                        val question = questions[i]

                        Log.i(TAG, "question${i}: $question\n")

                        resultString += "${i + 1}) ${question[0]}\n"
                        // resultString += "Description: ${question[1]}\n\n"

                        val sumMap = eachQeuestionsEachChoiceCount!![i]

                        Log.i(TAG, "Inner map")
                        Log.i(TAG, sumMap.toString())

                        for (j in 2 until questions[i].size) {
                            // j's are choices
                            Log.i(TAG, "j = $j AND j-2 == ${j - 2}")
                            resultString += "\t\t${question[j]}: count: ${sumMap.getOrDefault((j-2).toString(), 0)}\n"

                        }
                        resultString += "\n\n"
                    }
                    launcher.putExtra("RESULTS", resultString)
                    startActivity(launcher)
                }
            }

        }
    }
}