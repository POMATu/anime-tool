import java.awt.datatransfer.DataFlavor
import java.awt.event._
import java.awt._
import java.io.File
import java.lang.reflect.Type
import java.net.URI

import sys.process._
import javax.swing._
import mdlaf.themes.{AbstractMaterialTheme, JMarsDarkTheme, MaterialLiteTheme, MaterialOceanicTheme}
import javax.swing.event._
import java.lang.{Comparable, ProcessBuilder}
import java.util
import java.util.{Collections, Comparator}

import javax.swing.GroupLayout.Alignment
import javax.swing.{DefaultListSelectionModel, JLabel}
import jiconfont.icons.font_awesome.FontAwesome
import mdlaf.MaterialLookAndFeel

import scala.collection.mutable.ListBuffer
import jiconfont.swing.IconFontSwing

object Main extends App {

  val theme = new MaterialOceanicTheme
  //theme.getBorderList.
  UIManager.setLookAndFeel(new MaterialLookAndFeel(theme))
  IconFontSwing.register(FontAwesome.getIconFont)


  val audioDelayLabel = new JLabel("Audio Delay",makeIcon(FontAwesome.MUSIC, classOf[JButton]),SwingConstants.LEFT)
  audioDelayLabel.setFont(theme.getButtonFont)
  val subDelayLabel = new JLabel("Sub Delay",makeIcon(FontAwesome.CC, classOf[JButton]),SwingConstants.LEFT)
  subDelayLabel.setFont(theme.getButtonFont)

  val clabel1 = new JLabel("")
  clabel1.setFont(theme.getButtonFont)
  val clabel2 = new JLabel("")
  clabel2.setFont(theme.getButtonFont)
  val clabel3 = new JLabel("")
  clabel3.setFont(theme.getButtonFont)





  //val audioDelaySpinnerLabel = new JLabel("Audio Delay",makeIcon(FontAwesome.CLOCK_O, new JButton),SwingConstants.RIGHT)
  val audioDelayText = new JSpinner()
  audioDelayText.setAlignmentX(SwingConstants.RIGHT)
  //audioDelayText.setForeground(theme.getButtonTextColor)
  //audioDelayText.setAlignmentX(SwingConstants.LEFT)
  //audioDelayText.setBorder(BorderFactory.createLineBorder(theme.getButtonTextColor))
  val subDelayText = new JSpinner()
  subDelayText.setAlignmentX(SwingConstants.RIGHT)

  val videoModel = new SortableListModel[ShortFile]
  val audioModel = new SortableListModel[ShortFile]
  val subModel = new SortableListModel[ShortFile]
  val videoList = new JList[ShortFile](videoModel)
  val audioList = new JList[ShortFile](audioModel)
  val subList =  new JList[ShortFile](subModel)

  videoList.setDropMode(DropMode.INSERT)
  videoList.setTransferHandler(ListHandler(videoModel))
  //videoList.setBorder(BorderFactory.createLineBorder(Color.black))
  videoList.setSelectionModel(new ToggleSelectionModel(videoList,audioList,subList))
  //videoList.getSelectionModel.addListSelectionListener(new SharedListSelectionHandler(audioList, subList))
  videoList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
  videoList.addMouseListener(new RightClickMouseAdapter(videoList))

  audioList.setDropMode(DropMode.INSERT)
  audioList.setTransferHandler(ListHandler(audioModel))
  //audioList.setBorder(BorderFactory.createLineBorder(Color.black))
  audioList.setSelectionModel(new ToggleSelectionModel(audioList,videoList, subList))
  //audioList.getSelectionModel.addListSelectionListener(new SharedListSelectionHandler(videoList,subList))
  audioList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
  audioList.addMouseListener(new RightClickMouseAdapter(audioList))


  subList.setDropMode(DropMode.INSERT)
  subList.setTransferHandler(ListHandler(subModel))
  //subList.setBorder(BorderFactory.createLineBorder(Color.black))
  subList.setSelectionModel(new ToggleSelectionModel(subList,videoList,audioList))
  //subList.getSelectionModel.addListSelectionListener(new SharedListSelectionHandler(videoList,audioList))
  subList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
  subList.addMouseListener(new RightClickMouseAdapter(subList))

  val up1btn = new JButton(makeIcon(FontAwesome.CHEVRON_CIRCLE_UP,classOf[JButton]))
  up1btn.addActionListener(UpActionListener(videoList))

  val up2btn = new JButton(makeIcon(FontAwesome.CHEVRON_CIRCLE_UP, classOf[JButton]))
  up2btn.addActionListener(UpActionListener(audioList))
  val up3btn = new JButton(makeIcon(FontAwesome.CHEVRON_CIRCLE_UP, classOf[JButton]))
  up3btn.addActionListener(UpActionListener(subList))


