# Focus!
## Overview
_Focus!_ is an Android application that seeks to eliminate distracting Android notifications from showing, according to the user-defined set of profiles. This application will allow users to:

- selected applications from launching
- notifications of selected applications from showing

all while allowing other services to run unchanged.

## Authors
| Name              | Email                      |
| ----------------- | --------------------------:|
| Bowei Chen        | boweiche [@] usc [dot] edu |
| Tushar Singhal    | tsinghal [@] usc [dot] edu |
| Shatrujeet Naruka |   naruka [@] usc [dot] edu |
| Prateek Bhatia    | prateekb [@] usc [dot] edu |
| Aarav Malpani     |  malpani [@] usc [dot] edu |

## Building
This project includes a Gradle file that specifies all dependencies required to build this project.

1. Import project into Android Studio by invoking `File > Open...`
1. Sync Gradle file with project by invoking `Tools > Android > Sync Project with Gradle Files`
1. The application is now ready to be built and run.
Invoke `Run > Run 'app'` and select a target Android device or Android emulator to run the app.

## Testing
This project includes suites of tests that can prove the correctness of our work. The suites are split between vanilla JUnit tests and Android Instrumented Tests.
### File location
| Test suites       | Path                                           |
| ----------------- | ---------------------------------------------- |
| JUnit 4           | `app/src/test/java/dreamteam/focus`            |
| Instrumented Test | `app/src/androidTest/java/dreamteam/focus`     |

The test files are organized according to the package structure as specified in the documentation. The test cases are named in the template of `<class name>Test.java`.

For example, `androidTest/dreamteam/focus/client/Profiles/ProfilesActivityTest.java` contains test cases intended to test `main/dreamteam/focus/client/Profiles/ProfilesActivity.java`.


### Instructions
1. Sync Gradle file with project by invoking `Tools > Android > Sync Project with Gradle Files`
1. Point to the specific test to run. Individual test cases are grouped by the class it is run against.
1. Click on the "play" button on the left of class declaration to run the test.
	- If the test is a vanilla JUnit 4 test, the test will execute in the local machine.
	- If the test is an Android Instrumented Test, Android Studio will prompt for a device to run the specified test on. The user has the option to run the test on a virtual machine or a physical Android device.
