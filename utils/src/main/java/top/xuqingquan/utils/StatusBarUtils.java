package top.xuqingquan.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import android.support.annotation.ColorInt;
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.v4.view.ViewCompat;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import top.xuqingquan.utils.R;

/**
 * Created by 许清泉 on 2019-07-23 20:00
 * 从QMUI上提取后加工改造
 * {@link #translucent 可以设置沉浸式}
 * {@link #setStatusBarTextBlack 可以将statusbar的文字设置成黑色}
 * {@link #setStatusBarTextWhite 可以将statusbar的文字设置成白色}
 * {@link #isFullScreen 判断是否全屏}
 * {@link #getStatusbarHeight 获取状态栏高度}
 * {@link #setStatusBarBackgroundColor 可以设置状态栏颜色}
 * {@link #setNavigationBackgroundColor 可以设置导航栏颜色}
 * {@link #showhideBar 控制导航栏和状态栏的显示和隐藏}
 * {@link #setNavigationIconDark 设置导航栏图标是否为暗色}
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class StatusBarUtils {

    private final static int STATUSBAR_TYPE_DEFAULT = 0;
    private final static int STATUSBAR_TYPE_MIUI = 1;
    private final static int STATUSBAR_TYPE_FLYME = 2;
    private final static int STATUSBAR_TYPE_ANDROID6 = 3; // Android 6.0
    private final static int STATUS_BAR_DEFAULT_HEIGHT_DP = 25; // 大部分状态栏都是25dp
    // 在某些机子上存在不同的density值，所以增加两个虚拟值
    public static float sVirtualDensity = -1;
    public static float sVirtualDensityDpi = -1;
    private static int sStatusBarHeight = -1;
    private static @StatusBarType
    int mStatusBarType = STATUSBAR_TYPE_DEFAULT;
    private static Integer sTransparentValue;


    private static final int DEFAULT_STATUS_BAR_ALPHA = 0;//默认状态栏透明度
    private static final int FAKE_STATUS_BAR_VIEW_ID = R.id.scaffold_fake_status_bar_view;
    private static final int STATUSBARUTILS_NAVIGATION_BAR_VIEW = R.id.scaffold_navigation_bar_view;
    private static int sStatusbarHeight = -1;
    /**
     * 导航栏竖屏高度标识位
     */
    private static final String IMMERSION_NAVIGATION_BAR_HEIGHT = "navigation_bar_height";
    /**
     * 导航栏宽度标识位位
     */
    private static final String IMMERSION_NAVIGATION_BAR_WIDTH = "navigation_bar_width";
    /**
     * 导航栏横屏高度标识位
     */
    private static final String IMMERSION_NAVIGATION_BAR_HEIGHT_LANDSCAPE = "navigation_bar_height_landscape";
    /**
     * MIUI导航栏显示隐藏标识位
     */
    private static final String IMMERSION_MIUI_NAVIGATION_BAR_HIDE_SHOW = "force_fsg_nav_bar";
    /**
     * EMUI导航栏显示隐藏标识位
     */
    private static final String IMMERSION_EMUI_NAVIGATION_BAR_HIDE_SHOW = "navigationbar_is_min";

    public static void translucent(Activity activity) {
        translucent(activity.getWindow());
    }

    public static void translucent(Window window) {
        translucent(window, 0x40000000);
    }

    private static boolean supportTranslucent() {
        // Essential Phone 在 Android 8 之前沉浸式做得不全，系统不从状态栏顶部开始布局却会下发 WindowInsets
        return !(RomUtils.isEssential() && Build.VERSION.SDK_INT < 26);
    }

    /**
     * 沉浸式状态栏。
     * 支持 4.4 以上版本的 MIUI 和 Flyme，以及 5.0 以上版本的其他 Android。
     *
     * @param activity 需要被设置沉浸式状态栏的 Activity。
     */
    public static void translucent(Activity activity, @ColorInt int colorOn5x) {
        Window window = activity.getWindow();
        translucent(window, colorOn5x);
    }

    @TargetApi(19)
    public static void translucent(Window window, @ColorInt int colorOn5x) {
        if (!supportTranslucent()) {
            // 版本小于4.4，绝对不考虑沉浸式
            return;
        }

        if (isNotchOfficialSupport()) {
            handleDisplayCutoutMode(window);
        }

        // 小米和魅族4.4 以上版本支持沉浸式
        // 小米 Android 6.0 ，开发版 7.7.13 及以后版本设置黑色字体又需要 clear FLAG_TRANSLUCENT_STATUS, 因此还原为官方模式
        if (RomUtils.isFlymeLowerThan(8) || (RomUtils.isMIUI() && Build.VERSION.SDK_INT < Build.VERSION_CODES.M)) {
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && supportTransclentStatusBar6()) {
                // android 6以后可以改状态栏字体颜色，因此可以自行设置为透明
                // ZUK Z1是个另类，自家应用可以实现字体颜色变色，但没开放接口
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
            } else {
                // android 5不能修改状态栏字体颜色，因此直接用FLAG_TRANSLUCENT_STATUS，nexus表现为半透明
                // 魅族和小米的表现如何？
                // update: 部分手机运用FLAG_TRANSLUCENT_STATUS时背景不是半透明而是没有背景了。。。。。
//                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

                // 采取setStatusBarColor的方式，部分机型不支持，那就纯黑了，保证状态栏图标可见
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(colorOn5x);
            }
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            // android4.4的默认是从上到下黑到透明，我们的背景是白色，很难看，因此只做魅族和小米的
//        } else if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1){
//            // 如果app 为白色，需要更改状态栏颜色，因此不能让19一下支持透明状态栏
//            Window window = activity.getWindow();
//            Integer transparentValue = getStatusBarAPITransparentValue(activity);
//            if(transparentValue != null) {
//                window.getDecorView().setSystemUiVisibility(transparentValue);
//            }
        }
    }

    @TargetApi(28)
    private static void handleDisplayCutoutMode(final Window window) {
        View decorView = window.getDecorView();
        if (ViewCompat.isAttachedToWindow(decorView)) {
            realHandleDisplayCutoutMode(window, decorView);
        } else {
            decorView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {
                    v.removeOnAttachStateChangeListener(this);
                    realHandleDisplayCutoutMode(window, v);
                }

                @Override
                public void onViewDetachedFromWindow(View v) {

                }
            });
        }
    }

    @TargetApi(28)
    private static void realHandleDisplayCutoutMode(Window window, View decorView) {
        if (decorView.getRootWindowInsets() != null &&
                decorView.getRootWindowInsets().getDisplayCutout() != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.layoutInDisplayCutoutMode = WindowManager.LayoutParams
                    .LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            window.setAttributes(params);
        }
    }

    /**
     * 设置状态栏黑色字体图标，
     * 支持 4.4 以上版本 MIUI 和 Flyme，以及 6.0 以上版本的其他 Android
     *
     * @param activity 需要被处理的 Activity
     */
    public static boolean setStatusBarTextBlack(Activity activity) {
        if (activity == null) return false;
        // 无语系列：ZTK C2016只能时间和电池图标变色。。。。
        if (RomUtils.isZTKC2016()) {
            return false;
        }

        if (mStatusBarType != STATUSBAR_TYPE_DEFAULT) {
            return setStatusBarLightMode(activity, mStatusBarType);
        }
        if (isMIUICustomStatusBarLightModeImpl() && MIUISetStatusBarLightMode(activity.getWindow(), true)) {
            mStatusBarType = STATUSBAR_TYPE_MIUI;
            return true;
        } else if (FlymeSetStatusBarLightMode(activity.getWindow(), true)) {
            mStatusBarType = STATUSBAR_TYPE_FLYME;
            return true;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Android6SetStatusBarLightMode(activity.getWindow(), true);
            mStatusBarType = STATUSBAR_TYPE_ANDROID6;
            return true;
        }
        return false;
    }

    /**
     * 已知系统类型时，设置状态栏黑色字体图标。
     * 支持 4.4 以上版本 MIUI 和 Flyme，以及 6.0 以上版本的其他 Android
     *
     * @param activity 需要被处理的 Activity
     * @param type     StatusBar 类型，对应不同的系统
     */
    private static boolean setStatusBarLightMode(Activity activity, @StatusBarType int type) {
        if (type == STATUSBAR_TYPE_MIUI) {
            return MIUISetStatusBarLightMode(activity.getWindow(), true);
        } else if (type == STATUSBAR_TYPE_FLYME) {
            return FlymeSetStatusBarLightMode(activity.getWindow(), true);
        } else if (type == STATUSBAR_TYPE_ANDROID6) {
            return Android6SetStatusBarLightMode(activity.getWindow(), true);
        }
        return false;
    }


    /**
     * 设置状态栏白色字体图标
     * 支持 4.4 以上版本 MIUI 和 Flyme，以及 6.0 以上版本的其他 Android
     */
    public static boolean setStatusBarTextWhite(Activity activity) {
        if (activity == null) return false;
        if (mStatusBarType == STATUSBAR_TYPE_DEFAULT) {
            // 默认状态，不需要处理
            return true;
        }

        if (mStatusBarType == STATUSBAR_TYPE_MIUI) {
            return MIUISetStatusBarLightMode(activity.getWindow(), false);
        } else if (mStatusBarType == STATUSBAR_TYPE_FLYME) {
            return FlymeSetStatusBarLightMode(activity.getWindow(), false);
        } else if (mStatusBarType == STATUSBAR_TYPE_ANDROID6) {
            return Android6SetStatusBarLightMode(activity.getWindow(), false);
        }
        return true;
    }

    @TargetApi(23)
    private static int changeStatusBarModeRetainFlag(Window window, int out) {
        out = retainSystemUiFlag(window, out, View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        out = retainSystemUiFlag(window, out, View.SYSTEM_UI_FLAG_FULLSCREEN);
        out = retainSystemUiFlag(window, out, View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        out = retainSystemUiFlag(window, out, View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        out = retainSystemUiFlag(window, out, View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        out = retainSystemUiFlag(window, out, View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        return out;
    }

    public static int retainSystemUiFlag(Window window, int out, int type) {
        int now = window.getDecorView().getSystemUiVisibility();
        if ((now & type) == type) {
            out |= type;
        }
        return out;
    }


    /**
     * 设置状态栏字体图标为深色，Android 6
     *
     * @param window 需要设置的窗口
     * @param light  是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    @TargetApi(23)
    private static boolean Android6SetStatusBarLightMode(Window window, boolean light) {
        View decorView = window.getDecorView();
        int systemUi = light ? View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR : View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        systemUi = changeStatusBarModeRetainFlag(window, systemUi);
        decorView.setSystemUiVisibility(systemUi);
        if (RomUtils.isMIUIV9()) {
            // MIUI 9 低于 6.0 版本依旧只能回退到以前的方案
            // https://github.com/Tencent/QMUI_Android/issues/160
            MIUISetStatusBarLightMode(window, light);
        }
        return true;
    }

    /**
     * 设置状态栏字体图标为深色，需要 MIUIV6 以上
     *
     * @param window 需要设置的窗口
     * @param light  是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回 true
     */
    public static boolean MIUISetStatusBarLightMode(Window window, boolean light) {
        boolean result = false;
        if (window != null) {
            Class<?> clazz = window.getClass();
            try {
                int darkModeFlag;
                @SuppressLint("PrivateApi")
                Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (light) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);//状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
                }
                result = true;
            } catch (Throwable ignored) {

            }
        }
        return result;
    }

    /**
     * 更改状态栏图标、文字颜色的方案是否是MIUI自家的， MIUI9 && Android 6 之后用回Android原生实现
     * 见小米开发文档说明：https://dev.mi.com/console/doc/detail?pId=1159
     */
    private static boolean isMIUICustomStatusBarLightModeImpl() {
        if (RomUtils.isMIUIV9() && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        return RomUtils.isMIUIV5() || RomUtils.isMIUIV6() ||
                RomUtils.isMIUIV7() || RomUtils.isMIUIV8();
    }

    /**
     * 设置状态栏图标为深色和魅族特定的文字风格
     * 可以用来判断是否为 Flyme 用户
     *
     * @param window 需要设置的窗口
     * @param light  是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    @SuppressWarnings("JavaReflectionMemberAccess")
    public static boolean FlymeSetStatusBarLightMode(Window window, boolean light) {
        boolean result = false;
        if (window != null) {

            Android6SetStatusBarLightMode(window, light);

            // flyme 在 6.2.0.0A 支持了 Android 官方的实现方案，旧的方案失效
            // 高版本调用这个出现不可预期的 Bug,官方文档也没有给出完整的高低版本兼容方案
            if (RomUtils.isFlymeLowerThan(7)) {
                try {
                    WindowManager.LayoutParams lp = window.getAttributes();
                    Field darkFlag = WindowManager.LayoutParams.class
                            .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                    Field meizuFlags = WindowManager.LayoutParams.class
                            .getDeclaredField("meizuFlags");
                    darkFlag.setAccessible(true);
                    meizuFlags.setAccessible(true);
                    int bit = darkFlag.getInt(null);
                    int value = meizuFlags.getInt(lp);
                    if (light) {
                        value |= bit;
                    } else {
                        value &= ~bit;
                    }
                    meizuFlags.setInt(lp, value);
                    window.setAttributes(lp);
                    result = true;
                } catch (Throwable ignored) {

                }
            } else if (RomUtils.isFlyme()) {
                result = true;
            }
        }
        return result;
    }

    /**
     * 获取是否全屏
     *
     * @return 是否全屏
     */
    public static boolean isFullScreen(Activity activity) {
        boolean ret = false;
        try {
            WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
            ret = (attrs.flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) != 0;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * API19之前透明状态栏：获取设置透明状态栏的system ui visibility的值，这是部分有提供接口的rom使用的
     * http://stackoverflow.com/questions/21865621/transparent-status-bar-before-4-4-kitkat
     */
    public static Integer getStatusBarAPITransparentValue(Context context) {
        if (sTransparentValue != null) {
            return sTransparentValue;
        }
        String[] systemSharedLibraryNames = context.getPackageManager()
                .getSystemSharedLibraryNames();
        String fieldName = null;
        if (systemSharedLibraryNames != null) {
            for (String lib : systemSharedLibraryNames) {
                if ("touchwiz".equals(lib)) {
                    fieldName = "SYSTEM_UI_FLAG_TRANSPARENT_BACKGROUND";
                } else if (lib.startsWith("com.sonyericsson.navigationbar")) {
                    fieldName = "SYSTEM_UI_FLAG_TRANSPARENT";
                }
            }
        }

        if (fieldName != null) {
            try {
                Field field = View.class.getField(fieldName);
                Class<?> type = field.getType();
                if (type == int.class) {
                    sTransparentValue = field.getInt(null);
                }
            } catch (Throwable ignored) {
            }
        }
        return sTransparentValue;
    }

    /**
     * 检测 Android 6.0 是否可以启用 window.setStatusBarColor(Color.TRANSPARENT)。
     */
    public static boolean supportTransclentStatusBar6() {
        return !(RomUtils.isZUKZ1() || RomUtils.isZTKC2016());
    }

    /**
     * 获取状态栏的高度。
     */
    public static int getStatusbarHeight(Context context) {
        if (sStatusBarHeight == -1) {
            initStatusBarHeight(context);
        }
        return sStatusBarHeight;
    }

    @SuppressLint("PrivateApi")
    private static void initStatusBarHeight(Context context) {
        Class<?> clazz;
        Object obj = null;
        Field field = null;
        try {
            clazz = Class.forName("com.android.internal.R$dimen");
            obj = clazz.newInstance();
            if (RomUtils.isMeizuPhone()) {
                try {
                    field = clazz.getField("status_bar_height_large");
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
            if (field == null) {
                field = clazz.getField("status_bar_height");
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        if (field != null) {
            try {
                //noinspection ConstantConditions
                int id = Integer.parseInt(field.get(obj).toString());
                sStatusBarHeight = context.getResources().getDimensionPixelSize(id);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        if (RomUtils.isTablet(context)
                && sStatusBarHeight > DimensionsKt.dip(context, STATUS_BAR_DEFAULT_HEIGHT_DP)) {
            //状态栏高度大于25dp的平板，状态栏通常在下方
            sStatusBarHeight = 0;
        } else {
            if (sStatusBarHeight <= 0) {
                if (sVirtualDensity == -1) {
                    sStatusBarHeight = DimensionsKt.dip(context, STATUS_BAR_DEFAULT_HEIGHT_DP);
                } else {
                    sStatusBarHeight = (int) (STATUS_BAR_DEFAULT_HEIGHT_DP * sVirtualDensity + 0.5f);
                }
            }
        }
    }

    public static void setVirtualDensity(float density) {
        sVirtualDensity = density;
    }

    public static void setVirtualDensityDpi(float densityDpi) {
        sVirtualDensityDpi = densityDpi;
    }

    @IntDef({STATUSBAR_TYPE_DEFAULT, STATUSBAR_TYPE_MIUI, STATUSBAR_TYPE_FLYME, STATUSBAR_TYPE_ANDROID6})
    @Retention(RetentionPolicy.SOURCE)
    private @interface StatusBarType {
    }


    private static boolean isNotchOfficialSupport() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P;
    }

    /**
     * 设置状态栏颜色
     *
     * @param activity 需要设置的 activity
     * @param color    状态栏颜色值
     */
    public static void setStatusBarBackgroundColor(Activity activity, @ColorInt int color) {
        setStatusBarBackgroundColor(activity, color, DEFAULT_STATUS_BAR_ALPHA);
    }

    /**
     * 设置状态栏颜色
     *
     * @param activity       需要设置的activity
     * @param color          状态栏颜色值
     * @param statusBarAlpha 状态栏透明度
     */

    @SuppressLint("ObsoleteSdkInt")
    public static void setStatusBarBackgroundColor(Activity activity, @ColorInt int color, @IntRange(from = 0, to = 255) int statusBarAlpha) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            activity.getWindow().setStatusBarColor(calculateStatusColor(color, statusBarAlpha));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            View fakeStatusBarView = decorView.findViewById(FAKE_STATUS_BAR_VIEW_ID);
            if (fakeStatusBarView != null) {
                if (fakeStatusBarView.getVisibility() == View.GONE) {
                    fakeStatusBarView.setVisibility(View.VISIBLE);
                }
                fakeStatusBarView.setBackgroundColor(calculateStatusColor(color, statusBarAlpha));
            } else {
                decorView.addView(createStatusBarView(activity, color, statusBarAlpha));
            }
            setRootView(activity);
        }
    }


    /**
     * 设置根布局参数
     */
    private static void setRootView(Activity activity) {
        ViewGroup parent = activity.findViewById(android.R.id.content);
        for (int i = 0, count = parent.getChildCount(); i < count; i++) {
            View childView = parent.getChildAt(i);
            if (childView instanceof ViewGroup) {
                childView.setFitsSystemWindows(true);
                ((ViewGroup) childView).setClipToPadding(true);
            }
        }
    }

    /**
     * 生成一个和状态栏大小相同的半透明矩形条
     *
     * @param activity 需要设置的activity
     * @param color    状态栏颜色值
     * @param alpha    透明值
     * @return 状态栏矩形条
     */
    private static View createStatusBarView(Activity activity, @ColorInt int color, int alpha) {
        // 绘制一个和状态栏一样高的矩形
        View statusBarView = new View(activity);
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusbarHeight(activity));
        statusBarView.setLayoutParams(params);
        statusBarView.setBackgroundColor(calculateStatusColor(color, alpha));
        statusBarView.setId(FAKE_STATUS_BAR_VIEW_ID);
        return statusBarView;
    }


    /**
     * 计算状态栏颜色
     *
     * @param color color值
     * @param alpha alpha值
     * @return 最终的状态栏颜色
     */
    private static int calculateStatusColor(@ColorInt int color, int alpha) {
        if (alpha == 0) {
            return color;
        }
        float a = 1 - alpha / 255f;
        int red = color >> 16 & 0xff;
        int green = color >> 8 & 0xff;
        int blue = color & 0xff;
        red = (int) (red * a + 0.5);
        green = (int) (green * a + 0.5);
        blue = (int) (blue * a + 0.5);
        return 0xff << 24 | red << 16 | green << 8 | blue;
    }

    /**
     * 判断是否有导航栏
     *
     * @param activity 当前Activity
     * @return 是否有导航栏
     */
    public static boolean hasNavigationBar(Activity activity) {
        return getNavigationBarHeight(activity) > 0;
    }

    /**
     * 是否竖屏
     */
    private static boolean inPortrait(Context context) {
        Resources res = context.getResources();
        return (res.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
    }

    /**
     * 获取导航栏高度
     */
    private static int getNavigationBarHeight(Activity activity) {
        int result = 0;
        if (hasNavBar(activity)) {
            String key;
            if (inPortrait(activity)) {
                key = IMMERSION_NAVIGATION_BAR_HEIGHT;
            } else {
                key = IMMERSION_NAVIGATION_BAR_HEIGHT_LANDSCAPE;
            }
            return getInternalDimensionSize(activity, key);
        }
        return result;
    }

    /**
     * 获取导航栏宽度
     */
    private static int getNavigationBarWidth(Activity activity) {
        int result = 0;
        if (hasNavBar(activity)) {
            return getInternalDimensionSize(activity, IMMERSION_NAVIGATION_BAR_WIDTH);
        }
        return result;
    }

    @SuppressLint("ObsoleteSdkInt")
    private static boolean hasNavBar(Activity activity) {
        //判断小米手机是否开启了全面屏，开启了，直接返回false
        if (Settings.Global.getInt(activity.getContentResolver(), IMMERSION_MIUI_NAVIGATION_BAR_HIDE_SHOW, 0) != 0) {
            return false;
        }
        //判断华为手机是否隐藏了导航栏，隐藏了，直接返回false
        if (RomUtils.isHuawei()) {
            if (RomUtils.getEmuiVersion().startsWith("3") || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                if (Settings.System.getInt(activity.getContentResolver(), IMMERSION_EMUI_NAVIGATION_BAR_HIDE_SHOW, 0) != 0) {
                    return false;
                }
            } else {
                if (Settings.Global.getInt(activity.getContentResolver(), IMMERSION_EMUI_NAVIGATION_BAR_HIDE_SHOW, 0) != 0) {
                    return false;
                }
            }
        }
        //其他手机根据屏幕真实高度与显示高度是否相同来判断
        WindowManager windowManager = activity.getWindowManager();
        Display d = windowManager.getDefaultDisplay();
        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        d.getRealMetrics(realDisplayMetrics);
        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);
        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;
        return (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
    }

    private static int getInternalDimensionSize(Context context, String key) {
        int result = 0;
        try {
            int resourceId = Resources.getSystem().getIdentifier(key, "dimen", "android");
            if (resourceId > 0) {
                int sizeOne = context.getResources().getDimensionPixelSize(resourceId);
                int sizeTwo = Resources.getSystem().getDimensionPixelSize(resourceId);

                if (sizeTwo >= sizeOne) {
                    return sizeTwo;
                } else {
                    float densityOne = context.getResources().getDisplayMetrics().density;
                    float densityTwo = Resources.getSystem().getDisplayMetrics().density;
                    float f = sizeOne * densityTwo / densityOne;
                    return (int) ((f >= 0) ? (f + 0.5f) : (f - 0.5f));
                }
            }
        } catch (Throwable ignored) {
            return 0;
        }
        return result;
    }


    public static void setNavigationBackgroundColor(Activity activity, @ColorInt int color) {
        setNavigationBackgroundColor(activity, color, DEFAULT_STATUS_BAR_ALPHA);
    }

    /**
     * 设置一个可以自定义颜色的导航栏
     */
    public static void setNavigationBackgroundColor(Activity activity, @ColorInt int color, @IntRange(from = 0, to = 255) int alpha) {
        if (!hasNavBar(activity)) {
            return;
        }
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setNavigationBarColor(color);
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            ViewGroup mDecorView = (ViewGroup) window.getDecorView();
            View navigationBarView = mDecorView.findViewById(STATUSBARUTILS_NAVIGATION_BAR_VIEW);
            if (navigationBarView == null) {
                navigationBarView = new View(activity);
                navigationBarView.setId(STATUSBARUTILS_NAVIGATION_BAR_VIEW);
                mDecorView.addView(navigationBarView);
            }
            FrameLayout.LayoutParams params;
            if (getSmallestWidthDp(activity) >= 600 || inPortrait(activity)) {
                params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, getNavigationBarHeight(activity));
                params.gravity = Gravity.BOTTOM;
            } else {
                params = new FrameLayout.LayoutParams(getNavigationBarWidth(activity), FrameLayout.LayoutParams.MATCH_PARENT);
                params.gravity = Gravity.END;
            }
            navigationBarView.setLayoutParams(params);
            navigationBarView.setBackgroundColor(calculateStatusColor(color, alpha));
            navigationBarView.setVisibility(View.VISIBLE);
        }
    }

    private static float getSmallestWidthDp(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        float widthDp = metrics.widthPixels / metrics.density;
        float heightDp = metrics.heightPixels / metrics.density;
        return Math.min(widthDp, heightDp);
    }

    /**
     * @param activity      当前Acitivty
     * @param statusBar     是否显示statusbar
     * @param navigationBar 是否显示navigationBar
     */
    @SuppressWarnings("ConstantConditions")
    public static void showhideBar(Activity activity, boolean statusBar, boolean navigationBar) {
        ViewGroup mDecorView = (ViewGroup) activity.getWindow().getDecorView();
        int flag = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        if (!statusBar && !navigationBar) {//全部隐藏
            flag |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.INVISIBLE;
        } else if (statusBar && navigationBar) {//全部显示
            flag |= View.SYSTEM_UI_FLAG_VISIBLE;
        } else if (!navigationBar) {//如果有导航栏且隐藏导航栏
            flag |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        } else if (!statusBar) {//隐藏状态栏
            flag |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.INVISIBLE;
        }
        flag |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        mDecorView.setSystemUiVisibility(flag);
    }

    /**
     * 将导航栏图标设为暗色
     *
     * @param activity 当前Activity
     * @param dark     是否设置暗色
     */
    public static void setNavigationIconDark(Activity activity, boolean dark) {
        ViewGroup mDecorView = (ViewGroup) activity.getWindow().getDecorView();
        int flag = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        if (dark) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                flag |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            }
        }
        mDecorView.setSystemUiVisibility(flag);
    }


}
