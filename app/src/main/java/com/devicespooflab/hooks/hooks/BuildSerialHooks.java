package com.devicespooflab.hooks.hooks;

import com.devicespooflab.hooks.utils.ConfigManager;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class BuildSerialHooks {

    public static void hook(XC_LoadPackage.LoadPackageParam lpparam) {
        Class<?> build = XposedHelpers.findClassIfExists("android.os.Build", lpparam.classLoader);
        if (build == null) return;

        try {
            XposedHelpers.findAndHookMethod(build, "getSerial", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) {
                    param.setResult(ConfigManager.getSerial());
                }
            });
        } catch (Throwable ignored) {}

        try {
            XposedHelpers.setStaticObjectField(build, "SERIAL", ConfigManager.getSerial());
        } catch (Throwable ignored) {}
    }
}
