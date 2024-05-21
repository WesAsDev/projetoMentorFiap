package com.example.myapplication

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.app.NotificationCompat
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.Screens.AtualizarPerfil
import com.example.myapplication.Screens.Home
import com.example.myapplication.Screens.Login
import com.example.myapplication.Screens.Matches
import com.example.myapplication.Screens.Settings
import com.example.myapplication.Screens.SignUp
import com.example.myapplication.Services.MyFirebaseMessagingService
import com.example.myapplication.Services.NotificationService
import com.example.myapplication.Services.ToastService
import com.example.myapplication.Services.UserServices
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.messaging.FirebaseMessaging

data class BottomNavItem(
    val title: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    var hasNews: Boolean,
    val badgeCount: Int? = null
)

class MainActivity : ComponentActivity(), LocationListener  {


    private lateinit var locationManager: LocationManager
    private lateinit var tvGpsLocation: TextView
    private var lat: Double? = 0.0
    private var long: Double? = 0.0
    private val locationPermissionCode = 2

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            // TODO: Inform user that that your app will not show notifications.
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    companion object navItens{
        val Home =  BottomNavItem(
                title = "Home",
                label = "Home",
                unselectedIcon = Icons.Filled.Home,
                selectedIcon = Icons.Filled.Home,
                hasNews = false
        )
        val Matches = BottomNavItem(
            title = "Matches",
            label = "Matches",
            unselectedIcon = Icons.Filled.Person,
            selectedIcon = Icons.Filled.Person,
            hasNews = false
        )
            val Settings =  BottomNavItem(
                title = "Settings",
                label = "Configurações",
                unselectedIcon = Icons.Filled.Settings,
                selectedIcon = Icons.Filled.Settings,
                hasNews = false
            )
        val items = MutableList<BottomNavItem>(3){
            Home
        }

        fun changeArray(index: Int, value: BottomNavItem){
            items[index] = value
        }

        fun getMenuArrayIndex(index: Int): BottomNavItem{
            return items[index]
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        getLocation()

        askNotificationPermission()
        super.onCreate(savedInstanceState)

        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()

                val currentRoute = remember {
                    mutableStateOf<String?>(null)
                }
                currentRoute.value = navController.currentBackStackEntry?.destination?.route

                val navBackStackEntry by navController.currentBackStackEntryAsState()

                var startDest = "Login"

                if (UserServices().isUserLogged()) startDest = "Home"



                changeArray(1, Matches)

                changeArray(2, Settings)
                var selectedItemIndex by rememberSaveable{
                    mutableStateOf(0)
                }
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {




                        Scaffold(
                            modifier = Modifier.background(color = Color.Transparent),
                            bottomBar = {

                                if(navBackStackEntry?.destination?.route != "Login" && navBackStackEntry?.destination?.route != "SignUp" && navBackStackEntry?.destination?.route != null){
                                NavigationBar {
                                    items.forEachIndexed { index, item ->
                                        NavigationBarItem(
                                            selected = selectedItemIndex == index,
                                            onClick = {
                                                selectedItemIndex = index

                                                navController.navigate(item.title)
                                            },
                                            label = {
                                                Text(text = item.label)
                                            },
                                            icon = {
                                                BadgedBox(badge = {
                                                    if(item.badgeCount != null){
                                                        Badge{
                                                            Text(text = item.badgeCount.toString())
                                                        }
                                                    } else if(item.hasNews){
                                                        Badge()
                                                    }

                                                }) {
                                                    Icon(
                                                        imageVector = if(index == selectedItemIndex){
                                                            item.selectedIcon}
                                                        else item.unselectedIcon
                                                    ,
                                                        contentDescription = item.title)

                                                }

                                            }
                                        )
                                    }
                                }}},
                            ) {

                            Log.d("CURRENTROUT", "${currentRoute}")

                            Box(modifier = Modifier.padding(it)) {


                                NavHost(
                                    navController = navController,
                                    startDestination = startDest
                                ) {
                                    composable(route = "Home") { Home() }
                                    composable(route = "Matches") { Matches() }
                                    composable(route = "UpdateProfile") {
                                        AtualizarPerfil()
                                    }
                                    composable(route = "Settings") {
                                        Settings(OnNavigateToLogin = {
                                            navController.navigate(
                                                "Login"
                                            )
                                        }, OnNavigateToUpdate = {
                                            navController.navigate(
                                                "UpdateProfile"
                                            )
                                        })
                                    }
                                    composable(route = "Login") {
                                        Login(OnNavigateToSignUp = {
                                            navController.navigate(
                                                "SignUp"
                                            )
                                        }, OnNavigateToHome = { navController.navigate("Home") })
                                    }
                                    composable(route = "SignUp") {
                                        getLocation()
                                        SignUp(
                                            lat = lat,
                                            long = long,
                                            navigateToLogin = { navController.navigate("Login") })
                                    }
                                }
                            }
                        }




                    ToastService.setContext(this)
                    NotificationService.setContext(this)
                    NotificationService().createNotificationChannel()


                }
            }
        }
    }

    override fun onStop() {
        super.onStop()



        Log.i("PAROU", "PAROU")
    }

    override fun onStart() {
        super.onStart()
        Log.i("VOLTO", "VOLTO")
    }

    private fun selicitaPermissoes(){
        /*{ TODO
              - SOLICITAR PERMISSAO DE CAMERA
              - SOLICITAR PERMISSAO DE GALERIA
              }*/
    }
    fun getLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionCode)

        }
        var locatei = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        Log.i("locationFirst", "lat: ${locatei?.longitude}, long: ${locatei?.longitude}")
        lat = locatei?.latitude
        long = locatei?.longitude

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4800000, 500f, this)

    }
    override fun onLocationChanged(location: Location) {
        UserServices().getUserInfo { user->
            user.longitude = long
            user.latitude = lat

            var updates = hashMapOf<String,Any>(
                "latitude" to user.latitude!!,
                "longitude" to user.longitude!!,
                "lastLocationUpdate" to Timestamp.now()
            )

            UserServices().updateUser(user, updates, {})



        }
        Log.i("location CHANGED", "lat: ${location.latitude}, long: ${location.longitude}")
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }

    }

}

