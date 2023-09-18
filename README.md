# Text Editor

A text - editor which allows you to save formatted text with different styles and images.
Build with kotlin and follows MVVM, Clean architecture for data flow and
single activity architecture for UI.

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

|                                                                                                                         |                                                                                                                       |                                                                                                                       |
|-------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------|
| ![Screenshot_1695014891](https://github.com/sDevPrem/text-editor/assets/130966261/81a09078-cc7b-41cf-948a-f0681b2b6c16) | ![Screenshot_1695014888](https://github.com/sDevPrem/run-track/assets/130966261/3ba73724-fd17-4f02-acba-b3e1e35111b3) | ![Screenshot_1695014882](https://github.com/sDevPrem/run-track/assets/130966261/944418f8-1601-4c09-85b5-dd5852bbe591) |

## How to use

Write, select and format that text using any formatting feature
at the bottom.

## Project Structure

* [data](app/src/main/java/com/sdevprem/basictexteditor/data): Contains database entity and database
  related classes.
* [di](app/src/main/java/com/sdevprem/basictexteditor/di): Contains Hilt Module.
* [Domain](app/src/main/java/com/sdevprem/basictexteditor/domain): Contains common use cases like
  converting database note
  to a format that can be understandable to UI layer and vice-versa.
* [ui](app/src/main/java/com/sdevprem/basictexteditor/ui): Contains UI related classes like
  fragment.

![Editor Architecture ](https://github.com/sDevPrem/text-editor/assets/130966261/1cfca646-97c4-4c45-a685-15cb7efd0c34)

### Editor

[EditorFragment](app/src/main/java/com/sdevprem/basictexteditor/ui/editor/EditorFragment.kt)
is the UI where user edits and formats their notes. It maintains a list
of [Range](app/src/main/java/com/sdevprem/basictexteditor/ui/editor/util/Range.kt)
of [Style](app/src/main/java/com/sdevprem/basictexteditor/ui/editor/util/Style.kt) in the
[EditorViewModel](app/src/main/java/com/sdevprem/basictexteditor/ui/editor/EditorViewModel.kt)
which updates whenever user enters or removes any text in the editor accordingly to remain updated
with the editor spans. When user selects a text and click on the formatting options, it creates a
range for that formatting according to the selection.

When user finishes editing, it creates and sends
the [NoteWithStyle](app/src/main/java/com/sdevprem/basictexteditor/domain/model/NoteWithStyle.kt)
to the [domain layer](app/src/main/java/com/sdevprem/basictexteditor/domain/usecase) which converts
it to a
suitable [Note](app/src/main/java/com/sdevprem/basictexteditor/data/model/Note.kt) format which can
be stored in the database. The domain layer depends
on [Asset Providers](app/src/main/java/com/sdevprem/basictexteditor/domain/provider)
which helps it to convert images and fonts to a
suitable [Style](app/src/main/java/com/sdevprem/basictexteditor/ui/editor/util/Style.kt)
when fetching the notes from the database.

## Build With

[Kotlin](https://kotlinlang.org/):
As the programming language.

[Jetpack Navigation](https://developer.android.com/jetpack/compose/navigation) :
For navigation between screens.

[Room](https://developer.android.com/jetpack/androidx/releases/room) :
To store and manage running statistics.

[Hilt](https://developer.android.com/training/dependency-injection/hilt-android) :
For injecting dependencies.

[Data Binding](https://developer.android.com/topic/libraries/data-binding)
and [View Binding](https://developer.android.com/topic/libraries/view-binding):
For easy integration of data with UI.

[Timber](https://github.com/JakeWharton/timber): For logging.

[Gson](https://github.com/google/gson): To parse object to json and vice-versa.
