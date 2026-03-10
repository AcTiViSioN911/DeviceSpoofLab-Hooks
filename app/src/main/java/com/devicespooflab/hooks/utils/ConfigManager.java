package com.devicespooflab.hooks.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import android.util.Log;

/**
 * Manages configuration for spoofed values.
 * Cleaned from hardcoded Pixel 7 Pro defaults.
 */
public class ConfigManager {

    private static final String TAG = "DeviceSpoofLab-Hooks";

    private static final String[] CONFIG_PATHS = {
        "/data/data/com.devicespooflab.hooks/files/device_profile.conf",
        "/sdcard/DeviceSpoofLab-Hooks/device_profile.conf",
        "/data/local/tmp/DeviceSpoofLab-Hooks/device_profile.conf"
    };

    private static Map<String, String> allProperties = null;

    // Cache for identifiers
    private static String cachedIMEI = null;
    private static String cachedSerial = null;
    private static String cachedAndroidId = null;
    private static String cachedGSFId = null;
    private static String cachedGAID = null;
    private static String cachedAppSetId = null;
    private static byte[] cachedMediaDrmId = null;

    public static void init() {
        allProperties = readConfigFile();
    }

    private static Map<String, String> readConfigFile() {
        Map<String, String> config = new HashMap<>();
        for (String configPath : CONFIG_PATHS) {
            File configFile = new File(configPath);
            if (configFile.exists() && configFile.canRead()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (line.isEmpty() || line.startsWith("#")) continue;

                        int equalIndex = line.indexOf('=');
                        if (equalIndex > 0) {
                            String key = line.substring(0, equalIndex).trim();
                            String value = line.substring(equalIndex + 1).trim();
                            if (value.startsWith("\"") && value.endsWith("\"")) {
                                value = value.substring(1, value.length() - 1);
                            }
                            config.put(key, value);
                        }
                    }
                    return config;
                } catch (Exception e) {
                    Log.e(TAG, "Error reading config: " + e.getMessage());
                }
            }
        }
        return getEmbeddedDefaults();
    }

    /**
     * CLEANED DEFAULTS: Removed all Cheetah/Pixel 7 Pro hardcoding.
     * Only essential security and persona flags remain.
     */
    private static Map<String, String> getEmbeddedDefaults() {
        Map<String, String> defaults = new HashMap<>();

        // Security Masking (Necessary for root/unlock)
        defaults.put("ro.boot.verifiedbootstate", "green");
        defaults.put("ro.boot.flash.locked", "1");
        defaults.put("ro.boot.vbmeta.device_state", "locked");
        defaults.put("ro.build.selinux", "0");
        defaults.put("ro.debuggable", "0");
        defaults.put("ro.secure", "1");

        // Essential Persona (Kazakhstan Kcell)
        defaults.put("gsm.operator.alpha", "Kcell");
        defaults.put("gsm.operator.numeric", "40102");
        defaults.put("gsm.sim.operator.iso-country", "kz");
        defaults.put("persist.sys.timezone", "Asia/Almaty");

        return defaults;
    }

    private static String getConfigValue(String key) {
        if (allProperties == null) init();
        return allProperties.get(key);
    }

    public static String getSystemProperty(String key, String defaultValue) {
        String value = getConfigValue(key);
        return (value != null) ? value : defaultValue;
    }

    public static boolean hasConfigValue(String key) {
        String value = getConfigValue(key);
        return value != null && !value.isEmpty();
    }

    // ==================== Identifiers ====================

    public static String getSerial() {
        if (cachedSerial == null) {
            String val = getConfigValue("ro.serialno");
            cachedSerial = (val != null && !val.isEmpty()) ? val : RandomGenerator.generateSerial();
        }
        return cachedSerial;
    }

    public static String getAndroidId() {
        if (cachedAndroidId == null) {
            String val = getConfigValue("ANDROID_ID");
            cachedAndroidId = (val != null && !val.isEmpty()) ? val : RandomGenerator.generateAndroidId();
        }
        return cachedAndroidId;
    }

    public static String getIMEI() {
        if (cachedIMEI == null) {
            String tac = getConfigValue("imei.tac");
            cachedIMEI = (tac != null && !tac.isEmpty()) ? 
                         RandomGenerator.generateIMEIWithTAC(tac) : RandomGenerator.generateIMEI();
        }
        return cachedIMEI;
    }

    public static byte[] getMediaDrmId() {
        if (cachedMediaDrmId == null) cachedMediaDrmId = RandomGenerator.generateMediaDrmId();
        return cachedMediaDrmId;
    }

    // ==================== Build Accessors (Cleaned) ====================

    public static String getBuildModel() { return getConfigValue("ro.product.model"); }
    public static String getBuildBrand() { return getConfigValue("ro.product.brand"); }
    public static String getBuildDevice() { return getConfigValue("ro.product.device"); }
    public static String getBuildProduct() { return getConfigValue("ro.product.name"); }
    public static String getBuildDescription() { return getConfigValue("ro.build.description"); }
    public static String getBuildFingerprint() { return getConfigValue("ro.build.fingerprint"); }
    public static String getBuildManufacturer() { return getConfigValue("ro.product.manufacturer"); }
    public static String getBuildBoard() { return getConfigValue("ro.product.board"); }
    public static String getBuildHardware() { return getConfigValue("ro.hardware"); }
    public static String getBuildId() { return getConfigValue("ro.build.id"); }
    public static String getBuildDisplay() { return getConfigValue("ro.build.display.id"); }
    public static String getBuildTags() { return getConfigValue("ro.build.tags"); }
    public static String getBuildType() { return getConfigValue("ro.build.type"); }
    public static String getBuildVersionRelease() { return getConfigValue("ro.build.version.release"); }
    public static String getBuildVersionIncremental() { return getConfigValue("ro.build.version.incremental"); }
    public static String getBuildVersionCodename() { return getConfigValue("ro.build.version.codename"); }
    public static String getBuildCharacteristics() { return getConfigValue("ro.build.characteristics"); }
    public static String getBuildFlavor() { return getConfigValue("ro.build.flavor"); }
    public static String getBuildVersionSecurityPatch() { return getConfigValue("ro.build.version.security_patch"); }
    public static String getWebViewUserAgent() { return getConfigValue("webview.user_agent"); }
    public static String getBuildBootloader() { return getConfigValue("ro.bootloader"); }
    
    public static String getGSFId() { if (cachedGSFId == null) cachedGSFId = RandomGenerator.generateGSFId(); return cachedGSFId; }
    public static String getGAID() { if (cachedGAID == null) cachedGAID = RandomGenerator.generateGAID(); return cachedGAID; }
    public static String getAppSetId() { if (cachedAppSetId == null) cachedAppSetId = RandomGenerator.generateGAID(); return cachedAppSetId; }
    
    // Fallbacks for compatibility
    public static String getMEID() { return RandomGenerator.generateMEID(); }
    public static String getIMSI() { return RandomGenerator.generateIMSI(); }
    public static String getICCID() { return RandomGenerator.generateICCID(); }
    public static String getPhoneNumber() { return RandomGenerator.generatePhoneNumber(); }

    public static int getBuildVersionSdk() {
        String sdk = getConfigValue("ro.build.version.sdk");
        try { return Integer.parseInt(sdk); } catch (Exception e) { return 36; } // Default Android 16
    }

    public static String getCpuAbi() { return getConfigValue("ro.product.cpu.abi"); }
    public static String getCpuAbiList() { return getConfigValue("ro.product.cpu.abilist"); }
    public static String getCpuAbiList64() { return getConfigValue("ro.product.cpu.abilist64"); }
    public static String getCpuAbiList32() { return getConfigValue("ro.product.cpu.abilist32"); }
    
    public static boolean isConfigAvailable() {
        if (allProperties == null) init();
        return !allProperties.isEmpty();
    }
}
