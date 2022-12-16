package com.example.slipfinal

var ContactList = mutableListOf<Contact>()
val CONTACT_ID_EXTRA = "contactExtra"
class Contact {
    lateinit var UserID: String
    lateinit var Name: String

    var PhoneNumber: String? = null
    var SnapChat : String? = null
    var Instagram: String? = null
    var Facebook : String? = null
    val id: Int?= ContactList.size

    constructor(uID: String, name: String) {
        UserID = uID
        Name = name
    }

    private constructor() {}
}