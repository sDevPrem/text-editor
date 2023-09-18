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
For navigation between screens and deep linking.

[Room](https://developer.android.com/jetpack/androidx/releases/room) :
To store and manage running statistics.

[Hilt](https://developer.android.com/training/dependency-injection/hilt-android) :
For injecting dependencies.

[Timber](https://github.com/JakeWharton/timber): For logging.

[Gson](https://github.com/google/gson): To parse object to json and vice-versa.



