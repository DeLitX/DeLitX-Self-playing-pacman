package framework.obj

import framework.search.Search
import objects.PacMan
import java.awt.image.BufferedImage

abstract class Ghost(arrayX: Int, arrayY: Int, array: GameArray, val pacman: PacMan) : Sprite(arrayX, arrayY, array) {
    lateinit var image: BufferedImage
    abstract val search: Search

    init {
        velocity = 2F
    }
}
