package com.example.myapplication.Screens

import android.content.ActivityNotFoundException
import android.content.Intent
import android.icu.text.ListFormatter.Width
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.example.myapplication.Components.CardUsuario
import com.example.myapplication.MainActivity
import com.example.myapplication.Model.UserModel
import com.example.myapplication.Services.MatchServices

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Matches(){
    var uss = remember{
        mutableStateOf(mutableListOf(UserModel()))
    }

    var actualUser = remember{
        mutableStateOf(UserModel())
    }

    var hasUser = remember{
        mutableStateOf(false)
    }
    LaunchedEffect(true){

        MatchServices().getMatchList(){ uList ->

            if(uList.isEmpty()) return@getMatchList

            val matches = MainActivity.navItens.getMenuArrayIndex(1)

            if(matches.hasNews == true){
                matches.hasNews = false
                MainActivity.changeArray(1, matches)
            }

            uss.value = uList.toMutableList()

            if(uss.value.size >0) {
                actualUser.value = uss.value[0]
                hasUser.value = true
                Log.d("ENTROU ANTESLISCE", "${uss.value[0]}")
            }

        }
    }


    LazyColumn(modifier = Modifier
        .padding(30.dp)
        .fillMaxSize()){
        items(items = uss.value, itemContent = {
            if (it.nome != "") {
                CardUsuario(it,{},it.mentor,{},true)
                Spacer(modifier = Modifier.size(10.dp))
            }else{
                Box(modifier = Modifier.fillMaxSize()){
                    Icon(imageVector = Icons.Filled.Clear, contentDescription = "")

                }
            }
        })
    }
}

@Composable
fun MatchItem(user: UserModel){
    val ctx = LocalContext.current
    Column(
        modifier = Modifier
            .padding(32.dp)
            .background(Color.Blue)
            .width(300.dp)
    ) {
        Text(text = "Nome:")
        if (user.nome != null) {
            Text(text = user.nome)
        }
        Text(text = "email:")
        if (user.email != null) {
            Text(text = user.email!!)
        }

    }
}