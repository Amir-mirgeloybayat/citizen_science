package com.example.citizen_science

import android.provider.Settings
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.io.Serializable
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class Interpreter(surveyID : String) : Serializable {

    public companion object {
        const val SPOTTER_KEY = "Spotter"
        const val MULTIPLE_KEY = "Multi Choice"

        const val AUTHOR_KEY = "surveyAuthor"
        const val DESC_KEY = "surveyDescription"
        const val ID_KEY = "surveyID"
        const val TITLE_KEY = "surveyName"
        const val TYPE_KEY = "surveyType"

        const val TRACKABLE_KEY = "trackables"
        const val QUESTION_KEY = "questions"

        const val RESPONSE_KEY = "responses"
        const val NUM_RESPONSE_KEY = "numResponses"

        const val SPOTTER_TYPE = "SP"
        const val MULTIPLE_TYPE = "MC"

        const val TAG = "Interpreter"

        /**
         * Creates and pushes a new spotter survey based on the following inputs
         * See format spec posted in general
         * Returns ID
         */
        fun createSpotterSurvey(
            title: String,
            desc: String,
            trackables: List<String>
        ) : String {

            // Generate ID
            val id = IDGenerator(IDGenerator.SPOTTER_TYPE).surveyID

            Log.i(TAG, "Creating spotter survey with ID " + id)

            // Set up entry
            val surveyRoot = Firebase.database.reference.child(SPOTTER_KEY).child(id)
            surveyRoot.child(AUTHOR_KEY).setValue(Firebase.auth.currentUser!!.uid)
            surveyRoot.child(TITLE_KEY).setValue(title)
            surveyRoot.child(DESC_KEY).setValue(desc)
            surveyRoot.child(ID_KEY).setValue(id)
            surveyRoot.child(TYPE_KEY).setValue(SPOTTER_TYPE)
            surveyRoot.child(TRACKABLE_KEY).setValue(trackables)

            // Return ID
            return id
        }

        /**
         * Creates and pushes a new spotter survey based on the following inputs
         * See format spec posted in general
         * Returns ID
         */
        fun createMultiChoiceSurvey (
            title: String,
            desc: String,
            questions: List<List<String>>
        ) : String {

            // Generate ID
            val id = IDGenerator(IDGenerator.MULTIPLE_QUESTION_TYPE).surveyID

            // Set up entry
            val surveyRoot = Firebase.database.reference.child(MULTIPLE_KEY).child(id)
            surveyRoot.child(AUTHOR_KEY).setValue(Firebase.auth.currentUser!!.uid)
            surveyRoot.child(TITLE_KEY).setValue(title)
            surveyRoot.child(DESC_KEY).setValue(desc)
            surveyRoot.child(ID_KEY).setValue(id)
            surveyRoot.child(TYPE_KEY).setValue(MULTIPLE_TYPE)
            surveyRoot.child(QUESTION_KEY).setValue(questions)

            // Return ID
            return id
        }

        /**
         * Adds a new question to a multiple choice question set - to ease survey construction
         */
        fun addQuestion(
            set: MutableList<List<String>>,
            question: String,
            description: String,
            answers: List<String>
        ): List<List<String>> {

            // Create new question + answers list in the required format
            val newQuestion = MutableList<String>(answers.size + 2) {
                if (it == 0) {
                    question
                } else if (it == 1) {
                    description
                } else {
                    answers[it - 2]
                }
            }

            // Add and return
            set.add(newQuestion)
            return set
        }

        /**
         * Collates spotter responses into sums
         * Converts from format described in "addResponse" (bottom of page) to the following:
         * [# of total sightings of thing 1, # of total sightings of thing 2, ...]
         */
        public fun collateSpotterResponses(resp : Map<String, List<Int>>) : List<Int>? {

            if(resp.size == 0) {
                return null
            }

            // Infer number of trackables
            var numTrackables = 0
            resp.forEach { _, v ->
                numTrackables = v.size
            }

            // Populate list
            val result = List<Int>(numTrackables) {
                var sum = 0
                resp.forEach { _, v ->
                    sum += v[it]
                }
                sum
            }

            // Return
            return result
        }

        /**
         * Collates multichoice responses into sums
         * Converts from format described in "addResponse" (bottom of page) to the following:
         *      A list of int->int maps, with each map corresponding to one question.
         *      Each maps the index of each possible answer to the number of people who chose that answer.
         */
        public fun collateMultiResponses(resp : Map<String, List<Int>>) : List<Map<String,Int>>? {
            if(resp.isEmpty()) {
                return null
            }

            // Infer number of questions
            var numQuestions = 0
            resp.forEach { _, v ->
                numQuestions = v.size
            }

            val result = List<Map<String,Int>> (numQuestions) { q ->

                // Construct the sum map for a single question
                val sums = HashMap<String,Int>()

                // Go through each user
                resp.forEach { _, v ->

                    // For each user, check the current question
                    // v[q] corresponds to whatever index the user checked for this question
                    sums.put(v[q].toString(), sums.getOrDefault(v[q].toString(), 0) + 1)
                }
                sums
            }
            return result
        }

        /**
         * Checks if a firebase request is valid - i.e. succeeds and returns a non-null value
         */
        private fun taskValid(task: Task<DataSnapshot>): Boolean {
            Log.i(TAG, "task.isSuccessful: " + task.isSuccessful)
            Log.i(TAG, "task.result.value non-null?: " + (task.result.value != null))
            return task.isSuccessful && (task.result.value != null)
        }
    }

    private var mSurveyID: String = surveyID
    private var mSurveyType: String
    private var mDatabaseReference = Firebase.database.reference
    private var mSurveyRoot: DatabaseReference

    // Initializes interpreter based on provided ID
    // Do not proceed if invalid
    init {
        Log.i(TAG, "Initializing interpreter")

        if (mSurveyID.length == 0 || mSurveyID[0] == 'S') {
            mSurveyType = IDGenerator.SPOTTER_TYPE
            mSurveyRoot = mDatabaseReference.child(SPOTTER_KEY).child(mSurveyID)
        } else {
            mSurveyType = IDGenerator.MULTIPLE_QUESTION_TYPE
            mSurveyRoot = mDatabaseReference.child(MULTIPLE_KEY).child(mSurveyID)
        }

    }

    /**
     * The functions below are getters for all basic survey nodes
     */
    public val author: DatabaseReference
        get() {
            return mSurveyRoot.child(AUTHOR_KEY)
        }


    public val description: DatabaseReference
        get() {
            return mSurveyRoot.child(DESC_KEY)
        }


    public val id: DatabaseReference
        get() {
            return mSurveyRoot.child(ID_KEY)
        }

    public val title: DatabaseReference
        get() {
            return mSurveyRoot.child(TITLE_KEY)
        }

    public val type: DatabaseReference
        get() {
            return mSurveyRoot.child(TYPE_KEY)
        }

    // Here we have a simple indexed list of trackables
    public val trackables: DatabaseReference?
        get() {
            if(mSurveyType == IDGenerator.SPOTTER_TYPE)
                return mSurveyRoot.child(TRACKABLE_KEY)
            else
                return null
        }

    // Here we have an indexed list of questions and their answers
    // First element of each sublist is the question
    // Each subsequent element is a potential answer
    public val questions : DatabaseReference?
        get() {
            if(mSurveyType == IDGenerator.MULTIPLE_QUESTION_TYPE)
                return mSurveyRoot.child(QUESTION_KEY)
            else
                return null
        }

    // Here we have a Map<User ID, List of Counts or Answer #s>>
    public val responses : DatabaseReference
        get() {
            return mSurveyRoot.child(RESPONSE_KEY)
        }

    /**
     * Add response to existing survey
     * For spotter, expects format [# of first thing spotted, # of second thing spotted...]
     * For multichoice, expects format of [ans for Q1, ans for Q2, ...]
     */
    public fun addResponse(response : List<Int>) {
        mSurveyRoot.child(RESPONSE_KEY).child(Firebase.auth.currentUser!!.uid).setValue(response)
    }

}