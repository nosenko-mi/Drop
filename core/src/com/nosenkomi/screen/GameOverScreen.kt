package com.nosenkomi.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.ScreenUtils
import com.nosenkomi.Constants
import com.nosenkomi.drop.DropGame

class GameOverScreen(val game: DropGame, val score: Int) : Screen {


    private var camera: OrthographicCamera = OrthographicCamera()

    init {
        camera.setToOrtho(false, Constants.WIDTH, Constants.HEIGHT)
    }

    override fun render(delta: Float) {
        ScreenUtils.clear(0f, 0f, 0.2f, 1f)
        val centerYPos = Constants.HEIGHT/2

        camera.update()
        game.batch.projectionMatrix = camera.combined

        game.batch.begin()
        game.font.draw(game.batch, "Game over", Constants.WIDTH/3, centerYPos)
        game.font.draw(game.batch, "You scored $score", Constants.WIDTH/3, centerYPos - 50f)
        game.font.draw(game.batch, "Tap restart", Constants.WIDTH/3, centerYPos - 100f)
        game.batch.end()

        if (Gdx.input.isTouched) {
            game.screen = GameScreen(game)
            dispose()
        }
    }

    override fun show() {

    }

    override fun resize(width: Int, height: Int) {
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun hide() {
    }

    override fun dispose() {
    }
}