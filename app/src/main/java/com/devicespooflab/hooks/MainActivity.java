package com.devicespooflab.hooks;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView textView = new TextView(this);
        textView.setPadding(50, 50, 50, 50);
        textView.setTextSize(16);

        File configFile = new File(getFilesDir(), "device_profile.conf");

        if (!configFile.exists()) {
            try {
                createDefaultConfig(configFile);
                textView.setText(
                    "✅ Setup Complete!\n\n" +
                    "Config: " + configFile.getAbsolutePath() + "\n\n" +
                    "Mode: Pixel 7a (Lynx) Identity Rotation\n" +
                    "Persona: Kcell KZ\n\n" +
                    "Next: Enable in LSPosed & Reboot."
                );
                Toast.makeText(this, "Config created", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                textView.setText("❌ Error: " + e.getMessage());
            }
        } else {
            textView.setText(
                "✅ DeviceSpoofLab-Hooks\n\n" +
                "Config: " + configFile.getAbsolutePath() + "\n\n" +
                "Status: Ready"
            );
        }

        setContentView(textView);
    }

    private void createDefaultConfig(File configFile) throws IOException {
        String defaultConfig =
            "ro.serialno=\n" +
            "ro.boot.serialno=\n" +
            "ANDROID_ID=\n\n" +
            "ro.boot.verifiedbootstate=green\n" +
            "ro.boot.flash.locked=1\n" +
            "ro.boot.vbmeta.device_state=locked\n" +
            "ro.build.selinux=0\n" +
            "ro.secure=1\n" +
            "ro.debuggable=0\n" +
            "ro.boot.veritymode=enforcing\n" +
            "ro.crypto.state=encrypted\n\n" +
            "gsm.operator.alpha=Kcell\n" +
            "gsm.operator.numeric=40102\n" +
            "gsm.sim.operator.alpha=Kcell\n" +
            "gsm.sim.operator.numeric=40102\n" +
            "gsm.sim.operator.iso-country=kz\n" +
            "persist.sys.timezone=Asia/Almaty\n";

        try (FileOutputStream fos = new FileOutputStream(configFile)) {
            fos.write(defaultConfig.getBytes());
            fos.flush();
        }
    }
}
