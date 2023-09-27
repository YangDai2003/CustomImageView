# ImageViewLib

ImageViewLib 实现了多种常用且实用的自定义视图：

MosaicView 是继承于 View 实现的马赛克视图，可自定义方格大小、颜色、圆角，可用于表示图片的透明背景。

ImageViewPro 继承于 AppCompatImageView，在此基础上实现了图片的滑动、缩放、点击等操作，可用于图片的查看。

RoundRectImageView 继承于 AppCompatImageView，在此基础上实现了自定义边框大小、边框颜色、圆角大小，可用于图片的美化和展示。

-----------------------------------------------------

ImageViewLib implements a variety of commonly used and practical custom views:

MosaicView is based on View. It can customize the square size, color, and rounded corners, and can be used to represent the transparent background of pictures.

ImageViewPro is based on AppCompatImageView. On this basis, it implements operations such as sliding, zooming, and clicking on pictures, and can be used to view pictures.

RoundRectImageView is based on AppCompatImageView. On this basis, it implements custom border size, border color, and rounded corner size, which can be used to beautify and display pictures.

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
    implementation 'com.github.YangDai2003:ImageViewLib:1.0.8'
}
```

Maven

```code
<dependency>
    <groupId>com.github.YangDai2003</groupId>
	<artifactId>ImageViewLib</artifactId>
	<version>1.0.8</version>
</dependency>
```

## How to use?

XML

```xml
<com.yangdai.imageviewpro.ImageViewPro
    android:id="@+id/image_view"
    android:layout_width="0dp"
    android:layout_height="0dp"
    app:layout_constraintBottom_toBottomOf="parent"
    app:layout_constraintEnd_toEndOf="parent"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintTop_toTopOf="parent" />
```

```xml
<com.yangdai.imageviewpro.RoundRectImageView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:adjustViewBounds="true"
    android:src="@drawable/screenshot"
    app:layout_constraintBottom_toBottomOf="parent"
    app:layout_constraintEnd_toEndOf="parent"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintTop_toTopOf="parent"
    app:mBorderColor="@android:color/holo_red_dark"
    app:mBorderWidth="16dp"
    app:mRoundRadius="32dp"
    app:mShapeType="rounded_rect" />
```
