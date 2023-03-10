package objects

import framework.factory.BufferedImageLoader
import framework.factory.NodeFactory
import framework.factory.PillFactory
import framework.intf.Renderable
import framework.intf.Tickable
import framework.obj.GameArray
import framework.obj.ObjectHandler
import java.awt.Graphics2D
import java.awt.image.BufferedImage

class GameWorld : Tickable, Renderable {

    private val world = GameArray()
    private val handler = ObjectHandler()
    var gameOver = false
    var gameWon = false

    lateinit var levelImg: BufferedImage
    lateinit var pacman: PacMan

    override fun tick() {
        handler.tick()

        if (pacman.lives == 0) {
            gameOver = true
        }

        if (world.listOfUneatenPills.isEmpty()) {
            gameOver = true
            gameWon = true
        }
    }

    override fun render(g: Graphics2D) {
        world.render(g)
        handler.render(g)
    }

    fun loadLevelFromImage(path: String) {
        getLevelImage(path)
        iterateOverImagePixels()
        addPacMan()
        addGhosts()
    }

    private fun getLevelImage(path: String) {
        levelImg = BufferedImageLoader.getImage(path)
    }

    private fun iterateOverImagePixels() {
        for (x in 0 until levelImg.width) {
            for (y in 0 until levelImg.height) {
                val currentPixel = Pixel(levelImg.getRGB(x, y))
                updateGameArray(currentPixel, x, y)
            }
        }
    }

    private fun updateGameArray(pixel: Pixel, x: Int, y: Int) {
        val node = when {
            pixel.isWall() -> NodeFactory.getWallNode(x, y)
            else -> NodeFactory.getOpenNode(x, y)
        }

        if (pixel.isPill()) {
            node.pill = PillFactory.getPill(x, y)
        }

        world.addNode(node)
    }

    private fun addPacMan() {
        pacman = PacMan(14, 23, world, handler)
        handler.addGameObject(pacman)
    }

    private fun addGhosts() {
        val redGhost = RedGhost(13, 14, world, pacman)
        val orangeGhost = OrangeGhost(13, 14, world, pacman)

        handler.addGameObject(redGhost)
        handler.addGameObject(orangeGhost)
    }
}
