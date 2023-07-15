package com.eegsmart.blesdk.check;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.eegsmart.blesdk.util.OrderUtils;

import static com.eegsmart.blesdk.check.OrderState.Type.*;

/**
 * Created by yunting on 2018/10/16.
 */

public class CheckOrderUtils {

    private static Handler mHandler;
    private static OrderState.Type ORDER_STEP;
    private static int resendCount = 0;

    private static void checkMMTimeOut() {
        resendCount = 0;
        destroyHandler();
        mHandler = new Handler(Looper.getMainLooper());
        mHandler.postDelayed(mRunnable, 500);
    }

    public static void destroyHandler() {
        if (null != mHandler) {
            mHandler.removeCallbacks(mRunnable);
            mHandler = null;
        }
    }

    private static final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (ORDER_STEP == GET_OVER) {
                destroyHandler();
            } else {
                resendCount++;
                Log.d("check", "resendCount " + resendCount);
                if (resendCount > 5) {
                    destroyHandler();
                    resendCount = 0;
                    return;
                }
                setOrder(ORDER_STEP);
                mHandler.postDelayed(mRunnable, 500);
            }
        }
    };

    public static void setTimerOrder(OrderState.Type type) {
        setOrder(type);
        ORDER_STEP = type;
        checkMMTimeOut();
    }

    private static void setOrder(OrderState.Type ORDER_STEP) {
        Log.d("check", "setOrder " + ORDER_STEP.name());
        switch (ORDER_STEP) {
            case GET_HD_VERSION:
                OrderUtils.getDeviceHardVersion();
                break;
            case GET_SN_NUMBER:
                OrderUtils.getDeviceSN();
                break;
            case GET_DEV_SW_MSG:
                OrderUtils.getDeviceSoftVersion();
                break;
            case GET_SYS_TIME:
                OrderUtils.startSysTime();
                break;
            case GET_SYS_OPEN_DATE:
                OrderUtils.openBatchControl();
                break;
            case GET_OVER:
                destroyHandler();
                break;
        }
    }

}
