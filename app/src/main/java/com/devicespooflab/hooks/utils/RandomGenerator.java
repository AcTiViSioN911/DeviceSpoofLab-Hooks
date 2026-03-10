package com.devicespooflab.hooks.utils;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class RandomGenerator {

    private static final SecureRandom random = new SecureRandom();

    public static String generateIMEI() {
        return generateIMEIWithTAC("35328461");
    }

    public static String generateIMEIWithTAC(String tac) {
        StringBuilder serial = new StringBuilder();
        for (int i = 0; i < 14 - tac.length(); i++) {
            serial.append(random.nextInt(10));
        }
        String imeiWithoutCheck = tac + serial.toString();
        return imeiWithoutCheck + calculateLuhnCheckDigit(imeiWithoutCheck);
    }

    public static String generateMEID() {
        StringBuilder meid = new StringBuilder();
        String hexChars = "0123456789ABCDEF";
        for (int i = 0; i < 14; i++) {
            meid.append(hexChars.charAt(random.nextInt(16)));
        }
        return meid.toString();
    }

    public static String generateIMSI() {
        String mcc = "401";
        String mnc = "02";
        StringBuilder msin = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            msin.append(random.nextInt(10));
        }
        return mcc + mnc + msin.toString();
    }

    public static String generateICCID() {
        String prefix = "89701";
        String issuer = "02";
        StringBuilder account = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            account.append(random.nextInt(10));
        }
        String iccidWithoutCheck = prefix + issuer + account.toString();
        return iccidWithoutCheck + calculateLuhnCheckDigit(iccidWithoutCheck);
    }

    public static String generatePhoneNumber() {
        int prefix = random.nextBoolean() ? 701 : 702;
        int subscriber = 1000000 + random.nextInt(9000000);
        return String.format("+7%d%d", prefix, subscriber);
    }

    public static String generateSerial() {
        String chars = "0123456789ABCDEFGHJKLMNPQRSTUVWXYZ";
        StringBuilder serial = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            serial.append(chars.charAt(random.nextInt(chars.length())));
        }
        return serial.toString();
    }

    public static String generateGAID() {
        return UUID.randomUUID().toString();
    }

    public static byte[] generateMediaDrmId() {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return bytes;
    }

    public static String generateGSFId() {
        StringBuilder gsf = new StringBuilder();
        String hexChars = "0123456789abcdef";
        for (int i = 0; i < 16; i++) {
            gsf.append(hexChars.charAt(random.nextInt(16)));
        }
        return gsf.toString();
    }

    public static String generateAndroidId() {
        StringBuilder androidId = new StringBuilder();
        String hexChars = "0123456789abcdef";
        for (int i = 0; i < 16; i++) {
            androidId.append(hexChars.charAt(random.nextInt(16)));
        }
        return androidId.toString();
    }

    public static String generateFingerprint() {
        return "google/lynx/lynx:16/CP1A.260305.018/14887507:user/release-keys";
    }

    public static String generateBuildId() {
        return "CP1A.260305.018";
    }

    public static String generateIncremental() {
        return "14887507";
    }

    public static String generateBootloader() {
        StringBuilder hex = new StringBuilder();
        String hexChars = "0123456789ABCDEF";
        for (int i = 0; i < 8; i++) {
            hex.append(hexChars.charAt(random.nextInt(16)));
        }
        return "lynx-16.4-" + hex.toString();
    }

    public static String generateSecurityPatch() {
        return "2026-03-05";
    }

    public static String generateHex(int length) {
        StringBuilder hex = new StringBuilder();
        String hexChars = "0123456789abcdef";
        for (int i = 0; i < length; i++) {
            hex.append(hexChars.charAt(random.nextInt(16)));
        }
        return hex.toString();
    }

    private static int calculateLuhnCheckDigit(String number) {
        int sum = 0;
        boolean alternate = true;
        for (int i = number.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(number.charAt(i));
            if (alternate) {
                digit *= 2;
                if (digit > 9) digit -= 9;
            }
            sum += digit;
            alternate = !alternate;
        }
        return (10 - (sum % 10)) % 10;
    }
}
