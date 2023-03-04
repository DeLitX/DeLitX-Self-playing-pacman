package objects

import framework.extension.render
import framework.obj.*
import framework.search.AStar
import java.awt.Color
import java.awt.Graphics2D
import java.io.File
import javax.imageio.ImageIO

class RedGhost(arrayX: Int, arrayY: Int, array: GameArray, pacman: PacMan) : Ghost(arrayX, arrayY, array, pacman) {

    override val search = AStar(array)

    init {
        image = ImageIO.read(File("res/BlinkyGhost.png"))
        path = search.findAPathToPacMan(getCurrentLocation(), pacman.getCurrentLocation(), prevDir)
        nextSquareToMoveTo = path.remove()
    }

    override fun updatePath() {
        path = search.findAPathToPacMan(getCurrentLocation(), pacman.getCurrentLocation(), prevDir)
    }

    override fun render(g: Graphics2D) {
        super.render(g)
        g.drawImage(image, affineTransform, null)

        g.color = Color.RED
        path.render(g)
    }
}
