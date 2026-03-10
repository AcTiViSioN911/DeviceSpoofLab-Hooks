package com.devicespooflab.hooks.hooks;

import com.devicespooflab.hooks.utils.ConfigManager;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class AdvertisingIdHooks {

    public static void hook(XC_LoadPackage.LoadPackageParam lpparam) {
        XC_MethodHook idHook = new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                param.setResult(ConfigManager.getGAID());
            }
        };

        try {
            Class<?> infoClass = XposedHelpers.findClassIfExists("com.google.android.gms.ads.identifier.AdvertisingIdClient$Info", lpparam.classLoader);
            if (infoClass != null) {
                XposedHelpers.findAndHookMethod(infoClass, "getId", idHook);
            }
        } catch (Throwable ignored) {}

        try {
            Class<?> zzxClass = XposedHelpers.findClassIfExists("com.google.android.gms.common.api.internal.zzx", lpparam.classLoader);
            if (zzxClass != null) {
                XposedHelpers.findAndHookMethod(zzxClass, "getId", idHook);
            }
        } catch (Throwable ignored) {}

        try {
            Class<?> clientClass = XposedHelpers.findClassIfExists("com.google.android.gms.ads.identifier.AdvertisingIdClient", lpparam.classLoader);
            if (clientClass != null) {
                XposedHelpers.findAndHookMethod(clientClass, "getAdvertisingIdInfo", android.content.Context.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {}
                });
            }
        } catch (Throwable ignored) {}
    }
}
