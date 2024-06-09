package com.example.ballactivity

import android.app.Activity
import android.graphics.Color
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
import java.time.LocalDate
import kotlin.math.log

class MainActivity : Activity(),SensorEventListener {
    private var Posx = 0f
    private var Posy = 0f

    private lateinit var sensorManager: SensorManager
    private var AccSensor:Sensor? = null
    private lateinit var ball:ImageView

     //top
    private var deviceWidth = 0
    private var deviceHeight = 0
    private var marginX01 = 0f..0.1f
    private var marginX07 = 0.11.toFloat() ..0.7.toFloat()
    private var marginX1 = 0.71.toFloat()..1f
    //Left
    private var marginY01 = 0f..0.1f
    private var marginY03 = 0.11.toFloat()..0.3.toFloat()
    private var marginY06 = 0.31.toFloat() ..0.6.toFloat()
    private var marginY1 = 0.61.toFloat() ..1.0.toFloat()
    //bottom
    private var marginX08 = 0f..0.8f
    private var marginX10 = 0.81f..1f
    //Right
    private var marginY02 = 0f..0.2f
    private var marginY04 = 0.21f..0.4f
    private var marginY09 = 0.41f..0.9f
    private var marginY10 = 0.91f..1f

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

    private var currentEdge:Edge = Edge.NONE

    enum class Edge {
        NONE,LEFT,RIGHT,BOTTOM,TOP
    }

    override fun onSensorChanged(event: SensorEvent?){
        val x:Float
        val y:Float


        if (event?.sensor?.type === Sensor.TYPE_ACCELEROMETER){
                x = event.values[0]
            y = event.values[1]
            ballPosition(-x,y)
        }
    }

    private fun ballPosition(deltax:Float, deltay:Float){
        val ScaleFactor = 5
        Posx += deltax*ScaleFactor
        Posy += deltay*ScaleFactor

        val layoutParams = ball.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.marginStart = Posx.toInt().coerceIn(0,(ball.parent as ConstraintLayout).width-ball.width)
        layoutParams.topMargin = Posy.toInt().coerceIn(0,(ball.parent as ConstraintLayout).height-ball.height)
        ball.layoutParams = layoutParams

        deviceWidth = (ball.parent as ConstraintLayout).width-ball.width
        val positionX = layoutParams.marginStart/deviceWidth.toFloat()
        deviceHeight = (ball.parent as ConstraintLayout).height-ball.height
        val positionY = layoutParams.topMargin/deviceHeight.toFloat()

        val newedge = when{
            positionX <=0 -> Edge.LEFT
            positionX >= 1 -> Edge.RIGHT
            positionY <= 0 ->Edge.TOP
            positionY >= 1 -> Edge.BOTTOM
            else -> {Edge.NONE}
        }
        Log.i("$newedge","$positionY")
        if (newedge != currentEdge){
            currentEdge = newedge
            val coledge = when{
                //red
                positionX in marginX01 && positionY.toInt() == 0-> "#EE4E4E"
                //blue
                positionX in marginX07 && positionY.toInt() == 0-> "#6DC5D1"
                //yellow
                positionX in marginX1 && positionY.toInt() == 0 -> "#FFDA78"
                //red
                positionY in marginY01 && positionX.toInt() == 0 -> "#EE4E4E"
                //grey
                positionY in marginY03 && positionX.toInt() == 0 -> "#6B8A7A"
                //orange
                positionY in marginY06 && positionX.toInt() == 0-> "#FF7F3E"
                //blue
                positionY in marginY1 && positionX.toInt() == 0 -> "#6DC5D1"
                //blue
                positionX in marginX08 && positionY.toInt() == 1 -> "#6DC5D1"
                //orange
                positionX in marginX10 && positionY.toInt() == 1 -> "#FF7F3E"
                //gray
                positionY in marginY02 && positionX.toInt() == 1 -> "#686D76"
                //blue
                positionY in marginY04 && positionX.toInt() == 1 -> "#6DC5D1"
                //parple
                positionY in marginY09 && positionX.toInt() == 1 -> "#D2649A"
                //orange
                positionY in marginY10 && positionX.toInt() == 1 -> "#FF7F3E"

                else ->"#EE4E4E"
            }
            ball.setColorFilter(Color.parseColor(coledge.toString()))
            Log.d("$currentEdge","$positionY")
        }

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