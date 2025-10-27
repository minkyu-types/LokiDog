#!/bin/bash
# rebuild_ios_all.sh

echo "🔨 Building iOS Frameworks..."

# iOS Device
./gradlew :composeApp:assembleDebugIosFatFrameworkForComposeAppXCFramework

# iOS Simulator
./gradlew :composeApp:assembleDebugIosSimulatorFatFrameworkForComposeAppXCFramework

# XCFramework 생성
./gradlew :composeApp:assembleComposeAppDebugXCFramework

echo "✅ Build complete!"
