package com.yuebinyun.badge

import org.gradle.api.Plugin
import org.gradle.api.Project

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

class Maker implements Plugin<Project> {

  @Override void apply(Project project) {

    project.extensions.create("badge", BadgeExtension)

    def debugBadges = project.container(BadgeFlavor)
    project.extensions.badgeFlavor = debugBadges

    project.tasks.all() { task ->

      task.doLast {

        if (task.name.equals("processDebugManifest")) {

          // no flavor in this module

          // debug res dir
          File debugDir = new File(project.buildDir, "intermediates/res/merged/debug")

          def BadgeFlavor badge = new BadgeFlavor("debug")
          badge.label = project.extensions.badge.label
          badge.labelColor = project.extensions.badge.labelColor
          badge.labelBgColor = project.extensions.badge.labelBgColor

          // debug version AndroidManifest.xml
          String manifestPath = project.buildDir.absolutePath +
              "/intermediates/manifests/full/debug/AndroidManifest.xml";

          changeAllIconsInMultiDirs(project, debugDir, badge, manifestPath)
          // ---
        } else if (task.name.startsWith("process") && task.name.endsWith("DebugManifest")) {

          String curFlavorName = task.name.replace("process", "").
              replace("DebugManifest", "").
              toLowerCase();
          BadgeFlavor badge = null;
          for (BadgeFlavor temp : project.extensions.badgeFlavor) {
            if (temp.name.toLowerCase().equals(curFlavorName)) {
              badge = temp;
              break
            }
          }

          if (badge == null) {
            // not set badge for current flavor
            println "--> NOT SET BADGE FOR FLAVOR :" + curFlavorName
            return
          }

          // debug res dir
          File debugResDir = new File(project.buildDir,
              "intermediates/res/merged/" + badge.name + "/debug")

          // flavor debug manifest file
          String manifestFile = project.buildDir.absolutePath + "/intermediates/manifests/full/" +
              badge.name +
              "/debug/AndroidManifest.xml"

          changeAllIconsInMultiDirs(project, debugResDir, badge, manifestFile)
        }
      }
    }
  }

  /**
   *
   * @param project
   * @param releaseResDir
   */
  public static void changeAllIconsInMultiDirs(Project project, File debugDir,
      BadgeFlavor badge, String manifestPath) {
    String[] str = getAppIconInfo(new File(manifestPath))
    if (str == null) {
      return;
    }

    String iconDirPrefix = str[0].replace("@", "")
    String iconFileName = str[1]

    for (File releaseImgDir : debugDir.listFiles(new FileFilter() {
      @Override
      boolean accept(File file) {
        return file.name.startsWith(iconDirPrefix)
      }
    })) {
      File debugIcon = new File(debugDir,
          releaseImgDir.name + "/" + iconFileName + ".png")
      if (debugIcon.exists()) {
        println "--> ADD BADGE ON " + debugIcon.absolutePath
        addBadgeOnIcon(debugIcon, badge.label, badge.labelColor, badge.labelBgColor)
      } else {
        println "*** BADGE PLUGIN WARNING : NO SUCH FILE: " + debugIcon.absolutePath
      }
    }
  }

  /**
   * 新建 Debug 版本的 ICON 图标
   * @param icon ----  Release 版本的图标文件名
   * @param dstImg ----  Debug 版本的图标文件名
   * @param label -----  Debug 版本图标上要显示的文字信息
   * @param labelColor - label 文字色
   * @param labelBgColor ---  label 背景色
   */
  public static void addBadgeOnIcon(File icon, String label, Integer labelColor,
      Integer labelBgColor) {

    BufferedImage image;
    image = ImageIO.read(icon);

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

    if (labelBgColor == null || labelBgColor < 0 || labelBgColor > 0xFFFFFF) {
      g2d.setColor(Color.RED)
      g2d.setPaint(Color.RED);
    } else {
      g2d.setPaint(new Color(labelBgColor))
      g2d.setColor(new Color(labelBgColor))
    }

    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.fillRect(x - 4, y, newImg.getWidth() - x + 4, newImg.getHeight() - y);

    if (labelColor == null || labelColor < 0 || labelBgColor > 0xFFFFFF) {
      g2d.setPaint(Color.WHITE);
      g2d.setColor(Color.WHITE);
    } else {
      g2d.setPaint(new Color(labelColor));
      g2d.setColor(new Color(labelColor));
    }

    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.drawString(label, x, newImg.getHeight() - fm.getDescent());
    g2d.dispose();

    // 保存生成的图片
    boolean ret = ImageIO.write(newImg, "png", icon);
    if (!ret) {
      println "*** Failed to add badge"
    }
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