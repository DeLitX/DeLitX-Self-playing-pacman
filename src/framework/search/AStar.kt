package framework.search

import framework.obj.GameArray
import framework.obj.GameObject
import framework.obj.Node
import framework.obj.Pair
import objects.OrangeGhost
import objects.RedGhost
import java.util.*

class AStar(private val array: GameArray, val objects: LinkedList<GameObject> = LinkedList()) : Search {

    private val movementCost = 1

    private val open = LinkedList<Node>()
    private val closed = LinkedList<Node>()
    private val goalFound = false
    lateinit var startNode: Node
    lateinit var goalNode: Node
    lateinit var currentNode: Node

    override fun findAPathToPacMan(fromPair: Pair, toPair: Pair, dir: DIRECTION): LinkedList<Node> {
        open.clear()
        closed.clear()

        startNode = array.getNode(fromPair)
        goalNode = array.getNode(toPair)
        var firstRetrievalOfNeighbours = true

        if (isAtGoal(startNode, toPair)) {
            val list = LinkedList<Node>()
            list.addFirst(startNode)
            return list
        }

        open.add(startNode)

        while (!goalFound) {
            currentNode = getLowestFNodeFromOpenList()
            swapNodeFromOpenToClosedList(currentNode)

            if (isAtGoal(currentNode, toPair)) {
                return getPathToGoal(startNode, currentNode)
            }

            val neighbours = if (firstRetrievalOfNeighbours) {
                firstRetrievalOfNeighbours = false
                getInitialNeighbours(currentNode, dir)
            } else {
                getNeighbours(currentNode)
            }

            for (neighbour in neighbours) {
                if (open.contains(neighbour)) {
                    if (neighbour.costToGetHereSoFarG > currentNode.costToGetHereSoFarG + movementCost) {
                        neighbour.costToGetHereSoFarG = currentNode.costToGetHereSoFarG + movementCost
                        neighbour.nodeCostF = neighbour.distanceToGoalH + neighbour.costToGetHereSoFarG
                        neighbour.parentNode = currentNode
                    }
                } else {
                    if (!closed.contains(neighbour)) {
                        neighbour.distanceToGoalH = NodeDistance.getDistanceBetweenNodes(neighbour, goalNode)
                        neighbour.costToGetHereSoFarG = currentNode.costToGetHereSoFarG + movementCost
                        neighbour.nodeCostF = neighbour.distanceToGoalH + neighbour.costToGetHereSoFarG
                        neighbour.parentNode = currentNode
                        open.add(neighbour)
                    }
                }
            }
            if (open.isEmpty()) {
                return LinkedList()
            }
        }
        return LinkedList()
    }

    override fun findAPathToPill(fromPair: Pair, toPair: Pair): LinkedList<Node> {
        open.clear()
        closed.clear()

        startNode = array.getNode(fromPair)
        goalNode = array.getNode(toPair)

        if (isAtGoal(startNode, toPair)) {
            val list = LinkedList<Node>()
            list.addFirst(startNode)
            return list
        }

        open.add(startNode)

        while (!goalFound) {
            currentNode = getLowestFNodeFromOpenList()
            swapNodeFromOpenToClosedList(currentNode)

            if (isAtGoal(currentNode, toPair)) {
                return getPathToGoal(startNode, currentNode)
            }

            val neighbours = getNeighbours(currentNode)

            for (neighbour in neighbours) {
                if (open.contains(neighbour)) {
                    if (neighbour.costToGetHereSoFarG > currentNode.costToGetHereSoFarG + movementCost) {
                        neighbour.costToGetHereSoFarG = currentNode.costToGetHereSoFarG + movementCost
                        neighbour.nodeCostF = neighbour.distanceToGoalH + neighbour.costToGetHereSoFarG

                        objects.forEach {
                            if (it is RedGhost || it is OrangeGhost) {
                                if (neighbour.x == it.arrayX && neighbour.y == it.arrayY) {
                                    neighbour.nodeCostF += 1000
                                }
                            }
                        }
                    }
                } else {
                    if (!closed.contains(neighbour)) {
                        neighbour.distanceToGoalH = NodeDistance.getDistanceBetweenNodes(neighbour, goalNode)
                        neighbour.costToGetHereSoFarG = currentNode.costToGetHereSoFarG + movementCost
                        neighbour.nodeCostF = neighbour.distanceToGoalH + neighbour.costToGetHereSoFarG

                        objects.forEach {
                            if (it is RedGhost || it is OrangeGhost) {
                                if (neighbour.x == it.arrayX && neighbour.y == it.arrayY) {
                                    neighbour.nodeCostF += 1000
                                }
                            }
                        }

                        neighbour.parentNode = currentNode
                        open.add(neighbour)
                    }
                }
            }
            if (open.isEmpty()) {
                return LinkedList()
            }
        }
        return LinkedList()
    }

