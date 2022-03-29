<div id="header" align="center">
  <h1>GMTE-Thesis</h1>
  <p><img src="https://user-images.githubusercontent.com/11558887/160644932-d1f0dc13-a498-42c8-b784-a712cf9bc71c.gif" width="50%"/></p>
  <p>This repository holds all the code written for my GMTE-Thesis at Utrecht University.</p>
  <p><strong>Made with &#10084; by <a href="https://github.com/Markieautarkie">Markieautarkie</a></strong></p>
  <a href="https://github.com/Markieautarkie/GMTE-Thesis/releases"><img src="https://img.shields.io/badge/release-1.0.0-success"/></a>
</div>

## MTBalance
<img src="https://user-images.githubusercontent.com/11558887/160163476-235f19cf-3c3c-4767-ae6d-b6ffcfad73a3.png" align="right" width="15%"/>
<img src="https://user-images.githubusercontent.com/11558887/160162297-2c3881c4-5289-47fa-ac79-a758dc26c0e5.png" align="right" width="15%"/>
This is the main Android application of the thesis, which is used to construct a system that is capable of providing novice mountain bikers with real-time proprioceptive feedback on balance.

&nbsp;

The code is written in **Java** within the *Android Studio IDE*. All code is fully documented in-line.

### Install
The target device must support `.apk` installation from unknown sources.

Open *Android Studio IDE* and build the full MTBalance root folder. Ensure that the `.gradle` and **SDK** versions have been verified. Next, build the application directly to the target device.

## RTFeedback
<img src="https://user-images.githubusercontent.com/11558887/160166068-7f718118-c647-4578-93c9-8e2c6ca07f65.png" align="right" width="15%"/>
<img src="https://user-images.githubusercontent.com/11558887/160166081-de66212a-ef18-4fdd-b2df-6158ebe64804.png" align="right" width="15%"/>

Code written for the real-time feedback devices used in the *MTBalance* system, utilizing **Cpp** within the *Arduino IDE*.

### Install
Flash the `.ino` file within the root folder directly to an Arduino device using the *Arduino IDE*. Ensure that the target board matches the used Arduino controller.

## Plotting
Simple **Python** scripts that have been written for figure generation. Although these scripts are not particularly useful for the main application framework, they have been added for the sake of completeness.
