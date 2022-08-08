package com.example.citizen_science

class SelectableItem {

    public var title: String = ""
    public var description : String = ""
    public var selected : Boolean = false

    constructor(title : String, description : String, selected : Boolean) {
        this.title = title
        this.description = description
        this.selected = selected
    }
}