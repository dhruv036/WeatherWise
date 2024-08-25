package io.dhruv.weatherwise

import android.content.IntentSender
import androidx.compose.ui.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import io.dhruv.weatherwise.ui.theme.WeatherWiseTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    val viewModel by viewModels<WeatherViewModel>()
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val resolutionForResult = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                viewModel.getPosts()
            } else {
                Toast.makeText(this, "Need Gps", Toast.LENGTH_SHORT).show()
            }
        }

        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {


        }
        permissionLauncher.launch(arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
        ))

        viewModel.isGpsAvailable.observe(this){
            if(it.not()){
                val locationRequest = LocationRequest.create().apply {
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                }

                val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
                val settingsClient = LocationServices.getSettingsClient(this)
                val task = settingsClient.checkLocationSettings(builder.build())
                task.addOnSuccessListener {
                    // Location settings are satisfied
                }.addOnFailureListener { exception ->
                    if (exception is ResolvableApiException) {
                        try {
                            val intentSenderRequest = IntentSenderRequest.Builder(exception.resolution).build()
                            resolutionForResult.launch(intentSenderRequest) // Launch the dialog to prompt user
                        } catch (sendEx: IntentSender.SendIntentException) {
                            // Handle the exception
                        }
                    }
                }
            }
        }

        enableEdgeToEdge()
        setContent {
            WeatherWiseTheme {
                Scaffold(modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color(0xFFFE7E75))) { innerPadding ->
                     WeatherScreen(modifier = Modifier.padding(innerPadding),viewModel)
                }
            }
        }
    }

    fun askLocationPermission(){
//        val locationRequest = LocationRequest.create().apply {
//            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//        }

        // Step 2: Build LocationSettingsRequest
//        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
//        val settingsClient = LocationServices.getSettingsClient(AppContext.context!!)
//        val task = settingsClient.checkLocationSettings(builder.build())

        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                // User enabled location
            } else {
                Toast.makeText(this, "Need Gps", Toast.LENGTH_SHORT).show()
            }
        }
    }

}



@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WeatherWiseTheme {
        Greeting("Android")
    }
}