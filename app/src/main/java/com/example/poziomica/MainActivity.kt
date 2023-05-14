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
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // Nie jest to wymagane, ale można dodać logikę, która zostanie uruchomiona, gdy dokładność akcelerometru się zmieni
            }

            override fun onSensorChanged(event: SensorEvent?) {
                if (event != null) {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastUpdate > 1000) { // opóźnienie wynoszące 1 sekundę
                        val x = event.values[0] // wartość akcelerometru wzdłuż osi x
                        val y = event.values[1] // wartość akcelerometru wzdłuż osi y
                        val z = event.values[2] // wartość akcelerometru wzdłuż osi z

                        // dodaj logikę, która będzie wykonywana przy zmianie wartości akcelerometru
                        textView.setText("x: ${"%.3f".format(x)}, y: ${"%.3f".format(y)}, z: ${"%.3f".format(z)}") // ustawienie wartości w TextView z dokładnością do 3 miejsc po przecinku

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