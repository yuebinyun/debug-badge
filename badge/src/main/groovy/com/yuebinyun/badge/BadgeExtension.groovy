package com.yuebinyun.badge;

public class BadgeExtension {

  def String label = "Debug"
  def Integer labelColor = -1
  def Integer labelBgColor = -1

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("BadgeExtension{");
    sb.append("label='").append(label).append('\'');
    sb.append(", labelColor=").append(labelColor);
    sb.append(", labelBgColor=").append(labelBgColor);
    sb.append('}');
    return sb.toString();
  }
}
