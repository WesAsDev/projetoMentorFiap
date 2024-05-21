package com.example.myapplication.Screens

import android.net.Uri
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.net.toUri
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.Components.Filters
import com.example.myapplication.MainActivity
import com.example.myapplication.Model.UserModel
import com.example.myapplication.R
import com.example.myapplication.Util.Validators.isValidEmail
import com.example.myapplication.Services.AuthenticationService
import com.example.myapplication.Services.firebaseAuth
import com.example.myapplication.Util.Tags
import com.google.firebase.storage.FirebaseStorage

@Composable
fun SignUp(lat: Double?, long: Double?, navigateToLogin: () -> Unit){
    val AuthService = AuthenticationService()
    var nomeState = remember {
        mutableStateOf("")
    }

    val storage by lazy {
        FirebaseStorage.getInstance()
    }

    var emailState = remember {
        mutableStateOf("")
    }

    var senhaState = remember {
        mutableStateOf("")
    }

    var telefoneState = remember {
        mutableStateOf("")
    }

    var isMentor = remember {
        mutableStateOf(false)
    }

    var isValidEmail = remember{
        mutableStateOf(false)
    }

    var loading = remember {
        mutableStateOf(false)
    }

    val selectedImageUri = remember {
        mutableStateOf("")
    }

    var interesseList = remember {
        mutableStateListOf<String>()
    }


    var formacaoList = remember {
        mutableStateListOf<String>()

    }





    fun uploadImageStorage(uri: String, userId: String){
       val uId = firebaseAuth.currentUser?.uid

       if( userId != null) {
           storage.getReference("fotos")
               .child("usuarios")
               .child(userId)
               .child("perfil.jpg")
               .putFile(uri.toUri())
               .addOnSuccessListener {upTask ->
                   upTask.metadata?.reference?.downloadUrl

               }.addOnFailureListener {

               }
       }
    }

    val photoPickerLaunch = rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) {

        selectedImageUri.value = it.toString()



    }


    Column(modifier = Modifier
        .padding(32.dp)
        .fillMaxSize()
        .verticalScroll(rememberScrollState())) {
        Text(text = "Registre-se", modifier = Modifier.align(Alignment.CenterHorizontally), fontSize = 30.sp)

        ProfilePicture(photoPickerLaunch, selectedImageUri)

        Column(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Column() {
                OutlinedTextField(label = {Text(text="Nome", fontSize = 20.sp)}, value = nomeState.value, onValueChange = {
                    nomeState.value = it
                    Log.i("teste", it)
                }, singleLine = true)
            }

            Spacer(modifier = Modifier.size(30.dp))

            Column() {

                OutlinedTextField(label = {Text(text="Email", fontSize = 20.sp)}, value = emailState.value, onValueChange = {
                    emailState.value = it
                    isValidEmail.value = !isValidEmail(it)
                    Log.i("testeEmail", it)
                },
                    isError = isValidEmail.value, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), singleLine = true)

            }

            Spacer(modifier = Modifier.size(30.dp))

            Column() {

                OutlinedTextField(label = {Text(text="Senha", fontSize = 20.sp)},value = senhaState.value, onValueChange = {
                    senhaState.value = it
                    Log.i("testeSenha", it)
                }, visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), singleLine = true )

            }
            Spacer(modifier = Modifier.size(30.dp))

            Column() {

                OutlinedTextField(label = {Text(text="Telefone/Whatsapp", fontSize = 20.sp)},value = telefoneState.value, onValueChange = {
                    telefoneState.value = it
                    Log.i("testeSenha", it)
                }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true )

            }
            Spacer(modifier = Modifier.size(30.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text="Você é tutor?", fontSize = 20.sp)
                Checkbox(checked = isMentor.value, onCheckedChange = {
                    isMentor.value = it
                    Log.i("tutor", "$it")
                })
            }

            Spacer(modifier = Modifier.size(30.dp))
            Column() {
                Text(text="Selecione seus interesses", fontSize = 20.sp)
                Filters(tagList = Tags.interesseList, onListChange = {
                    interesseList = it
                    Log.d("INTERESSE----", "$interesseList")
                })
            }


            Spacer(modifier = Modifier.size(30.dp))

            Column() {
                Text(text="Selecione sua(s) formação(ões)", fontSize = 20.sp)
                Filters(tagList = Tags.formacaoList, onListChange = {
                    formacaoList = it
                    Log.d("FORMACAO----", "$formacaoList")
                })
            }

            Spacer(modifier = Modifier.size(30.dp))

            Spacer(modifier = Modifier.size(30.dp))


        }


        Button(onClick = { Log.i("CHECKLIST", "ListIntersse: ${interesseList} \n ListFormacao: ${formacaoList}")}) {
            Text(text = "checlkist")
        }
        Button(onClick = {
            if(lat == null || long == null){
                MainActivity().getLocation()
            }

            if(emailState.value != "" && senhaState.value != "" && nomeState.value != ""){
                val user = UserModel(
                    nome=nomeState.value,
                    email=emailState.value,
                    mentor=isMentor.value,
                    telefone = telefoneState.value,
                    latitude = lat,
                    geohash = "",
                    longitude = long,
                    formacaoList = formacaoList,
                    interesseList = interesseList
                    )

                Log.d("pre signup","${user}")

                AuthService.signUp(emailState.value, senhaState.value, user, navigateToLogin,selectedImageUri.value, onError = {
                    loading.value = false
                }){userId->
//                    if(userId != null && selectedImageUri.value != ""){
//
//                        uploadImageStorage(selectedImageUri.value, userId)
//                    }
                }
                loading.value = true
            }

        }, modifier = Modifier.fillMaxWidth(), enabled = !loading.value) {
            Text(text="Registro", modifier = Modifier.padding(20.dp), fontSize = 20.sp)

        }

        if(!loading.value) return

        Dialog(onDismissRequest = { }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally),

                shape = RoundedCornerShape(16.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .width(64.dp)
                            .align(Alignment.CenterVertically)
                    )
                }
            }
        }

    }
}

@Composable
fun ProfilePicture(
    photoPickerLaunch: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>,
    selectedImageUri: MutableState<String>
) {
    val imageUrl = remember {
        mutableStateOf("")
    }

    val painter = rememberAsyncImagePainter(
        if(selectedImageUri.value.isEmpty())
            R.drawable.ic_user
        else
            selectedImageUri.value
    )

    Column(modifier = Modifier
        .padding(9.dp)
        .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {

        Card(shape = CircleShape) {
            Image(painter = painter, contentDescription = null,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(200.dp)
                    .clickable {
                        photoPickerLaunch.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center
            )
        }



        Text(text = "Mude a foto de perfil")
    }
}
