package robot.smart.com.smartrobot;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.List;

/**
 * Skeleton of an Android Things activity.
 * <p>
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 * <p>
 * <pre>{@code
 * PeripheralManagerService service = new PeripheralManagerService();
 * mLedGpio = service.openGpio("BCM6");
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
 * mLedGpio.setValue(true);
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 */
public class MainActivity extends Activity {
    MotorController mMotorController = MotorController.getInstance();
    UltrasonicDetectorController mUltrasonicController = UltrasonicDetectorController.getInstance();
    ServoMotorsController mServoCotroller = ServoMotorsController.getInstance();
    private HandlerThread  mBackHandlerThread;
    private Handler mBackHander;
    private boolean motorRet;
    private boolean ultrasonRet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initBackThread();
//        initMotor();
//        initUltrasonic();
        initServoMotorController();

    }

    private void initBackThread(){
       mBackHandlerThread = new HandlerThread("MainBackThread");
       mBackHandlerThread.start();
       mBackHander = new Handler(mBackHandlerThread.getLooper());
    }

    private void initMotor(){
        motorRet = mMotorController.initMotor();
        if (motorRet){
            mMotorController.run(MotorController.Direction.FORWARD);
        }
    }

    private void initUltrasonic(){
        ultrasonRet = mUltrasonicController.initUltrasonic();
        if (ultrasonRet){
            mBackHander.post(new Runnable() {//试验超声波测距，不断读取
                @Override
                public void run() {
                    while (true){
                        float distance = mUltrasonicController.measureDistance();
                        Log.d("distance",  distance + "cm");
                        try {
                            Thread.sleep(4000);
                        }catch (Exception e){}
                    }
                }
            });
        }
    }

    private void initServoMotorController(){
        if ( mServoCotroller.initServoMotor()){
            mBackHander.post(new Runnable() {
                @Override
                public void run() {//试验舵机转动角度
                    while (true){
                        for (ServoMotorsController.RotateAngle angle : ServoMotorsController.RotateAngle.values()){
                            mServoCotroller.setAngle(angle);
                            try {
                                Thread.sleep(5000);
                            }catch (Exception e){}
                        }

                        for (int i = ServoMotorsController.RotateAngle.values().length - 1; i >= 0; i--) {
                            mServoCotroller.setAngle(ServoMotorsController.RotateAngle.values()[i]);
                            try {
                                Thread.sleep(5000);
                            } catch (Exception e) {
                            }

                        }
                    }

                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMotorController.closeIO();
        mUltrasonicController.closeUltrasonGPIO();
        mServoCotroller.closeServoController();
    }
}
