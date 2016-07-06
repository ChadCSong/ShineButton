# ShineButton
This is a UI lib for Android. Effects like shining.
![preview](https://github.com/ChadCSong/ShineButton/blob/master/demo_small.gif)

## Usage
 shineButton = (ShineButton) findViewById(R.id.shine_button);
 shineButton.init(activity);
#### Simple Usage

Icon shape is made from png mask. Please see raw files.
```shell
app:siShape
```

Default button color.
```shell
app:btn_color
```
Fill button color.
```shell
app:btn_fill_color
```
If this property is true,the effects will become random color shine.
```shell
app:allow_random_color
```

```xml
 <com.sackcentury.shinebuttonlib.ShineButton
                android:layout_width="50dp"
                android:layout_height="50dp"
                android:layout_centerInParent="true"
                android:src="@android:color/darker_gray"
                android:id="@+id/po_image2"
                app:btn_color="@android:color/darker_gray"
                app:btn_fill_color="@android:color/holo_green_dark"
                app:allow_random_color="false"
                app:siShape="@raw/smile"/>
 ```
#### Complex Usage

```shell
app:shine_count="8"
app:shine_turn_angle="90"
```
![preview](https://github.com/ChadCSong/ShineButton/blob/master/demo_more_shine.gif)


 ```xml
 <com.sackcentury.shinebuttonlib.ShineButton
                 android:layout_width="50dp"
                 android:layout_height="50dp"
                 android:layout_centerInParent="true"
                 android:src="@android:color/darker_gray"
                 android:id="@+id/po_image1"
                 app:btn_color="@android:color/darker_gray"
                 app:btn_fill_color="#FF6666"
                 app:allow_random_color="false"
                 app:enable_flashing="false"
                 app:big_shine_color="#FF6666"
                 app:click_animation_duration="200"
                 app:shine_animation_duration="1500"
                 app:shine_turn_angle="10"
                 app:small_shine_offset_angle="20"
                 app:shine_distance_multiple="1.5f"
                 app:small_shine_color="#CC9999"
                 app:shine_count="8"
                 app:siShape="@raw/like"/>
  ```


## Requirements

- Android 4.0+

## Code Reference

[android-shape-imageview](https://github.com/siyamed/android-shape-imageview)

## Marven

Will coming...

##

## Credits

iOS lib [fave-button](https://github.com/xhamr/fave-button) Android implement.
FaveButton was inspired by Twitter’s Like Heart Animation;

License
------------
    The MIT License (MIT)
    
    Copyright (c) 2016 Chad Song 
    
    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:
    
    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.
    
    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
