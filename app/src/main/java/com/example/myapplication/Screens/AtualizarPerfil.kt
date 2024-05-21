package com.example.myapplication.Screens

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.myapplication.Components.Filters
import com.example.myapplication.Model.UserModel
import com.example.myapplication.Services.ToastService
import com.example.myapplication.Services.UserServices
import com.example.myapplication.Util.Tags
import com.google.firebase.storage.FirebaseStorage


@Composable
fun AtualizarPerfil(){

    var userState = remember {
        mutableStateOf(false)
    }
    var usuarioAtual = remember{
        mutableStateOf<UserModel>(UserModel())
    }
    UserServices().getUserInfo { userAtual ->
        usuarioAtual.value = userAtual
        userState.value = true

    }

    if(!userState.value) return

    var nomeState = remember {
        mutableStateOf(usuarioAtual.value.nome)
    }

    val storage by lazy {
        FirebaseStorage.getInstance()
    }

    var telefoneState = remember {
        mutableStateOf(usuarioAtual.value.telefone)
    }

    var loading = remember {
        mutableStateOf(false)
    }

    val selectedImageUri = remember {
        mutableStateOf(usuarioAtual.value.profileUrl.toString())
    }

    var interesseList = remember {
        mutableStateListOf<String>()
    }


    var formacaoList = remember {
        mutableStateListOf<String>()

    }




    LaunchedEffect("teste") {
        nomeState.value = usuarioAtual.value.nome
        telefoneState.value =  usuarioAtual.value.telefone?: ""
        selectedImageUri.value =  usuarioAtual.value.profileUrl?:""
        formacaoList.addAll(usuarioAtual.value.formacaoList?.toList()!!)
        interesseList.addAll(usuarioAtual.value.interesseList?.toList()!!)
    }


    val photoPickerLaunch = rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) {

        selectedImageUri.value = it.toString()

    }


    Column(modifier = Modifier
        .padding(32.dp)
        .fillMaxSize()
        .verticalScroll(rememberScrollState())) {
        Text(text = "Atualize o perfil", modifier = Modifier.align(Alignment.CenterHorizontally), fontSize = 30.sp)

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

                OutlinedTextField(label = {Text(text="Telefone/Whatsapp", fontSize = 20.sp)},value = telefoneState.value!!, onValueChange = {
                    telefoneState.value = it
                    Log.i("testeSenha", it)
                }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true )

            }

            Spacer(modifier = Modifier.size(30.dp))
            Column() {
                Text(text="Selecione seus interesses", fontSize = 20.sp)
                Filters(tagList = Tags.interesseList, onListChange = {
                    interesseList = it
                    Log.d("INTERESSE----", "$interesseList")
                }, isInUpdateUser = true, commonInterest = usuarioAtual.value.interesseList?.toSet()!!)
            }


            Spacer(modifier = Modifier.size(30.dp))

            Column() {
                Text(text="Selecione sua(s) formação(ões)", fontSize = 20.sp)
                Filters(tagList = Tags.formacaoList, onListChange = {
                    formacaoList = it
                    Log.d("FORMACAO----", "$formacaoList")
                }, isInUpdateUser = true, commonInterest = usuarioAtual.value.formacaoList?.toSet()!!)
            }

            Spacer(modifier = Modifier.size(30.dp))

            Spacer(modifier = Modifier.size(30.dp))


        }


        Button(onClick = { Log.i("CHECKLIST", "ListIntersse: ${interesseList} \n ListFormacao: ${formacaoList}")}) {
            Text(text = "checlkist")
        }
        Button(onClick = {

            if(nomeState.value != ""){
                val user = UserModel(
                    nome=nomeState.value,
                    telefone = telefoneState.value,
                    formacaoList = formacaoList,
                    interesseList = interesseList
                )

                Log.d("pre signup","${user}")

                val userMap = hashMapOf<String, Any>(
                    "nome" to user.nome,
                    "telefone" to user.telefone!!,
                    "formacaoList" to user.formacaoList!!,
                    "interesseList" to user.interesseList!!,
                    "profileUrl" to selectedImageUri.value
                )
                loading.value = true

                UserServices().updateUser(user, userMap) {
                    if (it != "") {
                        loading.value = false
                        ToastService.makeToastMessage(it, Toast.LENGTH_LONG)
                    } else {
                        loading.value = false
                        ToastService.makeToastMessage("Usuario atualizado", Toast.LENGTH_LONG)
                    }
                }

            }

        }, modifier = Modifier.fillMaxWidth(), enabled = !loading.value) {
            Text(text="Atualizar", modifier = Modifier.padding(20.dp), fontSize = 20.sp)

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