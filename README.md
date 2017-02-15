## debug badge

在 Debug app 的图标上显示版本信息。

![screenshot](attr/badge.png)


Add it to your project!

```gradle
buildscript {
  repositories {
    mavenCentral()
  }

  dependencies {
    classpath 'com.github.yuebinyun.debug-badge:debug-badge:0.1.0'
  }
}

apply plugin: 'com.android.application'
apply plugin: 'com.yuebinyun.badge'

......
......

badge {
  //  label = "${project.android.defaultConfig.versionName}"
  // label = "Debug"
  label = "Dev"
}

preBuild.dependsOn addDebugBadge
```