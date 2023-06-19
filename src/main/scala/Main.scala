import jiconfont.icons.font_awesome.FontAwesome
import jiconfont.swing.IconFontSwing
import mdlaf.MaterialLookAndFeel
import mdlaf.themes.MaterialOceanicTheme
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.SystemUtils

import java.awt.datatransfer.DataFlavor
import java.awt.event.{ActionEvent, ActionListener, ComponentAdapter, ComponentEvent, KeyEvent, MouseAdapter, MouseEvent, WindowEvent, WindowFocusListener, WindowListener, WindowStateListener}
import java.awt.image.BufferedImage
import java.awt.{Color, Component, Dimension, Font, Frame, Image, Insets, KeyEventDispatcher, KeyboardFocusManager, Point, Rectangle}
import java.io.{File, InputStream, OutputStream, PrintStream}
import java.lang.ProcessBuilder
import java.net.URI
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths, StandardCopyOption}
import java.util
import java.util.Collections
import java.util.regex.Pattern
import javax.imageio.ImageIO
import javax.swing.event.{ListSelectionEvent, ListSelectionListener}
import javax.swing.{BorderFactory, DefaultListModel, DefaultListSelectionModel, DropMode, Icon, ImageIcon, JButton, JCheckBox, JFrame, JLabel, JList, JMenu, JMenuBar, JMenuItem, JOptionPane, JPanel, JScrollPane, JSpinner, JTextArea, JTextField, JToolBar, ListSelectionModel, SpinnerNumberModel, SwingConstants, SwingUtilities, Timer, TransferHandler, UIManager, WindowConstants}
import scala.collection.mutable.ListBuffer
import scala.language.postfixOps
import scala.util.Try
import javax.swing.BorderFactory
import javax.swing.UIManager
import java.awt.Color

object Main extends App {

  val stdout = System.out
  val stderr = System.err
  val theme = new MaterialOceanicTheme
  /*val MpvProcessWorkerMpvProcessWorker = new ProcessBuilder().start()*/
  var mpvProc : java.lang.Process = null
  val workingDirectory = new java.io.File(".").getCanonicalPath
  val ImageStream: InputStream = getClass.getResourceAsStream("/icon.png")
  var AppIconInit : BufferedImage = null



  val lookandfeel = new MaterialLookAndFeel(theme)
  UIManager.setLookAndFeel(lookandfeel)
  IconFontSwing.register(FontAwesome.getIconFont)
  try {
    AppIconInit = ImageIO.read(ImageStream)
  }
  catch {
    case _: Throwable => println("error 0x000002a")
  }



  UIManager.put("Menu.border", BorderFactory.createLineBorder(theme.getMenuBackground, 1))
  UIManager.put("MenuBar.border", BorderFactory.createLineBorder(theme.getMenuBackground, 1))
  UIManager.put("MenuItem.border", BorderFactory.createLineBorder(theme.getMenuBackground, 1))

  val menuBar = new JMenuBar()
  menuBar.setMargin(new Insets(0,0,0,0))
  val fileMenu = new JMenu("File")
  fileMenu.setMargin(new Insets(0,0,0,0))
  fileMenu.setMnemonic(KeyEvent.VK_F)
  val eMenuItem = new JMenuItem("Exit")
  eMenuItem.setMnemonic(KeyEvent.VK_E)
  eMenuItem.setToolTipText("Exit application")
  eMenuItem.addActionListener((event) => System.exit(0))
  fileMenu.add(eMenuItem)
  menuBar.add(fileMenu)

  val audioDelayLayout = new RelativeLayout(RelativeLayout.X_AXIS)
  audioDelayLayout.setGap(5)
  audioDelayLayout.setBorderGap(5)

