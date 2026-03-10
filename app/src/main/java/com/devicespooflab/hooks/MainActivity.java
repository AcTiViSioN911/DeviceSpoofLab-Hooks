package com.devicespooflab.hooks;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import com.devicespooflab.hooks.utils.RandomGenerator;
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
                    "✅ Identity Fixed!\n\n" +
                    "Config: " + configFile.getAbsolutePath() + "\n\n" +
                    "Status: IDs generated and saved to file.\n" +
                    "These will NOT change on reboot.\n\n" +
                    "To rotate identity: Delete the file or use ADB command."
                );
                Toast.makeText(this, "New Persona Created", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                textView.setText("❌ Error: " + e.getMessage());
            }
        } else {
            textView.setText(
                "✅ Identity Active\n\n" +
                "Config: " + configFile.getAbsolutePath() + "\n\n" +
                "Status: IDs are persistent from file."
            );
        }

        setContentView(textView);
    }

    private void createDefaultConfig(File configFile) throws IOException {
        // Генерируем данные один раз для записи в файл
        String serial = RandomGenerator.generateSerial();
        String androidId = RandomGenerator.generateAndroidId();
        String imei1 = RandomGenerator.generateIMEI();
        String imei2 = deriveImei2(imei1);

        String defaultConfig =
            "ro.serialno=" + serial + "\n" +
            "ro.boot.serialno=" + serial + "\n" +
            "ANDROID_ID=" + androidId + "\n" +
            "IMEI=" + imei1 + "\n" +
            "IMEI2=" + imei2 + "\n\n" +
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

    private String deriveImei2(String imei1) {
        try {
            String base = imei1.substring(0, 14);
            long num = Long.parseLong(base) + 8;
            String newBase = String.format("%014d", num);
            return newBase + calculateLuhn(newBase);
        } catch (Exception e) {
            return RandomGenerator.generateIMEI();
        }
    }

    private int calculateLuhn(String n) {
        int s = 0;
        boolean a = true;
        for (int i = n.length() - 1; i >= 0; i--) {
            int d = Character.getNumericValue(n.charAt(i));
            if (a) { d *= 2; if (d > 9) d -= 9; }
            s += d;
            a = !a;
        }
        return (10 - (s % 10)) % 10;
    }
}
