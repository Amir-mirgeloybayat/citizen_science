package com.example.citizen_science

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SearchActivity : AppCompatActivity()
{

    private companion object {
        const val TAG = "SearchActivity"
    }

    // Views
    private lateinit var buttonSearch: Button
    private lateinit var buttonCreate : Button
    private lateinit var textEditSearchID: EditText
    private lateinit var buttonLogout: Button

    // database reference for querying the ID
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // Set up search field
        textEditSearchID = findViewById(R.id.editTextSearchByID)

        // Set up buttons
        buttonSearch = findViewById(R.id.buttonSearchByID)
        buttonSearch.setOnClickListener {
            startSearch()
        }

        buttonCreate = findViewById(R.id.createSurveyButton)
        buttonCreate.setOnClickListener {
            startActivity(Intent(this,CreateNewSurveyActivity::class.java))
        }

        buttonLogout = findViewById(R.id.logOutButton)
        buttonLogout.setOnClickListener{
            Firebase.auth.signOut()
            finish()
        }

        database = Firebase.database.reference
    }

    /**
     * Packages contents of search text box and sends to next activity.
     */
    private fun startSearch() {

        var id = textEditSearchID.text.toString()
        var survey = Interpreter(id)
        var authorString = ""



        survey.type.get().addOnCompleteListener() {
            if (it.result.value == null) {
                Log.i(TAG, "Survey not found!")
                Toast.makeText(this, "Survey not found!", Toast.LENGTH_SHORT).show()
            } else {
                Log.i(TAG, "Survey found!")
                val type = it.result.value as String
                survey.responses.get().addOnCompleteListener() {
                    Log.i(TAG, "Checking responses!")
                    var alreadyResponded = false
                    var responseMap:HashMap<String,List<Int>> = hashMapOf()
                    if (it.result.value != null) {
                        val responses = it.result.value as Map<String, List<Int>>
                        responseMap = HashMap(responses)
                        responses.forEach {
                            if (it.key == Firebase.auth.currentUser!!.uid) {
                                alreadyResponded = true
                            }
                        }
                    }
                    if (alreadyResponded) {
                        Log.i(TAG, "Already responded to survey!")
                        Toast.makeText(
                            this,
                            "You've already responded to this survey!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        survey.author.get().addOnCompleteListener {
//                            Log.i("AMIR", it.result.value as String)
                            val surveyAuthor = it.result.value as String
                            if (Firebase.auth.currentUser!!.uid.equals(surveyAuthor))
                            {
                                Log.i(TAG,"Survey is owned by user. Sending user to results display.")
                                intent = Intent(this@SearchActivity,ResultListActivity::class.java)
                                intent.putExtra("ID",id)
                                intent.putExtra("RESULTS", responseMap)

                                startActivity(intent)
                            }
                            else
                            {
                                Log.i(TAG, "Haven't responded, launching next activity of type " + type)
                                when (type) {
                                    Interpreter.SPOTTER_TYPE -> {
                                        Log.i(TAG, "Launching spotter")
                                        intent = Intent(this@SearchActivity, SpotterSurvey::class.java)
                                        intent.putExtra("ID", id)
                                        startActivity(intent)
                                    }
                                    Interpreter.MULTIPLE_TYPE -> {
                                        Log.i(TAG, "Launching multi choice")
                                        intent =
                                            Intent(this@SearchActivity, MultiChoiceSurvey::class.java)
                                        intent.putExtra("ID", id)
                                        startActivity(intent)
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
    }
}