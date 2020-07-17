# react-native-bdk
React Native Wrapper for BDK and LDK

# WIP DO NOT USE 

## Preqs
- Clone BDK
https://github.com/bitcoindevkit/bdk
- Clone BDK-Android
https://github.com/bitcoindevkit/bdk-android

## 1. Compiling Rust/JNI to AAR (BDK)
1. Update BDK repo `build_lib.sh` with target SDK for Android (28, 29 etc..)
3. Changing export vars to matching Android version

```
export CXX_x86_64_linux_android=$ANDROID_NDK_HOME/toolchains/llvm/prebuilt/linux-x86_64/bin/x86_64-linux-android29-clang++
export CXX_aarch64_linux_android=$ANDROID_NDK_HOME/toolchains/llvm/prebuilt/linux-x86_64/bin/aarch64-linux-android29-clang++
export CXX_armv7_linux_androideabi=$ANDROID_NDK_HOME/toolchains/llvm/prebuilt/linux-x86_64/bin/armv7a-linux-androideabi29-clang++
export CXX_i686_linux_android=$ANDROID_NDK_HOME/toolchains/llvm/prebuilt/linux-x86_64/bin/i686-linux-android29-clang++
```
4.  Compile BDK 

## 2.  Building AAR (bdk-android)
1. Make sure requried NDK is installed
`sdkmanager --install ndk;21.3.6528147`
2. Update BDK-Android `lib/build.gradle` with target SDK for Android 
3. Set env variable for JS lib 
`export LD_LIBRARY_PATH="$(pwd)/lib/src/test/jniLibs/x86_64/"
- TODO Make this gradle task system var

4. Build AAR in bdk-android by running
`./gradlew clean build`
5. AAR will be built in:
`~../bdk-android/lib/build/outputs/aar/lib-debug.aar`

## 3. Adding BDK AAR to React Native Project
1. Copy compiled AAR to an Anroid subfolder in RN project ex: './android/bdk-debug-lib/'
2. in 'app/build.grade` add dependency:
`implementation project(path: ':bdk-debug-lib')`
3. in  `settings.gradle` add `include ':bdk-debug-lib'`
4. modify `android/build.gradle` 'minSdkVersion' to be at least 23 to match lib min version.

## 4. Start React native app !

