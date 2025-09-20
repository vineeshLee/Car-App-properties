# CarAppDemo

This Android application demonstrates how to interact with car hardware services using the Android Automotive OS APIs. Specifically, it shows how to retrieve and display information about the car's current gear status and fuel level.

## Features

*   **Gear Status Display:** Shows the current gear the car is in (e.g., Park, Drive, Neutral, Reverse).
*   **Fuel Level Display:** Shows information related to the car's fuel level.
*   **Asynchronous Updates:** Uses Kotlin Coroutines and Flows to observe and update UI elements with data from car services.
*   **ViewModel Architecture:** Leverages `ViewModel` to manage UI-related data in a lifecycle-conscious way.
*   **View Binding:** Uses View Binding to interact with UI elements safely and efficiently.

## Core Components

### `MainActivity.kt`

This is the main entry point of the application. It is responsible for:

*   Initializing and connecting to the `Car` object.
*   Setting up `ViewModel` instances (`GearViewModel` and `FuelViewModel`) to fetch and observe car data.
*   Updating the UI with the latest gear and fuel information.
*   Handling user interactions (e.g., button clicks to initiate data fetching).
*   Managing the lifecycle of connections to car services (registering listeners in `onResume`, unregistering in `onPause`, and disconnecting in `onDestroy`).

### `GearViewModel.kt`

*   Likely responsible for managing the logic to retrieve and expose the car's current gear status.
*   Uses `CarPropertyManager` to listen for gear changes.
*   Exposes the gear status via a Kotlin Flow (`gearFlow`).

### `FuelViewModel.kt`

*   Likely responsible for managing the logic to retrieve and expose information about the car's fuel level.
*   Uses `CarPropertyManager` to listen for fuel level changes.
*   Exposes fuel information via a Kotlin Flow (`fuelInfoFlow`).

## How it Works

1.  **Car Connection:** The application establishes a connection to the car services using `Car.createCar()`.
2.  **ViewModel Initialization:** `GearViewModel` and `FuelViewModel` are initialized. These ViewModels likely contain the logic to interact with the `CarPropertyManager`.
3.  **Data Observation:** `MainActivity` observes Kotlin Flows exposed by the ViewModels.
4.  **UI Updates:** When new data (gear status or fuel info) is emitted by the Flows, the UI in `MainActivity` is updated accordingly.
5.  **User Interaction:**
    *   Clicking the "Gear Status" button likely triggers the connection to the car and initializes the `GearViewModel` to start listening for gear changes.
    *   Clicking the "Fuel Status" button likely triggers the connection to the car and initializes the `FuelViewModel` to start listening for fuel information. A short delay is introduced before unregistering the listener, suggesting it might fetch the current value and then stop listening.
6.  **Lifecycle Management:** Listeners for car properties are registered in `onResume` and unregistered in `onPause` to prevent resource leaks and ensure data is only fetched when the activity is active. The connection to the `Car` object is explicitly disconnected in `onDestroy`.

## Dependencies

This project utilizes several Android Jetpack libraries, including:

*   `androidx.appcompat:appcompat`
*   `androidx.core:core-ktx`
*   `androidx.activity:activity-ktx` / `androidx.activity:activity`
*   `androidx.lifecycle:lifecycle-viewmodel-ktx`
*   `androidx.lifecycle:lifecycle-livedata-ktx` (though Flows are primarily used for observation in the provided code)
*   `androidx.constraintlayout:constraintlayout`
*   `com.google.android.material:material`
*   View Binding (`androidx.databinding:viewbinding`)
*   Kotlin Coroutines (`kotlinx-coroutines-android`)

## How to Build and Run

1.  Clone the repository.
2.  Open the project in Android Studio.
3.  Ensure you have an Android Automotive OS emulator or a compatible physical device set up.
4.  Build and run the `automotive` module.

