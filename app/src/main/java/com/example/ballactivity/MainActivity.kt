package com.example.ballactivity

import android.app.Activity
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.log

class MainActivity : Activity(),SensorEventListener {
    private var Posx = 0f
    private var Posy = 0f

    private lateinit var sensorManager: SensorManager
    private var AccSensor:Sensor? = null
    private lateinit var ball:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ball = findViewById(R.id.ball)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        AccSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    private var maxStartPostion = (ball.parent as ConstraintLayout).width-ball.width
    private var maxHeightPosition = (ball.parent as ConstraintLayout).height - ball.height
    private var startRadio:Float = 0f
    private var heightRadio:Float = 0f

    override fun onSensorChanged(event: SensorEvent?){
        val x:Float
        val y:Float


        if (event?.sensor?.type === Sensor.TYPE_ACCELEROMETER){
            x = event.values[0]
            y = event.values[1]
            ballPosition(-x,y)

        }
    }

    private fun ballPosition(deltax:Float,deltay:Float){
        val ScaleFactor = 5
        Posx += deltax*ScaleFactor
        Posy += deltay*ScaleFactor

        val layoutParams = ball.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.marginStart = Posx.toInt().coerceIn(0,(ball.parent as ConstraintLayout).width-ball.width)
        layoutParams.topMargin = Posy.toInt().coerceIn(0,(ball.parent as ConstraintLayout).height-ball.height)
        ball.layoutParams = layoutParams
        startRadio = (layoutParams.marginStart/maxStartPostion).toFloat()
        heightRadio = (layoutParams.topMargin/maxHeightPosition).toFloat()

        Log.d("Mainactivity","$startRadio")
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }
    override fun onResume(){
        super.onResume()
        sensorManager.registerListener(this,AccSensor,SensorManager.SENSOR_DELAY_GAME)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

}