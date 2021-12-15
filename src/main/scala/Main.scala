import java.awt.event.{ActionEvent, ActionListener, ComponentAdapter, ComponentEvent}
import java.awt.{Color, FlowLayout, Rectangle}
import java.io.File
import java.net.URI

import sys.process._
import javax.swing._
import javax.swing.event._
import java.lang.ProcessBuilder


object Main extends App {

  val videoModel = new DefaultListModel[ShortFile]()
  val audioModel = new DefaultListModel[ShortFile]()
  val subModel = new DefaultListModel[ShortFile]()
  val videoList = new JList[ShortFile](videoModel)
  val audioList = new JList[ShortFile](audioModel)
  val subList =  new JList[ShortFile](subModel)

  videoList.setDropMode(DropMode.INSERT)
  videoList.setTransferHandler(new ListHandler(videoModel))
  videoList.setBorder(BorderFactory.createLineBorder(Color.black))
  videoList.getSelectionModel.addListSelectionListener(new SharedListSelectionHandler(audioList, subList))

  audioList.setDropMode(DropMode.INSERT)
  audioList.setTransferHandler(new ListHandler(audioModel))
  audioList.setBorder(BorderFactory.createLineBorder(Color.black))
  audioList.getSelectionModel.addListSelectionListener(new SharedListSelectionHandler(videoList,subList))

  subList.setDropMode(DropMode.INSERT)
  subList.setTransferHandler(new ListHandler(subModel))
  subList.setBorder(BorderFactory.createLineBorder(Color.black))
  subList.getSelectionModel.addListSelectionListener(new SharedListSelectionHandler(videoList,audioList))

  val playButton = new JButton("\u25B6 Play")
  playButton.addActionListener(new ActionListener {
    override def actionPerformed(actionEvent: ActionEvent): Unit = {
      println("Summoning your waifu...")
      val p = new ProcessBuilder(
        "mpv",
        videoList.getSelectedValue.getAbsolutePath ,
        "--audio-file="+audioList.getSelectedValue.getAbsolutePath,
        "--sub-file=" + subList.getSelectedValue.getAbsolutePath,
      ).redirectOutput(ProcessBuilder.Redirect.INHERIT).redirectError(ProcessBuilder.Redirect.INHERIT).start
     // Stream.continually(p.getInputStream.read).takeWhile(-1 !=).

    }
  })

  //println("Hello, world!")
    val frame = new JFrame("AnimeTool [mpv]")

  frame.setSize(800, 600)
  frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
  val panel = new JPanel

  frame.add(panel)

  placeComponents(panel)

  frame.setVisible(true)

  frame.addComponentListener(new ResizeListener())



  case class ResizeListener() extends ComponentAdapter {
      override def componentResized(e : ComponentEvent) : Unit = {
        panel.removeAll();
        placeComponents(panel)
    }
  }

  private def getBoundsInBounds(offset: Int, count: Int, paddingBetween : Int, rectangle: Rectangle) : Rectangle = {
    val relativeX = rectangle.width * offset / count
    val relativeY = 0

    val width = rectangle.width / count - paddingBetween
    val height = rectangle.height

    val result = new Rectangle(rectangle.x + relativeX, rectangle.y + relativeY, width, height)
   // println(result)
    result
  }

  private def getBoundsInBoundsV(offset: Int, count: Int, paddingBetween : Int, rectangle: Rectangle) : Rectangle = {
    val relativeY = rectangle.height * offset / count
    val relativeX = 0

    val actualPaddingBeween = if (offset +1 == count ) 0 else paddingBetween

    val height = rectangle.height / count - actualPaddingBeween
    val width = rectangle.width

    val result = new Rectangle(rectangle.x + relativeX, rectangle.y + relativeY, width, height)
   // println(result)
    result
  }

  private def getNextBounds(height: Int, verticalPadding : Int, rectangle: Rectangle): Rectangle = {
        val lastUnfuckMargin = 30
        //val endY = rectangle.y + rectangle.height + verticalPadding + height
        val actualHeight =
          if (height < 0)
            //height - (endY - frame.getHeight)
            (frame.getHeight - (rectangle.y + rectangle.height + verticalPadding)) - verticalPadding - lastUnfuckMargin
          else
            height


        val result = new Rectangle(rectangle.x, rectangle.y + rectangle.height + verticalPadding, rectangle.width, actualHeight )
       // println(frame.getHeight)
        //println(result)
        result
  }


