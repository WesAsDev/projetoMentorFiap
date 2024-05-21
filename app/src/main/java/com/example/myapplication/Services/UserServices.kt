package com.example.myapplication.Services

import android.util.Log
import androidx.core.net.toUri
import com.example.myapplication.Model.UserModel
import com.example.myapplication.Util.Utils
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.common.primitives.Floats
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage


public class UserServices {
    private var database = FirebaseFirestore.getInstance()
    private var uid: String = ""

    companion object{
        var searchDistance = 10000.0F
        fun setDitanciaBusca(newDistance: Float) {
            searchDistance = newDistance
        }
    }


    fun getUserInfo(callback:(data: UserModel)->Unit){
        val uId = firebaseAuth.currentUser?.uid

        if (uId != null) {
            val refUser = database.collection("users").document(uId)
            Log.d("REFUSER", "$refUser")
            refUser.get().addOnSuccessListener { snap ->
                val data = snap.data
                val obj = snap.toObject(UserModel::class.java)
                Log.d("REFUSEROBJECT", "$obj")
                if(obj != null) {
                    obj.id = snap.id
                    callback(obj)
                    Log.i("funciona", "name: ${obj}")
                }
                if(data != null){

                }

            }
        }
    }

    fun getUserList(callback: (List<UserModel>) -> Unit){
        AuthenticationService().getUserAt(){ usuarioAtual ->

            Log.d("USUARIOATUAL2", "${usuarioAtual}")
            if(usuarioAtual != null) {
                val ref = database.collection("users")
                    .where(Filter.notEqualTo("mentor", usuarioAtual.mentor))
                val lst = mutableListOf<UserModel>()
                ref.get().addOnSuccessListener { qs ->

                        Log.i("LOG SNAPSHOT", "${qs.documents}")
                    loop@ for (doc in qs.documents) {
                        val data = doc.data;
                        val docId = doc.id;

                        Log.d("CONTAINS REJECT", "${data}")
                        if (data != null && docId != null && docId != firebaseAuth.uid) {
                            val user = doc.toObject(UserModel::class.java)
                            Log.d("CONTAINS REJECT", "${user}")
                            if (usuarioAtual.matchList?.contains(docId) == true || usuarioAtual.rejectList?.contains(docId) == true) continue@loop
                            if (user != null) {
                                var ditancias: FloatArray = floatArrayOf(0.0F)
                                user.id = docId
                                Log.d("LOCATION DISTANCE", "${usuarioAtual.latitude}")
                                Log.d("LOCATION DISTANCE", "${user.latitude}")
                                android.location.Location.distanceBetween(usuarioAtual.latitude!!, usuarioAtual.longitude!!, user.latitude!!, user.longitude!!, ditancias).let { distance ->

                                    if(ditancias[0] < 1000){
                                        user.distance = "${String.format("%.1f", ditancias[0])} Metros"
                                        user.distanceNumber = ditancias[0].toInt().toFloat()
                                    }else{
                                        user.distance = "${
                                            String.format(
                                                "%.1f",
                                                Utils().meterToKm(ditancias[0].toInt())
                                            )
                                        }Km "
                                        user.distanceNumber = ditancias[0].toInt().toFloat()
                                    }

                                    Log.d("LOCATION DISTANCE", user.distance!!)
                                    val intersect = usuarioAtual.interesseList?.intersect(user.interesseList!!)

                                    if (intersect != null) {
                                        if(intersect.isNotEmpty()){
                                            user.commomInterest = intersect
                                            if(user.distanceNumber!! < searchDistance*1000){
                                                lst.add(user)
                                            }
                                        }
                                    }


                                    lst.sortBy {
                                        it.distanceNumber
                                    }

                                    callback(lst.toList())

                                }

                            }

                        }
                    }


                    Log.d("finalizou Lista", "${lst}")
                }

            }
        };


    }

    fun isUserLogged(): Boolean{
        if(firebaseAuth.currentUser?.uid != null){
            return true
        }

        return false
    }

    fun logOut(){
        firebaseAuth.signOut()
    }

    fun setUserStatus(){

    }

    fun getUsersByDistance(){
        val tasks: MutableList<Task<QuerySnapshot>> = ArrayList()

        Log.i("entr", "Entrou")
        val uId = firebaseAuth.currentUser?.uid
        Log.w("uid","${uId}")
        if(uId != null) {
            val refUser = database.collection("users").document(uId)
            var latitude: Double
            var longitude: Double
            refUser.get().addOnSuccessListener { doc ->
                val dados = doc.data
                if( dados != null){
                    Log.i("dados", "dados--------------------")
                    doc.getDouble("latitude")
                    doc.getDouble("longitude")
                    latitude = doc.getDouble("latitude")!!
                    longitude = doc.getDouble("longitude")!!

                    val bounds =
                        GeoFireUtils.getGeoHashQueryBounds(GeoLocation(latitude, longitude), 50.0 * 1000.0)


                    Log.i("GEOLOCATION", "${GeoLocation(latitude, longitude)}")
                    for (b in bounds) {
                        Log.i("bounds", "${b}")
                        Log.i("startHash", "${b.startHash}")
                        Log.i("endHash", "${b.endHash}")
                        val q = database.collection("users")
                            .orderBy("geohash")
                            .startAt(b.startHash)
                            .endAt(b.endHash)
                        tasks.add(q.get())
                    }

                    Tasks.whenAllComplete(tasks)
                        .addOnCompleteListener {
                            Log.i("task", "task--------------------")
                            val matchingDocs: MutableList<DocumentSnapshot> = ArrayList()
                            for (task in tasks) {
                                Log.i("tasks", "task--------------------")
                                val snap = task.result
                                for (doc in snap!!.documents) {
                                    val lat = doc.getDouble("latitude")!!
                                    val lng = doc.getDouble("longitude")!!

                                    // We have to filter out a few false positives due to GeoHash
                                    // accuracy, but most will match
                                    val docLocation = GeoLocation(lat, lng)
                                    val distanceInM = GeoFireUtils.getDistanceBetween(docLocation, GeoLocation(latitude, longitude))
                                    if (distanceInM <= 50.0 * 1000.0 && doc.id != uId) {
                                        matchingDocs.add(doc)
                                    }
                                }
                            }

                            for(doc in matchingDocs){
                                Log.d("LOGGDISTANCE", "${doc}")
                            }
                        }

                }



            }

            // Collect all the query results together into a single list

        }
    }
    fun saveUserNotificationToken(token: String){
        var messagim = FirebaseMessaging.getInstance()
        val current = firebaseAuth.currentUser
        if(current != null){
            current.uid

            database.collection("users").document(current.uid).update("notificationToken", token)

        }
    }

    fun updateUser(user: UserModel, fields: HashMap<String,Any>, postUpdate: (String)-> Unit){


        val current = firebaseAuth.currentUser
        if(current != null) {
            current.uid
            Log.d("UPDATE INFO", "User:${user}\n Field:${fields}")
            if(fields.contains("profileUrl")){
                FirebaseStorage.getInstance().getReference("fotos")
                    .child("usuarios")
                    .child(current.uid)
                    .child("perfil.jpg")
                    .putFile((fields.get("profileUrl") as String).toUri())
                    .addOnSuccessListener {upTask ->
                        upTask.metadata?.reference?.downloadUrl?.addOnSuccessListener { imageUri->
                            user.profileUrl = imageUri.toString()
                            fields.set("profileUrl", imageUri.toString())
                            database.collection("users").document(current.uid).update(fields).addOnSuccessListener {
                                postUpdate("")
                            }.addOnFailureListener{ ex->
                                postUpdate(ex.message!!)

                            }
                        }
                    }.addOnFailureListener {

                    }
            }else{
                database.collection("users").document(current.uid).update(fields).addOnSuccessListener {
                    postUpdate("")
                }.addOnFailureListener{ ex->
                    postUpdate(ex.message!!)

                }
            }

        }
    }


    fun saveUser(
        uid: String,
        email: String,
        nome: String,
        latitude: Double,
        longitude: Double,
        mentor: Boolean,
        user: UserModel
    ){
        val hash = GeoFireUtils.getGeoHashForLocation(GeoLocation(latitude, longitude))


        val data = mapOf(
            "nome" to nome,
            "email" to email,
            "geohash" to hash,
            "latitude" to latitude,
            "longitude" to longitude,
            "mentor" to mentor,
            "formacaoList" to user.formacaoList,
            "interesseList" to user.interesseList,
            "profileUrl" to user.profileUrl,
            "telefone" to user.telefone
        )

        Log.d("PRE SIGNUYP", "$data")

        database.collection("users").document(uid).set(data).addOnSuccessListener {

        }
    }


}