    private fun getLowestFNodeFromOpenList(): Node {
        var lowestFNode = open.first

        open.forEach {
            if (lowestFNode.nodeCostF > it.nodeCostF) {
                lowestFNode = it
            }
        }

        return lowestFNode
    }

    private fun swapNodeFromOpenToClosedList(node: Node) {
        closed.add(node)
        open.remove(node)
    }

    private fun isAtGoal(node: Node, toPair: Pair) = (node.x == toPair.first) && (node.y == toPair.second)

    private fun getInitialNeighbours(node: Node, dir: DIRECTION): LinkedList<Node> {
        val neighbours = LinkedList<Node>()

        with(node) {
            if (y > 0) { // ABOVE
                if (dir != DIRECTION.DOWN) {
                    val neighbour = array.getNode(x, y - 1)
                    if (!neighbour.wall) {
                        neighbours.add(neighbour)
                    }
                }
            }

            if (x > 0) { // LEFT
                if (dir != DIRECTION.RIGHT) {
                    val neighbour = array.getNode(x - 1, y)
                    if (!neighbour.wall) {
                        neighbours.add(neighbour)
                    }
                }
            }

            if (x < GameArray.WIDTH) { // RIGHT
                if (dir != DIRECTION.LEFT) {
                    val neighbour = array.getNode(x + 1, y)
                    if (!neighbour.wall) {
                        neighbours.add(neighbour)
                    }
                }
            }

            if (y < GameArray.HEIGHT) { // BELOW
                if (dir != DIRECTION.UP) {
                    val neighbour = array.getNode(x, y + 1)
                    if (!neighbour.wall) {
                        neighbours.add(neighbour)
                    }
                }
            }
        }

        return neighbours
    }

    private fun getNeighbours(node: Node): LinkedList<Node> {
        val neighbours = LinkedList<Node>()

        with(node) {
            if (y > 0) {
                val neighbour = array.getNode(x, y - 1)
                if (!neighbour.wall) {
                    neighbours.add(neighbour)
                }
            }

            if (x > 0) {
                val neighbour = array.getNode(x - 1, y)
                if (!neighbour.wall) {
                    neighbours.add(neighbour)
                }
            }

            if (x < GameArray.WIDTH) {
                val neighbour = array.getNode(x + 1, y)
                if (!neighbour.wall) {
                    neighbours.add(neighbour)
                }
            }

            if (y < GameArray.HEIGHT) {
                val neighbour = array.getNode(x, y + 1)
                if (!neighbour.wall) {
                    neighbours.add(neighbour)
                }
            }
        }
        return neighbours
    }

    private fun getPathToGoal(start: Node, goal: Node): LinkedList<Node> {
        val path = LinkedList<Node>()
        var pathCompleted = false
        var currNode: Node? = goal

        while (!pathCompleted) {
            path.addFirst(currNode)
            currNode = currNode!!.parentNode

            if (currNode!!.x == start.x && currNode.y == start.y) {
                pathCompleted = true
            }
        }

        return path
    }
}
