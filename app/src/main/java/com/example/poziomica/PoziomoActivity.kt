package com.example.poziomica

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Surface
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate

class PoziomoActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var kwadrat: TextView
    private lateinit var info: TextView
    private lateinit var kolo: ImageView
    private var ostatniPomiar: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.poziomo)

        //Funkcja, która nie pozwala na uruchomienie ciemnego trybu ekranu
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        kwadrat = findViewById(R.id.kwadrat_pomiarowy)
        kolo = findViewById(R.id.kolo_pomiarowe)
        info = findViewById(R.id.info)

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
                val orientation = windowManager.defaultDisplay.rotation
                if (orientation == Surface.ROTATION_90 || orientation == Surface.ROTATION_270) {
                // lewoPrawo = Przechylenie telefonu w lewo(180) i prawo(-180)
                val lewoPrawo = (event.values[1].toDouble() * 18) + 6.6

                // goraDol = Przechylenie telefonu w górę(180), płasko (0), do góry nogami (-180)
                val goraDol = (event.values[0].toDouble() * 18) + 1.0

                // Obracanie i przesuwanie kwadratu na podstawie przechylenia telefonu
                kolo.apply {
                    translationX = (lewoPrawo * -2).toFloat()
                    translationY = (goraDol * 2).toFloat()
                }


                kwadrat.text = "góra/dół ${"%.1f".format(goraDol)}°,\n lewo/prawo ${"%.1f".format(lewoPrawo)}°"
                if (goraDol >= -0.8 && goraDol <= 0.8 && lewoPrawo >= -0.8 && lewoPrawo <= 0.8){
                    info.text = "Płaszczyzna mieści się w normach"
                }else{
                    info.text = "Płaszczyzna nie jest w normie"
                }
                if(goraDol == 0.0 && lewoPrawo == 0.0){
                    info.text="Idealnie w poziomie"
                }

            }else{
                    // lewoPrawo = Przechylenie telefonu w lewo(180) i prawo(-180)
                    val lewoPrawo = (event.values[0].toDouble() * 18) + 1.0

                    // goraDol = Przechylenie telefonu w górę(180), płasko (0), do góry nogami (-180)
                    val goraDol = (event.values[1].toDouble() * 18) + 6.6

                    // Obracanie i przesuwanie kwadratu na podstawie przechylenia telefonu
                    kolo.apply {
                        translationX = (lewoPrawo * -2).toFloat()
                        translationY = (goraDol * 2).toFloat()
                    }


                    kwadrat.text = "góra/dół ${"%.1f".format(goraDol)}°,\n lewo/prawo ${"%.1f".format(lewoPrawo)}°"
                    if (goraDol >= -0.8 && goraDol <= 0.8 && lewoPrawo >= -0.8 && lewoPrawo <= 0.8){
                        info.text = "Płaszczyzna mieści się w normach"
                    }else{
                        info.text = "Płaszczyzna nie jest w normie"
                    }
                    if(goraDol == 0.0 && lewoPrawo == 0.0){
                        info.text="Idealnie w poziomie"
                    }

            }
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