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
import kotlin.math.abs

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

                // Obracanie i przesuwanie kwadratu na podstawie przechylenia telefonu
                kwadrat.apply {
                    rotationX = goraDol * 3f
                    translationY = goraDol * 10
                }

                // Zmiana koloru kwadratu oraz tekstu, jeśli jest całkowicie na płaskiej powierzchn
                val color = if (abs(goraDol) <= 0.16) Color.GREEN else Color.RED
                kwadrat.setBackgroundColor(color)

                kwadrat.text = "góra/dół ${"%.2f".format(goraDol)}"

               /*
               Oblicznie kąta nachylenia mieszczącego się w normach budowlanych:
               odchylenie od kierunku pionowego ściany nie więcej niż 3 mm na długości 1 m

               Długość pierwszej przyprostokątnej (a): 200 cm
               Długość drugiej przyprostokątnej (b): 0.5 cm

               Aby obliczyć kąt α:
               α = arctan(b / a) = arctan(0.5 / 200) ≈ 0.14 stopnia

               10 jednostek --> 90 stopni
               x jednostek --> 0.14 stopnia

               (x / 10) = (0.14 / 90)
               x = (0.14 / 90) * 10
               x ≈ 0.1556 ≈ 0.16 jednostek*/
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