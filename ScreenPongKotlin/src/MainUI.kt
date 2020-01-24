import java.awt.*
import java.awt.event.ActionListener
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.event.WindowEvent
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.border.EmptyBorder


fun main(args : Array<String>)
{
    val f = runGUI()
}
fun runGUI() {
    val f = JFrame("Screen Pong Kotlin edition")
    f.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    f.setBounds(200, 200, 400, 300)

    // creates the content pain
    val contentPane = JPanel()
    contentPane.border = EmptyBorder(5, 5, 5, 5)

    //adds the content pane to the frame "windowed Applet"
    f.contentPane = contentPane
    contentPane.layout = null
    // creates the lable
    val lblNewLabel = JLabel("Screen Pong Kotlin edition")
    lblNewLabel.setBounds(28, 13, 344, 38)
    lblNewLabel.font = Font("Times New Roman", Font.PLAIN, 32)
    contentPane.add(lblNewLabel)

    // creates the button
    // creates the button
    val btnStart = JButton("Start!!!")
    btnStart.addActionListener {
        try {
            GameLoop(4,4)
            f.isVisible = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    btnStart.font = Font("Times New Roman", Font.PLAIN, 17)
    btnStart.background = Color(255, 255, 255)
    btnStart.setBounds(38, 75, 323, 152)
    contentPane.add(btnStart)

    // sets the frame to be visible
    f.isVisible = true
}
class GameLoop(var devW: Int, var devH: Int)
{
    var blkArray = arrayOf<Array<Block>>()
    var gameFrame: JFrame
    var ball: Ball
    var pattle: Pattle
    var winCount = 0
    init
    {
        blkArray = Imaging().slice(devH,devW)
        gameFrame = JFrame("GameWindow")
        gameFrame.defaultCloseOperation = JFrame.DO_NOTHING_ON_CLOSE
        gameFrame.setUndecorated(true);
        gameFrame.isVisible = true

        ball = Ball()
        pattle = Pattle(1000,1000,300,50, ball)

        var al = ActionListener()
        {
            updateframe()
        }
        Timer(1000/30,al).start()
    }
    fun updateframe()
    {
        ball.update(gameFrame)
        pattle.update(gameFrame)
        var panel = object : JPanel() {
            override fun paintComponent(g: Graphics) {
                super.paintComponent(g)
                winCount = 0
                for (x in blkArray)
                {
                    for (b in x)
                    {
                        b.update(ball)
                        if (b.hasBroken == false || b.breakable){
                            g.drawImage(b.img, b.posX, b.posY, b.img.width, b.img.height, this)
                            if (!b.breakable)
                            {
                                winCount += 1
                            }
                        }
                    }
                }
                g.drawImage(ball.img, ball.x, ball.y, 50, 50, this)
                g.fillRect(pattle.posX,pattle.posY,pattle.width,pattle.height)
                if (winCount == 0)
                {
                    println("Game Complete!")
                    gameFrame.dispatchEvent(WindowEvent(gameFrame, WindowEvent.WINDOW_CLOSING))
                }
                if ((ball.y >= Toolkit.getDefaultToolkit().screenSize.height) && (ball.vY > 0))
                {
                    println("___________________________________________")
                    println("___________________________________________")
                    println("___________________________________________")
                    println("___________________________________________")
                    println("___________________________________________")
                    println("___________________________________________")
                    println("___________________________________________")
                    ball.x = 1000
                    ball.y = 1000
                    ball.vY *-1
                    blkArray = emptyArray()
                    blkArray = Imaging().slice(10,10)
                }
            }
        }
        gameFrame.add(panel)
        val ge = GraphicsEnvironment.getLocalGraphicsEnvironment()
        val gs = ge.defaultScreenDevice
        // makes the window Full Screen
        gs.fullScreenWindow = gameFrame

        gameFrame.validate()
        if(winCount == 0)
        {
//         gameFrame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        }
    }
}
class Block(val posX: Int, val posY: Int, val img: BufferedImage, var breakable :Boolean)
{
    var v = 0
    var hasBroken = false
    var rect: Rectangle = Rectangle(posX,posY,img.width,img.height)
    fun update(ball: Ball)
    {
        rect = Rectangle(posX,posY,img.width,img.height)
        if (rect.intersects(ball.rect) && hasBroken == false && !breakable)
        {
            hasBroken = true
            ball.vX = (-1)*ball.vX
            ball.vY = (-1)*ball.vY
        }
    }
}
class Pattle(var posX: Int, var posY: Int, var width: Int,var height: Int,val ball:Ball ) {
    var v = 0
    var rect: Rectangle = Rectangle(posX, posY, width, height)
    fun update(gameFrame: JFrame) {
        rect = Rectangle(posX, posY, width, height)
        var listener = object : KeyListener {
                override fun keyTyped(e: KeyEvent) {
                    var key = e.keyCode
                }
                override fun keyPressed(e: KeyEvent) {
                    var key = e.keyCode
                    if (key == KeyEvent.VK_LEFT) {
                        v = -25
                    }
                    if (key == KeyEvent.VK_RIGHT) {
                        v = 25
                    }
                }
                override fun keyReleased(e: KeyEvent) {
                    var key = e.keyCode
                    if (key == KeyEvent.VK_LEFT) {
                        v = 0;
                    }
                    if (key == KeyEvent.VK_RIGHT) {
                        v = 0;
                    }
                }
            }
        if (posX <= 0 && v<0 || ((posX + width) >= Toolkit.getDefaultToolkit().screenSize.width) && v>0) {
            v=0
        }
        posX += v
        gameFrame.addKeyListener(listener)
        if (rect.intersects(ball.rect)){
            ball.vY = (ball.vY * -1)
        }
        }
    }

    class Ball {
        var x = 500
        var y = 1000
        var vX = -15
        var vY = -15
        val img: BufferedImage
        var rect: Rectangle

        init {
            img = ImageIO.read(File("src/ball.jpg"))
            rect = Rectangle(x, y, 50, 50)
        }

        fun update(gameFrame: JFrame) {
            if ((x <= 0) && (vX < 0) || (x >= Toolkit.getDefaultToolkit().screenSize.width) && (vX > 0)) {
                vX = (-1 * vX)
            }
            if (y <= 0 && (vY < 0)) {
                vY = (-1) * vY
            }
            x = x + vX
            y = y + vY
            rect = Rectangle(x, y, 50, 50)
        }
    }

    class Imaging() {
        var img: BufferedImage;
        var width: Int
        var height: Int

        init {
            width = Toolkit.getDefaultToolkit().screenSize.width
            height = Toolkit.getDefaultToolkit().screenSize.height
            img = Robot().createScreenCapture(Rectangle(width, height))
        }

        fun slice(devH: Int, devW: Int): Array<Array<Block>> {
            var blkArray = arrayOf<Array<Block>>()
            for (x in 0..(devW - 1)) {
                var array = arrayOf<Block>()
                for (y in 0..(devH - 1)) {
                    if (y > (devH - 1) / 2) {
                        println("___________________________")
                        println("("+x+","+y+")")
                        array += Block(
                            x * (width / devW),
                            y * (height / devH),
                            img.getSubimage(x * (width / devW), y * (height / devH), (width / devW), (height / devH)),
                            true )
                    } else {
                        array += Block(
                            x * (width / devW),
                            y * (height / devH),
                            img.getSubimage(x * (width / devW), y * (height / devH), (width / devW), (height / devH)),
                            false )
                    }
                }
                blkArray += array
            }
            return blkArray
        }
    }


