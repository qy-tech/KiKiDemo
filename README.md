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

    use tinycap to record 4 channel audio 

    to start record 

    ```bash
    tinycap /data/test.wav -D 1 -d 0 -c 4 -r 16000 -b 16
    ```

    to stop record 

    ```bash
    kill -2 $(pifof tinycap)
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

    Google Camerax api 

