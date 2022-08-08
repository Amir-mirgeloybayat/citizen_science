package com.example.citizen_science

import android.content.Intent

class MCQuestionItem {

    public var title: String = ""
    public var description : String = ""
    public var choices : List<String> = List<String>(0) { "" }
    public var selected : Boolean = false

    constructor(title : String, description : String, choices : List<String>, selected : Boolean) {
        this.title = title
        this.description = description
        this.choices = choices
        this.selected = selected
    }

    constructor(question : MultiChoiceQuestion) {
        this.title = question.title
        this.description = question.description
        this.choices = question.choices
        this.selected = false
    }
}

public interface MCRecyclerListener {
    public fun edit(item : MCQuestionItem, position : Int)
}