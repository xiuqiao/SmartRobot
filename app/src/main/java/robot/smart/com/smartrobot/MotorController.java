package robot.smart.com.smartrobot;

import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;

/**
 * Created by Qiao on 2018/1/7.
 */

public class MotorController {
    //定义小车运动方向
    public enum Direction{
        FORWARD, BACK, LEFT, RIGHT, STOP
    }

    private static String TAG = "MotorController";
    private static final String ENABLE_A = "GPIO6_IO12";
    private static final String ENABLE_B = "GPIO6_IO13";
    //左电机
    private static final String IN1 = "GPIO2_IO05";
    private static final String IN2 = "GPIO2_IO00";
    //右电机
    private static final String IN3 = "GPIO2_IO02";
    private static final String IN4 = "GPIO2_IO01";

    //左电机使能IO
    private Gpio mEAIO;
    //右电机使能IO
    private Gpio mEBIO;

    //左电机控制IO
    private Gpio mIN1IO;
    private Gpio mIN2IO;
    //右电机控制IO
    private Gpio mIN3IO;
    private Gpio mIN4IO;

    private PeripheralManagerService mPeripheralManagerService = new PeripheralManagerService();

    private MotorController(){
    }

    private static class MotorControllerInstance{
        private static MotorController motorController = new MotorController();
    }

    public static MotorController getInstance(){
        return MotorControllerInstance.motorController;
    }

    /**
     *  初始化马达 IO 口
     * */
    public boolean initMotor(){
        try {
            mEAIO = mPeripheralManagerService.openGpio(ENABLE_A);
            mEAIO.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            mEAIO.setActiveType(Gpio.ACTIVE_HIGH);

            mEBIO = mPeripheralManagerService.openGpio(ENABLE_B);
            mEBIO.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            mEBIO.setActiveType(Gpio.ACTIVE_HIGH);

            mIN1IO = mPeripheralManagerService.openGpio(IN1);
            mIN1IO.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            mIN1IO.setActiveType(Gpio.ACTIVE_HIGH);

            mIN2IO = mPeripheralManagerService.openGpio(IN2);
            mIN2IO.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            mIN2IO.setActiveType(Gpio.ACTIVE_HIGH);

            mIN3IO = mPeripheralManagerService.openGpio(IN3);
            mIN3IO.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            mIN3IO.setActiveType(Gpio.ACTIVE_HIGH);

            mIN4IO = mPeripheralManagerService.openGpio(IN4);
            mIN4IO.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            mIN4IO.setActiveType(Gpio.ACTIVE_HIGH);

            Log.d(TAG, "init IO success");
            return true;
        }catch (Exception e){
            Log.e(TAG, "init motor gpio error", e);
            return false;
        }
    }

    public void run(Direction direction){
        switch (direction){
            case BACK:
                runBack();
                break;
            case LEFT:
                runLeft();
                break;
            case RIGHT:
                runRight();
                break;
            case FORWARD:
                runForward();
                break;
            case STOP:
            default:
                stop();
        }
    }

    public void closeIO(){
        try {
            if (mEAIO != null){
                mEAIO.close();
            }
            if (mEBIO != null){
                mEBIO.close();
            }
            if (mIN1IO != null){
                mIN1IO.close();
            }
            if (mIN2IO != null){
                mIN2IO.close();
            }
            if (mIN3IO != null){
                mIN3IO.close();
            }
            if (mIN4IO != null){
                mIN4IO.close();
            }
        }catch (Exception e){}

    }

    private void runBack(){
        leftMotorReversalRotating();
        rightMotorReversalRotating();
    }

    private void runForward(){
        leftMotorForwardRotating();
        rightMotorForwardRotating();
    }

    private void runLeft(){
        rightMotorForwardRotating();
        leftMotorReversalRotating();
    }

    private void runRight(){
        rightMotorReversalRotating();
        leftMotorForwardRotating();
    }

    private void stop(){
        try {
            mEAIO.setValue(false);
            mEBIO.setValue(false);
        }catch ( Exception e){
            Log.e(TAG, "stop error", e);
        }
    }

    /**
     * 左电机
     * */
    private void leftMotorForwardRotating(){
        try {
            mEAIO.setValue(true);
            mIN1IO.setValue(false);
            mIN2IO.setValue(true);
            Log.d(TAG, "left forward success");
        }catch ( Exception e){
            Log.e(TAG, "left forward error", e);
        }
    }

    private void leftMotorReversalRotating(){
        try {
            mEAIO.setValue(true);
            mIN1IO.setValue(true);
            mIN2IO.setValue(false);
            Log.d(TAG, "left reversal success");
        }catch ( Exception e){
            Log.e(TAG, "left back error",e);
        }
    }

    /**
     * 右电机
     * */
    private void rightMotorForwardRotating(){
        try {
            mEBIO.setValue(true);
            mIN3IO.setValue(false);
            mIN4IO.setValue(true);
            Log.d(TAG, "right forward success");
        }catch (Exception e){
            Log.e(TAG, "right forward error", e);
        }
    }

    private void rightMotorReversalRotating(){
        try {
            mEBIO.setValue(true);
            mIN3IO.setValue(true);
            mIN4IO.setValue(false);
            Log.d(TAG, "right reversal success");
        }catch (Exception e){
            Log.e(TAG, "right back error",e);
        }
    }
}