  val audioDelayPanel = new JPanel(audioDelayLayout)
  audioDelayPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),"Audio Delay (ms)"))

  //val audioDelayLabel = new JLabel("Audio Delay",makeIcon(FontAwesome.MUSIC, classOf[JButton]),SwingConstants.RIGHT)
  //audioDelayLabel.setFont(theme.getButtonFont)
  //audioDelayPanel.add(audioDelayLabel)

  val audioDelayText = new JSpinner(new SpinnerNumberModel(0,Int.MinValue,Int.MaxValue,100))
  audioDelayText.setAlignmentX(SwingConstants.RIGHT)
  audioDelayPanel.add(audioDelayText,3 : Float)

  val subDelayLayout = new RelativeLayout(RelativeLayout.X_AXIS)
  subDelayLayout.setGap(5)
  subDelayLayout.setBorderGap(5)

  val subDelayPanel = new JPanel(subDelayLayout)
  subDelayPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),"Sub Delay (ms)"))

  val subDelayText = new JSpinner(new SpinnerNumberModel(0,Int.MinValue,Int.MaxValue,100))
  subDelayText.setAlignmentX(SwingConstants.RIGHT)

  subDelayPanel.add(subDelayText, 3 : Float)

  val optionsLayout = new RelativeLayout(RelativeLayout.X_AXIS)
  optionsLayout.setGap(5)
  optionsLayout.setBorderGap(5)

  val optionsPanel = new JPanel(optionsLayout)
  optionsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),"Convenience Options"))

  val fullscreenOption = new JCheckBox("Fullscreen")
  fullscreenOption.setSelected(true)
  val subsVisible = new JCheckBox("Subs Visible")

  optionsPanel.add(fullscreenOption)
  optionsPanel.add(subsVisible)

  //val subDelayLabel = new JLabel("Sub Delay",makeIcon(FontAwesome.CC, classOf[JButton]),SwingConstants.LEFT)
  //subDelayLabel.setFont(theme.getButtonFont)


  val label1 = new JLabel("")
  label1.setFont(theme.getButtonFont)
  val label2 = new JLabel("")
  label2.setFont(theme.getButtonFont)
  val label3 = new JLabel("")
  label3.setFont(theme.getButtonFont)

  val fontsLayout = new RelativeLayout(RelativeLayout.X_AXIS)
  fontsLayout.setGap(10)
  fontsLayout.setBorderGap(10)

  val fontsPanel = new JPanel(fontsLayout)
  fontsPanel.setTransferHandler(new FontsHandler)

  val fontsText = new JTextField("")
  fontsText.setEditable(false)
  fontsText.setTransferHandler(new FontsHandler)

  val fontsLabel = new JLabel("Fonts Folder",makeIcon(FontAwesome.FONT,classOf[JButton]),SwingConstants.LEFT)
  fontsLabel.setTransferHandler(new FontsHandler)
  //fontsText.setPreferredSize(new Dimension(Int.MaxValue,Int.MaxValue))
  fontsPanel.add(fontsLabel)

  val test : Float = 3
  fontsPanel.add(fontsText,test)

  val fontsClear = new JButton(makeIcon(FontAwesome.TRASH,classOf[JButton]))

  fontsClear.addActionListener((actionEvent: ActionEvent) => fontsText.setText(""))

  fontsPanel.add(fontsClear)

 /* val fontsPanel = new MyPanel(
    Array(fontsLabel, fontsText),
    fontsText
  )*/
  fontsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),"Fonts Inject"))

  /*val audioDelaySpinnerLabel = new JLabel("Audio Delay",makeIcon(FontAwesome.CLOCK_O, new JButton),SwingConstants.RIGHT)
  audioDelayText.setForeground(theme.getButtonTextColor)
  audioDelayText.setAlignmentX(SwingConstants.LEFT)
  audioDelayText.setBorder(BorderFactory.createLineBorder(theme.getButtonTextColor))*/

  val videoSourceLabel = new JLabel("Video Source",makeIcon(FontAwesome.FILE_VIDEO_O, classOf[JButton]),SwingConstants.LEFT)
  val audioSourceLabel = new JLabel("Audio Source",makeIcon(FontAwesome.FILE_AUDIO_O, classOf[JButton]),SwingConstants.LEFT)
  val subSourceLabel = new JLabel("Subs Source",makeIcon(FontAwesome.FILE_TEXT_O, classOf[JButton]),SwingConstants.LEFT)




  val videoModel = new SortableListModel[ShortFile]
  val audioModel = new SortableListModel[ShortFile]
  val subModel = new SortableListModel[ShortFile]
  val videoList = new BackgroundImageList[ShortFile](videoModel,IconFontSwing.buildImage(FontAwesome.FILE_VIDEO_O,800, theme.getButtonTextColor ))
  val audioList = new BackgroundImageList[ShortFile](audioModel,IconFontSwing.buildImage(FontAwesome.FILE_AUDIO_O,800, theme.getButtonTextColor ))
  val subList =  new BackgroundImageList[ShortFile](subModel,IconFontSwing.buildImage(FontAwesome.FILE_TEXT_O,800, theme.getButtonTextColor ))

  //videoList.setOpaque(false)
  //videoList.setBackground(new Color(0, 0, 0, 0))
  //videoList.setForeground(Color.WHITE)

  videoList.setSelectionBackground(Color.BLUE)
  videoList.setDropMode(DropMode.INSERT)
  videoList.setTransferHandler(ListHandler(videoModel))
  //videoList.setBorder(BorderFactory.createLineBorder(Color.black))
  videoList.setSelectionModel(new ToggleSelectionModel(videoList,audioList,subList))
  //videoList.getSelectionModel.addListSelectionListener(new SharedListSelectionHandler(audioList, subList))
  videoList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
  videoList.addMouseListener(new RightClickMouseAdapter(videoList))

  val videoListScrollPane = new JScrollPane
  videoListScrollPane.setOpaque(false)
  videoListScrollPane.getViewport.setOpaque(false)
  videoListScrollPane.add(videoList)
  videoListScrollPane.setViewportView(videoList)

  audioList.setSelectionBackground(Color.BLUE)
  audioList.setDropMode(DropMode.INSERT)
  audioList.setTransferHandler(ListHandler(audioModel))
  //audioList.setBorder(BorderFactory.createLineBorder(Color.black))
  audioList.setSelectionModel(new ToggleSelectionModel(audioList,videoList, subList))
  //audioList.getSelectionModel.addListSelectionListener(new SharedListSelectionHandler(videoList,subList))
  audioList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
  audioList.addMouseListener(new RightClickMouseAdapter(audioList))

  val audioListScrollPane = new JScrollPane
  audioListScrollPane.setOpaque(false)
  audioListScrollPane.getViewport.setOpaque(false)
  audioListScrollPane.add(audioList)
  audioListScrollPane.setViewportView(audioList)

  subList.setSelectionBackground(Color.BLUE)
  subList.setDropMode(DropMode.INSERT)
  subList.setTransferHandler(ListHandler(subModel))
  //subList.setBorder(BorderFactory.createLineBorder(Color.black))
  subList.setSelectionModel(new ToggleSelectionModel(subList,videoList,audioList))
  //subList.getSelectionModel.addListSelectionListener(new SharedListSelectionHandler(videoList,audioList))
  subList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
  subList.addMouseListener(new RightClickMouseAdapter(subList))

  val subListScrollPane = new JScrollPane
  subListScrollPane.setOpaque(false)
  subListScrollPane.getViewport.setOpaque(false)
  subListScrollPane.add(subList)
  subListScrollPane.setViewportView(subList)

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

  val playIcon: ImageIcon =  new ImageIcon(AppIconInit)
  val playButton = new JButton("", playIcon)

  playButton.setFont(playButton.getFont.deriveFont(30: Float).deriveFont(Font.BOLD))
  //playButton.setFont(DEFAULT_FONT
  //playButton.setHorizontalTextPosition(SwingConstants.RIGHT)
  //playButton.setHorizontalAlignment(SwingConstants.LEFT)
  playButton.addActionListener((_: ActionEvent) => { play() })

  val textArea = new JTextArea()
  textArea.setBackground(Color.BLACK)
  textArea.setForeground(Color.LIGHT_GRAY)
  textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12))

  val consoleScrollPane = new JScrollPane
  consoleScrollPane.add(textArea)
  consoleScrollPane.setViewportView(textArea)

  System.setOut(new PrintStream(DualOutputStream(false)))
  System.setErr(new PrintStream(DualOutputStream(true)))


  val frame = new JFrame("AnimeTool")
  if (AppIconInit != null)
  {
    frame.setIconImage(AppIconInit)
  }
  //frame.addListeners()
  //mdl-af.MaterialLookAndFeel.changeTheme(new MaterialLiteTheme)

  frame.setSize(800, 800)
  frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)


  val panel = new JPanel
  frame.setJMenuBar(menuBar)
  frame.add(panel)
  frame.setVisible(true)
  frame.addComponentListener(ResizeListener())
  placeComponents(panel)

  frame.setTitle("AnimeTool [" + checkMpv() + "]")
  println("AnimeTool init: " + workingDirectory)



  KeyboardFocusManager.getCurrentKeyboardFocusManager.addKeyEventDispatcher(new KeyEventDispatcher() {
    override def dispatchKeyEvent(e: KeyEvent): Boolean = {
      //System.out.println("Got key event: " + e.toString )

      if (e.getID == KeyEvent.KEY_PRESSED) {
        val result = e.getKeyCode match {
          case KeyEvent.VK_DOWN =>
            selectAnyList(1)
            true /* block futher processing of events cuz we catched it */
          case KeyEvent.VK_UP =>
            selectAnyList(-1)
            true /* block futher processing of events cuz we catched it */
          case KeyEvent.VK_SPACE => play()
          case KeyEvent.VK_ENTER => play()
          case _ => false /* returning false so UI can process futher */
        }
        result
      } else {
        false
      }
    }
  })

  def selectAnyList(increment: Int): Unit = {
    if (videoModel.size() > 0) {
      if (videoList.getSelectedIndex >= 0)
        videoList.setSelectedIndex(videoList.getSelectedIndex + increment)
      else
        videoList.setSelectedIndex(0)
    } else if (audioModel.size() > 0) {
      if (audioList.getSelectedIndex >= 0)
        audioList.setSelectedIndex(audioList.getSelectedIndex + increment)
      else
        audioList.setSelectedIndex(0)
    } else if (subModel.size() > 0) {
      if (subList.getSelectedIndex >= 0)
        subList.setSelectedIndex(subList.getSelectedIndex + increment)
      else
        subList.setSelectedIndex(0)
    }
  }

  def checkMpv() = {
    try {
      val args = ListBuffer[String]("mpv", "--version")

      val process = new ProcessBuilder(args.toSeq: _*).start()

      val result = IOUtils.toString(process.getInputStream, StandardCharsets.UTF_8)
      process.waitFor()

        try {
              val p = Pattern.compile(".*\\b(mpv [^ ]+?) .*")
              val m = p.matcher(result)
              if (m.find()) {
                println(m.group(1))
                m.group(1)
              }
              else {  "mpv" }

        }
        catch { case _: Throwable => println("error 0x000002b :mpv")}
      }
      catch {
        case _: Throwable =>
          val errortext = "\nОшибка 0x000001! это же очевидно как ее решить!!!\nMPV cant be found in PATH\nPlease check if MPV is installed to working directory ("+workingDirectory+") or added to PATH\nYou cant watch your anime if you dont have a video player (obviously)\n"
          val title = "Fatal Error 0x000001"
          if (AppIconInit != null) {
            JOptionPane.showMessageDialog(
              frame,
              errortext,
              title,
              JOptionPane.ERROR_MESSAGE,
              new ImageIcon(resize(AppIconInit, 128, 128))
            )
          } else {
            JOptionPane.showMessageDialog(
              frame,
              errortext,
              title,
              JOptionPane.ERROR_MESSAGE
            )
          }
          System.exit(1)
          ""
      }
  }

  def play() : Boolean = {
    if (videoList.getSelectedIndex < 0) {
      println("You havent selected any video file")
      return false
    }

    val args = ListBuffer[String]("mpv", videoList.getSelectedValue.getAbsolutePath)

    if (audioList.getSelectedIndex >= 0) { args += "--audio-file=" + audioList.getSelectedValue.getAbsolutePath}
    if (subList.getSelectedIndex >= 0) {args += "--sub-file=" + subList.getSelectedValue.getAbsolutePath}
    if (subList.getSelectedIndex >= 0) {args += "--sub-file=" + subList.getSelectedValue.getAbsolutePath}
    if (audioDelayText.getValue.toString.trim.nonEmpty) {args += "--audio-delay=" + audioDelayText.getValue.toString.trim.toFloat / 1000}
    if (subDelayText.getValue.toString.trim.nonEmpty) {args += "--sub-delay=" + subDelayText.getValue.toString.trim.toFloat / 1000}
    if (fullscreenOption.isSelected){args += "--fs"}
    if (!subsVisible.isSelected) {args += "--no-sub-visibility"}
    if (!fontsText.getText.trim.isEmpty) { args += "--sub-fonts-dir=" + fontsText.getText.trim }

    killPrevMpv
    println("Summoning your waifu... " + args.toString() + "\n")


    new Thread(new Runnable {
      override def run(): Unit = {
        //example : val process = new ProcessBuilder("java", "-version").start
        //val MpvProcessWorker = new ProcessBuilder(args.toSeq:_*).redirectOutput(ProcessBuilder.Redirect.INHERIT).redirectError(ProcessBuilder.Redirect.INHERIT).start()
        //MpvProcessWorker.waitFor()
        mpvProc = new ProcessBuilder(args.toSeq:_*).redirectOutput(ProcessBuilder.Redirect.INHERIT).redirectError(ProcessBuilder.Redirect.INHERIT).start()
        mpvProc.waitFor()
      }
    }).start()

    true
  }

  def killPrevMpv: Unit = {
    if (mpvProc != null) {
      try {
        mpvProc.destroy()
      }
      catch { case _: Throwable => println("error 0x000002d") }
    }
  }

  /**
   * Converts a given Image into a BufferedImage
   *
   * @param img The Image to be converted
   * @return The converted BufferedImage
   */
  def toBufferedImage(img: Image): BufferedImage = {
    img match {
      case image: BufferedImage => return image
      case _ =>
    }
    // Create a buffered image with transparency
    val bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB)
    // Draw the image on to the buffered image
    val bGr = bimage.createGraphics
    bGr.drawImage(img, 0, 0, null)
    bGr.dispose()
    // Return the buffered image
    bimage
  }

  def resize(inputImage: BufferedImage, scaledWidth: Int, scaledHeight: Int): BufferedImage = { // reads input image
    // creates output image
    val outputImage = new BufferedImage(scaledWidth, scaledHeight, inputImage.getType)
    // scales the input image to the output image
    val g2d = outputImage.createGraphics
    g2d.drawImage(inputImage, 0, 0, scaledWidth, scaledHeight, null)
    g2d.dispose()
    outputImage
  }

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
      val lastMargin = 30
      val actualHeight =
        if (height < 0)
          { (frame.getHeight - (rectangle.y + rectangle.height + verticalPadding)) - verticalPadding - lastMargin }
        else
          { height }
      new Rectangle(rectangle.x, rectangle.y + rectangle.height + verticalPadding, rectangle.width, actualHeight )
  }

  private def placeComponents(panel: JPanel): Unit = {
    panel.setLayout(null)
    panel.removeAll()
    val genericPaddingLeft = 5
    val verticalPadding = 5
    val startRect = new Rectangle(genericPaddingLeft, 0, panel.getWidth - genericPaddingLeft, 1)
    //val menuBarRect = getNextBounds(40,verticalPadding,startRect)

    //menuBar.setBounds(0,0,1,1)
    //menuBar.setPreferredSize(new Dimension(menuBarRect.getSize()))
    //menuBar.setBounds(menuBarRect)
    //menuBar.setBackground(Color.BLUE)

    val playRect = getNextBounds(58,verticalPadding,startRect)

    playButton.setBounds(getBoundsInBounds(0,3,genericPaddingLeft,playRect))

    if (playIcon != null)
      { playIcon.setImage(resize(AppIconInit, playRect.height*2/3, playRect.height*2/3)) }

    panel.add(playButton)

    val allDelayContainerRect = getBoundsInBounds(1,3,genericPaddingLeft,playRect)
    val audioDelayContainerRect = getBoundsInBounds(0,2,0,allDelayContainerRect)

    audioDelayPanel.setBounds(audioDelayContainerRect)
    panel.add(audioDelayPanel)

    val optionsContainerRect = getBoundsInBounds(2,3,genericPaddingLeft,playRect)
    optionsPanel.setBounds(optionsContainerRect)
    panel.add(optionsPanel)

    /*audioDelayLabel.setBounds(getBoundsInBounds(0, 2,genericPaddingLeft,audioDelayContainerRect))
    panel.add(audioDelayLabel)
    audioDelaySpinnerLabel.setBounds(getBoundsInBounds(1, 3,genericPaddingLeft,audioDelayContainerRect))
    panel.add(audioDelaySpinnerLabel)
    audioDelayText.setBounds(getBoundsInBounds(1, 2,genericPaddingLeft,audioDelayContainerRect))
    panel.add(audioDelayText)*/

    val subDelayContainerRect = getBoundsInBounds(1,2,0,allDelayContainerRect)
    subDelayPanel.setBounds(subDelayContainerRect)
    panel.add(subDelayPanel)
   // subDelayText.setBounds(getBoundsInBounds(1, 2,genericPaddingLeft,subDelayContainerRect))
   // panel.add(subDelayText)

    var buttonsRect : Rectangle = null
    val cLabelsRect = getNextBounds(70, verticalPadding, playRect)
    fontsPanel.setBounds(cLabelsRect)
    panel.add(fontsPanel)
    buttonsRect = getNextBounds(40,verticalPadding,cLabelsRect)
    //fontsPanel.replaceAll()

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

    val listLabelsRect = getNextBounds(20,verticalPadding,buttonsRect)
    val videoSourceLabelRect = getBoundsInBounds(0, 3, genericPaddingLeft, listLabelsRect)
    videoSourceLabel.setBounds(videoSourceLabelRect)
    panel.add(videoSourceLabel)
    val audioSourceLabelRect = getBoundsInBounds(1, 3, genericPaddingLeft, listLabelsRect)
    audioSourceLabel.setBounds(audioSourceLabelRect)
    panel.add(audioSourceLabel)
    val subSourceLabelRect = getBoundsInBounds(2, 3, genericPaddingLeft, listLabelsRect)
    subSourceLabel.setBounds(subSourceLabelRect)
    panel.add(subSourceLabel)

    val consoleHeight = 120
    val listRect = getNextBounds(-1,verticalPadding,listLabelsRect)

    listRect.height -= (consoleHeight + verticalPadding)

    videoListScrollPane.setBounds(getBoundsInBounds(0,3,genericPaddingLeft, listRect))
    panel.add(videoListScrollPane)

    audioListScrollPane.setBounds(getBoundsInBounds(1,3,genericPaddingLeft, listRect))
    panel.add(audioListScrollPane)

    subListScrollPane.setBounds(getBoundsInBounds(2,3,genericPaddingLeft, listRect))
    panel.add(subListScrollPane)

    val consoleRect = getNextBounds(-1, verticalPadding,listRect)
    consoleRect.width -= genericPaddingLeft
    consoleScrollPane.setBounds(consoleRect)
    panel.add(consoleScrollPane)

    frame.revalidate()
  }

  class SharedListSelectionHandler(list1: JList[ShortFile], list2: JList[ShortFile]) extends ListSelectionListener {
    override def valueChanged(e: ListSelectionEvent): Unit = {
      val lsm = e.getSource.asInstanceOf[ListSelectionModel]
      if (!lsm.isSelectionEmpty) {
        val minIndex = lsm.getMinSelectionIndex
        val i = minIndex
        println("Selected: " + i)
        if (list1.getModel.getSize - 1 < i)
          { list1.clearSelection() }
        else
          { list1.setSelectedIndex(i) }

        if (list2.getModel.getSize -1 < i)
          { list2.clearSelection() }
        else
          { list2.setSelectedIndex(i) }
      }
    }
  }

  class RightClickMouseAdapter(list: JList[ShortFile]) extends MouseAdapter {
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
        /*if (isSelectedIndex(index0)) {
          println(index0)
          if (!frozen)
            { super.setSelectionInterval(index0, index1) }

        } else { */
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
  //  }
    /*
    override def setValueIsAdjusting(isAdjusting: Boolean): Unit = {
      if (isAdjusting == false) gestureStarted = false
    }
    */
  }

  case class ClearActionListener(model: SortableListModel[ShortFile]) extends ActionListener {
      override def actionPerformed(e: ActionEvent): Unit = {
        model.clear()
      }
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
        { return }
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
      if (i == list.getModel.getSize -1) { return }
      val model = list.getModel.asInstanceOf[SortableListModel[ShortFile]]
      val swap = model.get(i)
      model.set(i, model.get(i+1))
      model.set(i+1,swap)
      list.setSelectedIndex(i+1)
    }
  }

  class SortableListModel[ShortFile] extends DefaultListModel[ShortFile] {
    def sort(): Unit = {
      val list = new util.ArrayList[ShortFile]()
      for (i <- 0 until this.getSize) {
        list.add(this.getElementAt(i))
        Collections.sort(list, (t: ShortFile, t1: ShortFile) => t.toString.compareTo(t1.toString))
      }
      for (i <- 0 until list.size()) {
        this.set(i,list.get(i))
      }
    }
  }

  case class ShortFile(uri: URI) extends File(uri) {
    override def toString: String = this.getName
  }

  class FontsHandler extends TransferHandler {
    override def canImport(support: TransferHandler.TransferSupport): Boolean = {
      val result = support.isDrop && (support.isDataFlavorSupported(DataFlavor.javaFileListFlavor) || support.isDataFlavorSupported(DataFlavor.stringFlavor) )
      result
    }

    override def importData(support: TransferHandler.TransferSupport): Boolean = canImport(support) && {
      val transferable = support.getTransferable
      val try_files = Try(transferable.getTransferData(DataFlavor.javaFileListFlavor))
        .map {
          case list: util.AbstractList[File] =>
            println(list)
            list.toArray( Array[File]() )
        }
      val try_strip = Try(transferable.getTransferData(DataFlavor.stringFlavor))
        .map {
          case text: String =>
            text.split("\n").map(l => {
              //println(l.trim)
              val file = new File(l.trim.replaceFirst("file://",""))
              if (file.exists())
                file
              else
                throw new Exception("File " + file + " not exists")
            }
            )
            // println(text)
        }

      val try_uris = Try(transferable.getTransferData(DataFlavor.stringFlavor))
        .map {
          case text: String =>
            text.split("\n").map(l => {
              //println(l.trim)
              val uri = new URI(l.trim)
              val file = new File(uri)
              if (file.exists())
                file
              else
                throw new Exception("URI " + uri + " not exists")
            }
            )
            // println(text)
        }
      val file_list = try_files.orElse(try_strip).orElse(try_uris).getOrElse(
        throw new Exception("Dragndrop failure")
      )
      for (item <- file_list) {
        if (item.exists()) {
          fontsText.setText(item.getAbsolutePath)
          println("Added fonts dir: " + item.getAbsolutePath)
        } else {
          println("Error: Fonts dir doesnt exist: " + item.getAbsolutePath)
        }
      }
      true
    }
  }

  case class ListHandler(model: SortableListModel[ShortFile]) extends TransferHandler {
    override def canImport(support: TransferHandler.TransferSupport): Boolean = {
      val result = support.isDrop && (support.isDataFlavorSupported(DataFlavor.javaFileListFlavor) || support.isDataFlavorSupported(DataFlavor.stringFlavor) )
      result
    }

    override def importData(support: TransferHandler.TransferSupport): Boolean = canImport(support) && {
        val transferable = support.getTransferable
        val try_files = Try(transferable.getTransferData(DataFlavor.javaFileListFlavor))
          .map {
            case list: util.AbstractList[File] =>
              println(list)
              list.toArray( Array[File]() )
          }
        val try_strip = Try(transferable.getTransferData(DataFlavor.stringFlavor))
          .map {
            case text: String =>
              text.split("\n").map(l => {
                //println(l.trim)
                  val file = new File(l.trim.replaceFirst("file://",""))
                  if (file.exists()) {
                    file
                  } else {
                    throw new Exception("File " + file + " not exists")
                  }
              }
              )
              // println(text)
          }

      val try_uris = Try(transferable.getTransferData(DataFlavor.stringFlavor))
        .map {
          case text: String =>
            text.split("\n").map(l => {
              //println(l.trim)
              val uri = new URI(l.trim)
              val file = new File(uri)
              if (file.exists())
                file
              else
                throw new Exception("URI " + uri + " not exists")
            }
            )
            // println(text)
        }
        val file_list = try_files.orElse(try_strip).orElse(try_uris).getOrElse(
          throw new Exception("Dragndrop failure")
        )
          for (item <- file_list) {
            if (item.toString.trim.nonEmpty) {
              val file = ShortFile(item.toURI)
              var worked = false;
              if (file.exists && file.isFile) {
                model.add(model.getSize, file)
                println("Imported file: " + file.getAbsolutePath)
                worked = true;
              }
              if (worked && model.equals(subModel)) {
                subsVisible.setSelected(true)
              }
            }
          }
          true
      }
  }

  case class DualOutputStream(error: Boolean) extends OutputStream {
      override def write(b: Int): Unit = {

        try {
          if (error){ stderr.print(b.toChar) }
          else { stdout.print(b.toChar) }
        }
        catch { case _: Throwable => println("error 0x000002e")}

        try {
          textArea.append(String.valueOf(b.toChar))
          //textArea.setCaretPosition(textArea.getDocument.getLength)
          textArea.getCaret.setDot(Integer.MAX_VALUE)
        }
        catch { case _: Throwable => println("error 0x000002f")}

      }
  }

  class MyPanel extends JPanel {
    private var elements : Array[Component] = null
    private var fill: Component = null

    def this(elements: Array[Component], fill: Component)
    {
      this()
      this.elements = elements
      this.fill = fill
    }

    def replaceAll() = {
      this.removeAll()
      for (element <- elements) {
        element match {
          case x if x == this.fill => this.add (fill)
          case _ => this.add(element)
        }
      }

      this.revalidate()
      var usedWidth = 0
      for (element <- elements) {
        element match {
          case x if x == this.fill =>
          case _ => usedWidth += element.getBounds().width
        }
      }
      val before = this.fill.getBounds
      //println(fill.getBounds)
      this.remove(fill)
      before.width = this.getWidth - usedWidth
      fill.setBounds(before)
      //fill.setPreferredSize(new Dimension(before.width,before.height))
      this.add(fill)
      //println(fill.getBounds)
      this.revalidate()
      //this.repaint()
    }
  }

  class EventJFrame(str: String) extends JFrame(str: String) with WindowListener with WindowFocusListener with WindowStateListener {
    def displayStateMessage(prefix: String, e: WindowEvent): Unit = {
      val state = e.getNewState
      val oldState = e.getOldState
      val msg = prefix + "New state: " + convertStateToString(state) + "Old state: " + convertStateToString(oldState)
      println(msg)
    }
    def addListeners(): Unit = {
      addWindowListener(this)
      addWindowFocusListener(this)
      addWindowStateListener(this)
      checkWM()
    }
    def checkWM(): Unit = {
      val tk = frame.getToolkit
      if (!tk.isFrameStateSupported(Frame.ICONIFIED)) displayMessage("Your window manager doesn't support ICONIFIED.")
      else displayMessage("Your window manager supports ICONIFIED.")
      if (!tk.isFrameStateSupported(Frame.MAXIMIZED_VERT)) displayMessage("Your window manager doesn't support MAXIMIZED_HEIGHT.")
      else displayMessage("Your window manager supports MAXIMIZED_HEIGHT.")
      if (!tk.isFrameStateSupported(Frame.MAXIMIZED_HORIZ)) displayMessage("Your window manager doesn't support MAXIMIZED_WIDTH.")
      else displayMessage("Your window manager supports MAXIMIZED_WIDTH.")
      if (!tk.isFrameStateSupported(Frame.MAXIMIZED_BOTH)) displayMessage("Your window manager doesn't support MAXIMIZED_BOTH.")
      else displayMessage("Your window manager supports MAXIMIZED_BOTH.")
    }
    def windowClosing(e: WindowEvent): Unit = {
      displayMessage("WindowListener method called: windowClosing.")
      //A pause so user can see the message before
      //the window actually closes.
      //val alreadyDisposed = false
      val task = new ActionListener() {
        override
        def actionPerformed(e: ActionEvent): Unit = {
          if (frame.isDisplayable) {
            //val alreadyDisposed = true
            frame.dispose
          }
        }
      }
    }
    def windowClosed(e: WindowEvent): Unit = { //This will only be seen on standard output.
      displayMessage("WindowListener method called: windowClosed.")
    }
    def windowOpened(e: WindowEvent): Unit = {
      displayMessage("WindowListener method called: windowOpened.")
    }
    def windowIconified(e: WindowEvent): Unit = {
      displayMessage("WindowListener method called: windowIconified.")
    }
    def windowDeiconified(e: WindowEvent): Unit = {
      displayMessage("WindowListener method called: windowDeiconified.")
    }
    def windowActivated(e: WindowEvent): Unit = {
      displayMessage("WindowListener method called: windowActivated.")
    }
    def windowDeactivated(e: WindowEvent): Unit = {
      displayMessage("WindowListener method called: windowDeactivated.")
    }
    def windowGainedFocus(e: WindowEvent): Unit = {
      displayMessage("WindowFocusListener method called: windowGainedFocus.")
    }
    def windowLostFocus(e: WindowEvent): Unit = {
      displayMessage("WindowFocusListener method called: windowLostFocus.")
    }
    def windowStateChanged(e: WindowEvent): Unit = {
      val task = new ActionListener() {
        override def actionPerformed(e: ActionEvent): Unit = {
          println(frame.getWidth)
          //placeComponents(panel)
          val bounds = panel.getBounds()
          bounds.width += 1
          panel.setBounds(bounds)
        }
      }
      val timer = new Timer(500, task) //fire every half second
      timer.setInitialDelay(1000) //first delay 2 seconds
      timer.setRepeats(false)
      timer.start()
      displayStateMessage("WindowStateListener method called: windowStateChanged.", e)
    }
    def displayMessage(msg: String): Unit = {
      //display.append(msg + newline)
      println(msg)
    }
    def convertStateToString(state: Int): String = {
      if (state == Frame.NORMAL) return "NORMAL"
      var strState = " "
      if ((state & Frame.ICONIFIED) != 0) strState += "ICONIFIED"
      //MAXIMIZED_BOTH is a concatenation of two bits, so
      //we need to test for an exact match.
      if ((state & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) strState += "MAXIMIZED_BOTH"
      else {
        if ((state & Frame.MAXIMIZED_VERT) != 0) strState += "MAXIMIZED_WIDTH"
        if ((state & Frame.MAXIMIZED_HORIZ) != 0) strState += "MAXIMIZED_HEIGHT"
        if (" " == strState) strState = "UNKNOWN"
      }
      strState.trim
    }
  }

}