  val down1btn = new JButton(makeIcon(FontAwesome.CHEVRON_CIRCLE_DOWN, classOf[JButton]))
  down1btn.addActionListener(DownActionListener(videoList))
  val down2btn = new JButton(makeIcon(FontAwesome.CHEVRON_CIRCLE_DOWN, classOf[JButton]))
  down2btn.addActionListener(DownActionListener(audioList))
  val down3btn = new JButton(makeIcon(FontAwesome.CHEVRON_CIRCLE_DOWN, classOf[JButton]))
  down3btn.addActionListener(DownActionListener(subList))


  val clear1btn = new JButton(makeIcon(FontAwesome.FILE, classOf[JButton]))
  clear1btn.addActionListener(ClearActionListener(videoModel))
  val clear2btn = new JButton(makeIcon(FontAwesome.FILE, classOf[JButton]))
  clear2btn.addActionListener(ClearActionListener(audioModel))
  val clear3btn = new JButton(makeIcon(FontAwesome.FILE, classOf[JButton]))
  clear3btn.addActionListener(ClearActionListener(subModel))

  val del1btn = new JButton(makeIcon(FontAwesome.SORT_ALPHA_ASC, classOf[JButton]))
  val del2btn = new JButton(makeIcon(FontAwesome.SORT_ALPHA_ASC, classOf[JButton]))
  del1btn.addActionListener(SortActionListener(videoList, videoModel))

  del2btn.addActionListener(SortActionListener(audioList, audioModel))

  val del3btn = new JButton(makeIcon(FontAwesome.SORT_ALPHA_ASC, classOf[JButton]))
  del3btn.addActionListener(SortActionListener(subList, subModel))

  val playButton = new JButton("Play",makeIcon(FontAwesome.PLAY, classOf[JButton]))
  playButton.addActionListener((_: ActionEvent) => {
    println("Summoning your waifu...")
    new ProcessBuilder(
      "mpv",
      videoList.getSelectedValue.getAbsolutePath,
      "--audio-file=" + audioList.getSelectedValue.getAbsolutePath,
      "--sub-file=" + subList.getSelectedValue.getAbsolutePath,
    ).redirectOutput(ProcessBuilder.Redirect.INHERIT).redirectError(ProcessBuilder.Redirect.INHERIT).start
  })

  val frame = new JFrame("AnimeTool [mpv]")
  //mdlaf.MaterialLookAndFeel.changeTheme(new MaterialLiteTheme)

  frame.setSize(800, 600)
  frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
  val panel = new JPanel

  frame.add(panel)

  placeComponents(panel)

  frame.setVisible(true)
  frame.addComponentListener(ResizeListener())


  private def makeIcon(icon : FontAwesome, element: Any): Icon = {

    val sz = element match {
      case _: JButton => theme.getButtonFont.getSize*1.5
      case _: JLabel =>  theme.getButtonFont.getSize
      case _ => theme.getButtonFont.getSize
    }
    IconFontSwing.buildIcon(icon,Math.round(sz), theme.getButtonTextColor)
  }

  case class ResizeListener() extends ComponentAdapter {
      override def componentResized(e : ComponentEvent): Unit = {
        panel.removeAll()
        placeComponents(panel)
    }
  }

  private def getBoundsInBounds(offset: Int, count: Int, paddingBetween : Int, rectangle: Rectangle) : Rectangle = {
    val relativeX = rectangle.width * offset / count
    val relativeY = 0

    val width = rectangle.width / count - paddingBetween
    val height = rectangle.height

    new Rectangle(rectangle.x + relativeX, rectangle.y + relativeY, width, height)
  }

  private def getBoundsInBoundsV(offset: Int, count: Int, paddingBetween : Int, rectangle: Rectangle) : Rectangle = {
    val relativeY = rectangle.height * offset / count
    val relativeX = 0

    val actualPaddingBeween = if (offset + 1 == count ) 0 else paddingBetween

    val height = rectangle.height / count - actualPaddingBeween
    val width = rectangle.width

    new Rectangle(rectangle.x + relativeX, rectangle.y + relativeY, width, height)
  }

