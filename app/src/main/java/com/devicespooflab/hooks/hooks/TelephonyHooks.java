package com.devicespooflab.hooks.hooks;

import com.devicespooflab.hooks.utils.ConfigManager;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class TelephonyHooks {

    public static void hook(XC_LoadPackage.LoadPackageParam lpparam) {
        Class<?> tm = XposedHelpers.findClassIfExists("android.telephony.TelephonyManager", lpparam.classLoader);
        if (tm == null) return;

        hookMethod(tm, "getDeviceId", ConfigManager.getIMEI());
        hookMethod(tm, "getImei", ConfigManager.getIMEI());
        hookMethod(tm, "getMeid", ConfigManager.getMEID());
        hookMethod(tm, "getSubscriberId", ConfigManager.getIMSI());
        hookMethod(tm, "getSimSerialNumber", ConfigManager.getICCID());
        hookMethod(tm, "getLine1Number", ConfigManager.getPhoneNumber());

        hookProp(tm, "getNetworkOperator", "gsm.operator.numeric");
        hookProp(tm, "getNetworkOperatorName", "gsm.operator.alpha");
        hookProp(tm, "getSimOperator", "gsm.sim.operator.numeric");
        hookProp(tm, "getSimOperatorName", "gsm.sim.operator.alpha");
        hookProp(tm, "getSimCountryIso", "gsm.sim.operator.iso-country");
        hookProp(tm, "getNetworkCountryIso", "gsm.operator.iso-country");
    }

    private static void hookMethod(Class<?> clazz, String methodName, final String result) {
        XC_MethodHook hook = new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                param.setResult(result);
            }
        };
        try { XposedHelpers.findAndHookMethod(clazz, methodName, hook); } catch (Throwable t) {}
        try { XposedHelpers.findAndHookMethod(clazz, methodName, int.class, hook); } catch (Throwable t) {}
    }

    private static void hookProp(Class<?> clazz, String methodName, final String propKey) {
        XposedHelpers.findAndHookMethod(clazz, methodName, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                String val = ConfigManager.getSystemProperty(propKey, null);
                if (val != null) param.setResult(val);
            }
        });
    }
}
