package com.example.myapplication.Services

import android.util.Log
import com.example.myapplication.MainActivity
import com.example.myapplication.Model.MatchModel
import com.example.myapplication.Model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import okhttp3.internal.wait

class MatchServices {
    private var database = FirebaseFirestore.getInstance()


    fun findMatch(uid: String){
        var currentUserId = firebaseAuth.currentUser?.uid
        var mentorId = ""
        var aprendizId = ""
        if(currentUserId != null){
            AuthenticationService().getUserAt { actualUser ->
                if(actualUser == null) return@getUserAt
                mentorId = uid
                aprendizId = currentUserId
                if(actualUser.mentor == true){
                    mentorId = currentUserId
                    aprendizId = uid
                }

                var match = MatchModel(idMentor = mentorId, idAprendiz = aprendizId, match = false)
                var matchRef = database.collection("matches").document(mentorId+aprendizId)
                database.collection("users").document(currentUserId).update("matchList",FieldValue.arrayUnion(uid))
                Log.i("ENTROU NO MATCH", "ENTROU")
                matchRef.get().addOnCompleteListener{doc->

                    Log.i("ENTROU NO ref", "ref ${doc.result.data} - ${doc.isSuccessful}")
                    if(doc.result.data != null){
                        if(doc.result.data?.get("match") == false) {
                            val refMatchDoc =
                                database.collection("matches").document(mentorId + aprendizId)
                            refMatchDoc.update("match", true)
                        }

                    }else{
                        val refMatchDoc = database.collection("matches").document(mentorId+aprendizId)
                        refMatchDoc.set(match)

                        Thread.sleep(1_000)
                        refMatchDoc.addSnapshotListener(MetadataChanges.INCLUDE){ snap, error ->

                            val isitmatch = snap?.data?.get("match") as Boolean

                            if(isitmatch){
                                val matches = MainActivity.Matches
                                matches.hasNews = true
                                MainActivity.changeArray(1, matches)
                                NotificationService.create("Deu match!", "Veja seu novo match")

                            }
                            Log.d("ENTROUUUUUUU", "${snap?.data}")
                        }
                    }

                }.addOnFailureListener { exception ->
                    Log.d("ERROR", "get failed with ", exception)
                }


//                foundMatch.get().addOnSuccessListener {
//                    if (it.documents.size == 0) {
//                        database.collection("matches").document().set(match)
//                    } else {
//                        val matchObj = it.documents.get(0).toObject(MatchModel::class.java)
//                        val matchId = it.documents.get(0).id
//                        if (matchObj != null) {
//                            if (matchObj.match) {
//                                NotificationService.create("Deu match", "DEU MATCH!!!!")
//                            } else {
//                                matchObj.match = true;
//
//                                database.collection("matches").document(matchId)
//                                    .update("match", true)
//                            }
//                        }
//
//                    }
//                    Log.d("EncontrouQuery", "${it.documents}")
//                }

            }
        }
    }

    fun reject(uid: String){
        var currentUserId = firebaseAuth.currentUser?.uid

        if(currentUserId != null){
            database.collection("users").document(currentUserId).update("rejectList", FieldValue.arrayUnion(uid))

        }


    }

    fun getMatchList(callback: (List<UserModel>) -> Unit){
        val uid = firebaseAuth.currentUser?.uid
        AuthenticationService().getUserAt(){ usuarioAtual ->
            val matchList: MutableList<String>
            Log.d("USUARIOATUAL2", "${usuarioAtual}")
            if(usuarioAtual != null) {
                var mentorAprendiz: String = "idAprendiz"
                if(usuarioAtual.mentor == true){
                    mentorAprendiz = "idMentor"
                }
                val ref = database.collection("matches")
                    .where(Filter.and(Filter.equalTo(mentorAprendiz, uid), Filter.equalTo("match", true)))

                val lst = mutableListOf<UserModel>()
                val lstMatchString = mutableListOf<String>()
                ref.get().addOnSuccessListener { qs ->
                    run forEach@{
                        for (doc in qs.documents) {
                            val data = doc.data;
                            val docId = doc.id;

                            if (data != null && docId != null && docId != firebaseAuth.uid) {
                                val match = doc.toObject(MatchModel::class.java)
                                if (match != null) {
                                    if(usuarioAtual.mentor == true){
                                        match.idAprendiz?.let { lstMatchString.add(it) }
                                    }else{
                                        match.idMentor?.let { lstMatchString.add(it) }
                                    }

                                }

                            }
                        }
                    }

                    if (lstMatchString.isEmpty()) return@addOnSuccessListener
                    val ref2 = database.collection("users")
                        .where(Filter.inArray(FieldPath.documentId(),lstMatchString))

                    ref2.get().addOnSuccessListener { qs ->
                        run forEach@{
                            for (doc in qs.documents) {
                                val data = doc.data;
                                val docId = doc.id;

                                if (data != null && docId != null && docId != firebaseAuth.uid) {
                                    val match = doc.toObject(UserModel::class.java)
                                    if (match != null) {
                                        lst.add(match)

                                    }

                                }
                            }
                        }
                        callback(lst.toList())
                        Log.d("finalizou Lista de encontro", "${lst}")
                    }.addOnFailureListener{
                        Log.d("Deu bo", "${it}")
                    }
                }

            }
        };

    }
    fun getUserList(callback: (List<UserModel>) -> Unit){


    }

}