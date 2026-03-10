package com.devicespooflab.hooks.hooks;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PackageManagerHooks {

    private static final String TAG = "DeviceSpoofLab-PM";
    private static final Set<String> DENIED_FEATURES = new HashSet<>(Arrays.asList(
        "android.hardware.sensor.emulator", "goldfish", "ranchu"
    ));

    public static void hook(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            Class<?> pmClass = XposedHelpers.findClassIfExists("android.app.ApplicationPackageManager", lpparam.classLoader);
            if (pmClass != null) {
                hookHasSystemFeature(pmClass);
                hookGetSystemAvailableFeatures(pmClass);
            }
        } catch (Exception e) {
            XposedBridge.log(TAG + ": " + e.getMessage());
        }
    }

    private static void hookHasSystemFeature(Class<?> pmClass) {
        XC_MethodHook hook = new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                String feature = (String) param.args[0];
                if (feature == null) return;
                for (String denied : DENIED_FEATURES) {
                    if (feature.toLowerCase().contains(denied)) {
                        param.setResult(false);
                        return;
                    }
                }
            }
        };
        try {
            XposedHelpers.findAndHookMethod(pmClass, "hasSystemFeature", String.class, hook);
            XposedHelpers.findAndHookMethod(pmClass, "hasSystemFeature", String.class, int.class, hook);
        } catch (Exception ignored) {}
    }

    private static void hookGetSystemAvailableFeatures(Class<?> pmClass) {
        try {
            XposedHelpers.findAndHookMethod(pmClass, "getSystemAvailableFeatures", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Object[] features = (Object[]) param.getResult();
                    if (features == null) return;

                    Class<?> featureInfoClass = features.getClass().getComponentType();
                    List<Object> filtered = new ArrayList<>();
                    for (Object f : features) {
                        try {
                            String name = (String) XposedHelpers.getObjectField(f, "name");
                            if (name != null) {
                                boolean isDenied = false;
                                for (String denied : DENIED_FEATURES) {
                                    if (name.toLowerCase().contains(denied)) {
                                        isDenied = true;
                                        break;
                                    }
                                }
                                if (!isDenied) filtered.add(f);
                            } else {
                                filtered.add(f);
                            }
                        } catch (Exception e) {
                            filtered.add(f);
                        }
                    }
                    Object typedArray = java.lang.reflect.Array.newInstance(featureInfoClass, filtered.size());
                    for (int i = 0; i < filtered.size(); i++) {
                        java.lang.reflect.Array.set(typedArray, i, filtered.get(i));
                    }
                    param.setResult(typedArray);
                }
            });
        } catch (Exception ignored) {}
    }
}
