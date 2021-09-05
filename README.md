# TapDetector

init libs at Application class </br>
```
FraudDetectorClient
                .application(this@SampleApp)
                .enableDebug(BuildConfig.DEBUG)
                .setUserId(userId)
                .build()      
                
 ```
                
                
Activity will be tracked automatically
to track tap action on fragment 
```
view.setOnTouchListener(FraudDetectorClient.touchListener())
