package robot.smart.com.smartrobot;

import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.PeripheralManagerService;

/**
 * Created by Qiao on 2018/1/7.
 */

public class UltrasonicDetectorController {
    private static final String TAG = "UltrasonicDetectorController";
    private static final String TRIG = "GPIO2_IO03";
    private static final String ECHO = "GPIO1_IO10";
    private Gpio mTrigIO;
    private Gpio mEchoIO;
    private PeripheralManagerService mPeripheralManagerService = new PeripheralManagerService();

    private UltrasonicDetectorController(){}

    static class UltrasonicDetectorControllerInstance{
        private static UltrasonicDetectorController instance = new UltrasonicDetectorController();
    }

    public static UltrasonicDetectorController getInstance(){
        return UltrasonicDetectorControllerInstance.instance;
    }

    /**
     * 必须初始化
     * */
    public boolean initUltrasonic(){
        try {
            mTrigIO = mPeripheralManagerService.openGpio(TRIG);
            mTrigIO.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            mTrigIO.setActiveType(Gpio.ACTIVE_HIGH);

            mEchoIO = mPeripheralManagerService.openGpio(ECHO);
            mEchoIO.setDirection(Gpio.DIRECTION_IN);
            Log.d(TAG, "Ultrasonic init success");
            return true;
        }catch ( Exception e){
            Log.e(TAG, "ultrasonic init failed",e);
            return false;
        }
    }

    /**
     * 返回的是厘米
     * */
    public float measureDistance(){
        try {
            mTrigIO.setValue(false);
            mTrigIO.setValue(true);
            Thread.sleep(0, 20);
            mTrigIO.setValue(false);
            while (!mEchoIO.getValue());//低电平等高电平
            long startTime =  System.nanoTime();
            while ( mEchoIO.getValue()); // 高电平等低电平
            long endTime = System.nanoTime();
            if (endTime > startTime){
                long timeN = endTime - startTime;
                float timeS = (float) timeN / (float)(1000 * 1000 * 1000);
                return ( timeS * 340 / 2 ) * 100;
            }
        }catch ( Exception e){
            return 0;
        }
        return 0;
    }

    public void closeUltrasonGPIO(){
        try {
            if (mEchoIO != null){
                mEchoIO.close();
            }
            if (mTrigIO != null){
                mTrigIO.close();
            }
        }catch (Exception e){}
    }

    private GpioCallback mGpioCallBack = new GpioCallback() {
        @Override
        public boolean onGpioEdge(Gpio gpio) {
            return true;
        }

        @Override
        public void onGpioError(Gpio gpio, int error) {
            super.onGpioError(gpio, error);
        }
    };

}
