package com.example.myapplication.Model

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class UserModel (
    var id: String? = null,
    var nome: String = "",
    var email: String? = "",
    var mentor: Boolean = false,
    var latitude: Double? = null,
    var geohash: String? = "",
    var longitude: Double? = null,
    var matchList: MutableList<String>? = null,
    var rejectList: MutableList<String>? = null,
    var formacaoList: List<String>? = null,
    var interesseList: List<String>? = null,
    var profileUrl: String? = "https://static.vecteezy.com/system/resources/thumbnails/009/292/244/small/default-avatar-icon-of-social-media-user-vector.jpg",
    var telefone: String? = "",
    var distance: String? = "",
    var distanceNumber: Float? = 0.0f,
    var commomInterest: Set<String>? = null,
    var lastLocationUpdate: Timestamp? = null

    ) {
    operator fun get(field: String) {

    }
}