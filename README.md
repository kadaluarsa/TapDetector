# TapDetector
1.get latest version on the release section : 
https://github.com/kadaluarsa/TapDetector/releases </br>
2.init libs at Application class </br>
```
FraudDetectorClient.Config()
            .application(this@SampleApp)
            .enableDebug(BuildConfig.DEBUG)
            .setUserId(userId)
            .build()
                
 ```
                
                
3.Activity will be tracked automatically
to track tap action on your fragment 
```
view.setOnTouchListener(FraudDetectorClient.touchListener())
