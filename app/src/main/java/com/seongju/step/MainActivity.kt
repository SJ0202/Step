package com.seongju.step

import android.Manifest
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.seongju.step.ui.theme.StepTheme

@OptIn(ExperimentalPermissionsApi::class)
class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var stepsSensor: Sensor
    var running = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            /**
             * Permission
             */
            val lifecycleOwner = LocalLifecycleOwner.current
            val permissionsState = rememberMultiplePermissionsState(
                permissions = listOf(
                    Manifest.permission.ACTIVITY_RECOGNITION,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.BLUETOOTH,
                )
            )
            DisposableEffect(
                key1 = lifecycleOwner,
                effect = {
                    val observer = LifecycleEventObserver { _, event ->
                        if (event == Lifecycle.Event.ON_RESUME) {
                            permissionsState.launchMultiplePermissionRequest()
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)
                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                    }
                }
            )

            /**
             *
             */

            sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
            stepsSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

            if (stepsSensor == null) {
                Toast.makeText(this, "No Step Counter Sensor !", Toast.LENGTH_SHORT).show()
            } else {
                sensorManager.registerListener(this, stepsSensor, SensorManager.SENSOR_DELAY_FASTEST)
                Log.d("센서: ", stepsSensor.toString())
            }


            StepTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        Log.d("변경 이벤트 발생: ", event.toString())
        running ++
        Log.d("구분선", "---------------------------------------------------------------------------")
        Log.d("걸음 수: ", running.toString())
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d("센서 이벤트 발생: ", sensor.toString())
        Log.d("가속도 이벤트 발생: ", accuracy.toString())
    }

}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    StepTheme {
        Greeting("Android")
    }
}