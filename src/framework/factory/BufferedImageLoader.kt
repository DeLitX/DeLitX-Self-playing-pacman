package framework.factory

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

object BufferedImageLoader {

    fun getImage(path: String): BufferedImage = ImageIO.read(File(path))
}
