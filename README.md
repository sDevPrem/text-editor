# Text Editor

A text - editor which allows you to save formatted text with different styles and images.
Build with kotlin and follows MVVM architecture and single activity architecture.

## Features

1. Formatting supported:
   * Bold
   * Italics
   * Underline
   * Font Size
   * Font-Family
   * Insert Images
2. Create, Update and Delete formatted notes with images.
3. Dark and Light Mode support

# Screenshot

|                                                                                                                       |                                                                                                                       |                                                                                                                       |
|-----------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------|
| ![Screenshot_1695014891](https://github.com/sDevPrem/run-track/assets/130966261/9288e994-33c1-4466-b34f-cb03a1802556) | ![Screenshot_1695014888](https://github.com/sDevPrem/run-track/assets/130966261/3ba73724-fd17-4f02-acba-b3e1e35111b3) | ![Screenshot_1695014882](https://github.com/sDevPrem/run-track/assets/130966261/944418f8-1601-4c09-85b5-dd5852bbe591) |

## How to use

Write, select and format that text using any formatting feature
at the bottom.

## Project Package Structure

* `data`: Contains database entity and database related classes.
* `di`: Contains Hilt Module.
* `Domain`: Contains common use cases like converting database note
  to a format that can be understandable to UI layer and vice-versa.
* `UI`: Contains UI related classes like fragment.

## Build With

[Kotlin](https://kotlinlang.org/):
As the programming language.

[Jetpack Navigation](https://developer.android.com/jetpack/compose/navigation) :
For navigation between screens.

[Room](https://developer.android.com/jetpack/androidx/releases/room) :
To store and manage running statistics.

[Hilt](https://developer.android.com/training/dependency-injection/hilt-android) :
For injecting dependencies.

[Timber](https://github.com/JakeWharton/timber): For logging.

[Gson](https://github.com/google/gson): To parse object to json and vice-versa.
