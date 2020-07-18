package top.xuqingquan.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2018/07/04
 *     desc  : utils about rom
 * </pre>
 * 合并 QMUI 的 DeviceHelper
 */
@SuppressLint("PrivateApi")
public final class RomUtils {

    private static final String[] ROM_HUAWEI = {"huawei", "honor"};
    private static final String[] ROM_VIVO = {"vivo"};
    private static final String[] ROM_XIAOMI = {"xiaomi"};
    private static final String[] ROM_OPPO = {"oppo"};
    private static final String[] ROM_LEECO = {"leeco", "letv"};
    private static final String[] ROM_360 = {"360", "qiku"};
    private static final String[] ROM_ZTE = {"zte"};
    private static final String[] ROM_ONEPLUS = {"oneplus"};
    private static final String[] ROM_NUBIA = {"nubia"};
    private static final String[] ROM_COOLPAD = {"coolpad", "yulong"};
    private static final String[] ROM_LG = {"lg", "lge"};
    private static final String[] ROM_GOOGLE = {"google"};
    private static final String[] ROM_SAMSUNG = {"samsung"};
    private static final String[] ROM_MEIZU = {"meizu"};
    private static final String[] ROM_LENOVO = {"lenovo"};
    private static final String[] ROM_SMARTISAN = {"smartisan"};
    private static final String[] ROM_HTC = {"htc"};
    private static final String[] ROM_SONY = {"sony"};
    private static final String[] ROM_GIONEE = {"gionee", "amigo"};
    private static final String[] ROM_MOTOROLA = {"motorola"};
    private static final String[] ROM_ESSENTIAL = {"essential"};

    private static final String VERSION_PROPERTY_HUAWEI = "ro.build.version.emui";
    private static final String VERSION_PROPERTY_VIVO = "ro.vivo.os.build.display.id";
    private static final String VERSION_PROPERTY_XIAOMI = "ro.build.version.incremental";
    private static final String VERSION_PROPERTY_OPPO = "ro.build.version.opporom";
    private static final String VERSION_PROPERTY_LEECO = "ro.letv.release.version";
    private static final String VERSION_PROPERTY_360 = "ro.build.uiversion";
    private static final String VERSION_PROPERTY_ZTE = "ro.build.MiFavor_version";
    private static final String VERSION_PROPERTY_ONEPLUS = "ro.rom.version";
    private static final String VERSION_PROPERTY_NUBIA = "ro.build.rom.id";
    private final static String UNKNOWN = "unknown";

    private static RomInfo bean = null;

    private RomUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * Return whether the rom is made by huawei.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isHuawei() {
        return ROM_HUAWEI[0].equals(getRomInfo().name);
    }

