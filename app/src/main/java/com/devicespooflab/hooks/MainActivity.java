package com.devicespooflab.hooks;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Main activity for DeviceSpoofLab-Hooks LSPosed module.
 * Optimized for Pixel 7a (Lynx) - Android 16 - Kazakhstan Persona.
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView textView = new TextView(this);
        textView.setPadding(50, 50, 50, 50);
        textView.setTextSize(16);

        // Auto-create config file on first launch
        File configFile = new File(getFilesDir(), "device_profile.conf");

        if (!configFile.exists()) {
            try {
                createDefaultConfig(configFile);
                textView.setText(
                    "✅ DeviceSpoofLab-Hooks Setup Complete!\n\n" +
                    "Config file created at:\n" +
                    configFile.getAbsolutePath() + "\n\n" +
                    "Target Persona: Pixel 7a (Android 16) | Kcell KZ\n\n" +
                    "Next Steps:\n" +
                    "1. Open LSPosed Manager\n" +
                    "2. Enable this module\n" +
                    "3. Select target apps in Scope\n" +
                    "4. Restart target apps\n\n" +
                    "No manual file pushing required!\n" +
                    "Check logs: adb logcat | grep DeviceSpoofLab"
                );
                Toast.makeText(this, "Config file created successfully!", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                textView.setText(
                    "❌ Failed to create config file:\n" +
                    e.getMessage() + "\n\n" +
                    "The module will use embedded defaults."
                );
                Toast.makeText(this, "Using embedded defaults", Toast.LENGTH_LONG).show();
            }
        } else {
            textView.setText(
                "✅ DeviceSpoofLab-Hooks\n\n" +
                "Config file exists at:\n" +
                configFile.getAbsolutePath() + "\n\n" +
                "Target Persona: Pixel 7a (Android 16) | Kcell KZ\n\n" +
                "Status: Ready\n\n" +
                "To reconfigure:\n" +
                "1. Edit the config file, OR\n" +
                "2. Delete it and reopen this app"
            );
        }

        setContentView(textView);
    }

    private void createDefaultConfig(File configFile) throws IOException {
        String defaultConfig =
            "# DeviceSpoofLab-Hooks Optimized Config\n" +
            "# Target: Native Pixel 7a (Lynx) with unique persona\n" +
            "# This file is in app's private storage - no Magisk conflicts!\n\n" +

            "# Carrier/GSM (Kazakhstan Kcell Persona)\n" +
            "gsm.operator.alpha=Kcell\n" +
            "gsm.operator.numeric=40102\n" +
            "gsm.sim.operator.alpha=Kcell\n" +
            "gsm.sim.operator.numeric=40102\n" +
            "gsm.sim.operator.iso-country=kz\n" +
            "persist.sys.timezone=Asia/Almaty\n" +
            "persist.sys.usb.config=none\n\n" +

            "# Security (Root/Unlock masking)\n" +
            "ro.debuggable=0\n" +
            "ro.secure=1\n" +
            "ro.adb.secure=1\n" +
            "ro.build.selinux=0\n" +
            "ro.boot.verifiedbootstate=green\n" +
            "ro.boot.flash.locked=1\n" +
            "ro.boot.vbmeta.device_state=locked\n" +
            "ro.boot.warranty_bit=0\n" +
            "sys.oem_unlock_allowed=0\n" +
            "ro.boot.veritymode=enforcing\n" +
            "ro.crypto.state=encrypted\n" +
            "ro.kernel.qemu=0\n" +
            "ro.boot.qemu=0\n\n" +

            "# Build Identity (Android 16 Lynx Baseline)\n" +
            "ro.build.description=lynx-user 16 CP1A.260305.018 14887507 release-keys\n" +
            "ro.build.version.release=16\n" +
            "ro.build.version.sdk=36\n\n" +

            "# Auto-generated identifiers (leave blank for random)\n" +
            "ro.serialno=\n" +
            "ro.boot.serialno=\n" +
            "ro.bootloader=\n" +
            "ANDROID_ID=\n";

        try (FileOutputStream fos = new FileOutputStream(configFile)) {
            fos.write(defaultConfig.getBytes());
            fos.flush();
        }
    }
}
