# Android POS Integration SDK

This repository contains the implementation of a library that allows your application to connect with PayPlaza terminal app to perform a payment in CM|PayPlaza environment. You only would need an Android POS device with PayPlaza terminal app installed.

## Adding POS Integration Library to project
You can include this library into your Android project by adding first the JitPack repository to your root build.gradle

```groovy
allprojects {
    repositories {
        google()
        jcenter()
        maven {
            url 'https://jitpack.io'
        }
    }
}
```

Next, add the dependency to your app-level build.gradle

```groovy
dependencies {
    // CM|PayPlaza Android POS Integration library
    implementation 'com.github.CMDotCom:androidposintegrationsdk:1.0.0'
}
```

After this dependency is included, you can start using functionality contained in this library to perform operations in CM|PayPlaza environment.

See file in doc folder in this project for more information on how to work with this library.

