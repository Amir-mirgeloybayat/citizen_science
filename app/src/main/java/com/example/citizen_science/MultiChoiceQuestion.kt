package com.example.citizen_science

import java.io.Serializable

/**
 * Class representing a multiple-choice question, for use inside a multiple choice survey.
 */
class MultiChoiceQuestion(var title:String, var description:String, var choices:List<String>) : Serializable
{

}