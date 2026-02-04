# References Resolver for UG Bot

This repository contains the source code and necessary tools to build this plugin.
This plugin is designed to handle dynamic importing and namespacing patterns on the UG Bot monorepo project.
This plugin is designed specifically for PyCharm, to correctly refence classes, methods or attributes which are otherwise incorrectly referenced and documented by the standard Python resolution algorithm.

### Dependencies:
 - JVM 24 – No earlier or later releases
 - PyCharm 2025.3 - It is designed for this
 - IntelliJ IDEA 2025.3 – It is developed by this, configured with the following plugins:
 - Plugin DevKit: https://plugins.jetbrains.com/plugin/22851-plugin-devkit
 - Gradle: https://plugins.jetbrains.com/plugin/27367-gradle-for-java
 - Maven: https://plugins.jetbrains.com/plugin/27164-maven

### Development:
 - Clone the repository, and open in IntelliJ IDEA with the necessary plugins installed.
 - Refer to docs for general instruction on plugin development: https://plugins.jetbrains.com/docs/intellij/welcome.html
 - Refer here for PyCharm specific extension API: https://plugins.jetbrains.com/docs/intellij/intellij-community-plugins-extension-point-list.html

### Build & Install:
 - Run `./gradlew buildPlugin`
 - Install the resulting plugin from the `build/distributions` directory.
 - Follow instructions here to install plugin from disk: https://www.jetbrains.com/help/idea/managing-plugins.html#install_plugin_from_disk
 - Make sure to select the .zip in build/distributions when following instructions in link above