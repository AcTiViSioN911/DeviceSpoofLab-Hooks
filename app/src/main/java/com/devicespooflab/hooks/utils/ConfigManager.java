package com.devicespooflab.hooks.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    private static final String[] CONFIG_PATHS = {
        "/data/data/com.devicespooflab.hooks/files/device_profile.conf",
        "/sdcard/DeviceSpoofLab-Hooks/device_profile.conf"
    };

    private static Map<String, String> allProperties = null;
    private static Map<Integer, String> cachedImeis = new HashMap<>();

    private static String cachedMEID = null;
    private static String cachedIMSI = null;
    private static String cachedICCID = null;
    private static String cachedPhoneNumber = null;
    private static String cachedSerial = null;
    private static String cachedGAID = null;
    private static String cachedGSFId = null;
    private static String cachedAndroidId = null;
    private static byte[] cachedMediaDrmId = null;
    private static String cachedAppSetId = null;

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
                } catch (Exception e) {}
            }
        }
        return getEmbeddedDefaults();
    }

    private static Map<String, String> getEmbeddedDefaults() {
        Map<String, String> defaults = new HashMap<>();
        defaults.put("ro.boot.verifiedbootstate", "green");
        defaults.put("ro.boot.flash.locked", "1");
        defaults.put("ro.boot.vbmeta.device_state", "locked");
        defaults.put("ro.build.selinux", "0");
        defaults.put("ro.debuggable", "0");
        defaults.put("ro.secure", "1");
        defaults.put("ro.boot.veritymode", "enforcing");
        defaults.put("ro.crypto.state", "encrypted");
        defaults.put("ro.kernel.qemu", "0");
        defaults.put("ro.boot.qemu", "0");
        defaults.put("gsm.operator.alpha", "Kcell");
        defaults.put("gsm.operator.numeric", "40102");
        defaults.put("gsm.sim.operator.alpha", "Kcell");
        defaults.put("gsm.sim.operator.numeric", "40102");
        defaults.put("gsm.sim.operator.iso-country", "kz");
        defaults.put("persist.sys.timezone", "Asia/Almaty");
        defaults.put("persist.sys.usb.config", "none");
        return defaults;
    }

    private static String getConfigValue(String key) {
        if (allProperties == null) init();
        return allProperties.get(key);
    }

    private static boolean hasConfigValue(String key) {
        String value = getConfigValue(key);
        return value != null && !value.isEmpty();
    }

    public static String getSystemProperty(String key, String defaultValue) {
        String value = getConfigValue(key);
        return (value != null) ? value : defaultValue;
    }

    public static String getIMEI() {
        return getIMEI(0);
    }

    public static String getIMEI(int slot) {
        if (cachedImeis.get(slot) == null) {
            if (slot == 0) {
                String val = getConfigValue("IMEI");
                cachedImeis.put(0, (val != null && !val.isEmpty()) ? val : RandomGenerator.generateIMEI());
            } else {
                String val2 = getConfigValue("IMEI2");
                if (val2 != null && !val2.isEmpty()) {
                    cachedImeis.put(1, val2);
                } else {
                    cachedImeis.put(1, deriveImei2(getIMEI(0)));
                }
            }
        }
        return cachedImeis.get(slot);
    }

    private static String deriveImei2(String imei1) {
        try {
            String base = imei1.substring(0, 14);
            long num = Long.parseLong(base) + 8;
            String newBase = String.format("%014d", num);
            return newBase + calculateLuhn(newBase);
        } catch (Exception e) {
            return RandomGenerator.generateIMEI();
        }
    }

    private static int calculateLuhn(String n) {
        int s = 0;
        boolean a = true;
        for (int i = n.length() - 1; i >= 0; i--) {
            int d = Character.getNumericValue(n.charAt(i));
            if (a) {
                d *= 2;
                if (d > 9) d -= 9;
            }
            s += d;
            a = !a;
        }
        return (10 - (s % 10)) % 10;
    }

    public static String getMEID() {
        if (cachedMEID == null) cachedMEID = RandomGenerator.generateMEID();
        return cachedMEID;
    }

    public static String getIMSI() {
        if (cachedIMSI == null) cachedIMSI = RandomGenerator.generateIMSI();
        return cachedIMSI;
    }

    public static String getICCID() {
        if (cachedICCID == null) cachedICCID = RandomGenerator.generateICCID();
        return cachedICCID;
    }

    public static String getPhoneNumber() {
        if (cachedPhoneNumber == null) cachedPhoneNumber = RandomGenerator.generatePhoneNumber();
        return cachedPhoneNumber;
    }

    public static String getSerial() {
        if (cachedSerial == null) {
            if (hasConfigValue("ro.serialno")) {
                cachedSerial = getConfigValue("ro.serialno");
            } else if (hasConfigValue("SERIAL_NUMBER")) {
                cachedSerial = getConfigValue("SERIAL_NUMBER");
            } else {
                cachedSerial = RandomGenerator.generateSerial();
            }
        }
        return cachedSerial;
    }

    public static String getGAID() {
        if (cachedGAID == null) cachedGAID = RandomGenerator.generateGAID();
        return cachedGAID;
    }

    public static String getGSFId() {
        if (cachedGSFId == null) cachedGSFId = RandomGenerator.generateGSFId();
        return cachedGSFId;
    }

    public static String getAndroidId() {
        if (cachedAndroidId == null) {
            if (hasConfigValue("ANDROID_ID")) {
                cachedAndroidId = getConfigValue("ANDROID_ID");
            } else {
                cachedAndroidId = RandomGenerator.generateAndroidId();
            }
        }
        return cachedAndroidId;
    }

    public static byte[] getMediaDrmId() {
        if (cachedMediaDrmId == null) cachedMediaDrmId = RandomGenerator.generateMediaDrmId();
        return cachedMediaDrmId;
    }

    public static String getAppSetId() {
        if (cachedAppSetId == null) cachedAppSetId = RandomGenerator.generateGAID();
        return cachedAppSetId;
    }

    public static boolean isConfigAvailable() {
        if (allProperties == null) init();
        return !allProperties.isEmpty();
    }

    public static String getBuildFingerprint() { return getConfigValue("ro.build.fingerprint"); }
    public static String getBuildModel() { return getConfigValue("ro.product.model"); }
    public static String getBuildDevice() { return getConfigValue("ro.product.device"); }
    public static String getBuildManufacturer() { return getConfigValue("ro.product.manufacturer"); }
    public static String getBuildBrand() { return getConfigValue("ro.product.brand"); }
    public static String getBuildProduct() { return getConfigValue("ro.product.name"); }
    public static String getBuildBoard() { return getConfigValue("ro.product.board"); }
    public static String getBuildHardware() { return getConfigValue("ro.hardware"); }

    public static String getBuildBootloader() {
        String bootloader = getConfigValue("ro.bootloader");
        return (bootloader == null || bootloader.isEmpty()) ? null : bootloader;
    }

    public static String getBuildId() { return getConfigValue("ro.build.id"); }
    public static String getBuildDisplay() { return getConfigValue("ro.build.display.id"); }
    public static String getBuildTags() { return getConfigValue("ro.build.tags"); }
    public static String getBuildType() { return getConfigValue("ro.build.type"); }
    public static String getBuildVersionRelease() { return getConfigValue("ro.build.version.release"); }

    public static int getBuildVersionSdk() {
        String sdk = getConfigValue("ro.build.version.sdk");
        try { return Integer.parseInt(sdk); } catch (Exception e) { return 36; }
    }

    public static String getBuildVersionSecurityPatch() { return getConfigValue("ro.build.version.security_patch"); }
    public static String getBuildVersionIncremental() { return getConfigValue("ro.build.version.incremental"); }
    public static String getBuildVersionCodename() { return getConfigValue("ro.build.version.codename"); }
    public static String getBuildDescription() { return getConfigValue("ro.build.description"); }
    public static String getBuildCharacteristics() { return getConfigValue("ro.build.characteristics"); }
    public static String getBuildFlavor() { return getConfigValue("ro.build.flavor"); }
    public static String getWebViewUserAgent() { return getConfigValue("webview.user_agent"); }

    public static String getCpuAbi() { return getConfigValue("ro.product.cpu.abi"); }
    public static String getCpuAbiList() { return getConfigValue("ro.product.cpu.abilist"); }
    public static String getCpuAbiList64() { return getConfigValue("ro.product.cpu.abilist64"); }
    public static String getCpuAbiList32() { return getConfigValue("ro.product.cpu.abilist32"); }
}
