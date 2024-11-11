# Android application for Open Thermal Camera

Application for displaying, taking and viewing thermal photos.

---

# Changes vs the origin for this fork

This version supports the commonly available MLX90640 module operating in its standard UART mode via an external USB to serial adapter. This removes the need for the PCB, but has the limitation that most of the fancy features of the module aren't exposed over the UART interface.

This is handy if you need to use the camera infrequently and don't care about emissivity adjustments or fast framerates. I mainly made these changes to avoid having specific hardware dedicated for a device I'd rarely use.

The repo has also been updated to a newer Android SDK and lost the crashlytics/firebase integrations.

# Hardware required

An MLX90640 module with TX and RX pins and a supported USB -> Serial adapter. See the [UsbSerial](https://github.com/felHR85/UsbSerial) library documentation for a list of supported devices.

# Known issues

I was unable to test the stability of the original implementation so I am not sure if this version is materially worse. Where possible I've tried to maintain backwards compatibility - the original OTC device should work with this build as the code paths should still be there. Since the Android SDK has jumped quite a bit it's likely this bundles some incorrect/to-be-deprecated usage of the APIs and might not be as stable, but it's been sufficient for my needs.