    /**
     * Return whether the rom is made by vivo.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isVivo() {
        return ROM_VIVO[0].equals(getRomInfo().name);
    }

    /**
     * Return whether the rom is made by xiaomi.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isXiaomi() {
        return ROM_XIAOMI[0].equals(getRomInfo().name);
    }

    /**
     * Return whether the rom is made by oppo.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isOppo() {
        return ROM_OPPO[0].equals(getRomInfo().name);
    }

    /**
     * Return whether the rom is made by leeco.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isLeeco() {
        return ROM_LEECO[0].equals(getRomInfo().name);
    }

    /**
     * Return whether the rom is made by 360.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean is360() {
        return ROM_360[0].equals(getRomInfo().name);
    }

    /**
     * Return whether the rom is made by zte.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isZte() {
        return ROM_ZTE[0].equals(getRomInfo().name);
    }

    /**
     * Return whether the rom is made by oneplus.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isOneplus() {
        return ROM_ONEPLUS[0].equals(getRomInfo().name);
    }

    /**
     * Return whether the rom is made by nubia.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isNubia() {
        return ROM_NUBIA[0].equals(getRomInfo().name);
    }

    /**
     * Return whether the rom is made by coolpad.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isCoolpad() {
        return ROM_COOLPAD[0].equals(getRomInfo().name);
    }

    /**
     * Return whether the rom is made by lg.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isLg() {
        return ROM_LG[0].equals(getRomInfo().name);
    }

    /**
     * Return whether the rom is made by google.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isGoogle() {
        return ROM_GOOGLE[0].equals(getRomInfo().name);
    }

    /**
     * Return whether the rom is made by samsung.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isSamsung() {
        return ROM_SAMSUNG[0].equals(getRomInfo().name);
    }

    /**
     * Return whether the rom is made by meizu.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isMeizu() {
        return ROM_MEIZU[0].equals(getRomInfo().name);
    }

    /**
     * Return whether the rom is made by lenovo.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isLenovo() {
        return ROM_LENOVO[0].equals(getRomInfo().name);
    }

    /**
     * Return whether the rom is made by smartisan.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isSmartisan() {
        return ROM_SMARTISAN[0].equals(getRomInfo().name);
    }

    /**
     * Return whether the rom is made by htc.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isHtc() {
        return ROM_HTC[0].equals(getRomInfo().name);
    }

    /**
     * Return whether the rom is made by sony.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isSony() {
        return ROM_SONY[0].equals(getRomInfo().name);
    }

    /**
     * Return whether the rom is made by gionee.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isGionee() {
        return ROM_GIONEE[0].equals(getRomInfo().name);
    }

    /**
     * Return whether the rom is made by motorola.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isMotorola() {
        return ROM_MOTOROLA[0].equals(getRomInfo().name);
    }

    /**
     * Return whether the rom is made by motorola.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isEssential() {
        return ROM_ESSENTIAL[0].equals(getRomInfo().name);
    }

    /**
     * Return the rom's information.
     *
     * @return the rom's information
     */
    public static RomInfo getRomInfo() {
        if (bean != null) return bean;
        bean = new RomInfo();
        final String brand = getBrand();
        final String manufacturer = getManufacturer();
        if (isRightRom(brand, manufacturer, ROM_HUAWEI)) {
            bean.name = ROM_HUAWEI[0];
            String version = getRomVersion(VERSION_PROPERTY_HUAWEI);
            String[] temp = version.split("_");
            if (temp.length > 1) {
                bean.version = temp[1];
            } else {
                bean.version = version;
            }
            return bean;
        }
        if (isRightRom(brand, manufacturer, ROM_VIVO)) {
            bean.name = ROM_VIVO[0];
            bean.version = getRomVersion(VERSION_PROPERTY_VIVO);
            return bean;
        }
        if (isRightRom(brand, manufacturer, ROM_XIAOMI)) {
            bean.name = ROM_XIAOMI[0];
            bean.version = getRomVersion(VERSION_PROPERTY_XIAOMI);
            return bean;
        }
        if (isRightRom(brand, manufacturer, ROM_OPPO)) {
            bean.name = ROM_OPPO[0];
            bean.version = getRomVersion(VERSION_PROPERTY_OPPO);
            return bean;
        }
        if (isRightRom(brand, manufacturer, ROM_LEECO)) {
            bean.name = ROM_LEECO[0];
            bean.version = getRomVersion(VERSION_PROPERTY_LEECO);
            return bean;
        }

        if (isRightRom(brand, manufacturer, ROM_360)) {
            bean.name = ROM_360[0];
            bean.version = getRomVersion(VERSION_PROPERTY_360);
            return bean;
        }
        if (isRightRom(brand, manufacturer, ROM_ZTE)) {
            bean.name = ROM_ZTE[0];
            bean.version = getRomVersion(VERSION_PROPERTY_ZTE);
            return bean;
        }
        if (isRightRom(brand, manufacturer, ROM_ONEPLUS)) {
            bean.name = ROM_ONEPLUS[0];
            bean.version = getRomVersion(VERSION_PROPERTY_ONEPLUS);
            return bean;
        }
        if (isRightRom(brand, manufacturer, ROM_NUBIA)) {
            bean.name = ROM_NUBIA[0];
            bean.version = getRomVersion(VERSION_PROPERTY_NUBIA);
            return bean;
        }

        if (isRightRom(brand, manufacturer, ROM_COOLPAD)) {
            bean.name = ROM_COOLPAD[0];
        } else if (isRightRom(brand, manufacturer, ROM_LG)) {
            bean.name = ROM_LG[0];
        } else if (isRightRom(brand, manufacturer, ROM_GOOGLE)) {
            bean.name = ROM_GOOGLE[0];
        } else if (isRightRom(brand, manufacturer, ROM_SAMSUNG)) {
            bean.name = ROM_SAMSUNG[0];
        } else if (isRightRom(brand, manufacturer, ROM_MEIZU)) {
            bean.name = ROM_MEIZU[0];
        } else if (isRightRom(brand, manufacturer, ROM_LENOVO)) {
            bean.name = ROM_LENOVO[0];
        } else if (isRightRom(brand, manufacturer, ROM_SMARTISAN)) {
            bean.name = ROM_SMARTISAN[0];
        } else if (isRightRom(brand, manufacturer, ROM_HTC)) {
            bean.name = ROM_HTC[0];
        } else if (isRightRom(brand, manufacturer, ROM_SONY)) {
            bean.name = ROM_SONY[0];
        } else if (isRightRom(brand, manufacturer, ROM_GIONEE)) {
            bean.name = ROM_GIONEE[0];
        } else if (isRightRom(brand, manufacturer, ROM_MOTOROLA)) {
            bean.name = ROM_MOTOROLA[0];
        } else if (isRightRom(brand, manufacturer, ROM_ESSENTIAL)) {
            bean.name = ROM_ESSENTIAL[0];
        } else {
            bean.name = manufacturer;
        }
        bean.version = getRomVersion("");
        return bean;
    }

