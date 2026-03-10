package com.devicespooflab.hooks.hooks;

import android.content.ContentResolver;
import com.devicespooflab.hooks.utils.ConfigManager;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class SettingsHooks {

    private static final String ANDROID_ID = "android_id";

    public static void hook(XC_LoadPackage.LoadPackageParam lpparam) {
        Class<?> secure = XposedHelpers.findClassIfExists("android.provider.Settings$Secure", lpparam.classLoader);
        if (secure == null) return;

        XC_MethodHook hook = new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                String name = (String) param.args[1];
                if (name == null) return;

                if (ANDROID_ID.equals(name)) {
                    param.setResult(ConfigManager.getAndroidId());
                } else if (name.contains("gsf")) {
                    param.setResult(ConfigManager.getGSFId());
                }
            }
        };

        try {
            XposedHelpers.findAndHookMethod(secure, "getString", ContentResolver.class, String.class, hook);
        } catch (Throwable t) {}

        try {
            XposedHelpers.findAndHookMethod(secure, "getString", ContentResolver.class, String.class, String.class, hook);
        } catch (Throwable t) {}
    }
}
