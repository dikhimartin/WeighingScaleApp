# WeighingScale Application

[![wakatime](https://wakatime.com/badge/user/6107bfd2-2e56-4e0a-b828-3e2ef709217d/project/9394bb88-21f9-4a45-82a3-9fcd6a4cd593.svg)](https://wakatime.com/@6107bfd2-2e56-4e0a-b828-3e2ef709217d/projects/xcuvhnfvba)

## Overview

**WeighingScale** is an Android application designed to integrate a digital scale with a smartphone to streamline and optimize rice harvest management. It leverages Bluetooth technology (via NodeMCU ESP32) to connect with the weighing scale device, enabling real-time data acquisition, processing, and visualization. The app also allows users to store batch data, generate reports, and export them as PDF files.

## Features

- **Bluetooth Integration**: Connect and manage communication with a Bluetooth-enabled digital scale.
- **Batch Management**: Capture batch data, including PIC, destination, and weighing details.
- **Real-Time Visualization**: Use charts to display weight and batch summaries using **MPAndroidChart**.
- **Export Reports**: Generate and export reports in PDF format using **iTextPDF**.

## Prerequisites

- Android Studio installed
- Minimum SDK version: 21
- Compile SDK version: 34
- NodeMCU ESP32 for scale connection

## Getting Started

### Cloning the Repository

To get started, clone this repository to your local machine using:

```bash
git clone https://github.com/yourusername/weighingscale.git
```

### Building the Project

1. **Open Android Studio** and load the project directory (`WeighingScale`).
2. Ensure you have the necessary **Gradle** configurations and dependencies set up:
   - MPAndroidChart (`v3.1.0`)
   - iTextPDF (`libs.itextpdf`)
3. **Sync the project** to download all required dependencies.
4. Once synced, build the project by clicking **Build > Make Project** or using the shortcut `Ctrl+F9`.

### Running the Application

1. Connect your Android device or start an emulator.
2. To install and run the app, click **Run > Run 'app'** or use the shortcut `Shift+F10`.
3. The app will launch, and you can start interacting with the Bluetooth scale and managing batch data.

### Releasing the App

To prepare a release version of the application:

1. Go to **Build > Generate Signed Bundle / APK**.
2. Select **APK** and click **Next**.
3. If you do not have a key yet, create one by providing the required information.
4. Choose the release variant and proceed with the build.
5. The signed APK will be generated in the `/app/release` directory.

### Permissions

The following permissions are requested by the app for Bluetooth connectivity and file management:

- `BLUETOOTH`, `BLUETOOTH_ADMIN`, `BLUETOOTH_CONNECT`, `BLUETOOTH_SCAN`
- `ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION`
- `READ_EXTERNAL_STORAGE`, `WRITE_EXTERNAL_STORAGE`

### Dependencies

The project uses the following key dependencies:

- [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart): For real-time charting and data visualization.
- [iTextPDF](https://github.com/itext/itextpdf): To generate and export PDF reports.

### Directory Structure

```bash
.
├── app
│   ├── build                    # Build output directory
│   ├── src                      # Source files (Java and XML layouts)
│   └── proguard-rules.pro        # Proguard configuration
├── gradle                       # Gradle wrapper
├── build.gradle                 # Project-level build file
├── settings.gradle              # Project settings
└── README.md                    # Project documentation
```



## Releases

You can download the latest APK from the [Releases page](https://github.com/dikhimartin/WeighingScaleApp/releases).

- **Latest Release**: [Version 1.0.2](https://github.com/dikhimartin/WeighingScaleApp/releases/tag/v1.0.2)