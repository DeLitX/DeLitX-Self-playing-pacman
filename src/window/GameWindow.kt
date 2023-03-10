package window

import java.awt.Dimension
import javax.swing.JFrame

/**
 * Created by michaelbrooke on 2016/04/23.
 */
class GameWindow(title: String, game: Game, width: Int, height: Int) {

    private val frame = JFrame(title)
    private val gameDimensions = Dimension(width, height)

    init {
        with(game) {
            maximumSize = gameDimensions
            minimumSize = gameDimensions
            preferredSize = gameDimensions
        }

        with(frame) {
            add(game)
            pack()
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            isResizable = false
            setLocationRelativeTo(null)
            isVisible = true
        }

        game.startGame()
    }
}
