package com.demo.badge

import org.gradle.api.Plugin
import org.gradle.api.Project

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

class Maker implements Plugin<Project> {

  final static TASK_NAME = "addDebugBadge"
  final static DEFAULT_LABEL = "Debug"
  final static EXTENSIONS_PARM = "badge"
  // 清单文件，文件路径
  final static FEST_PATH = "src/main/AndroidManifest.xml"

  @Override void apply(Project project) {

    project.extensions.create(EXTENSIONS_PARM, BadgeExtension)

    project.task(TASK_NAME) << {

      String label = "$project.badge.label"

      if (label == null || label.empty) {
        label = DEFAULT_LABEL
      }

      println project.projectDir.getAbsolutePath()

      String[] str = getAppIconInfo(new File(project.projectDir, FEST_PATH))

      if (str == null) {
        return
      }

      String iconDirPrefix = str[0].replace("@", "")
      String iconFileName = str[1]

      File releaseResDir = new File(project.projectDir, "src/main/res")

      for (File releaseImgDir : releaseResDir.listFiles()) {
        if (releaseImgDir.name.startsWith(iconDirPrefix)) {

          // 创建 Debug Res 文件夹
          File iconDir = new File(project.projectDir, "src/debug/res/" + releaseImgDir.name)
          iconDir.mkdirs()

          File releaseImg = new File(releaseImgDir, iconFileName + ".png")

          if (!releaseImg.exists()) {
            println "Error !!! NO SUCH FILE : " + releaseImg.absolutePath
            continue
          }

          File debugImg = new File(iconDir, iconFileName + ".png")

          generateImg(releaseImg, debugImg, label)
        }
      }
    }
  }

  /**
   * 新建 Debug 版本的 ICON 图标
   * @param srcImg ----  Release 版本的图标文件名
   * @param dstImg ----  Debug 版本的图标文件名
   * @param label -----  Debug 版本图标上要显示的文字信息
   */
  public static void generateImg(File srcImg, File dstImg, String label) {

    BufferedImage image;
    image = ImageIO.read(srcImg);

    int width = image.getWidth();
    int height = image.getHeight();

    BufferedImage newImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = newImg.createGraphics();
    g2d.drawImage(image, 0, 0, null);

    FontMetrics fm = null

    for (int i = 10; i < 50; ++i) {
      g2d.setFont(new Font("Serif", Font.PLAIN, i));
      fm = g2d.getFontMetrics();

      if (fm.getHeight() * 4 > newImg.getHeight()) {
        // 文字的高度需大于整张图片 1/4
        break
      }
    }

    int x = newImg.getWidth() - fm.stringWidth(label) - 2;
    int y = newImg.getHeight() - fm.getAscent();

    g2d.setPaint(Color.RED);
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.fillRect(x - 4, y, newImg.getWidth() - x + 4, newImg.getHeight() - y);
    g2d.setPaint(Color.WHITE);
    g2d.setColor(Color.WHITE);
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.drawString(label, x, newImg.getHeight() - fm.getDescent());
    g2d.dispose();

    // 保存生成的图片
    ImageIO.write(newImg, "png", dstImg);
  }

  // get app icon info
  public static String[] getAppIconInfo(File festFile) {

    if (!festFile.exists()) {
      println "Error, NO SUCH FILE " + festFile.absolutePath
      return null
    }

    def fest = new XmlSlurper().parse(festFile)
    String label = fest.application[0]['@android:icon']
    return label.split('/')
  }

  /*
  public static void main(String[] args) {
    File file = new File("./app/src/main/AndroidManifest.xml")
    if (file.exists()) {
      println getAppIconInfo(file)
    }
  }
  */
}