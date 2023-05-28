package com.example.poziomica

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Vibrator
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat

class PoziomoActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var kwadrat: TextView
    private lateinit var info: TextView
    private lateinit var kolo: ImageView
    private var ostatniPomiar: Long = 0
    private lateinit var buttonHome: ImageButton
    private lateinit var buttonZamiana: ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.poziomo)

        //Funkcja, która nie pozwala na uruchomienie ciemnego trybu ekranu
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        kwadrat = findViewById(R.id.kwadrat_pomiarowy)
        kolo = findViewById(R.id.kolo_pomiarowe)
        info = findViewById(R.id.info)

        buttonHome = findViewById(R.id.button_pause)
        buttonZamiana = findViewById(R.id.buton_zamiana)

        buttonHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        buttonZamiana.setOnClickListener {
            val intent = Intent(this, PionowoActivity::class.java)
            startActivity(intent)
        }

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

                // lewoPrawo = Przechylenie telefonu w lewo(90) i prawo(-90) + korekcja aparatu
                val lewoPrawo = (event.values[0].toDouble() * 9) + 0.3

                // goraDol = Przechylenie telefonu: przedni aparat wyżej(90), płasko - ekranem w stronę sufitu (0),
                // pasek nawigacyjny wyżej (-90) + korekcja aparatu
                val goraDol = (event.values[1].toDouble() * 9) + 3.7

                // Przesuwanie koła na podstawie przechylenia telefonu
                kolo.apply {
                    translationX = (lewoPrawo * -4).toFloat()
                    translationY = (goraDol * 4).toFloat()
                }


                kwadrat.text = "góra/dół ${"%.1f".format(goraDol)}°,\n lewo/prawo ${
                    "%.1f".format(-lewoPrawo)
                }°"
                if (goraDol >= -0.8 && goraDol <= 0.8 && lewoPrawo >= -0.8 && lewoPrawo <= 0.8) {
                    info.text = "Płaszczyzna mieści się w normach"
                    val vibrator: Vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    val pattern = longArrayOf(50, 150, 50)
                    // Wywołaj wibrację
                    vibrator.vibrate(pattern, -1)
                    info.background.setColorFilter(ContextCompat.getColor(this, R.color.green), PorterDuff.Mode.SRC_ATOP)
                } else {
                    info.text = "Płaszczyzna nie jest w normie"
                    info.background.setColorFilter(ContextCompat.getColor(this, R.color.red), PorterDuff.Mode.SRC_ATOP)
                }

                if (goraDol == 0.0 && lewoPrawo == 0.0) {
                    info.text = "Idealnie w poziomie"
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