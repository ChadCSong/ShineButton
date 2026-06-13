# Changelog

All notable changes to this project will be documented in this file.

## [0.2.0] - 2026-06-13

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

## [1.0.0] - 2016-07-10
... (rest of the file)

### Added
- Initial release of ShineButton.
- Support for custom shapes via PNG masks.
- Customizable shine colors, count, and animations.
- Dialog support.
