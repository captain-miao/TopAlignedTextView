TopAlignedTextView
Android library to top aligned text in a textView, suggest use in LinearLayout.

It's from [How to remove the top and bottom space on textview of Android-wizard@stackoverflow.com](http://stackoverflow.com/a/32836547/703225)

## don't support multi-lines

## screenshot
![screenshot_top_align](https://raw.githubusercontent.com/captain-miao/me.github.com/master/jpg/screenshot_top_align.jpg  "screenshot_top_align")


### Gradle
Get library from  [oss.sonatype.org.io](https://oss.sonatype.org/content/repositories/snapshots)
```java

repositories {

    maven { url 'https://oss.sonatype.org/content/repositories/releases' }
    maven { url "https://oss.sonatype.org/content/repositories/snapshots" }

}

dependencies {
    compile 'com.github.captain-miao:topalignedtextview:1.0.0'
}

```
### layout
```xml
    <com.github.captain_miao.view.TopAlignedTextView
        android:id="@+id/text_a"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:paddingLeft="@dimen/activity_horizontal_margin"
        android:text="@string/text_demo_a"
    />
```

## License
The MIT License (MIT)

Copyright (c) 2016 yan_lu

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
