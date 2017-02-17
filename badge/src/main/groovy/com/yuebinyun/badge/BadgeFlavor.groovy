package com.yuebinyun.badge;

public class BadgeFlavor {

  final String name;
  def String label = "Debug"
  def Integer labelColor = -1
  def Integer labelBgColor = -1

  public BadgeFlavor(String name) {
    this.name = name;
  }

  @Override public String toString() {
    final StringBuffer sb = new StringBuffer("BadgeFlavor{");
    sb.append("name='").append(name).append('\'');
    sb.append(", String=").append(String);
    sb.append(", Integer=").append(Integer);
    sb.append(", Integer=").append(Integer);
    sb.append('}');
    return sb.toString();
  }
}
