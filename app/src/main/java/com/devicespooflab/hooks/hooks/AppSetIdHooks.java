package com.devicespooflab.hooks.hooks;

import android.os.Build;
import com.devicespooflab.hooks.utils.ConfigManager;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class AppSetIdHooks {

    public static void hook(XC_LoadPackage.LoadPackageParam lpparam) {
        if (Build.VERSION.SDK_INT < 30) return;

        Class<?> infoClass = XposedHelpers.findClassIfExists("com.google.android.gms.appset.AppSetIdInfo", lpparam.classLoader);
        if (infoClass == null) return;

        try {
            XposedHelpers.findAndHookMethod(infoClass, "getId", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) {
                    param.setResult(ConfigManager.getAppSetId());
                }
            });

            XposedHelpers.findAndHookMethod(infoClass, "getScope", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) {
                    param.setResult(1);
                }
            });
        } catch (Throwable ignored) {}
    }
}
