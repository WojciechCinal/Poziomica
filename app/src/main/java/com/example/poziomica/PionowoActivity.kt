package com.example.poziomica

import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class PionowoActivity: AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var kwadrat: TextView
    private var ostatniPomiar: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pionowo)

        //Funkcja, która nie pozwala na uruchomienie ciemnego trybu ekranu
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        kwadrat = findViewById(R.id.kwadrat_pomiarowy)

        setUpSensorStuff()
    }

    private fun setUpSensorStuff() {
        // Utwórz menedżer sensorów
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        // Określam, że używanym sensorem jak akcelerometr
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also { accelerometer ->
            sensorManager.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        // Sprawdza, który sensor został wywołany
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            //Funkcja sprawia, że są 3 pomiary w ciągu 1 sekundy
            val curTime = System.currentTimeMillis()
            if (curTime - ostatniPomiar >= 333) {
                ostatniPomiar = curTime

                // goraDol = Przechylenie telefonu w górę(10), płasko (0), do góry nogami (-10)
                val goraDol = event.values[2]

                // lewoPrawo = Przechylenie telefonu w lewo(10) i prawo(-10)
                val lewoPrawo = event.values[0]

                // Obracanie i przesuwanie kwadratu na podstawie przechylenia telefonu
                kwadrat.apply {
                    rotationX = goraDol * 3f
                    rotationY = lewoPrawo * 3f
                    rotation = -lewoPrawo
                    translationX = lewoPrawo * -10
                    translationY = goraDol * 10
                }

                // Zmiana koloru kwadratu oraz tekstu, jeśli jest całkowicie na płaskiej powierzchni
                val color = if (goraDol.toInt() == 0 && lewoPrawo.toInt() == 0) Color.GREEN else Color.RED
                kwadrat.setBackgroundColor(color)

                kwadrat.text = "lewo/prawo ${"%.1f".format(lewoPrawo)},\n góra/dół ${"%.1f".format(goraDol)}"

            }
        }
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }

    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        super.onDestroy()
    }
}