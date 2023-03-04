package framework.search

import framework.obj.Node
import framework.obj.Pair
import java.util.*

interface Search {
    fun findAPathToPacMan(fromPair: Pair, toPair: Pair, dir: DIRECTION): LinkedList<Node>
    fun findAPathToPill(fromPair: Pair, toPair: Pair): LinkedList<Node>
}
