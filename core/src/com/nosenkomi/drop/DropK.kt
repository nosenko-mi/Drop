package com.nosenkomi.drop

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.TimeUtils

class DropK : ApplicationAdapter() {
    private var dropImage: Texture? = null
    private var bucketImage: Texture? = null
    private var dropSound: Sound? = null
    private var rainMusic: Music? = null
    private var camera: OrthographicCamera? = null
    private var batch: SpriteBatch? = null
    private var bucket: Rectangle? = null
    private var raindrops: Array<Rectangle>? = null
    private var lastDropTime: Long = 0
    private val touchPos = Vector3()
    override fun create() {
        loadAssets()
        batch = SpriteBatch()
        camera = OrthographicCamera()
        camera!!.setToOrtho(false, WIDTH.toFloat(), HEIGHT.toFloat())
        createBucket()
        raindrops = Array()
        spawnRaindrop()
    }

    override fun render() {
        ScreenUtils.clear(0f, 0f, 0.2f, 1f)

        // tell the camera to update its matrices.
        camera!!.update()

        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        batch!!.projectionMatrix = camera!!.combined

        // begin a new batch and draw the bucket and
        // all drops
        batch!!.begin()
        batch!!.draw(bucketImage, bucket!!.x, bucket!!.y)
        for (raindrop in raindrops!!) {
            batch!!.draw(dropImage, raindrop.x, raindrop.y)
        }
        batch!!.end()

        handleTouch()
        handleKeyPressed()
        if (bucket!!.x < 0) bucket!!.x = 0f
        if (bucket!!.x > WIDTH - 64) bucket!!.x = (WIDTH - 64).toFloat()

        // check if we need to create a new raindrop
        if (TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnRaindrop()

        // move the raindrops, remove any that are beneath the bottom edge of
        // the screen or that hit the bucket. In the latter case we play back
        // a sound effect as well.
        val iter: MutableIterator<Rectangle> = raindrops!!.iterator()
        while (iter.hasNext()) {
            val raindrop = iter.next()
            raindrop.y -= 200 * Gdx.graphics.deltaTime
            if (raindrop.y + 64 < 0) iter.remove()
            if (raindrop.overlaps(bucket)) {
                dropSound!!.play()
                iter.remove()
            }
        }
    }

    override fun dispose() {
        dropImage!!.dispose()
        bucketImage!!.dispose()
        dropSound!!.dispose()
        rainMusic!!.dispose()
        batch!!.dispose()
    }

    private fun spawnRaindrop() {
        val raindrop = Rectangle()
        raindrop.x = MathUtils.random(0, WIDTH - 64).toFloat()
        raindrop.y = HEIGHT.toFloat()
        raindrop.width = 64f
        raindrop.height = 64f
        raindrops!!.add(raindrop)
        lastDropTime = TimeUtils.nanoTime()
    }

    private fun handleKeyPressed() {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) bucket!!.x -= 200 * Gdx.graphics.deltaTime
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) bucket!!.x += 200 * Gdx.graphics.deltaTime
    }

    private fun handleTouch() {
        if (Gdx.input.isTouched) {
            touchPos[Gdx.input.x.toFloat(), Gdx.input.y.toFloat()] = 0f
            camera!!.unproject(touchPos)
            bucket!!.x = touchPos.x - 64 / 2
        }
    }

    private fun createBucket() {
        bucket = Rectangle()
        bucket!!.x = (WIDTH / 2 - 64 / 2).toFloat()
        bucket!!.y = 20f
        bucket!!.width = 64f
        bucket!!.height = 64f
    }

    private fun loadAssets() {
        // load the images for the droplet and the bucket, 64x64 pixels each
        dropImage = Texture(Gdx.files.internal("drop.png"))
        bucketImage = Texture(Gdx.files.internal("bucket.png"))

        // load the drop sound effect and the rain background "music"
        dropSound = Gdx.audio.newSound(Gdx.files.internal("waterdrop.wav"))
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"))

        // start the playback of the background music immediately
        rainMusic?.setLooping(true)
        rainMusic?.play()
    }

    companion object {
        private const val WIDTH = 480
        private const val HEIGHT = 800
    }
}