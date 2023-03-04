package framework.search

import framework.obj.Node
import kotlin.math.abs

object NodeDistance {

    @JvmStatic
    fun getDistanceBetweenNodes(from: Node, to: Node) =
        (abs(from.x - to.x) + abs(from.y - to.y)).toFloat()
}
