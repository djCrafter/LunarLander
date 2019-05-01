package com.example.lunarlander

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.AttributeSet
import android.view.MotionEvent
import stanford.androidlib.graphics.*


class LanderCanvas(context: Context, attrs: AttributeSet) : GCanvas(context, attrs)
{
    companion object {
        private const val FRAMES_PER_SECOND = 30
        private const val MAX_SAFE_LANDING_VELOCITY = 7.0f
        private const val MAX_ALTITUDE = -1000f
        private const val GRAVITY_ACCELERATION = .5f
        private const val THRUST_ACCELERATION = -.3f
    }

    private var boomFrames = 0
    private lateinit var rocketImage: Bitmap
    private var rocketImageThrusts = ArrayList<Bitmap>()
    private var rocketBoomImages = ArrayList<Bitmap>()
    private lateinit var rocket: GSprite
    private lateinit var moonSurface : GSprite
    private lateinit var label : GLabel

    private var isGameOver = false
    private var isRocketDestroyed = false

    override fun init() {
        backgroundColor = GColor.BLACK
        //GSprite.setDebug(true)

        loadSurfaceImage()
        loadRocketImage()
        loadRocketThrusts()
        loadRocketBoomImage()


        setOnTouchListener { _, event ->
            handleTouchEvent(event)
            true }
    }



    private fun loadSurfaceImage() {
        var moonSurfaceImage = BitmapFactory.decodeResource(resources, R.drawable.moonsurface)
        moonSurfaceImage = moonSurfaceImage.scaleToWidth(this.width.toFloat())

        moonSurface = GSprite(moonSurfaceImage)
        moonSurface.bottomY = this.height.toFloat()
        moonSurface.collisionMarginTop = moonSurface.height / 3
        add(moonSurface)
    }

    private fun loadRocketImage() {
        rocketImage = BitmapFactory.decodeResource(resources, R.drawable.rocketship1)
        rocketImage = rocketImage.scaleToWidth(this.width / 10f)

        rocket = GSprite(rocketImage)
        rocket.rightX = this.width.toFloat() / 2
        rocket.velocityY = 10f

        rocket.accelerationY = GRAVITY_ACCELERATION
        add(rocket)

    //    Log.e("Crafter", rocket.bottomY.toString())
    }

    private fun loadRocketThrusts() {
        var rocketImageThrust1 = BitmapFactory.decodeResource(resources, R.drawable.rocketship1thrust1)
        var rocketImageThrust2 = BitmapFactory.decodeResource(resources, R.drawable.rocketship1thrust2)
        var rocketImageThrust3 = BitmapFactory.decodeResource(resources, R.drawable.rocketship1thrust3)
        var rocketImageThrust4 = BitmapFactory.decodeResource(resources, R.drawable.rocketship1thrust4)

        rocketImageThrusts.add(rocketImageThrust1)
        rocketImageThrusts.add(rocketImageThrust2)
        rocketImageThrusts.add(rocketImageThrust3)
        rocketImageThrusts.add(rocketImageThrust4)

       for(i in rocketImageThrusts.indices) {
           rocketImageThrusts[i] = rocketImageThrusts[i].scaleToWidth(this.width / 10f)
       }
    }

    private fun loadRocketBoomImage() {
        var rocketBoomImage1 = BitmapFactory.decodeResource(resources, R.drawable.rocketship1boom1)
        var rocketBoomImage2  = BitmapFactory.decodeResource(resources, R.drawable.rocketship1boom2)
        var rocketBoomImage3 = BitmapFactory.decodeResource(resources, R.drawable.rocketship1boom3)


        rocketBoomImages.add(rocketBoomImage1)
        rocketBoomImages.add(rocketBoomImage2)
        rocketBoomImages.add(rocketBoomImage3)


        for(i in rocketBoomImages.indices) {
            rocketBoomImages[i] = rocketBoomImages[i].scaleToWidth(this.width / 10f)
        }
    }

    private fun handleTouchEvent(event: MotionEvent) {
        if(!isGameOver) {
            val x = event.x
            val y = event.y
            if (event.action == MotionEvent.ACTION_DOWN) {
                rocket.accelerationY = THRUST_ACCELERATION
                rocket.bitmaps = rocketImageThrusts
                rocket.framesPerBitmap = FRAMES_PER_SECOND / 6
            } else if (event.action == MotionEvent.ACTION_UP) {
                rocket.accelerationY = GRAVITY_ACCELERATION
                rocket.bitmap = rocketImage
            }
        }
    }

    private fun tick() {
        rocket.update()
        doCollisions()
        goAway()

        if(isGameOver && isRocketDestroyed){
            if(boomFrames >= FRAMES_PER_SECOND - 5) {
                animationStop()
                remove(rocket)
            }
            boomFrames++
        }
    }


    private fun goAway() {
        if(rocket.y <= MAX_ALTITUDE){
            animationStop()
            isGameOver = false
            displayTheMessage("You LOSE!!!")
        }
    }

    private fun doCollisions() {
        if(rocket.collidesWith(moonSurface)) {

            if(!isGameOver) {
                if (rocket.velocityY <= MAX_SAFE_LANDING_VELOCITY) {
                    displayTheMessage("You WIN!!!")
                } else {
                    rocket.bitmaps = rocketBoomImages
                    rocket.framesPerBitmap = FRAMES_PER_SECOND / 3
                    isRocketDestroyed = true
                    displayTheMessage("You LOSE!!!")
                }
                isGameOver = true
            }
            rocket.velocityY = 0f
            rocket.accelerationY = 0f
        }
    }


    private fun displayTheMessage(message: String) {
         label = GLabel(message)
         label.color = GColor.RED
         label.fontSize = 50f
         label.rightX = (this.width.toFloat() / 2) + (label.width / 2)
         label.bottomY = this.height.toFloat() / 2
         add(label)
    }


    fun startGame() {
        if(isGameOver)
        {
            animationStop()
            
            boomFrames = 0

            if(isRocketDestroyed) {
                loadRocketImage()
                isRocketDestroyed = false
            }

            rocket.y = 0f
            rocket.velocityY = 10f

            remove(label)
            isGameOver = false
        }
        else {
            animate(FRAMES_PER_SECOND) {
                tick()
            }
        }
    }

    fun stopGame() {
        animationStop()
    }
}