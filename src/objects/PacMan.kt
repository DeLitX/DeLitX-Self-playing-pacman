package objects

import framework.extension.render
import framework.obj.*
import framework.search.AStar
import framework.search.DIRECTION
import java.awt.Color
import java.awt.Graphics2D
import java.io.File
import java.util.*
import javax.imageio.ImageIO

class PacMan(arrayX: Int, arrayY: Int, array: GameArray, handler: ObjectHandler) : Sprite(arrayX, arrayY, array) {

    var lives = 3

    private val proximitySensitivity = 2

    private val imgUp = ImageIO.read(File("res/PacmanUp.png"))
    private val imgDown = ImageIO.read(File("res/PacmanDown.png"))
    private val imgLeft = ImageIO.read(File("res/PacmanLeft.png"))
    private val imgRight = ImageIO.read(File("res/PacmanRight.png"))

    val objects = handler.objects

    private val AStarSearch = AStar(array, objects)

    init {
        createInitialPath()
    }

    private fun createInitialPath() {
        path = AStarSearch.findAPathToPill(
            Pair(arrayX, arrayY),
            Pair(15, 23)
        )
        nextSquareToMoveTo = path.remove()
    }

    override fun tick() {
        super.tick()
        checkCollision()
    }

    override fun render(g: Graphics2D) {
        super.render(g)

        renderPacMan(g)
        renderLives(g)

        g.color = Color.GREEN
        path.render(g)
    }

    private fun renderPacMan(g: Graphics2D) {
        when (prevDir) {
            DIRECTION.UP -> g.drawImage(imgUp, affineTransform, null)
            DIRECTION.DOWN -> g.drawImage(imgDown, affineTransform, null)
            DIRECTION.LEFT -> g.drawImage(imgLeft, affineTransform, null)
            DIRECTION.RIGHT -> g.drawImage(imgRight, affineTransform, null)
            DIRECTION.NONE -> Unit
        }
    }

    private fun renderLives(g: Graphics2D) {
        if (lives > 2) {
            g.drawImage(imgRight, 64, 0, null)
        }

        if (lives > 1) {
            g.drawImage(imgRight, 32, 0, null)
        }

        if (lives > 0) {
            g.drawImage(imgRight, 0, 0, null)
        }
    }

    override fun updatePath() {
        checkForGhosts()
        checkIfCurrentPathIsFinished()
    }

    private fun checkForGhosts() {
        objects.forEach {
            if (it is Ghost) {
                if (ghostNearby(it)) {
                    getNewFleePath(it)
                }
            }
        }
    }

    private fun ghostNearby(ghost: Ghost) = ghostIsNearbyOnXAxis(ghost) && ghostIsNearbyOnYAxis(ghost)

    private fun ghostIsNearbyOnXAxis(ghost: Ghost) =
        ghost.arrayX >= (arrayX - proximitySensitivity) && ghost.arrayX <= (arrayX + proximitySensitivity)

    private fun ghostIsNearbyOnYAxis(ghost: Ghost) =
        ghost.arrayY >= (arrayY - proximitySensitivity) && ghost.arrayY <= (arrayY + proximitySensitivity)

    private fun getNewFleePath(ghost: Ghost) {
        array.clearValues()
        path = AStarSearch.findAPathToPill(getCurrentLocation(), getFleeLocation(ghost))
    }

    private fun checkIfCurrentPathIsFinished() {
        if (path.isEmpty()) {
            getNewPillPath()
        }
    }

    fun getNewPillPath() {
        array.clearValues()

        val nextPillLocation = array.getClosestPillLocation(arrayX, arrayY)

        if (thereIsAnotherPill(nextPillLocation)) {
            path = AStarSearch.findAPathToPill(getCurrentLocation(), nextPillLocation)
        }
    }

    private fun getFleeLocation(ghost: Ghost): Pair =
        // В залежності від того, з якої сторони до нас наближається привид, йдемо у протилежну частину карти
        when {
            ghostIsAboveLeft(ghost) -> getValidFleeLocation(15, 26, 20, 29)
            ghostIsAbove(ghost) -> getValidFleeLocation(1, 26, 20, 29)
            ghostIsAboveRight(ghost) -> getValidFleeLocation(1, 12, 20, 29)

            ghostIsLeft(ghost) -> getValidFleeLocation(15, 26, 1, 29)
            ghostIsRight(ghost) -> getValidFleeLocation(1, 11, 1, 29)

            ghostIsBelowLeft(ghost) -> getValidFleeLocation(15, 26, 1, 8)
            ghostIsBelow(ghost) -> getValidFleeLocation(1, 26, 1, 14)
            ghostIsBelowRight(ghost) -> getValidFleeLocation(1, 12, 1, 8)

            else -> Pair(1, 1)
        }

    private fun ghostIsAboveLeft(ghost: Ghost) = ghost.arrayX < arrayX && ghost.arrayY < arrayY
    private fun ghostIsAbove(ghost: Ghost) = ghost.arrayX == arrayX && ghost.arrayY < arrayY
    private fun ghostIsAboveRight(ghost: Ghost) = ghost.arrayX > arrayX && ghost.arrayY < arrayY

    private fun ghostIsLeft(ghost: Ghost) = ghost.arrayX < arrayX && ghost.arrayY == arrayY
    private fun ghostIsRight(ghost: Ghost) = ghost.arrayX > arrayX && ghost.arrayY == arrayY

    private fun ghostIsBelowLeft(ghost: Ghost) = ghost.arrayX < arrayX && ghost.arrayY > arrayY
    private fun ghostIsBelow(ghost: Ghost) = ghost.arrayX == arrayX && ghost.arrayY > arrayY
    private fun ghostIsBelowRight(ghost: Ghost) = ghost.arrayX > arrayX && ghost.arrayY > arrayY

    private fun getValidFleeLocation(lowX: Int, highX: Int, lowY: Int, highY: Int): Pair {
        var validLocationFound = false
        var fleeX = 0
        var fleeY = 0

        while (!validLocationFound) {
            fleeX = getRandomNumberInRange(lowX, highX)
            fleeY = getRandomNumberInRange(lowY, highY)

            if (isValidLocation(fleeX, fleeY)) {
                validLocationFound = true
            }
        }

        return Pair(fleeX, fleeY)
    }

    private fun isValidLocation(fleeX: Int, fleeY: Int) =
        !array.getNode(fleeX, fleeY).wall && fleeX != arrayX && fleeY != arrayY

    private fun getRandomNumberInRange(low: Int, high: Int) =
        Random().nextInt(high - low) + low

    private fun thereIsAnotherPill(pill: Pair) = pill.first != 0 && pill.second != 0

    private fun checkCollision() {
        checkForCollisionWithPill()
        checkForCollisionWithGhost()
    }

    private fun checkForCollisionWithPill() {
        val node = array.getNode(arrayX, arrayY)

        if (node.pill != null) {
            node.pill = null
        }
    }

    private fun checkForCollisionWithGhost() {
        objects.forEach {
            if (it is Ghost) {
                if (arrayX == it.arrayX && arrayY == it.arrayY) {
                    resetPacman()
                }
            }
        }
    }

    private fun resetPacman() {
        lives--

        if (lives > 0) {
            arrayX = 14
            arrayY = 23
            x = arrayX * 32F
            y = arrayY * 32F
            path = LinkedList()
            nextSquareToMoveTo = Node(14, 23)
        }
    }
}
