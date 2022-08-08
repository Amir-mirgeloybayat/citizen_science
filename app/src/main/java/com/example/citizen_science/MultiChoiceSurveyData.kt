package com.example.citizen_science

import java.io.Serializable

/**
 * Class representing a Multiple Choice type survey
 */
class MultiChoiceSurveyData(surveyName:String, var surveyDescription:String,
                            var surveyID:String,
                            var surveyAuthor:String ,
                            var questions: List<MultiChoiceQuestion>) : Serializable
{

}