  private def placeComponents(panel: JPanel): Unit = {
    panel.setLayout(null)


    val genericPaddingLeft = 5
    val verticalPadding = 5

    val startRect = new Rectangle(genericPaddingLeft, 0, frame.getWidth - genericPaddingLeft, 1)

    val playRect = getNextBounds(40,verticalPadding,startRect)


    playButton.setBounds(getBoundsInBounds(0,3,genericPaddingLeft,playRect))

    panel.add(playButton)

    val audioDelayContainerRect = getBoundsInBounds(1,3,genericPaddingLeft,playRect)

    val audioDelayLabel = new JLabel("Audio Delay")
    audioDelayLabel.setBounds(getBoundsInBoundsV(0, 2,genericPaddingLeft,audioDelayContainerRect))
    panel.add(audioDelayLabel)
    val audioDelayText = new JTextField(20)
    audioDelayText.setBounds(getBoundsInBoundsV(1, 2,genericPaddingLeft,audioDelayContainerRect))
    panel.add(audioDelayText)

    val subDelayContainerRect = getBoundsInBounds(2,3,genericPaddingLeft,playRect)
    val subDelayLabel = new JLabel("Subtitles Delay")
    subDelayLabel.setBounds(getBoundsInBoundsV(0, 2,genericPaddingLeft,subDelayContainerRect))
    panel.add(subDelayLabel)
    val subDelayText = new JTextField(20)
    subDelayText.setBounds(getBoundsInBoundsV(1, 2,genericPaddingLeft,subDelayContainerRect))
    panel.add(subDelayText)


    val cLabelsRect = getNextBounds(10,verticalPadding,playRect)
    val clabel1 = new JLabel("Video")
    clabel1.setBounds(getBoundsInBounds(0, 3, genericPaddingLeft, cLabelsRect))
    panel.add(clabel1)

    val clabel2 = new JLabel("Audio")
    clabel2.setBounds(getBoundsInBounds(1, 3, genericPaddingLeft, cLabelsRect))
    panel.add(clabel2)

    val clabel3 = new JLabel("Subtitles")
    clabel3.setBounds(getBoundsInBounds(2, 3, genericPaddingLeft, cLabelsRect))
    panel.add(clabel3)

    val buttonsRect = getNextBounds(40,verticalPadding,cLabelsRect)
    val buttons1Rect = getBoundsInBounds(0, 3, genericPaddingLeft, buttonsRect)
    val buttons2Rect = getBoundsInBounds(1, 3, genericPaddingLeft, buttonsRect)
    val buttons3Rect = getBoundsInBounds(2, 3, genericPaddingLeft, buttonsRect)

    val up1btn = new JButton("\u25B2")
    up1btn.setBounds(getBoundsInBounds(0,4,0, buttons1Rect))
    panel.add(up1btn)
    val down1btn = new JButton("\u25BC")
    down1btn.setBounds(getBoundsInBounds(1,4,genericPaddingLeft, buttons1Rect))
    panel.add(down1btn)
    val del1btn = new JButton("DEL")
    del1btn.setBounds(getBoundsInBounds(2,4,0, buttons1Rect))
    panel.add(del1btn)
    val clear1btn = new JButton("CLR")
    clear1btn.setBounds(getBoundsInBounds(3,4,0, buttons1Rect))
    clear1btn.addActionListener(new ClearActionListener(videoModel))
    panel.add(clear1btn)

    val up2btn = new JButton("\u25B2")
    up2btn.setBounds(getBoundsInBounds(0,4,0, buttons2Rect))
    panel.add(up2btn)
    val down2btn = new JButton("\u25BC")
    down2btn.setBounds(getBoundsInBounds(1,4,genericPaddingLeft, buttons2Rect))
    panel.add(down2btn)
    val del2btn = new JButton("DEL")
    del2btn.setBounds(getBoundsInBounds(2,4,0, buttons2Rect))
    panel.add(del2btn)
    val clear2btn = new JButton("CLR")
    clear2btn.setBounds(getBoundsInBounds(3,4,0, buttons2Rect))
    clear2btn.addActionListener(new ClearActionListener(audioModel))
    panel.add(clear2btn)

    val up3btn = new JButton("\u25B2")
    up3btn.setBounds(getBoundsInBounds(0,4,0, buttons3Rect))
    panel.add(up3btn)
    val down3btn = new JButton("\u25BC")
    down3btn.setBounds(getBoundsInBounds(1,4,genericPaddingLeft, buttons3Rect))
    panel.add(down3btn)
    val del3btn = new JButton("DEL")
    del3btn.setBounds(getBoundsInBounds(2,4,0, buttons3Rect))
    panel.add(del3btn)
    val clear3btn = new JButton("CLR")
    clear3btn.addActionListener(new ClearActionListener(subModel))
    clear3btn.setBounds(getBoundsInBounds(3,4,0, buttons3Rect))
    panel.add(clear3btn)


    val listRect = getNextBounds(-1,verticalPadding,buttonsRect)

    //import javax.swing.DefaultListModel
    //import javax.swing.JList
   // val model = new DefaultListModel[_]
    //val myList = new JList[_](model)


    videoList.setBounds(getBoundsInBounds(0,3,genericPaddingLeft, listRect))
    panel.add(videoList)

    audioList.setBounds(getBoundsInBounds(1,3,genericPaddingLeft, listRect))
    panel.add(audioList)


    subList.setBounds(getBoundsInBounds(2,3,genericPaddingLeft, listRect))
    panel.add(subList)


    //val userLabel = new JLabel("User");

    /*val userText = new JTextField(20)
    userText.setBounds(100, 20, 165, 25)
    panel.add(userText)

    val passwordLabel = new JLabel("Password")
    passwordLabel.setBounds(10, 50, 80, 25)
    panel.add(passwordLabel)

    val passwordText = new JPasswordField(20)
    passwordText.setBounds(100, 50, 165, 25)
    panel.add(passwordText)
    */


  }



