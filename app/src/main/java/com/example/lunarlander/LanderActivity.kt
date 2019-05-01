package com.example.lunarlander

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_lander.*


class LanderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lander)

        play_button.setOnClickListener { playClick(play_button) }
        stop_button.setOnClickListener { stopClick(stop_button) }
    }


    fun playClick(view: View) {
       mycanvas.startGame()
    }

    fun stopClick(view: View) {
       mycanvas.stopGame()
    }
}