  private def getNextBounds(height: Int, verticalPadding : Int, rectangle: Rectangle): Rectangle = {
      val lastUnfuckMargin = 30
      val actualHeight =
        if (height < 0)
          (frame.getHeight - (rectangle.y + rectangle.height + verticalPadding)) - verticalPadding - lastUnfuckMargin
        else
          height

      new Rectangle(rectangle.x, rectangle.y + rectangle.height + verticalPadding, rectangle.width, actualHeight )
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




    audioDelayLabel.setBounds(getBoundsInBounds(0, 2,genericPaddingLeft,audioDelayContainerRect))
    panel.add(audioDelayLabel)
    //audioDelaySpinnerLabel.setBounds(getBoundsInBounds(1, 3,genericPaddingLeft,audioDelayContainerRect))
    //panel.add(audioDelaySpinnerLabel)
    audioDelayText.setBounds(getBoundsInBounds(1, 2,genericPaddingLeft,audioDelayContainerRect))
    panel.add(audioDelayText)

    val subDelayContainerRect = getBoundsInBounds(2,3,genericPaddingLeft,playRect)
    subDelayLabel.setBounds(getBoundsInBounds(0, 2,genericPaddingLeft,subDelayContainerRect))
    panel.add(subDelayLabel)
    subDelayText.setBounds(getBoundsInBounds(1, 2,genericPaddingLeft,subDelayContainerRect))
    panel.add(subDelayText)

    /*
    val cLabelsRect = getNextBounds(10,verticalPadding,playRect)
    clabel1.setBounds(getBoundsInBounds(0, 3, genericPaddingLeft, cLabelsRect))
    panel.add(clabel1)

    clabel2.setBounds(getBoundsInBounds(1, 3, genericPaddingLeft, cLabelsRect))
    panel.add(clabel2)

    clabel3.setBounds(getBoundsInBounds(2, 3, genericPaddingLeft, cLabelsRect))
    panel.add(clabel3)
    */

    val buttonsRect = getNextBounds(40,verticalPadding,playRect)
    val buttons1Rect = getBoundsInBounds(0, 3, genericPaddingLeft, buttonsRect)
    val buttons2Rect = getBoundsInBounds(1, 3, genericPaddingLeft, buttonsRect)
    val buttons3Rect = getBoundsInBounds(2, 3, genericPaddingLeft, buttonsRect)

    up1btn.setBounds(getBoundsInBounds(0,4,0, buttons1Rect))
    panel.add(up1btn)
    down1btn.setBounds(getBoundsInBounds(1,4,genericPaddingLeft, buttons1Rect))
    panel.add(down1btn)
    del1btn.setBounds(getBoundsInBounds(2,4,0, buttons1Rect))
    panel.add(del1btn)
    clear1btn.setBounds(getBoundsInBounds(3,4,0, buttons1Rect))
    panel.add(clear1btn)

    up2btn.setBounds(getBoundsInBounds(0,4,0, buttons2Rect))
    panel.add(up2btn)
    down2btn.setBounds(getBoundsInBounds(1,4,genericPaddingLeft, buttons2Rect))
    panel.add(down2btn)
    del2btn.setBounds(getBoundsInBounds(2,4,0, buttons2Rect))
    panel.add(del2btn)
    clear2btn.setBounds(getBoundsInBounds(3,4,0, buttons2Rect))
    panel.add(clear2btn)

    up3btn.setBounds(getBoundsInBounds(0,4,0, buttons3Rect))
    panel.add(up3btn)
    down3btn.setBounds(getBoundsInBounds(1,4,genericPaddingLeft, buttons3Rect))
    panel.add(down3btn)
    del3btn.setBounds(getBoundsInBounds(2,4,0, buttons3Rect))
    panel.add(del3btn)
    clear3btn.setBounds(getBoundsInBounds(3,4,0, buttons3Rect))
    panel.add(clear3btn)

    val listRect = getNextBounds(-1,verticalPadding,buttonsRect)

    videoList.setBounds(getBoundsInBounds(0,3,genericPaddingLeft, listRect))
    panel.add(videoList)

    audioList.setBounds(getBoundsInBounds(1,3,genericPaddingLeft, listRect))
    panel.add(audioList)

    subList.setBounds(getBoundsInBounds(2,3,genericPaddingLeft, listRect))
    panel.add(subList)
  }

  class SharedListSelectionHandler(list1: JList[ShortFile], list2: JList[ShortFile]) extends ListSelectionListener {
    override def valueChanged(e: ListSelectionEvent): Unit = {
      val lsm = e.getSource.asInstanceOf[ListSelectionModel]

      if (!lsm.isSelectionEmpty) {
        val minIndex = lsm.getMinSelectionIndex
        val i = minIndex
        println("Selected: " + i)
        if (list1.getModel.getSize - 1 < i)
          list1.clearSelection()
        else
          list1.setSelectedIndex(i)

        if (list2.getModel.getSize -1 < i)
          list2.clearSelection()
        else
          list2.setSelectedIndex(i)
      }
    }
  }

