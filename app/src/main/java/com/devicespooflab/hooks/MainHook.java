package com.devicespooflab.hooks;

import android.os.Build;
import com.devicespooflab.hooks.hooks.*;
import com.devicespooflab.hooks.utils.ConfigManager;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainHook implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            ConfigManager.init();
        } catch (Exception e) {
            return;
        }

        try { SystemPropertiesHooks.hook(lpparam); } catch (Exception e) {}
        try { BuildHooks.hook(lpparam); } catch (Exception e) {}
        try { HardwareHooks.hook(lpparam); } catch (Exception e) {}
        try { EmulatorDetectionHooks.hook(lpparam); } catch (Exception e) {}
        try { TelephonyHooks.hook(lpparam); } catch (Exception e) {}
        try { SettingsHooks.hook(lpparam); } catch (Exception e) {}
        try { AdvertisingIdHooks.hook(lpparam); } catch (Exception e) {}
        
        if (Build.VERSION.SDK_INT >= 30) {
            try { AppSetIdHooks.hook(lpparam); } catch (Exception e) {}
        }

        try { MediaDrmHooks.hook(lpparam); } catch (Exception e) {}
        try { WebViewHooks.hook(lpparam); } catch (Exception e) {}
        try { PackageManagerHooks.hook(lpparam); } catch (Exception e) {}
    }
}
