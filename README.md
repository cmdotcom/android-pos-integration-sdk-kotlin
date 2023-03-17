#### Android POS Integration SDK

This repository contains the implementation of a library that allows your application to connect with PayPlaza terminal app to perform a payment in CM|PayPlaza environment. You only would need an Android POS device with PayPlaza terminal app installed.



## Release notes

### Compatibility table

| SDK   | Terminal |
| ----- | -------- |
| 1.1.3 | 2.0.1    |
| 1.1.2 | 2.0.0    |
| 1.1.1 | 1.2.4    |
| 1.1.0 | 1.2.0    |
| 1.0.1 | 1.1.1    |
| 1.0.0 | 1.1.1    |

### Versions

#### 1.1.3

* Not start new operation if another one is already in progress

* Remove isTipping parameter from SDK payment Data

#### 1.1.2

* Proguard problems in some scenarios

#### 1.1.1

* ECR App does not trigger the payment twice via the intent

* Add timeout mechanism to SDK

* Add wrong date of authorization request to SDK

#### 1.1.0

* Added Start pre authorization support
* Added Cancel pre authorization support
* Added Confirm pre authorization support
* Check device's battery

#### 1.0.1

* Forward AUTO_TIMEZONE_ENABLE error from Terminal

#### 1.0.0

* Initial implementation

  

## Adding POS Integration Library to project

You can include this library into your Android project by adding first the JitPack repository to your root build.gradle

```groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

Next, add the dependency to your app-level build.gradle

Development:

```groovy
dependencies {
    [...]
    // CM|PayPlaza Android POS Integration library
    implementation 'com.github.cmdotcom.android-pos-integration-sdk-kotlin:androidposintegrationsdk-debug:<version-tag>'
}
```

Production:

```groovy
dependencies {
    [...]
    // CM|PayPlaza Android POS Integration library
    implementation 'com.github.cmdotcom.android-pos-integration-sdk-kotlin:androidposintegrationsdk:<version-tag>'
}
```

Also you can download the desired .aar file from [releases](https://github.com/cmdotcom/android-pos-integration-sdk-kotlin/releases) and include it in your project:

```groovy
dependencies {
    [...]
    // CM|PayPlaza Android POS Integration library
    implementation files('lib/<aar_file_name>')
}
```

After this dependency is included, you can start using functionality contained in this library to perform operations in CM|PayPlaza environment.

More information on how to work with this library is available in the following link: [Getting started with App 2 app integration (cm.com)](https://developers.cm.com/payments-platform/v1.0.2/docs/app-2-app-integration).

Also an [example](https://github.com/cmdotcom/sunmi-ecr-example-kotlin) of how to implement the Integration SDK is available.