    private static boolean isRightRom(final String brand, final String manufacturer, final String... names) {
        for (String name : names) {
            if (brand.contains(name) || manufacturer.contains(name)) {
                return true;
            }
        }
        return false;
    }

    private static String getManufacturer() {
        try {
            String manufacturer = Build.MANUFACTURER;
            if (!TextUtils.isEmpty(manufacturer)) {
                return manufacturer.toLowerCase();
            }
        } catch (Throwable ignore) {/**/}
        return UNKNOWN;
    }

    private static String getBrand() {
        try {
            String brand = Build.BRAND;
            if (!TextUtils.isEmpty(brand)) {
                return brand.toLowerCase();
            }
        } catch (Throwable ignore) {/**/}
        return UNKNOWN;
    }

    private static String getRomVersion(final String propertyName) {
        String ret = "";
        if (!TextUtils.isEmpty(propertyName)) {
            ret = getSystemProperty(propertyName);
        }
        if (TextUtils.isEmpty(ret) || ret.equals(UNKNOWN)) {
            try {
                String display = Build.DISPLAY;
                if (!TextUtils.isEmpty(display)) {
                    ret = display.toLowerCase();
                }
            } catch (Throwable ignore) {/**/}
        }
        if (TextUtils.isEmpty(ret)) {
            return UNKNOWN;
        }
        return ret;
    }

    private static String getSystemProperty(final String name) {
        String prop = getSystemPropertyByShell(name);
        if (!TextUtils.isEmpty(prop)) return prop;
        prop = getSystemPropertyByStream(name);
        if (!TextUtils.isEmpty(prop)) return prop;
        if (Build.VERSION.SDK_INT < 28) {
            return getSystemPropertyByReflect(name);
        }
        return prop;
    }

