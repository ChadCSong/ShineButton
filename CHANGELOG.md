# Changelog

All notable changes to this project will be documented in this file.

## [1.0.0] - 2026-06-13

### Added
- **Final Release**: Reached milestone version 1.0.0 with all roadmap items completed.

### Changed
- **Performance Optimization**: Eliminated runtime object allocations in `onDraw()` across `ShineView` and `PorterImageView`. 
- Removed redundant `canvas.saveLayer()` calls, relying solely on highly efficient `Bitmap` drawing, boosting animation frames per second (FPS).
- Completed and checked all items in the project Roadmap.

## [Unreleased]

### Added
- Upgraded the Demo app to use Material 3 design (`Theme.Material3.DayNight`).
- Enabled dynamic colors (`DynamicColors`) support in the Demo app.

## [0.6.0] - 2026-06-13

### Added
- **Custom Animators Support**: Introduced ability to set custom `Interpolator` in View version and `AnimationSpec` in Compose version.
- Added `setInterpolator(Interpolator)` method to `ShineButton`.
- Exposed `shineAnimationSpec` and `scaleAnimationSpec` in `ShineButtonCompose`.

### Changed
- Bumped version to 0.6.0.

## [0.5.0] - 2026-06-13

### Added
- **Vector Support**: Fully supported `VectorDrawable` as button shapes in the classic View version.
- Optimized `PorterShapeImageView` rendering logic to handle all `Drawable` types with high-quality scaling.
- Added a Vector Support demo to the main activity.

### Changed
- Improved `ShineButtonCompose` rendering precision.
- Bumped version to 0.5.0.

## [0.4.1] - 2026-06-13

### Fixed
- Fixed CI build failures caused by missing Compose dependencies. Introduced Compose BOM (`compose-bom:2023.01.00`).
- Replaced remote `EasingInterpolator` dependency with an internal `QuartOutInterpolator` to resolve JitPack 403 Forbidden errors.
- Resolved `UnusedTransitionTargetStateParameter` Lint error in `ShineButtonCompose`.
- Synced `app` module `minSdkVersion` to 21 to match the library requirements.

## [0.4.0] - 2026-06-13

### Added
- **Jetpack Compose Support**: Introduced `ShineButtonCompose`, a native Composable implementation of the ShineButton.
- Added Compose animation (`Transition`, `Animatable`) and rendering logic.
- Supported custom shapes using `ImageVector` in Compose.

### Changed
- Increased `minSdkVersion` to 21 to support Jetpack Compose.
- Updated library version to 0.4.0.

## [0.3.0] - 2026-06-13

### Added
- **Kotlin Migration**: Fully converted the library module (`shinebuttonlib`) to Kotlin.
- Improved null safety and idiomatic Kotlin code.

### Changed
- Updated library version to 0.3.0.

## [0.2.1] - 2026-06-13

### Added
- GitHub Actions CI workflow for automated builds.
- Comprehensive ProGuard/R8 rules for library protection.
- Enhanced Javadoc and inline code documentation.
- `.editorconfig` for unified project-wide code style.
- JitPack support for EasingInterpolator dependency.
- Proper namespace configuration in build.gradle.

### Changed
- **Modernization**: Updated `compileSdkVersion` and `targetSdkVersion` to 34 (Android 14).
- **Build System**: Upgraded Android Gradle Plugin to 7.4.2 and Gradle to 7.5.1.
- **Dependency Migration**: Switched from `jcenter()` to `mavenCentral()`.
- **UI/UX**: Updated `androidx.appcompat` to 1.6.1 and fixed RTL layout issues.
- **Robustness**: Improved Activity context detection and fixed potential null pointers in Dialog rendering.
- **Performance**: Optimized `PorterImageView` by fixing deprecated `saveLayer` flags and improving drawing logic.
- **Accessibility**: Added missing content descriptions and accessibility hints.
- **Documentation**: Completely overhauled `README.md` and added `CONTRIBUTING.md`.

### Fixed
- Android 12+ compatibility by adding `android:exported="true"` to MainActivity.
- Deprecated `getDrawable()` calls replaced with `ResourcesCompat`.
- Non-final resource ID issues in switch-case statements.

## [0.2.0] - 2016-07-10
... (rest of the file)

### Added
- Initial release of ShineButton.
- Support for custom shapes via PNG masks.
- Customizable shine colors, count, and animations.
- Dialog support.
