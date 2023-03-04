package framework.obj

import framework.intf.Renderable
import framework.search.NodeDistance.getDistanceBetweenNodes
import java.awt.Color
import java.awt.Graphics2D
import java.util.*

class GameArray : Renderable {
    private val array = Array(WIDTH) { arrayOfNulls<Node>(HEIGHT) }
    fun addNode(node: Node) {
        array[node.x][node.y] = node
    }

    fun getNode(x: Int, y: Int): Node {
        return array[x][y]!!
    }

    fun getNode(pair: Pair): Node {
        return array[pair.first][pair.second]!!
    }

    fun clearValues() {
        for (i in 1..WORLD_WIDTH) {
            for (j in 1..WORLD_HEIGHT) {
                val node = array[i][j]
                node!!.parentNode = null
                node.costToGetHereSoFarG = 0f
                node.distanceToGoalH = 0f
                node.nodeCostF = 0f
            }
        }
    }

    val listOfUneatenPills: LinkedList<Node?>
        get() {
            val listOfNodes = LinkedList<Node?>()
            for (i in 1..WORLD_WIDTH) {
                for (j in 1..WORLD_HEIGHT) {
                    val newNode = array[i][j]
                    if (newNode!!.pill != null && !newNode.wall) {
                        listOfNodes.add(newNode)
                    }
                }
            }
            return listOfNodes
        }

    fun getClosestPillLocation(x: Int, y: Int): Pair {
        val listOfNodes = listOfUneatenPills
        var closestX = 0
        var closestY = 0
        if (!listOfNodes.isEmpty()) {
            var currentClosestNode = listOfNodes.remove()
            val currentLocation = array[x][y]
            for (node in listOfNodes) {
                if (getDistanceBetweenNodes(currentLocation!!, currentClosestNode!!)
                    >= getDistanceBetweenNodes(currentLocation, node!!)
                ) {
                    currentClosestNode = node
                }
            }
            closestX = currentClosestNode!!.x
            closestY = currentClosestNode.y
        }
        return Pair(closestX, closestY)
    }

    override fun render(g: Graphics2D) {
        for (x in 0..WORLD_WIDTH) {
            for (y in 0..WORLD_HEIGHT) {
                val node = array[x][y]
                if (node!!.wall) {
                    g.color = Color.blue
                    g.fillRect(x * 32, y * 32, 32, 32)
                } else {
                    if (node.pill != null) {
                        node.pill!!.render(g)
                    }
                }
            }
        }
    }

    companion object {
        const val WIDTH = 32
        const val HEIGHT = 32
        const val WORLD_WIDTH = 27
        const val WORLD_HEIGHT = 30
    }
}