    private static String getSystemPropertyByShell(final String propName) {
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            String ret = input.readLine();
            if (ret != null) {
                return ret;
            }
        } catch (IOException ignore) {
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException ignore) {/**/}
            }
        }
        return "";
    }

    private static String getSystemPropertyByStream(final String key) {
        try {
            Properties prop = new Properties();
            FileInputStream is = new FileInputStream(
                    new File(Environment.getRootDirectory(), "build.prop")
            );
            prop.load(is);
            return prop.getProperty(key, "");
        } catch (Exception ignore) {/**/}
        return "";
    }

    private static String getSystemPropertyByReflect(String key) {
        try {
            @SuppressLint("PrivateApi")
            Class<?> clz = Class.forName("android.os.SystemProperties");
            Method getMethod = clz.getMethod("get", String.class, String.class);
            return (String) getMethod.invoke(clz, key, "");
        } catch (Exception e) {/**/}
        return "";
    }

    public static String getVersion(String versionName) {
        if (versionName != null && !versionName.equals("")) {
            Pattern pattern = Pattern.compile("(\\d+\\.){1,3}\\d+");
            Matcher matcher = pattern.matcher(versionName);
            if (matcher.find()) {
                return matcher.group();
            }
        }
        return "";
    }

    /**
     * @return 获得版本号 x.x.x形式
     */
    public static String getVersion() {
        String versionName = getRomInfo().version;
        return getVersion(versionName);
    }

    public static class RomInfo {
        private String name;
        private String version;

        public String getName() {
            return name;
        }

        public String getVersion() {
            return version;
        }

        @NonNull
        @Override
        public String toString() {
            return "RomInfo{name=" + name +
                    ", version=" + version + "}";
        }
    }

    ///////////////////////////QMUI逻辑/////////////////////////////////////////////////

    private final static String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_FLYME_VERSION_NAME = "ro.build.display.id";
    private static final String KEY_EMUI_VERSION_NAME = "ro.build.version.emui";
    private final static String FLYME = "flyme";
    private final static String ZTEC2016 = "zte c2016";
    private final static String ZUKZ1 = "zuk z1";
    private final static String[] MEIZUBOARD = {"m9", "M9", "mx", "MX"};
    private static String sMiuiVersionName;
    private static String sFlymeVersionName;
    private static String sEmuiVersionName;
    private static boolean sIsTabletChecked = false;
    private static boolean sIsTabletValue = false;

    static {
        Properties properties = new Properties();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            // android 8.0，读取 /system/uild.prop 会报 permission denied
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(new File(Environment.getRootDirectory(), "build.prop"));
                properties.load(fileInputStream);
            } catch (Throwable e) {
                Timber.e(e, "read file error");
            } finally {
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        Class<?> clzSystemProperties;
        try {
            clzSystemProperties = Class.forName("android.os.SystemProperties");
            Method getMethod = clzSystemProperties.getDeclaredMethod("get", String.class);
            // miui
            sMiuiVersionName = getLowerCaseName(properties, getMethod, KEY_MIUI_VERSION_NAME);
            //flyme
            sFlymeVersionName = getLowerCaseName(properties, getMethod, KEY_FLYME_VERSION_NAME);
            //emui
            sEmuiVersionName = getLowerCaseName(properties, getMethod, KEY_EMUI_VERSION_NAME);
        } catch (Throwable e) {
            Timber.e(e, "read SystemProperties error");
        }
    }


    private static boolean _isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >=
                Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /**
     * 判断是否为平板设备
     */
    public static boolean isTablet(Context context) {
        if (sIsTabletChecked) {
            return sIsTabletValue;
        }
        sIsTabletValue = _isTablet(context);
        sIsTabletChecked = true;
        return sIsTabletValue;
    }

    /**
     * 判断是否是flyme系统
     */
    public static boolean isFlyme() {
        return !TextUtils.isEmpty(sFlymeVersionName) && sFlymeVersionName.contains(FLYME);
    }

    /**
     * 判断是否是MIUI系统
     */
    public static boolean isMIUI() {
        return !TextUtils.isEmpty(sMiuiVersionName);
    }

    public static boolean isMIUIV5() {
        return "v5".equals(sMiuiVersionName);
    }

    public static boolean isMIUIV6() {
        return "v6".equals(sMiuiVersionName);
    }

    public static boolean isMIUIV7() {
        return "v7".equals(sMiuiVersionName);
    }

    public static boolean isMIUIV8() {
        return "v8".equals(sMiuiVersionName);
    }

    public static boolean isMIUIV9() {
        return "v9".equals(sMiuiVersionName);
    }

    public static boolean isFlymeLowerThan(int majorVersion) {
        return isFlymeLowerThan(majorVersion, 0, 0);
    }

    public static boolean isFlymeLowerThan(int majorVersion, int minorVersion, int patchVersion) {
        boolean isLower = false;
        if (sFlymeVersionName != null && !sFlymeVersionName.equals("")) {
            try {
                String versionString = getVersion(sFlymeVersionName);
                if (versionString.length() > 0) {
                    String[] version = versionString.split("\\.");
                    if (version.length >= 1) {
                        if (Integer.parseInt(version[0]) < majorVersion) {
                            isLower = true;
                        }
                    }

                    if (version.length >= 2 && minorVersion > 0) {
                        if (Integer.parseInt(version[1]) < majorVersion) {
                            isLower = true;
                        }
                    }

                    if (version.length >= 3 && patchVersion > 0) {
                        if (Integer.parseInt(version[2]) < majorVersion) {
                            isLower = true;
                        }
                    }
                }
            } catch (Throwable ignore) {

            }
        }
        return isMeizu() && isLower;
    }

    public static boolean isMeizuPhone() {
        return isPhone(MEIZUBOARD) || isFlyme();
    }

    public static String getEmuiVersion() {
        return getVersion(sEmuiVersionName);
    }
    /**
     * 判断是否为 ZUK Z1 和 ZTK C2016。
     * 两台设备的系统虽然为 android 6.0，但不支持状态栏icon颜色改变，因此经常需要对它们进行额外判断。
     */
    public static boolean isZUKZ1() {
        final String board = Build.MODEL;
        return board != null && board.toLowerCase().contains(ZUKZ1);
    }

    public static boolean isZTKC2016() {
        final String board = Build.MODEL;
        return board != null && board.toLowerCase().contains(ZTEC2016);
    }

    private static boolean isPhone(String[] boards) {
        final String board = Build.BOARD;
        if (board == null) {
            return false;
        }
        for (String board1 : boards) {
            if (board.equals(board1)) {
                return true;
            }
        }
        return false;
    }


    @Nullable
    private static String getLowerCaseName(Properties p, Method get, String key) {
        String name = p.getProperty(key);
        if (name == null) {
            try {
                name = (String) get.invoke(null, key);
            } catch (Throwable ignored) {
            }
        }
        if (name != null) name = name.toLowerCase();
        return name;
    }
}