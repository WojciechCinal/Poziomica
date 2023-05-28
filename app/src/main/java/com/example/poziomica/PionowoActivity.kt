package com.example.poziomica

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import android.os.Vibrator

class PionowoActivity: AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var kwadrat: TextView
    private lateinit var info: TextView
    private lateinit var kolo: ImageView
    private var ostatniPomiar: Long = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pionowo)

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
            //Funkcja sprawia, że są pomiary co 600 milisekund
            val curTime = System.currentTimeMillis()
            if (curTime - ostatniPomiar >= 600) {
                ostatniPomiar = curTime


                // goraDol = Przechylenie telefonu ekranem w górę (-90), prostopadle do horyzontu (0), ekranem w dół (90)
                val goraDol = (event.values[2].toDouble() * 9)

                // Przesuwanie koła na podstawie przechylenia telefonu
                kolo.apply {
                    translationY = (goraDol * 4).toFloat()
                }

                kwadrat.text = "góra/dół ${"%.1f".format(-goraDol)}°"
                if (goraDol >= -0.8 && goraDol <= 0.8){
                    info.text = "Płaszczyzna mieści się w normach"
                    val vibrator: Vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    val pattern = longArrayOf(50, 150,50)
                    // Wywołaj wibrację
                    vibrator.vibrate(pattern, -1)
                }else{
                    info.text = "Płaszczyzna nie jest w normie"
                }
                if(goraDol == 0.0){
                    info.text="Idealnie w poziomie"
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