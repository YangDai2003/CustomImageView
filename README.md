# ImageViewLib-ImageViewPro

ImageViewPro 基于 AppCompatImageView, 在此基础上实现了图片的滑动、缩放、点击等操作。

ImageViewPro is based on AppCompatImageView, and on this basis, it realizes operations such as
sliding, zooming, and clicking of images.

[![jitpack](https://jitpack.io/v/YangDai2003/ImageViewLib.svg)](https://jitpack.io/#YangDai2003/ImageViewLib)

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
    implementation 'com.github.YangDai2003:ImageViewLib:1.0.6'
}
```

Maven

```code
<dependency>
    <groupId>com.github.YangDai2003</groupId>
	<artifactId>ImageViewLib</artifactId>
	<version>1.0.6</version>
</dependency>
```

## How to use?

XML

```xml
<com.yangdai.imageviewpro.ImageViewPro
    android:id="@+id/image_view"
    android:layout_width="0dp"
    android:layout_height="0dp"
    ...
    app:layout_constraintBottom_toBottomOf="parent"
    app:layout_constraintEnd_toEndOf="parent"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintTop_toTopOf="parent" />
```

JAVA

```java
ImageViewPro imageViewPro = findViewById(R.id.image_view);
```
