#include <jni.h>
#include <string>
#include <cstdint>
#include <unistd.h>
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <getopt.h>
#include <fcntl.h>
#include <sys/ioctl.h>
#include <linux/types.h>
#include <linux/spi/spidev.h>
#include <android/log.h>

#define LOG_TAG    "SPI Test"
#define LOG(LEVEL, ...)  __android_log_print(LEVEL, LOG_TAG, __VA_ARGS__)
#define LOGD(...)  LOG(ANDROID_LOG_DEBUG,__VA_ARGS__)
#define LOGI(...)  LOG(ANDROID_LOG_INFO,__VA_ARGS__)
#define LOGE(...)  LOG(ANDROID_LOG_ERROR, __VA_ARGS__)
#define LOGW(...)  LOG(ANDROID_LOG_WARN, __VA_ARGS__)
#define ARRAY_SIZE(a) (sizeof(a) / sizeof((a)[0]))

static const char *device = "/dev/spidev1.0";
static uint32_t mode = 0;
static uint8_t bits = 8;
static uint32_t speed = 5000000;
static uint16_t delay = 0;
static uint8_t tx[12] = {0};
static uint8_t rx[12] = {0};
static uint8_t color_array[][12] = {
    {0x00, 0x00, 0x00, 0x00, 0xf0, 0xff, 0x00, 0x00, 0xff, 0xff, 0xff, 0xff}, //blue
    {0x00, 0x00, 0x00, 0x00, 0xf0, 0x00, 0xff, 0x00, 0xff, 0xff, 0xff, 0xff}, //green
    {0x00, 0x00, 0x00, 0x00, 0xf0, 0x00, 0x00, 0xff, 0xff, 0xff, 0xff, 0xff}, //red
    {0x00, 0x00, 0x00, 0x00, 0xf0, 0x00, 0x00, 0x00, 0xff, 0xff, 0xff, 0xff}, //close
};

static void spi_init(int fd);
static void transfer(int fd, uint8_t const *tx, uint8_t const *rx, size_t len, char rw);
__unused static void array_copy(uint8_t *des, uint8_t *src, int count);
__unused static void pabort(const char *s);

extern "C" JNIEXPORT void JNICALL
Java_com_qytech_kikidemo_ui_main_MainViewModel_spiTest(
    JNIEnv *env,
    jobject /* this */) {
  std::string hello = "Hello from C++";
  int fd = 0;
  int ret = 0;

  fd = open(device, O_RDWR);
  if (fd < 0) {
    LOGE("can't open device");
  }
  spi_init(fd);

  transfer(fd, color_array[0], rx, 12, 'w');
  sleep(1);
  transfer(fd, color_array[1], rx, 12, 'w');
  sleep(1);
  transfer(fd, color_array[2], rx, 12, 'w');
  sleep(1);
  transfer(fd, color_array[3], rx, 12, 'w');
  sleep(1);

  close(fd);
}

static void transfer(int fd, uint8_t const *tx, uint8_t const *rx, size_t len, char rw) {
  int ret = 0;
  int i = 0;

  struct spi_ioc_transfer tr = {
      .tx_buf = (unsigned long) tx,
      .rx_buf = (unsigned long) rx,
      .len = (uint) len,
      .speed_hz = speed,
      .delay_usecs = delay,
      .bits_per_word = bits,
  };

  ret = ioctl(fd, SPI_IOC_MESSAGE(1), &tr);
  if (ret < 1) {
    LOGE("can't send spi message");
  }

  if (rw == 'w') {
    LOGD("!!!===tx:");
    for (i = 0; i < len; i++) {
      LOGD(" %02x", tx[i]);
    }
    LOGD("===\n");
  }
  if (rw == 'r') {
    LOGD("!!!***rx:");
    for (i = 0; i <= rx[0]; i++) {
      LOGD(" %02x", rx[i]);
    }
    LOGD("***\n");
  }
}

static void spi_init(int fd) {
  int ret = 0;

  //mode |= SPI_CPHA;

  ret = ioctl(fd, SPI_IOC_WR_MODE32, &mode);
  if (ret == -1)
    LOGE("can't set spi mode");
  ret = ioctl(fd, SPI_IOC_RD_MODE32, &mode);
  if (ret == -1)
    LOGE("can't get spi mode");

  ret = ioctl(fd, SPI_IOC_WR_BITS_PER_WORD, &bits);
  if (ret == -1)
    LOGE("can't set bits per word");
  ret = ioctl(fd, SPI_IOC_RD_BITS_PER_WORD, &bits);
  if (ret == -1)
    LOGE("can't get bits per word");

  ret = ioctl(fd, SPI_IOC_WR_MAX_SPEED_HZ, &speed);
  if (ret == -1)
    LOGE("can't set max speed hz");
  ret = ioctl(fd, SPI_IOC_RD_MAX_SPEED_HZ, &speed);
  if (ret == -1)
    LOGE("can't get max speed hz");

  LOGD("spi mode: 0x%x\n", mode);
  LOGD("bits per word: %d\n", bits);
  LOGD("max speed: %d Hz (%d KHz)\n", speed, speed / 1000);
}

__unused static void array_copy(uint8_t *des, uint8_t *src, int count) {
  int i = 0;
  for (i = 0; i < count; i++) {
    des[i] = src[i];
  }
}

__unused static void pabort(const char *s) {
  perror(s);
  abort();
}
