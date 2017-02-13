package com.demo.badge

import org.gradle.api.Plugin
import org.gradle.api.Project

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

class Maker implements Plugin<Project> {

  @Override void apply(Project project) {

    project.extensions.create("badge", BadgeExtension)

    project.task("addDebugBadge") << {

      String label = "$project.badge.label"

      if (label == null || label.empty) {
        label = "Debug"
      }

      println project.projectDir.getAbsolutePath()

      // 创建 Debug Res 文件夹
      File iconDir = new File(project.projectDir, "src/debug/res/mipmap-xxxhdpi")
      iconDir.mkdirs()

      File debugImg = new File(iconDir, "ic_launcher.png")
      File releaseImg = new File(project.projectDir, "src/main/res/mipmap-xxxhdpi/ic_launcher.png")

      BufferedImage image;
      image = ImageIO.read(releaseImg);

      int width = image.getWidth();
      int height = image.getHeight();

      println String.format(Locale.CHINA, "Img width : %d, Img height : %d", width, height)

      BufferedImage newImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
      Graphics2D g2d = newImg.createGraphics();
      g2d.drawImage(image, 0, 0, null);
      g2d.setFont(new Font("Serif", Font.PLAIN, 40));
      FontMetrics fm = g2d.getFontMetrics();

      int x = newImg.getWidth() - fm.stringWidth(label) - 2;
      int y = newImg.getHeight() - fm.getAscent();

      g2d.setPaint(Color.RED);
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2d.fillRect(x - 4, y, fm.stringWidth(label) + 8, fm.getHeight() - 8);

      g2d.setPaint(Color.WHITE);
      g2d.setColor(Color.WHITE);
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2d.drawString(label, x, newImg.getHeight() - fm.getDescent());
      g2d.dispose();

      // 保存生成的图片
      ImageIO.write(newImg, "png", debugImg);
    }
  }
}