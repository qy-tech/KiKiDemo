# KiKiDemo
kiki demo

## function list 

1. Fan control 

    write 0 ~ 255 to the pwm file to control fan

    ```bash
    echo 255 > /sys/devices/platform/pwm-fan/hwmon/hwmon1/pwm1
    echo 0 > /sys/devices/platform/pwm-fan/hwmon/hwmon1/pwm1
    ```

2. Microphone
    
    ~~use tinycap to record 4 channel audio~~ 

    ~~to start record~~ 

    ```bash
    tinycap /data/test.wav -D 1 -d 0 -c 4 -r 16000 -b 16
    ```

    ~~to stop record~~ 

    ```bash
    kill -2 $(pifof tinycap)
    ```
   
   Use libtinyalsa libary to record 4 channel audio
   
   ```java
   public class TinyCapture {
       private TinyCaptureCallback mCallback;
   
       public TinyCapture() {
       }
   
       public void setCallback(TinyCaptureCallback callback) {
           this.mCallback = callback;
       }
   
       public native boolean startTinyCapture();
   
       public native void stopTinyCapture();
   
       public void read(byte[] buffer) {
           if (this.mCallback != null) {
               this.mCallback.readBuffer(buffer);
           }
   
       }
   
       static {
           System.loadLibrary("jni_audiorecord");
       }
   }
   ```
   
   
   
3. SPI Led 

   use jni to control spi led

   ```cpp
   static uint8_t color_array[][12] = {
    {0x00, 0x00, 0x00, 0x00, 0xf0, 0xff, 0x00, 0x00, 0xff, 0xff, 0xff, 0xff}, //blue
    {0x00, 0x00, 0x00, 0x00, 0xf0, 0x00, 0xff, 0x00, 0xff, 0xff, 0xff, 0xff}, //green
    {0x00, 0x00, 0x00, 0x00, 0xf0, 0x00, 0x00, 0xff, 0xff, 0xff, 0xff, 0xff}, //red
    {0x00, 0x00, 0x00, 0x00, 0xf0, 0x00, 0x00, 0x00, 0xff, 0xff, 0xff, 0xff}, //close
    };
   ```

4. Camera 

    Google Camerax 

5. Sensor 

   get keyevent for sensor

   ```kotlin
   val sensorKeys = mapOf(
            61 to "61",
            62 to "62",
            63 to "63",
            64 to "64",
            65 to "65",
            66 to "66",
            67 to "67",
            68 to "68"
        )
   
   override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        Timber.d("onKeyDown message:  keyCode $keyCode scanCode ${sensorKeys[event?.scanCode]}")
        return super.onKeyDown(keyCode, event)
    }
   ```