  class SharedListSelectionHandler(list1: JList[ShortFile], list2: JList[ShortFile]) extends ListSelectionListener {
    override def valueChanged(e: ListSelectionEvent): Unit = {
      val lsm = e.getSource.asInstanceOf[ListSelectionModel]
      //val firstIndex = e.getFirstIndex
      //val lastIndex = e.getLastIndex
      //val isAdjusting = e.getValueIsAdjusting

      if (!lsm.isSelectionEmpty) {
        val minIndex = lsm.getMinSelectionIndex
        val i = minIndex
        println("Selected: " + i)
        list1.setSelectedIndex(i)
        list2.setSelectedIndex(i)
      }
    }
  }

  case class ClearActionListener(model: DefaultListModel[ShortFile]) extends ActionListener {
      override def actionPerformed(e: ActionEvent) : Unit = {
        model.clear()
      }
  }

  case class ShortFile(uri: URI) extends File(uri) {
    override def toString(): String = {
        this.getName
    }
  }

  case class ListHandler(model: DefaultListModel[ShortFile]) extends TransferHandler {

    import javax.swing.TransferHandler.TransferSupport
    import java.io.File

    import java.net.URI
    import java.awt.datatransfer.DataFlavor

    override def canImport(support: TransferHandler.TransferSupport): Boolean = {
      if (!support.isDrop) return false
      support.isDataFlavorSupported(DataFlavor.stringFlavor)
    }

    override def importData(support: TransferHandler.TransferSupport): Boolean = {
      if (!canImport(support)) return false
      val transferable = support.getTransferable
      var line = ""
      try
        line = transferable.getTransferData(DataFlavor.stringFlavor).asInstanceOf[String]
      catch {
        case e: Exception =>
          return false
      }
      val dl = support.getDropLocation.asInstanceOf[JList.DropLocation]
      var index = dl.getIndex
      val data = line.split("[,\\s]")
      for (item <- data) {
        if (!item.trim.isEmpty) {
          model.add(model.getSize,new ShortFile(new URI(item.trim)))
          println(item)

        }
      }
      true
    }
  }

}
