package com.example.myapplication.Components

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import coil.compose.AsyncImage
import com.example.myapplication.Model.UserModel
import com.example.myapplication.Util.Tags
import com.example.myapplication.ui.theme.ErrorRedPastel
import com.example.myapplication.ui.theme.backgroundC


@Composable
fun CardUsuario (User: UserModel, deuMatch: () -> Unit, mentor: Boolean, negouMatch: ()->Unit, compact: Boolean = false) {
    val ctx = LocalContext.current
    if(!compact){
        Box(modifier = Modifier
            .background(color = backgroundC)
            .fillMaxSize()){
            AsyncImage(model = User.profileUrl,
                contentDescription = "",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillHeight

            )
    //        Image(painter = , contentDescription = )


            val brush = Brush.verticalGradient(listOf(Color(0f,0f,0f,0.0f),Color(0f,0f,0f,0.70f)), endY = 100f)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(brush),
            ) {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)) {
                    Text(text = User.nome, fontSize = 30.sp)
                    Text(text = "${User.distance} de você")
                    Filters(onListChange = {}, tagList = User.interesseList!!, isShowUser = true, commonInterest = User.commomInterest!!)
                    Spacer(modifier = Modifier.size(20.dp))

                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()){
                        Button(onClick = { deuMatch()}) {
                            Text(text = if(mentor) "Seja meu Mentor" else "Seja meu Aprendiz", fontSize = 15.sp, modifier = Modifier.padding(15.dp))
                        }
                        Button(onClick = { negouMatch()}, colors = ButtonColors(ErrorRedPastel, MaterialTheme.colorScheme.onErrorContainer, MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.onTertiaryContainer)) {
                            Icon(imageVector = Icons.Filled.ThumbUp, contentDescription = "", modifier = Modifier
                                .padding(15.dp)
                                .rotate(180.0f))
                        }
                    }
                }


            }
        }

    }else{

            Card (modifier = Modifier.clickable {
                if(User.telefone == "") return@clickable
                val url = "https://api.whatsapp.com/send?phone=+55${User.telefone}"
                val i = Intent(Intent.ACTION_VIEW)
                i.setData(Uri.parse(url))
                startActivity(ctx,i, Bundle())

            }){
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp),
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    AsyncImage(
                        alignment = Alignment.Center,
                        model = User.profileUrl,
                        contentDescription = "",
                        contentScale = ContentScale.Crop,            // crop the image if it's not a square
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)                       // clip to the circle shape
                            .border(2.dp, Color.Gray, CircleShape)   // add a border (optional)

                    )
                    Spacer(modifier = Modifier.size(20.dp))
                    Column(modifier = Modifier.height(100.dp), verticalArrangement = Arrangement.Center) {
                        Text(text = User.nome, fontSize = 17.sp)
                        Spacer(modifier = Modifier.size(5.dp))
                        Text(text = User.email!!)
                        Spacer(modifier = Modifier.size(5.dp))
                        Text(text = User.telefone!!)
                    }
                    //        Image(painter = , contentDescription = )

                }
            }

        }
}

@Composable
@Preview
fun UsuarioPreview(){
    CardUsuario(User = UserModel(
        nome = "Weslley",
        telefone = "11 980987303",
        email = "WJS@wjs.com",
        interesseList = Tags.interesseList,
        formacaoList = Tags.formacaoList
    ),{},true,{}, true)
}