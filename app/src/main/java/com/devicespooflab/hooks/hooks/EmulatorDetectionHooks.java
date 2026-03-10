package com.devicespooflab.hooks.hooks;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class EmulatorDetectionHooks {

    private static final String[] EMULATOR_FILES = {
        "/dev/qemu_pipe", "/dev/goldfish_pipe", "/sys/qemu_trace",
        "/system/lib/libc_malloc_debug_qemu.so", "/system/lib64/libc_malloc_debug_qemu.so",
        "/sys/devices/virtual/misc/goldfish_pipe", "/sys/devices/virtual/misc/goldfish_sync"
    };

    private static final String[] EMULATOR_KEYWORDS = { "goldfish", "ranchu", "vbox", "qemu" };

    public static void hook(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            hookFileExists();
            hookFileListFiles();
        } catch (Exception ignored) {}
    }

    private static void hookFileExists() {
        XposedHelpers.findAndHookMethod(File.class, "exists", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                String path = ((File) param.thisObject).getAbsolutePath().toLowerCase();
                for (String emuFile : EMULATOR_FILES) {
                    if (path.contains(emuFile.toLowerCase())) {
                        param.setResult(false);
                        return;
                    }
                }
                for (String keyword : EMULATOR_KEYWORDS) {
                    if (path.contains(keyword)) {
                        param.setResult(false);
                        return;
                    }
                }
            }
        });
    }

    private static void hookFileListFiles() {
        XC_MethodHook listHook = new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                File[] files = (File[]) param.getResult();
                if (files == null) return;
                List<File> filtered = new ArrayList<>();
                for (File f : files) {
                    String p = f.getAbsolutePath().toLowerCase();
                    boolean isEmu = false;
                    for (String k : EMULATOR_KEYWORDS) if (p.contains(k)) { isEmu = true; break; }
                    if (!isEmu) for (String ef : EMULATOR_FILES) if (p.contains(ef.toLowerCase())) { isEmu = true; break; }
                    if (!isEmu) filtered.add(f);
                }
                param.setResult(filtered.toArray(new File[0]));
            }
        };

        try {
            XposedHelpers.findAndHookMethod(File.class, "listFiles", listHook);
            XposedHelpers.findAndHookMethod(File.class, "listFiles", java.io.FileFilter.class, listHook);
            XposedHelpers.findAndHookMethod(File.class, "listFiles", java.io.FilenameFilter.class, listHook);
        } catch (Exception ignored) {}
    }
}
