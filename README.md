# DEV.HNZ-ROAD_PROBLEMS

<br>

## our two solutions on real life problems

```
1.] GPS Tracker for alerting driver in accident prone zone
2.] Accident Detection
```

### GPS tracker for accident prone zone :

```
Our mobile app will help drivers to make them
aware and alert whenever they enter an accident
prone zone.

The app will run even in background even when
destroyed or closed using foreground services.

```

<p align="center">
<img width="300" height="500" src="IMG-20220108-WA0083.jpg"/>
</p>
Features of this app :

```
1.] Whenever the driver enter an accident prone zone an alert will be sent in the form of a voice

2.] The alert will be given in regular intervals until he/she does not exit the accident prone zone.

3.] If the speed is more than 30 km/h then there will be another alert in the form of voice to reduce speed.

4.] There is driving mode where the phone will be locked if the driver has entered accident prone zone

5.] The driving mode can be enabled or disabled as per the wish of the driver.
```


UI of the app:-
```

1.] Latitude and longitude are provided.

2.] Current Location of the driver.

3.] Distance from the closest accident prone zone.

4.] Speed of the car which is being driven.

5.] Enable Driving Mode/ Disable Driving Mode.

```

### Tech Stack used :

```
LocationManager library for getting latitude and longitude

GeoCoder for getting address, speed, etc

JAVA

```


### Accident detection:

```
This model will detect whether an accident is hapenning or not in a video.
So in a video each frame will be checked and if the frame has an accident, then 
it can be differentiated.
```

### Problems we faced:
```
1.] We tried using video object detection but were unable to succed using that method, due to 
less time as it was taking a lot of time to train at the least number of epoch also. 

2.] Finding a good dataset was difficult as the dataset we were searching was where 
we can get screenshots of cctv cameras.
```

# Feature of this mdoel
```
1.] It will help identify whenever there is an accident hapenning.

2.] Whenever accident is happening we can differentiate it.
```

### Problems this model will solve:

```
1.] The job of police will become much easier.

2.] Many lives will be saved. Deaths and injury will become low in road accidents.

```
### Tech stack used:
```
1.] OpenCv

2.] Tensorflow

3.] python
```

### Future Scope:

```
1.] The accuracy of this model will improve if we a lot of data set,
we have used limited data since it was taking a lot of time to train. 

2.] We can still make this model to video obeject detection with more data and training time, 
and therefore will be much more accurate and better.

```

