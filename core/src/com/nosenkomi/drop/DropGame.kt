package com.nosenkomi.drop

import com.badlogic.gdx.Game
import com.badlogic.gdx.graphics.g2d.BitmapFont

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.nosenkomi.screen.MainMenuScreen


class DropGame: Game() {

    public lateinit var batch: SpriteBatch
    public lateinit var font: BitmapFont

    override fun create() {
        batch = SpriteBatch()
        // use LibGDX's default Arial font
        font = BitmapFont()
        this.setScreen(MainMenuScreen(this))
    }

    override fun render() {
        //  Without this call, the Screen that you set in the create() method will not be rendered
        //  if you override the render method in your Game class!
        super.render()
    }

    override fun dispose() {
        this.getScreen().dispose()
        batch.dispose()
        font.dispose()
    }
}