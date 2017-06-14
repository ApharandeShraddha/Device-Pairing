# Device-Pairing (ANDROID and Firebase database)
Authenticate co-located devices using sensors (Android Project)

Project Title: Device Pairing (Sensor Used - Accelerometer and GPS)
 This application will pair two devices and the main purpose is to Authenticate two devices using sensors before sharing the data when the devices are co-located.

Implementation Details:
1.  LocationTracker.java - 
      This class fetches location of two devices and storing longitude and latitude values in to firebase database to find out weather devices are nearby or not because although devices are at distinct locations they may have same accelerator values.

2. MainAcitvity.java -
        1. OnSensorValueChanged event store the  accelerometer values continuously in to firebase. 
        2. On DataChanged event , Fetch location and accelerator's x,y,z axis values .
        3. Comparing location details if they are same then take difference of  x axis of device 1 and x axis of device 2  similarly with other axis and if this difference is less than 10 , Devices are paired.
