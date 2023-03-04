package objects

import framework.extension.render
import framework.obj.*
import framework.search.GreedyBFS
import java.awt.Color
import java.awt.Graphics2D
import java.io.File
import java.util.*
import javax.imageio.ImageIO

class OrangeGhost(arrayX: Int, arrayY: Int, array: GameArray, pacman: PacMan) : Ghost(arrayX, arrayY, array, pacman) {

    override val search = GreedyBFS(array)

    init {
        image = ImageIO.read(File("res/OrangeGhost.png"))
        path = LinkedList()
        nextSquareToMoveTo = Node(arrayX, arrayY)
    }

    override fun updatePath() {
        path = search.findAPathToPacMan(getCurrentLocation(), pacman.getCurrentLocation(), prevDir)
    }

    override fun render(g: Graphics2D) {
        super.render(g)
        g.drawImage(image, affineTransform, null)

        g.color = Color.ORANGE
        path.render(g)
    }
}
