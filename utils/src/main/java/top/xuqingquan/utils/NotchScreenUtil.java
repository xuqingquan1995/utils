package top.xuqingquan.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.DisplayCutout;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * notch screen (刘海屏) 检测utils
 * 适配了华为、oppo、小米、vivo
 *
 * @author 许清泉
 */
public class NotchScreenUtil {

    /**
     * 设置应用窗口在华为notch手机使用刘海区的flag值, 该值为华为官方提供, 不要修改
     */
    private static final int FLAG_NOTCH_SUPPORT_HW = 0x00010000;

    /**
     * vivo手机判断是否是notch, vivo官方提供, 不要修改
     */
    private static final int FLAG_NOTCH_SUPPORT_VIVO = 0x00000020;


    public static boolean checkNotchScreen(Context context) {
        //处理Android P 判断是否是刘海屏
        try {
            if (context instanceof Activity) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    WindowInsets windowInsets = ((Activity) context).getWindow().getDecorView().getRootWindowInsets();
                    if (windowInsets != null) {
                        DisplayCutout displayCutout = windowInsets.getDisplayCutout();
                        if (displayCutout != null) {
                            return true;
                        }
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        if (checkHuaWei(context)) {
            return true;
        } else if (checkVivo(context)) {
            return true;
        } else if (checkMiUi(context)) {
            return true;
        } else {
            return checkOppo(context);
        }
    }

    /**
     * oppo提供: 刘海屏判断.
     *
     * @return true, 刘海屏; false: 非刘海屏
     */
    private static boolean checkOppo(Context context) {
        try {
            return context.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
        } catch (Exception e) {
            Timber.e("checkOppo notchScreen exception");
        }
        return false;
    }

    /**
     * 小米提供: 刘海屏判断.
     *
     * @return true, 刘海屏; false: 非刘海屏
     */
    private static boolean checkMiUi(Context context) {
        int result;
        try {
            ClassLoader classLoader = context.getClassLoader();
            @SuppressLint("PrivateApi")
            @SuppressWarnings("rawtypes")
            Class systemProperties = classLoader.loadClass("android.os.SystemProperties");
            //参数类型
            @SuppressWarnings("rawtypes")
            Class[] paramTypes = new Class[2];
            paramTypes[0] = String.class;
            paramTypes[1] = int.class;
            //noinspection unchecked
            Method getInt = systemProperties.getMethod("getInt", paramTypes);
            //参数
            Object[] params = new Object[2];
            params[0] = "ro.miui.notch";
            params[1] = 0;
            //noinspection ConstantConditions
            result = (Integer) getInt.invoke(systemProperties, params);
            return result == 1;
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 华为提供: 判断是否是刘海屏
     *
     * @param context Context
     * @return true：刘海屏；false：非刘海屏
     */
    private static boolean checkHuaWei(Context context) {
        boolean ret = false;
        try {
            ClassLoader cl = context.getClassLoader();
            Class<?> hwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = hwNotchSizeUtil.getMethod("hasNotchInScreen");
            //noinspection ConstantConditions
            ret = (boolean) get.invoke(hwNotchSizeUtil);
        } catch (ClassNotFoundException e) {
            Timber.e("hasNotchInScreen ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            Timber.e("hasNotchInScreen NoSuchMethodException");
        } catch (Exception e) {
            Timber.e("hasNotchInScreen Exception");

        }
        return ret;
    }

    /**
     * vivo提供: 判断是否是刘海屏
     *
     * @param context Context
     * @return true：是刘海屏；false：非刘海屏
     */
    @SuppressWarnings({"ConstantConditions", "JavaReflectionInvocation"})
    private static boolean checkVivo(Context context) {
        boolean ret;
        try {
            ClassLoader cl = context.getClassLoader();
            @SuppressLint("PrivateApi")
            Class<?> ftFeature = cl.loadClass("android.util.FtFeature");
            Method isFeatureSupport = ftFeature.getMethod("isFeatureSupport");
            ret = (boolean) isFeatureSupport.invoke(ftFeature, FLAG_NOTCH_SUPPORT_VIVO);
        } catch (Exception e) {
            e.printStackTrace();
            ret = false;
        }
        return ret;
    }

    /**
     * 华为提供: 获取刘海尺寸
     *
     * @param context Context
     * @return int[0]值为刘海宽度 int[1]值为刘海高度。
     */
    public static int[] getNotchSize(Context context) {
        int[] ret = new int[]{0, 0};
        try {
            ClassLoader cl = context.getClassLoader();
            Class<?> hwnotchsizeutil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = hwnotchsizeutil.getMethod("getNotchSize");
            ret = (int[]) get.invoke(hwnotchsizeutil);
        } catch (ClassNotFoundException e) {
            Timber.e("getNotchSize ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            Timber.e("getNotchSize NoSuchMethodException");
        } catch (Exception e) {
            Timber.e("getNotchSize Exception");
        }
        return ret;
    }

    /**
     * 华为提供: 设置应用窗口在华为刘海屏手机使用刘海区
     *
     * @param window 应用页面window对象
     */
    public static void setFullScreenWindowLayoutInDisplayCutout(Window window) {
        if (window == null) {
            return;
        }
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        try {
            Class<?> layoutParamsExCls = Class.forName("com.huawei.android.view.LayoutParamsEx");
            Constructor<?> con = layoutParamsExCls.getConstructor(WindowManager.LayoutParams.class);
            Object layoutParamsExObj = con.newInstance(layoutParams);
            Method method = layoutParamsExCls.getMethod("addHwFlags", int.class);
            method.invoke(layoutParamsExObj, FLAG_NOTCH_SUPPORT_HW);
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            Timber.e("hw add notch screen flag api error");
        } catch (Exception e) {
            Timber.e("other Exception");
        }
    }

    /**
     * 华为提供: 设置应用窗口在华为刘海屏手机不使用刘海区
     *
     * @param window 应用页面window对象
     */
    public static void setNotFullScreenWindowLayoutInDisplayCutout(Window window) {
        if (window == null) {
            return;
        }
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        try {
            Class<?> layoutParamsExCls = Class.forName("com.huawei.android.view.LayoutParamsEx");
            Constructor<?> con = layoutParamsExCls.getConstructor(WindowManager.LayoutParams.class);
            Object layoutParamsExObj = con.newInstance(layoutParams);
            Method method = layoutParamsExCls.getMethod("clearHwFlags", int.class);
            method.invoke(layoutParamsExObj, FLAG_NOTCH_SUPPORT_HW);
            Timber.e("............clear");
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            Timber.e("hw clear notch screen flag api error");
        } catch (Exception e) {
            Timber.e("other Exception");
        }
    }
}
