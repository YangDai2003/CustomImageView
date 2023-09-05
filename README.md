# ImageViewLib-ImageViewPro

ImageViewPro 基于 AppCompatImageView, 在此基础上实现了图片的滑动、缩放、点击等操作。

ImageViewPro is based on AppCompatImageView, and on this basis, it realizes operations such as
sliding, zooming, and clicking of images.

[![jitpack](https://jitpack.io/v/YangDai2003/ImageViewPro.svg)](https://jitpack.io/#YangDai2003/ImageViewPro)

## How to import?

### Step 1. Add the JitPack repository to your build file

Gradle

Add it in your root build.gradle at the end of repositories:

```code
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

Maven

```code
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

### Step 2. Add the dependency

Gradle

```code
dependencies {
    implementation 'com.github.YangDai2003:ImageViewPro:1.0.3'
}
```

Maven

```code
<dependency>
    <groupId>com.github.YangDai2003</groupId>
        <artifactId>ImageViewPro</artifactId>
    <version>1.0.3</version>
</dependency>
```

## How to use?

XML

```xml
<com.yangdai.imageviewpro.ImageViewPro
    android:id="@+id/image_view"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:src="@drawable/screenshot" />
```

JAVA

```java
ImageViewPro imageViewPro = findViewById(R.id.image_view);
```
