# WeighingScale Application

<img src="https://dikhimartin.vercel.app/assets/img/portfolio/paradigm-scales/logo.svg">

[![wakatime](https://wakatime.com/badge/user/6107bfd2-2e56-4e0a-b828-3e2ef709217d/project/9394bb88-21f9-4a45-82a3-9fcd6a4cd593.svg)](https://wakatime.com/@6107bfd2-2e56-4e0a-b828-3e2ef709217d/projects/xcuvhnfvba)

## Overview

**WeighingScale** is an Android application developed to digitalize and optimize agricultural weighing processes, specifically for rice harvest management. By integrating with a digital weighing scale over Bluetooth, the app provides real-time, accurate measurements directly to a smartphone. The system uses the NodeMCU ESP32 microcontroller for reliable Bluetooth communication, with alternatives such as Arduino and HC-06 also considered for varying power and connectivity needs.

## Features

1. **Bluetooth Connectivity**: The app connects to digital scales via **NodeMCU ESP32** or other Bluetooth modules (e.g., HC-06, Arduino).
2. **Batch Management**: Supports detailed data entry for each weighing session, including responsible personnel (PIC), destination, weight, and pricing.
3. **Real-Time Data Visualization**: Displays batch summaries and weight statistics using **MPAndroidChart**.
4. **Data Export**: Exports batch histories and reports in **PDF format** with **iTextPDF**, and supports data backup in **CSV format** for easy transfer and recovery.
5. **Customizable Units and Pricing**: Adjusts units of weight and harvest pricing per kg for flexible data handling.

## Prerequisites

- **Android Studio** (version 4.0 or higher)
- **Android SDK**: Minimum SDK 21, Compile SDK 34
- **Bluetooth-enabled Weighing Device** with compatible microcontroller (e.g., NodeMCU ESP32)

## Getting Started

### Installation

To clone the repository:

```bash
git clone https://github.com/yourusername/weighingscale.git
```

1. **Open in Android Studio**: Import the project directory (`WeighingScale`).
2. **Sync Dependencies**:
   - MPAndroidChart (`v3.1.0`) for data charts
   - iTextPDF (`libs.itextpdf`) for report generation
3. **Build**: Click **Build > Make Project** or use `Ctrl+F9` to compile the project.

### Running the Application

1. Connect an Android device or start an emulator.
2. Install the app by clicking **Run > Run 'app'** or pressing `Shift+F10`.
3. Begin using the app to manage and record weighing data directly from the Bluetooth-connected scale.

### Building a Release Version

1. Go to **Build > Generate Signed Bundle / APK**.
2. Select **APK** and follow the prompts to create a signed release version.
3. The APK will be saved in `/app/release`.

## Permissions

The following permissions are required for Bluetooth operations and file management:

- Bluetooth: `BLUETOOTH`, `BLUETOOTH_ADMIN`, `BLUETOOTH_CONNECT`, `BLUETOOTH_SCAN`
- Location: `ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION`
- Storage: `READ_EXTERNAL_STORAGE`, `WRITE_EXTERNAL_STORAGE`

## Dependencies

- [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart): For live charting and visual data summaries.
- [iTextPDF](https://github.com/itext/itextpdf): For PDF report generation and export.

## Directory Structure

```bash
.
├── app
│   ├── build                    # Compiled code
│   ├── src                      # Source files (Java and XML layouts)
│   └── proguard-rules.pro        # Proguard configuration for release
├── gradle                       # Gradle wrapper files
├── build.gradle                 # Module build file
├── settings.gradle              # Project settings
└── README.md                    # Project documentation
```

## Releases

Download the latest APK from the [Releases page](https://github.com/dikhimartin/WeighingScaleApp/releases).