# Introduction
This is a Kotlin Multiplatform app targeting Android, iOS.
The app receives force data from pressure sensors using a bluetooth connection and
transferring the data through HTTP requests to a backend server.

The backend server is at: https://github.com/supa-hub/smartrecovery_backend 


* [/composeApp](./composeApp/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - [commonMain](./composeApp/src/commonMain/kotlin) is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    the [iosMain](./composeApp/src/iosMain/kotlin) folder would be the right place for such calls.
    Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./composeApp/src/jvmMain/kotlin)
    folder is the appropriate location.

* [/iosApp](./iosApp/iosApp) contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

  
# App structure
Most of the code is in the /composeApp/src/commonMain -folder and tested to work for both Android and iOS. <br />

The App uses Androids new <UI to backend> structure called model-view-viewmodel (mvvm). <br />

The folders inside /composeApp/src/commonMain contain part of the UI and its corresponding viewmodel (viewmodel is the UI components backend, which handles data etc.). <br />

For example, the **userDataGetter** -folder contains the popup dialog for inserting the users name etc. while also containing "UserDataGetterViewModel" <br />
For handling the inputs which the user gives, it also contain "DatePickerComposable", which contains a UI component for showing and choosing
dates from a calendar. <br />



# Running the app
The easiest way to run the app is by downloading Android studio and running it from there. <br />
Android studio also works for iOS builds, if you're using a Mac.

If you want to tst the bluetooth or internet features, you'll have to connect a mobile phone to your
compure/Android studio and run the app on the phone.