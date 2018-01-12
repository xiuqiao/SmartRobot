package robot.smart.com.smartrobot;

import android.util.Log;

import com.google.android.things.pio.PeripheralManagerService;
import com.google.android.things.pio.Pwm;

/**
 * Created by Qiao on 2018/1/8.
 */

public class ServoMotorsController {
    //范围为-90 - 0 - 90 度
    public enum RotateAngle{
        ANGLE_F90, ANGLE_F45, ANGLE_0, ANGLE_45, ANGLE_90
    }
    private static final String TAG = "ServoMotorsController";
    private final String M_PWM = "PWM1";
    private Pwm mPwm;
    private PeripheralManagerService mManagerService = new PeripheralManagerService();
    private final int PWM_HZ = 50;
    private final double BASE_DUTY = 2.5; // 舵机最小控制时间 0.5ms-2.5 -- -90 - 90  现在周期是 10 ms，因此一个单元的百分比是 5/100

    private static class ServoMotorsControllerInstance{
        private static ServoMotorsController mServoMotorsController = new ServoMotorsController();
    }

    public static ServoMotorsController getInstance(){
        return ServoMotorsControllerInstance.mServoMotorsController;
    }

    public boolean initServoMotor(){
        try {
            mPwm = mManagerService.openPwm(M_PWM);
            mPwm.setPwmFrequencyHz(PWM_HZ);
            Log.d(TAG, "servo init success");
            return true;
        }catch (Exception e){
            Log.e(TAG, "init servo error", e);
            return false;
        }
    }

    public void setAngle(RotateAngle angle){
        switch (angle){
            case ANGLE_F90:
                setPwmDuty(BASE_DUTY);
                break;
            case ANGLE_F45:
                setPwmDuty(2 * BASE_DUTY);
                break;
            case ANGLE_0:
                setPwmDuty(3 * BASE_DUTY);
                break;
            case ANGLE_45:
                setPwmDuty(4 * BASE_DUTY);
                break;
            case ANGLE_90:
                setPwmDuty(5 * BASE_DUTY);
                break;
                default:
                    try {
                        mPwm.setEnabled(false);
                    }catch (Exception e){}
        }
    }

    public void closeServoController(){
        if (mPwm != null){
            try {
                mPwm.close();
            }catch ( Exception e){}
        }
    }

    private void setPwmDuty(double duty){
        try {
            Log.d("set duty is", duty + "%");
            mPwm.setPwmDutyCycle(duty);
            mPwm.setEnabled(true);
        }catch (Exception e){
            Log.e(TAG, "set duty error", e);
        }
    }
}
