package com.example.citizen_science

import java.lang.Exception
import java.util.*
/*
    This class generates 10-character surveyIDs in form of:

        SP or MQ + (CHARACTER), (CHARACTER), (num), (num), (num), (character), (character), (num), (num), (num)
         type         upper       upper                             lower         lower

         ASCII range for numbers [48, 57]
         ASCII range for characters [65-90] lower case, [97, 122] upper case


        Instantiation:
            val surveyID = IDGenerator("SP") for the Spotter type
            val surveyID = IDGenerator("MC") for the Multiple question type

*/
class IDGenerator(surveyType: String) {
    public companion object {
        const val LOWER_BOUND_NUM = 48           // ASCII lower and upper bounds for numbers
        const val UPPER_BOUND_NUM = 57
        const val LOW_CHAR_LOWER_BOUND = 97      // ASCII lower and upper bounds for lower case letters
        const val LOW_CHAR_UPPER_BOUND = 122
        const val UP_CHAR_LOWER_BOUND = 65       // ASCII lower and upper bounds for upper case letters
        const val UP_CHAR_UPPER_BOUND = 90

        const val ID_SIZE = 10
        const val NUM_CHUNK_SIZE = 3
        const val CHAR_CHUNK_SIZE = 2

        const val SPOTTER_TYPE = "SP"
        const val MULTIPLE_QUESTION_TYPE = "MC"
    }

    val surveyID: String

    init {
        surveyID= when (surveyType) {
            SPOTTER_TYPE -> "$SPOTTER_TYPE${surveyIDMaker()}"
            MULTIPLE_QUESTION_TYPE -> "$MULTIPLE_QUESTION_TYPE${surveyIDMaker()}"
            else -> throw Exception("Invalid Survey Type Entered")
        }
    }

    /*
        This method creates a new random object with a new randomly chosen seed, and returns the newly
        created random object for further uses.
        This ensure that upon multiple chained calls, the random object doesn't return the same
        string of numbers by randomizing the seed as well.
    */
    private fun seedRandomizer(rand: Random): Random {
        val seed = rand.nextLong()
        val rand = Random(seed)

        return rand
    }

    // this method creates the number chunk of the ID randomly and populates the ASCII arraylist
    private fun numChunkMaker(rand: Random,randomASCIIArrayList: ArrayList<Int>) {
        repeat (NUM_CHUNK_SIZE) {
            val numRange = UPPER_BOUND_NUM - LOWER_BOUND_NUM + 1
            randomASCIIArrayList.add(rand.nextInt(numRange) + LOWER_BOUND_NUM)
        }
    }

    // this method creates the character chunk of the ID randomly and populates the ASCII arraylist
    private fun charChunkMaker(rand: Random, randomASCIIArrayList: ArrayList<Int>, upperCase: Boolean) {
        val numRange: Int
        val lower: Int
        if (upperCase) {
            numRange = UP_CHAR_UPPER_BOUND - UP_CHAR_LOWER_BOUND + 1
            lower = UP_CHAR_LOWER_BOUND
        } else {
            numRange = LOW_CHAR_UPPER_BOUND - LOW_CHAR_LOWER_BOUND + 1
            lower = LOW_CHAR_LOWER_BOUND
        }

        repeat(CHAR_CHUNK_SIZE) {
            randomASCIIArrayList.add(rand.nextInt(numRange) + lower)
        }
    }

    /*
        this method creates the chunks of the surveyID and randomizes the seed of the random object
        between each phase for security reasons.
        returns the ID
    * */
    private fun surveyIDMaker(): String {
        var surveyID: String = ""

        var rand = seedRandomizer(Random())
        val randomASCIIs = ArrayList<Int>(ID_SIZE)

        charChunkMaker(rand, randomASCIIs, true)
        rand = seedRandomizer(rand)
        numChunkMaker(rand, randomASCIIs)
        rand = seedRandomizer(rand)
        charChunkMaker(rand, randomASCIIs, false)
        rand = seedRandomizer(rand)
        numChunkMaker(rand, randomASCIIs)

        // there's probably a nicer Kotlin way to do this but I just leave it like this for now
        for (num in randomASCIIs) {
            surveyID += num.toChar()
        }

        return surveyID
    }
}