package com.nosenkomi.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.TimeUtils
import com.nosenkomi.Constants
import com.nosenkomi.drop.DropGame


class GameScreen(val game: DropGame) : Screen {
    var dropImage: Texture
    var bucketImage: Texture
    var dropSound: Sound
    var rainMusic: Music
    var camera: OrthographicCamera
    var bucket: Rectangle
    var raindrops: Array<Rectangle>
    var lastDropTime: Long = 0
    var dropIntervalTime: Long = 1000000000 // nano
    var dropsGathered = 0

    init {

        // load the images for the droplet and the bucket, 64x64 pixels each
        dropImage = Texture(Gdx.files.internal("drop.png"))
        bucketImage = Texture(Gdx.files.internal("bucket.png"))

        // load the drop sound effect and the rain background "music"
        dropSound = Gdx.audio.newSound(Gdx.files.internal("waterdrop.wav"))
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"))
        rainMusic.isLooping = true

        // create the camera and the SpriteBatch
        camera = OrthographicCamera()
        camera.setToOrtho(false, Constants.WIDTH, Constants.HEIGHT)

        // create a Rectangle to logically represent the bucket
        bucket = Rectangle()

        bucket.x = (Constants.HEIGHT / 2 - 64 / 2) // center the bucket horizontally
        bucket.y = 20F // bottom left corner of the bucket is 20 pixels above
        // the bottom screen edge
        bucket.width = Constants.BUCKET_WIDTH
        bucket.height = Constants.BUCKET_HEIGHT

        // create the raindrops array and spawn the first raindrop
        raindrops = Array<Rectangle>()
        spawnRaindrop()
    }

    private fun spawnRaindrop() {
        val raindrop = Rectangle()
        raindrop.x = MathUtils.random(0f, Constants.WIDTH - 128f)
        raindrop.y = Constants.HEIGHT
        raindrop.width = Constants.RAINDROP_WIDTH
        raindrop.height = Constants.RAINDROP_HEIGHT
        raindrops.add(raindrop)
        lastDropTime = TimeUtils.nanoTime()

        if (dropIntervalTime > 400000000){
            dropIntervalTime -= 10000000
        }

    }

    override fun render(delta: Float) {
        // clear the screen with a dark blue color. The
        // arguments to clear are the red, green
        // blue and alpha component in the range [0,1]
        // of the color to be used to clear the screen.
        ScreenUtils.clear(0f, 0f, 0.2f, 1f)

        // tell the camera to update its matrices.
        camera.update()

        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        game.batch.projectionMatrix = camera.combined

        // begin a new batch and draw the bucket and
        // all drops
        game.batch.begin()
        game.font.draw(game.batch, "Drops Collected: $dropsGathered", 20f, 780f)
        game.batch.draw(bucketImage, bucket.x, bucket.y, bucket.width, bucket.height)
        for (raindrop in raindrops) {
            game.batch.draw(dropImage, raindrop.x, raindrop.y)
        }
        game.batch.end()

        // process user input
        handleTouch()
        handleKeyPressed()

        // make sure the bucket stays within the screen bounds
        checkBucketPosition()

        // check if we need to create a new raindrop
        if (TimeUtils.nanoTime() - lastDropTime > dropIntervalTime) spawnRaindrop()

        // move the raindrops, remove any that are beneath the bottom edge of
        // the screen or that hit the bucket. In the later case we increase the
        // value our drops counter and add a sound effect.
        var iter = raindrops.iterator()
        while (iter.hasNext()) {
            val raindrop: Rectangle = iter.next()
            raindrop.y -= 200 * Gdx.graphics.deltaTime
            if (raindrop.y + 64 < 0) {
                iter.remove()
                stopGame()
            }
            if (raindrop.overlaps(bucket)) {
                dropsGathered++
                dropSound.play()
                iter.remove()
            }
        }
    }

    private fun handleKeyPressed() {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) bucket.x -= 200 * Gdx.graphics.deltaTime
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) bucket.x += 200 * Gdx.graphics.deltaTime
    }

    private fun handleTouch() {
        if (Gdx.input.isTouched) {
            val touchPos = Vector3()
            touchPos[Gdx.input.x.toFloat(), Gdx.input.y.toFloat()] = 0f
            camera.unproject(touchPos)
            bucket.x = touchPos.x - 64 / 2
        }
    }

    private fun checkBucketPosition(){
        if (bucket.x < 0) bucket.x = 0F
        if (bucket.x > Constants.HEIGHT - Constants.BUCKET_HEIGHT) bucket.x = (Constants.HEIGHT - Constants.BUCKET_HEIGHT)
    }

    private fun stopGame(){
        game.screen = GameOverScreen(game, dropsGathered)
        dispose()
    }

    override fun show() {
        // start the playback of the background music
        // when the screen is shown
        rainMusic.play()
    }

    override fun resize(width: Int, height: Int) {}

    override fun hide() {}
    override fun pause() {}
    override fun resume() {}
    override fun dispose() {
        dropImage.dispose()
        bucketImage.dispose()
        dropSound.dispose()
        rainMusic.dispose()
    }
}