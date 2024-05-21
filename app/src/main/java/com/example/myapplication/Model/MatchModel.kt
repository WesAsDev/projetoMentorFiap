package com.example.myapplication.Model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class MatchModel (
    var idMentor: String? = null,
    var idAprendiz: String? = null,
    var match: Boolean = false
) {
}


