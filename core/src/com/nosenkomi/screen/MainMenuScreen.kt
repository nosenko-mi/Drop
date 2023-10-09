package com.nosenkomi.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.ScreenUtils
import com.nosenkomi.Constants
import com.nosenkomi.drop.DropGame


class MainMenuScreen(val game: DropGame) : Screen {
    private var camera: OrthographicCamera = OrthographicCamera()

    init {
        camera.setToOrtho(false, Constants.WIDTH, Constants.HEIGHT)
    }

    override fun render(delta: Float) {
        ScreenUtils.clear(0f, 0f, 0.2f, 1f)

        camera.update()
        game.batch.projectionMatrix = camera.combined

        game.batch.begin()
        game.font.draw(game.batch, "Welcome to Drop!", 100f, 150f)
        game.font.draw(game.batch, "Tap anywhere to begin", 100f, 100f)
        game.batch.end()

        if (Gdx.input.isTouched) {
            Gdx.app.log("MainMenuScreen", "Touch detected")
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