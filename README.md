## Add badge(version code, version name, etc) for your **DEBUG APK**.

![screenshot](attr/badge.png)

Add it to your project!

```gradle
buildscript {
  repositories {
    mavenCentral()
  }

  dependencies {
    classpath 'com.github.yuebinyun.debug-badge:debug-badge:0.1.1'
  }
}

apply plugin: 'com.android.application'
apply plugin: 'com.yuebinyun.badge'

......
......

badge {
  /* The label you want to show*/
  // label = "${project.android.defaultConfig.versionCode}"
  // label = "${project.android.defaultConfig.versionName}"
  // label = "Debug"
  label = "Dev"

  // update 2017/02/15
  labelColor = 0x000000 // optional.  Default color is WHITE
  labelBg = 0x0099FF // optional.  Defualt color is RED
}

preBuild.dependsOn addDebugBadge
```