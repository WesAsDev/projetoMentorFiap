package com.example.myapplication.Services

import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import com.example.myapplication.Model.UserModel
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.storage.FirebaseStorage


public class AuthenticationService {

    fun getUserAt(callback: (UserModel?) -> Unit){
        var current = firebaseAuth.currentUser
        var userToRetun: UserModel? = UserModel()
        if(current != null){

            current.uid


            firebaseDatabases.collection("users").document(current.uid).get().addOnSuccessListener {


                userToRetun = it.toObject(UserModel::class.java)
                Log.d("RetornoDatabaseUsuarioAtualConvertido", "${userToRetun}")
                callback(userToRetun)
            }


        }


    }

    fun login(email:String, senha:String, callback: (Boolean)->Unit): kotlin.Any{

        var credential: AuthCredential
        var uid: String?
        Log.i("currentUser", "${firebaseAuth.currentUser}")
        firebaseAuth.signInWithEmailAndPassword(email, senha).addOnSuccessListener {
            Log.d("FINALIZOULOGIN", "${it.credential} -- ${it.additionalUserInfo} -- ${it.user?.email}")
            uid = it.user?.uid
            credential = EmailAuthProvider.getCredential(email, senha)
            Log.d("cred", "${credential}")


            callback(true)
        }.addOnFailureListener{
            ToastService.makeToastMessage(it.localizedMessage, Toast.LENGTH_SHORT)
            Log.e("FALHA NO LOGIN", "-${it.localizedMessage}")
            callback(false)

        }

        if( firebaseAuth.currentUser != null){

            return firebaseAuth.currentUser!!
        }
        return false
    }

    fun signUp(
        email: String,
        senha: String,
        user: UserModel,
        navigate: () -> Unit,
        imageUri: String,
        onError: () -> Unit,
        onComplete: (String?) -> Unit
    ){
        val uid = ""
        firebaseAuth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener{
            Log.d("FINALIZOU", "${it.isSuccessful}")
            if(!it.isSuccessful){
                ToastService.makeToastMessage(it.exception?.localizedMessage, Toast.LENGTH_SHORT)
                return@addOnCompleteListener
            }

            Log.d("PRE ADD USER", "$user")

            //            onComplete(it.result.user?.uid)

            if(it.result.user?.uid != null) {

                if(imageUri != ""){
                    FirebaseStorage.getInstance().getReference("fotos")
                        .child("usuarios")
                        .child(it.result.user!!.uid)
                        .child("perfil.jpg")
                        .putFile(imageUri.toUri())
                        .addOnSuccessListener {upTask ->
                            upTask.metadata?.reference?.downloadUrl?.addOnSuccessListener { imageUri->
                                user.profileUrl = imageUri.toString()
                                addUser(it.result.user?.uid, it.result.user?.email, user.nome, user.latitude, user.longitude, user.mentor, user)
                                navigate()
                            }
                        }.addOnFailureListener {

                        }
                }else{
                    addUser(it.result.user?.uid, it.result.user?.email, user.nome, user.latitude, user.longitude, user.mentor, user)
                    navigate()
                }

            }


        }.addOnFailureListener{
            onError()
            ToastService.makeToastMessage(it.localizedMessage, Toast.LENGTH_SHORT)
            Log.e("ERRO", "${it.message}")
        }
    }

    fun addUser(
        uId: String?,
        email: String?,
        name: String,
        lat: Double?,
        long: Double?,
        mentor: Boolean,
        user: UserModel
    ){
        if (uId == null || email == null || lat == null || long == null) return
//        db.child("users").child(uId).child("email").setValue(email)
//        db.child("users").child(uId).child("username").setValue(name)
        UserServices().saveUser(uId,email,name,lat,long, mentor, user)
        if (lat == null || long == null) return

//        db.child("users").child(uId).child("latitude").setValue(lat)
//        db.child("users").child(uId).child("longitude").setValue(long)

    }
}