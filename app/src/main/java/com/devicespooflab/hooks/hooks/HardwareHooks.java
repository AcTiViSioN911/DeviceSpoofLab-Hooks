package com.devicespooflab.hooks.hooks;

import android.app.ActivityManager;
import android.os.Debug;
import com.devicespooflab.hooks.utils.ConfigManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.RandomAccessFile;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HardwareHooks {

    public static void hook(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            hookRuntimeCores();
            hookActivityManagerMemory(lpparam);
            hookDebugMemory();
            hookFileReads();
        } catch (Exception ignored) {}
    }

    private static void hookRuntimeCores() {
        try {
            XposedHelpers.findAndHookMethod(Runtime.class, "availableProcessors", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    param.setResult(8);
                }
            });
        } catch (Exception ignored) {}
    }

    private static void hookActivityManagerMemory(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            Class<?> amClass = XposedHelpers.findClassIfExists("android.app.ActivityManager", lpparam.classLoader);
            if (amClass == null) return;

            XposedHelpers.findAndHookMethod(amClass, "getMemoryInfo", ActivityManager.MemoryInfo.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    ActivityManager.MemoryInfo memInfo = (ActivityManager.MemoryInfo) param.args[0];
                    if (memInfo != null && memInfo.totalMem > 0) {
                        // Native Pixel 7a RAM is 8GB (approx 7.5GB usable)
                    }
                }
            });

            XposedHelpers.findAndHookMethod(amClass, "getMemoryClass", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    param.setResult(256);
                }
            });

            XposedHelpers.findAndHookMethod(amClass, "getLargeMemoryClass", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    param.setResult(512);
                }
            });
        } catch (Exception ignored) {}
    }

    private static void hookDebugMemory() {
        try {
            XposedHelpers.findAndHookMethod(Debug.class, "getNativeHeapSize", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    // Pass-through native heap size
                }
            });
        } catch (Exception ignored) {}
    }

    private static void hookFileReads() {
        try {
            XposedHelpers.findAndHookMethod(RandomAccessFile.class, "readLine", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    String line = (String) param.getResult();
                    if (line != null && line.startsWith("MemTotal:")) {
                        // System native MemTotal remains
                    }
                }
            });
        } catch (Exception ignored) {}

        try {
            XposedHelpers.findAndHookMethod(File.class, "exists", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    String path = ((File) param.thisObject).getAbsolutePath();
                    if (path.contains("goldfish") || path.contains("ranchu")) {
                        param.setResult(false);
                    }
                }
            });
        } catch (Exception ignored) {}
    }
}
