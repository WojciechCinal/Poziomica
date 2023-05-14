package com.example.poziomica

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private lateinit var sensorEventListener: SensorEventListener

    private lateinit var textView: TextView // deklaracja TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView = findViewById(R.id.myText) // inicjalizacja TextView

        // Inicjalizacja SensorManagera i akcelerometru
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // Definicja obiektu SensorEventListener
        sensorEventListener = object : SensorEventListener {
            private var lastUpdate = System.currentTimeMillis()
            private var xAcc = 0.0f
            private var yAcc = 0.0f
            private var zAcc = 0.0f
            private val alpha = 0.8f

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // Nie jest to wymagane, ale można dodać logikę, która zostanie uruchomiona, gdy dokładność akcelerometru się zmieni
            }

            override fun onSensorChanged(event: SensorEvent?) {
                if (event != null) {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastUpdate > 500) { // opóźnienie wynoszące 0,5 sekundy
                        xAcc = alpha * xAcc + (1 - alpha) * event.values[0] // wartość akcelerometru wzdłuż osi x
                        yAcc = alpha * yAcc + (1 - alpha) * event.values[1] // wartość akcelerometru wzdłuż osi y
                        zAcc = alpha * zAcc + (1 - alpha) * event.values[2] // wartość akcelerometru wzdłuż osi z

                        val gX = -xAcc / SensorManager.GRAVITY_EARTH
                        val gY = yAcc / SensorManager.GRAVITY_EARTH
                        val gZ = zAcc / SensorManager.GRAVITY_EARTH

                        val gForce = Math.sqrt((gX * gX + gY * gY + gZ * gZ).toDouble())
                        textView.text = "x: ${"%.3f".format(gX)}, y: ${"%.3f".format(gY)}, z: ${"%.3f".format(gZ)}"

                        // Wykrycie czy telefon jest ustawiony poziomo lub pionowo
                        if (gForce > 1.0) {
                            if (gZ > gX) {
                                textView.text = "Orientacja pionowa:\n" +
                                        "x: ${"%.3f".format(gX)}, y: ${"%.3f".format(gY)}"
                            } else {
                                textView.text = "Orientacja pozioma:\n" +
                                        "y: ${"%.3f".format(gY)}, z: ${"%.3f".format(gZ)}"
                            }
                        }

                        lastUpdate = currentTime // zapisanie czasu ostatniego pomiaru
                    }
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_UI)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(sensorEventListener)
    }

}
