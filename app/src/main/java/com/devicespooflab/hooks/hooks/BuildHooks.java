package com.devicespooflab.hooks.hooks;

import com.devicespooflab.hooks.utils.ConfigManager;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class BuildHooks {

    public static void hook(XC_LoadPackage.LoadPackageParam lpparam) {
        Class<?> buildClass = XposedHelpers.findClassIfExists("android.os.Build", lpparam.classLoader);
        if (buildClass == null) return;

        try {
            XposedHelpers.findAndHookMethod(buildClass, "getSerial", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    param.setResult(ConfigManager.getSerial());
                }
            });
        } catch (Throwable ignored) {}
    }
}
