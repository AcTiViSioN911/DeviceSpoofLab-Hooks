package com.devicespooflab.hooks.hooks;

import com.devicespooflab.hooks.utils.ConfigManager;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MediaDrmHooks {

    public static void hook(XC_LoadPackage.LoadPackageParam lpparam) {
        Class<?> mediaDrmClass = XposedHelpers.findClassIfExists("android.media.MediaDrm", lpparam.classLoader);
        if (mediaDrmClass == null) return;

        try {
            XposedHelpers.findAndHookMethod(mediaDrmClass, "getPropertyByteArray", String.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) {
                    if ("deviceUniqueId".equals(param.args[0])) {
                        param.setResult(ConfigManager.getMediaDrmId());
                    }
                }
            });
        } catch (Throwable ignored) {}
    }
}
