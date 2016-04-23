IntentShare
==================

This project is a light open-source library that improves the sharing experience on Android.

* [Motivations](#motivations)
* [Gradle dependency](#gradle-dependency)
* [Sample app](#sample-app)
* [Usage](#usage)
  * [Simple usage](#simple-usage)
  * [Dialog label](#dialog-label)
  * [Image](#image)
* [Extra Provider](#extra-provider)
  * [Build-in extra provider](#build-in-extra-provider)
  * [Custom extra provider](#custom-extra-provider)
* [Listener](#listener)
* [Icon Loader](#icon-loader)
  * [Picasso](#picasso)
  * [Glide](#glide)
  * [Custom icon loader](#custom-icon-loader)
* [Contributing](#contributing)
* [License](#license)
* [Special Thanks](#special-thanks-to-)

Motivations
=======

Nowadays, sharing content is part of our daily life. Unfortunately, the Android framework tools do not provide a sharing experience which reaches all of our expectations. We decided to implement our own tool, IntentShare, that might improve the user experience of a sharing action by :

* providing different extras according to the chosen target activity in order to take advantages of each target's specificities.
* providing an easy way to track the selected target activities in order to improve/adapt the extras for a specific target.
* providing a sorted target activity list based on the context of your app in order to take advantages of how people use it.

Find more about our motivations [here](http://tvbarthel.github.io/IntentShares/).

# Sample app
Incoming.

# Gradle dependency
```groovy
compile 'fr.tvbarthel.intentshare:library:0.0.1'
```

# Usage

## Simple usage
```java
IntentShare.with(context)
    .text("Default text you would like to share.")
    .deliver();
```

## Dialog label
```java
IntentShare.with(context)
    .chooserTitle("Select a sharing target : ")
    .text("Default text you would like to share.")
    .deliver();
```

## Image
```java
IntentShare.with(context)
    .chooserTitle("Select a sharing target : ")
    .text("Default text you would like to share.")
    .image(Uri.parse("content://com.example.test.fileprovider/data/img.png"))
    .deliver();
```

# Extra Provider
An extra provider can specify shared content for a given target activity.

## Build-in extra provider

### Facebook
```java
IntentShare.with(context)
    .chooserTitle("Select a sharing target : ")
    .text("Default text you would like to share.")
    .image(Uri.parse("content://com.example.test.fileprovider/data/img.png"))
    .facebookBody(Uri.parse("http://tvbarthel.fr"))
    .deliver();
```

### Twitter
```java
IntentShare.with(context)
    .chooserTitle("Select a sharing target : ")
    .text("Default text you would like to share.")
    .image(Uri.parse("content://com.example.test.fileprovider/data/img.png"))
    .twitterBody("Tweet can only have 127 char!")
    .deliver();
```

### Mail clients
```java
IntentShare.with(context)
    .chooserTitle("Select a sharing target : ")
    .text("Default text you would like to share.")
    .image(Uri.parse("content://com.example.test.fileprovider/data/img.png"))
    .mailSubject("Mail subject.")
    .mailBody("Extended text you would like to share in mail body.")
    .deliver();
```

## Custom extra provider
```java
IntentShare.with(context)
    .chooserTitle("Select a sharing target : ")
    .text("Default text you would like to share.")
    .image(Uri.parse("content://com.example.test.fileprovider/data/img.png"))
    .addExtraProvider(
        new IntentShare.ExtraProvider("com.google.android.gm")
            .disableText()
            .overrideSubject("Gmail subject, no mail body.")
            .disableImage()
    )
    .deliver();
```

By default, an ExtraProvider will copy the default shared content.
Replace default shared content :
```java
/**
 * Provide a specific text for the linked package name.
 *
 * @param text text which will be used as {@link android.content.Intent#EXTRA_TEXT}
 * @return {@link ExtraProvider} for chaining.
 */
public ExtraProvider overrideText(String text);

/**
 * Provide a specific subject for the linked package name.
 *
 * @param subject subject which will be used as {@link android.content.Intent#EXTRA_SUBJECT}
 * @return {@link ExtraProvider} for chaining.
 */
public ExtraProvider overrideSubject(String subject);

/**
 * Provide a specific image for the linked package name.
 *
 * @param image image which will be used as {@link android.content.Intent#EXTRA_STREAM}
 * @return {@link ExtraProvider} for chaining.
 */
public ExtraProvider overrideImage(Uri image);
```
Avoid copying default shared content without overriding:
```java
/**
 * Disable the extra {@link android.content.Intent#EXTRA_TEXT} for the linked package
 * to avoid a copy from the default {@link IntentShare} text.
 *
 * @return {@link ExtraProvider} for chaining.
 */
public ExtraProvider disableText();

/**
 * Disable the extra {@link android.content.Intent#EXTRA_SUBJECT} for the linked package
 * to avoid a copy from the default {@link IntentShare} subject.
 *
 * @return {@link ExtraProvider} for chaining.
 */
public ExtraProvider disableSubject();

/**
 * Disable the extra {@link android.content.Intent#EXTRA_STREAM} for the linked package
 * to avoid a copy from the default {@link IntentShare} image.
 *
 * @return {@link ExtraProvider} for chaining.
 */
public ExtraProvider disableImage();
```

# Listener

```java
intentShareListener = new IntentShareListener() {
    @Override
    public void onCompleted(String packageName) {
        Log.d(TAG, "onCompleted : " + packageName);
    }

    @Override
    public void onCanceled() {
        Log.d(TAG, "onCanceled");
    }
};

IntentShare.with(context)
    .chooserTitle("Select a sharing target : ")
    .text("Default text you would like to share.")
    .listener(intentShareListener)
    .deliver();
```

# Icon loader
Default icon loader used to load target activities icons is based on AsyncTask.

## Picasso
If your are already using Picasso, you may want to consider using PicassoIconLoader:
```groovy
compile 'fr.tvbarthel.intentshare:picasso-loader:0.0.1'
```

```java
IntentShare.with(context)
    .chooserTitle("Select a sharing target : ")
    .text("Default text you would like to share.")
    .iconLoader(new PicassoIconLoader())
    .deliver();
```

## Glide
If your are already using Glide, you may want to consider using GlideIconLoader:
```groovy
compile 'fr.tvbarthel.intentshare:glide-loader:0.0.1'
```

```java
IntentShare.with(context)
    .chooserTitle("Select a sharing target : ")
    .text("Default text you would like to share.")
    .iconLoader(new GlideIconLoader())
    .deliver();
```

## Custom icon loader
Implement your own IconLoader:
```java
IntentShare.with(context)
    .chooserTitle("Select a sharing target : ")
    .text("Default text you would like to share.")
    .iconLoader(new IconLoader() {
        @Override
        public void load(Uri iconUri, ImageView imageView) {

        }

        @Override
        public void cancel(ImageView imageView) {

        }
    })
    .deliver();
```

# Contributing
Contributions are very welcome (: You can contribute through GitHub by forking the repository and sending a pull request.

When submitting code, please make sure ./gradlew check succeed.


# License
```
Copyright (C) 2015 tvbarthel

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

# Special Thanks to ...
Vincent Brison [https://github.com/vincentbrison](https://github.com/vincentbrison) , for his precious advice.
