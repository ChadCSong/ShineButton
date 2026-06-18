# ShineButton

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Platform](https://img.shields.io/badge/platform-Android-yellow.svg)](https://www.android.com)
[![API](https://img.shields.io/badge/API-14%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=14)
[![JitPack](https://jitpack.io/v/ChadCSong/ShineButton.svg)](https://jitpack.io/#ChadCSong/ShineButton)
[![Maven Central](https://img.shields.io/maven-central/v/com.sackcentury/shinebutton.svg)](https://search.maven.org/artifact/com.sackcentury/shinebutton)

A lightweight, customizable Android UI library that adds a "shining" effect to buttons, similar to Twitter's heart animation.

![Main Demo](image/demo_shine_others.gif)

## Features

- **Customizable Shapes**: Use any PNG mask as a button shape.
- **Vibrant Effects**: Adjust shine color, size, count, and distance.
- **Interactive**: Smooth animations for both clicking and shining.
- **Random Colors**: Option to enable random colors for the shine effect.
- **Dialog Support**: Works seamlessly inside Dialogs.
- **Lightweight**: Minimal dependencies and easy to integrate.

## Installation

### JitPack (Recommended for latest version)

1. Add the JitPack repository to your root `build.gradle` file:

```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

2. Add the dependency to your `app/build.gradle` file:

```gradle
dependencies {
    implementation 'com.github.ChadCSong:ShineButton:v0.6.0'
}
```

### Gradle (Maven Central)

Add the dependency to your `app/build.gradle` file:

```gradle
dependencies {
    implementation 'com.sackcentury:shinebutton:0.6.0'
}
```

### Maven

```xml
<dependency>
  <groupId>com.sackcentury</groupId>
  <artifactId>shinebutton</artifactId>
  <version>0.6.0</version>
  <type>aar</type>
</dependency>
```

## Usage

### XML Layout

The simplest way to use `ShineButton` is in your XML layout:

```xml
<com.sackcentury.shinebuttonlib.ShineButton
    android:id="@+id/shine_button"
    android:layout_width="50dp"
    android:layout_height="50dp"
    android:layout_centerInParent="true"
    android:src="@android:color/darker_gray"
    app:btn_color="@android:color/darker_gray"
    app:btn_fill_color="#FF6666"
    app:allow_random_color="false"
    app:siShape="@raw/like" />
```

### Java Implementation

```java
ShineButton shineButton = (ShineButton) findViewById(R.id.shine_button);
shineButton.init(activity);
```

### Jetpack Compose Implementation

ShineButton now supports Jetpack Compose natively!

```kotlin
var isChecked by remember { mutableStateOf(false) }

ShineButtonCompose(
    isChecked = isChecked,
    onCheckedChange = { isChecked = it },
    shape = Icons.Default.Favorite, // Use any ImageVector
    btnColor = Color.LightGray,
    btnFillColor = Color.Red,
    shineColor = Color.Red,
    shineSize = 50.dp,
    allowRandomColor = true
)
```

Or create it dynamically:

```java
ShineButton shineButtonJava = new ShineButton(this);
shineButtonJava.setBtnColor(Color.GRAY);
shineButtonJava.setBtnFillColor(Color.RED);
shineButtonJava.setShapeResource(R.raw.heart);
shineButtonJava.setAllowRandomColor(true);

LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(100, 100);
shineButtonJava.setLayoutParams(layoutParams);
linearLayout.addView(shineButtonJava);
```

### Dialog Support

If you are using `ShineButton` inside a `Dialog`, call `setFixDialog(dialog)` to ensure it renders correctly:

```java
shineButton.setFixDialog(dialog);
```

## Attributes

| Attribute | Java Method | Description | Default |
|-----------|-------------|-------------|---------|
| `app:siShape` | `setShapeResource(int)` | Raw resource (PNG mask) for the shape | - |
| `app:btn_color` | `setBtnColor(int)` | Initial color of the button | `Color.GRAY` |
| `app:btn_fill_color` | `setBtnFillColor(int)` | Color of the button after being checked | `Color.BLACK` |
| `app:allow_random_color`| `setAllowRandomColor(boolean)`| Whether the shine effects use random colors | `false` |
| `app:shine_count` | `setShineCount(int)` | Number of shine particles | `8` |
| `app:shine_size` | `setShineSize(int)` | Size of the shine particles in pixels | - |
| `app:big_shine_color` | `setBigShineColor(int)` | Color of the primary shine particles | - |
| `app:small_shine_color` | `setSmallShineColor(int)` | Color of the secondary shine particles | - |
| `app:shine_animation_duration` | `setAnimDuration(int)` | Duration of the shine animation (ms) | `1500` |
| `app:click_animation_duration` | `setClickAnimDuration(int)`| Duration of the click animation (ms) | `200` |
| `app:enable_flashing` | `enableFlashing(boolean)` | Enable a flashing effect | `false` |
| `app:shine_distance_multiple` | `setShineDistanceMultiple(float)`| Multiple of distance from button center | `1.5f` |
| `app:shine_turn_angle` | `setShineTurnAngle(float)` | Angle offset for shine particles | `20` |

## Preview

| Small Shine | More Shine | Others |
|-------------|------------|--------|
| ![Small](image/demo_small.gif) | ![More](image/demo_more_shine.gif) | ![Others](image/demo_shine_others.gif) |

## Roadmap

We are continuously working to improve ShineButton. Here is what we have planned:

- [x] **Kotlin Migration**: Fully convert the library to Kotlin for better safety and modern features.
- [x] **Jetpack Compose**: Provide a native Composable version of ShineButton.
- [x] **Vector Support**: Allow using `VectorDrawable` as shape masks.
- [x] **Custom Animators**: Support for custom easing and path-based animations.
- [ ] **Material 3**: Update the demo app with Material 3 design and dynamic colors.
- [ ] **Performance**: Further optimize canvas operations and memory allocation.

## Requirements

- Android API Level 14+ (Android 4.0+)

## Credits

- Inspired by [fave-button](https://github.com/xhamr/fave-button) for iOS.
- Uses [EasingInterpolator](https://github.com/MasayukiSuda/EasingInterpolator) for smooth animations.
- Concepts from [android-shape-imageview](https://github.com/siyamed/android-shape-imageview).

## Third Party Bindings
  		  
### React Native
You may now use this library with [React Native](https://github.com/facebook/react-native) via the module [here](https://github.com/prscX/react-native-shine-button)

### NativeScript
You may also use this library with [NativeScript](https://nativescript.org) via the plugin [here](https://github.com/hamdiwanis/nativescript-shine-button)


## License

MIT License. See [LICENSE](LICENSE) for details.

