# Life Cycle Data

Life Cycle Data is a porting of ViewModel android architecture component

## Set up Instructions
Set up the project dependencies. To use this library in your project:

Use the GitHub source and include that as a module dependency by following these steps:
 * Clone this library into a project named LifeCycleData, parallel to your own application project:
```shell
git clone https://github.com/baldapps/LifeCycleData.git
```
 * In the root of your application's project edit the file "settings.gradle" and add the following lines:
```shell
include ':lifecycledata'
project(':lifecycledata').projectDir = new File('../lifecycledata/')
```
 * In your application's main module (usually called "app"), edit your build.gradle to add a new dependency:
```shell
 dependencies {
    ...
    compile project(':lifecycledata')
 }
```
Now your project is ready to use this library

## Usage
See [here](https://developer.android.com/topic/libraries/architecture/viewmodel.html)

## References and how to report bugs
* If you find any issues with this library, please open a bug here on GitHub

## License
See LICENSE

## Change List

1.0.0
 * First version