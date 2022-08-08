package com.example.citizen_science

import java.io.Serializable

/**
 * Class representing a Spotter type survey
 */
class SpotterSurveyData(val surveyName:String,
                        val surveyDescription:String,
                        val surveyID:String,
                        val surveyAuthor:String , var trackables: List<String>) : Serializable
{

}



//class SpotterSurveyData(surveyName:String,
//                        surveyDescription:String,
//                        surveyID:String,
//                        surveyAuthor:String , val trackables: HashMap<String, Int>)
//    : SurveyData(0, surveyName,surveyDescription,surveyID,surveyAuthor)
//{
//
//}



//class SpotterSurveyData(surveyName:String,
//                        surveyDescription:String,
//                        surveyID:String,
//                        surveyAuthor:String , val trackables: List<String>)
//{
//
//}