  class RightClickMouseAdapter(list: JList[ShortFile]) extends MouseAdapter {

    import javax.swing.JList
    import javax.swing.SwingUtilities

    override def mousePressed(e: MouseEvent): Unit = {
      if (SwingUtilities.isRightMouseButton(e)) list.getModel.asInstanceOf[SortableListModel[ShortFile]].remove(getRow(e.getPoint))
    }

    private def getRow(point: Point) = list.locationToIndex(point)
  }

  class ToggleSelectionModel(selfList: JList[ShortFile],list1: JList[ShortFile], list2: JList[ShortFile]) extends DefaultListSelectionModel() {

    private var frozen = false

    def freeze(freeze: Boolean) = this.frozen = freeze

    override def setSelectionInterval(index0: Int, index1: Int): Unit = {
      //println(index0)
      //println(index1)
        if (isSelectedIndex(index0)) {
          if (!frozen)
            super.removeSelectionInterval(index0, index1)
        } else {
            super.setSelectionInterval(index0, index1)
            if (!frozen) {
                if (list1.getModel.getSize - 1 < index0) {
                  list1.clearSelection()
                }
                else {
                  list1.getSelectionModel.asInstanceOf[ToggleSelectionModel].freeze(true)
                  list1.setSelectedIndex(index0)
                  list1.getSelectionModel.asInstanceOf[ToggleSelectionModel].freeze(false)
                }

              if (list2.getModel.getSize - 1 < index0) {
                list2.clearSelection()
              }
              else {
                list2.getSelectionModel.asInstanceOf[ToggleSelectionModel].freeze(true)
                list2.setSelectedIndex(index0)
                list2.getSelectionModel.asInstanceOf[ToggleSelectionModel].freeze(false)
              }
            }
        }
    }
/*
    override def setValueIsAdjusting(isAdjusting: Boolean): Unit = {
      if (isAdjusting == false) gestureStarted = false
    }
    */
  }

  case class ClearActionListener(model: SortableListModel[ShortFile]) extends ActionListener {
      override def actionPerformed(e: ActionEvent): Unit = model.clear()
  }

  case class SortActionListener(list: JList[ShortFile], model: SortableListModel[ShortFile]) extends ActionListener {
    override def actionPerformed(e: ActionEvent): Unit = {
       model.sort()
    }
  }

  case class UpActionListener(list: JList[ShortFile]) extends ActionListener {
    override def actionPerformed(e: ActionEvent): Unit = {
      val i =list.getSelectedIndex

      if (i == 0)
        return

      val model = list.getModel.asInstanceOf[SortableListModel[ShortFile]]

      val swap = model.get(i)
      model.set(i, model.get(i-1))
      model.set(i-1,swap)

      list.setSelectedIndex(i-1)
    }
  }

  case class DownActionListener(list: JList[ShortFile]) extends ActionListener {
    override def actionPerformed(e: ActionEvent): Unit = {
      val i =list.getSelectedIndex

      if (i == list.getModel.getSize -1)
        return

      val model = list.getModel.asInstanceOf[SortableListModel[ShortFile]]

      val swap = model.get(i)
      model.set(i, model.get(i+1))
      model.set(i+1,swap)

      list.setSelectedIndex(i+1)
    }
  }

  class SortableListModel[ShortFile] extends DefaultListModel[ShortFile] {

    import java.util.Collections

    def sort(): Unit = {
      val list = new util.ArrayList[ShortFile]()
      for (i <- 0 until this.getSize) list.add(this.getElementAt(i))

      Collections.sort(list, (t: ShortFile, t1: ShortFile) => t.toString.compareTo(t1.toString))

      for (i <- 0 until list.size()) {
        this.set(i,list.get(i))
      }
    }
  }

  case class ShortFile(uri: URI) extends File(uri) {
    override def toString: String = this.getName
  }

  case class ListHandler(model: SortableListModel[ShortFile]) extends TransferHandler {
    override def canImport(support: TransferHandler.TransferSupport): Boolean
      = support.isDrop && support.isDataFlavorSupported(DataFlavor.stringFlavor)

    override def importData(support: TransferHandler.TransferSupport): Boolean =
      canImport(support) && {
      val transferable = support.getTransferable

      transferable.getTransferData(DataFlavor.stringFlavor) match {
        case line: String =>
          val data = line.split("[,\\s]")
          for (item <- data) {
            if (item.trim.nonEmpty) {
              val file = ShortFile(new URI(item.trim))
              if (file.exists && file.isFile) {
                model.add(model.getSize, file)
                println("Imported file: " + item)
              }

            }
          }
          true
        case _ => false
      }
    }
  }

}
