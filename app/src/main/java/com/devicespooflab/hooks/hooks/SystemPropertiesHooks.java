package com.devicespooflab.hooks.hooks;

import com.devicespooflab.hooks.utils.ConfigManager;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class SystemPropertiesHooks {

    private static final String TAG = "DeviceSpoofLab-SystemProps";
    private static final String SYSTEM_PROPERTIES_CLASS = "android.os.SystemProperties";

    public static void hook(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            hookSystemProperties(lpparam.classLoader);
            try {
                ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
                if (systemClassLoader != null && systemClassLoader != lpparam.classLoader) {
                    hookSystemProperties(systemClassLoader);
                }
            } catch (Exception ignored) {}
        } catch (Exception e) {
            XposedBridge.log(TAG + ": Error: " + e.getMessage());
        }
    }

    private static void hookSystemProperties(ClassLoader classLoader) {
        Class<?> sysPropClass = XposedHelpers.findClassIfExists(SYSTEM_PROPERTIES_CLASS, classLoader);
        if (sysPropClass == null) return;

        XC_MethodHook stringHook = new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                String key = (String) param.args[0];
                String spoofedValue = ConfigManager.getSystemProperty(key, null);
                if (spoofedValue != null) param.setResult(spoofedValue);
            }
        };

        try {
            XposedHelpers.findAndHookMethod(sysPropClass, "get", String.class, stringHook);
            XposedHelpers.findAndHookMethod(sysPropClass, "get", String.class, String.class, stringHook);
        } catch (Exception ignored) {}

        try {
            XposedHelpers.findAndHookMethod(sysPropClass, "getInt", String.class, int.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    String spoofedValue = ConfigManager.getSystemProperty((String) param.args[0], null);
                    if (spoofedValue != null) {
                        try { param.setResult(Integer.parseInt(spoofedValue)); } catch (Exception ignored) {}
                    }
                }
            });
        } catch (Exception ignored) {}

        try {
            XposedHelpers.findAndHookMethod(sysPropClass, "getBoolean", String.class, boolean.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    String spoofedValue = ConfigManager.getSystemProperty((String) param.args[0], null);
                    if (spoofedValue != null) {
                        param.setResult(spoofedValue.equals("1") || spoofedValue.equalsIgnoreCase("true"));
                    }
                }
            });
        } catch (Exception ignored) {}

        try {
            XposedHelpers.findAndHookMethod(sysPropClass, "getLong", String.class, long.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    String spoofedValue = ConfigManager.getSystemProperty((String) param.args[0], null);
                    if (spoofedValue != null) {
                        try { param.setResult(Long.parseLong(spoofedValue)); } catch (Exception ignored) {}
                    }
                }
            });
        } catch (Exception ignored) {}
    }
}
