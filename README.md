## debug badge

Add badge for app icon.

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