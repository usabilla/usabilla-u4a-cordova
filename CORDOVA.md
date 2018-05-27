# README #

# Tools required

1. Cordova

https://cordova.apache.org/docs/en/2.9.0/guide/cli/index.html

`sudo npm install -g cordova`

# Adding Platforms

`cordova platform add ios`
`cordova platform add android`

When adding a platform on cordova it will make sure it has all the tools to build that mobile app. It will therefore download all the plugins and cache them under `plugins/` folder.

# Building through the console

`cordova build android`
`cordova build ios`
this will generate the apk/ipa files that can be later on released

`cordova run android`
`cordova run ios`
same process as above but it will fire up the application as well. The android app might not start with this command but it will be installed and it's just a matter of starting the app.

# Build through the IDEs

For iOS

1. Run `cordova prepare ios` to load all the necessary pods
2. Open XCode project `hellocordova.xcodeproject` on `platforms/ios`
3. Under frameworks folder remove the file Pods_HelloCordova.framework (when building by console this step is not necessary)
4. Debug or Run app

For Android

1. Run `cordova prepare android` to perform any necessary prep
2. Open `platforms/android` folder withAndroidStudio
3. Some version of AndroidStudio will ask you to sync the project with gradle on a popup
4. If popup wasn't shown or when running the project you don't get the device selection screen: go to `File` and `Sync Project with Gradle File`

# Developing a plugin

- Use the IDEs to build and modify the code and then copy it over to the cordova plugin folder

- Copy each time the plugin folder manually into the `plugins/` folder.
```rm -r plugins/cordova-usabilla/*
 cp -r ../cordova-usabilla/* plugins/cordova-usabilla
 ```
 For the app to pick up the change you need to remove / add the platform.
1. Copy plugin to `plugins/` folder
2. `cordova platform remove android`
3. `cordova platform add android`

- Use plugman tool (it is a bit buggy in my opinion and doesn't copy hooks over)
```
plugman uninstall --platform ios -project platforms/ios/ -plugin ../cordova-usabilla/
plugman install --platform ios -project platforms/ios/ -plugin ../cordova-usabilla/
 ```
