package com.isen.secumobileisen

class Patients {


    var date: String = ""
    var name: String = ""
    var pathology: String = ""
    var treatments: String = ""
    var today: String = ""

    constructor() {}


    constructor(date: String, name: String, pathology: String, treatments: String, today: String) {
        this.date = date
        this.name = name
        this.pathology = pathology
        this.treatments = treatments
        this.today = today
    }
}