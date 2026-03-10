package com.devicespooflab.hooks.hooks;

import android.content.Context;
import com.devicespooflab.hooks.utils.ConfigManager;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class WebViewHooks {

    private static final String TAG = "DeviceSpoofLab-WebView";

    public static void hook(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            hookWebSettings(lpparam);
            hookWebViewConstructor(lpparam);
        } catch (Exception e) {
            XposedBridge.log(TAG + ": " + e.getMessage());
        }
    }

    private static void hookWebSettings(XC_LoadPackage.LoadPackageParam lpparam) {
        Class<?> webViewClass = XposedHelpers.findClassIfExists("android.webkit.WebView", lpparam.classLoader);
        if (webViewClass == null) return;

        try {
            XposedHelpers.findAndHookMethod(webViewClass, "getSettings", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Object settings = param.getResult();
                    if (settings != null) {
                        String spoofedUA = ConfigManager.getWebViewUserAgent();
                        if (spoofedUA != null) {
                            try {
                                XposedHelpers.callMethod(settings, "setUserAgentString", spoofedUA);
                            } catch (Exception e) {}
                        }
                    }
                }
            });
        } catch (Exception e) {
            XposedBridge.log(TAG + ": " + e.getMessage());
        }
    }

    private static void hookWebViewConstructor(XC_LoadPackage.LoadPackageParam lpparam) {
        Class<?> webViewClass = XposedHelpers.findClassIfExists("android.webkit.WebView", lpparam.classLoader);
        if (webViewClass == null) return;

        XC_MethodHook constructorHook = new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                try {
                    Object webView = param.thisObject;
                    Object settings = XposedHelpers.callMethod(webView, "getSettings");
                    String spoofedUA = ConfigManager.getWebViewUserAgent();
                    if (spoofedUA != null) {
                        XposedHelpers.callMethod(settings, "setUserAgentString", spoofedUA);
                    }
                } catch (Exception e) {}
            }
        };

        try {
            XposedHelpers.findAndHookConstructor(webViewClass, Context.class, constructorHook);
        } catch (Exception e) {}

        try {
            XposedHelpers.findAndHookConstructor(webViewClass, Context.class, android.util.AttributeSet.class, constructorHook);
        } catch (Exception e) {}

        try {
            XposedHelpers.findAndHookConstructor(webViewClass, Context.class, android.util.AttributeSet.class, int.class, constructorHook);
        } catch (Exception e) {}
    }
}
