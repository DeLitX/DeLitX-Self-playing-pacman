package window

import framework.obj.Pair
import objects.GameWorld
import java.awt.Canvas
import java.awt.Color
import java.awt.Graphics2D

class Game : Canvas(), Runnable {

    private val gameWorld = GameWorld()
    val bs by lazy { bufferStrategy }

    private var running = false

    lateinit var g: Graphics2D

    fun startGame() {
        if (running) return

        running = true
        Thread(this).start()
    }

    override fun run() {
        initGame()

        requestFocus()

        val numTicks = 60.0
        val nanoSeconds = 1000000000 / numTicks

        var currTime = System.nanoTime()
        var delta = 0.0
        var gameTimer = System.currentTimeMillis()

        while (running) {
            val newTime = System.nanoTime()

            delta += (newTime - currTime) / nanoSeconds
            currTime = newTime

            while (delta >= 1) {
                if (!gameWorld.gameOver) {
                    tick()
                }

                render()

                delta--
            }

            if (System.currentTimeMillis() - gameTimer > 1000) {
                gameTimer += 1000
            }
        }
    }

    private fun initGame() {
        createBufferStrategy(2)
        loadLevel()
    }

    private fun loadLevel() {
        gameWorld.loadLevelFromImage("res/PacManLevel.png")
    }

    private fun tick() {
        gameWorld.tick()
    }

    private fun render() {
        g = bs.drawGraphics as Graphics2D

        renderGame(g)

        g.dispose()
        bs.show()
    }

    private fun renderGame(g: Graphics2D) {
        renderBackground(g)
        renderWorld(g)
        renderEndGameResult(g)
    }

    private fun renderBackground(g: Graphics2D) {
        g.color = Color.black
        g.fillRect(0, 0, width, height)
    }

    private fun renderWorld(g: Graphics2D) {
        gameWorld.render(g)
    }

    private fun renderEndGameResult(g: Graphics2D) {
        if (gameWorld.gameOver) {
            val endText = getEndText()
            val centerPair = getCenterOfCanvas(g, endText)

            g.color = Color.RED
            g.drawString(getEndText(), centerPair.first, centerPair.second)
        }
    }

    private fun getCenterOfCanvas(g: Graphics2D, str: String): Pair {
        val fontMetrics = g.fontMetrics
        val strBounds = fontMetrics.getStringBounds(str, g)

        val centerX = ((width - strBounds.width) / 2).toInt()
        val centerY = ((height - strBounds.height) / 2 + fontMetrics.ascent).toInt() - 25

        return Pair(centerX, centerY)
    }

    private fun getEndText() = if (gameWorld.gameWon) "PacMan Wins!" else "Ghosts Win!"
}
