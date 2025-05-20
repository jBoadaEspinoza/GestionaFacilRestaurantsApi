package com.api.GestionaFacilRestaurants.utilities;


//import java.math.BigInteger;
import java.util.Arrays;

public class Base58Util {
    private static final char[] ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray();
    private static final int BASE_58 = ALPHABET.length;
    private static final int BASE_256 = 256;
    //private static final BigInteger BASE_58_BIG = BigInteger.valueOf(BASE_58);

    public static String encode(byte[] input) {
        if (input.length == 0) {
            return "";
        }

        input = Arrays.copyOf(input, input.length); // since we modify it in-place

        // Count leading zeroes.
        int zeroCount = 0;
        while (zeroCount < input.length && input[zeroCount] == 0) {
            ++zeroCount;
        }

        // The actual encoding.
        byte[] temp = new byte[input.length * 2];
        int j = temp.length;

        int startAt = zeroCount;
        while (startAt < input.length) {
            byte mod = divmod58(input, startAt);
            if (input[startAt] == 0) {
                ++startAt;
            }
            temp[--j] = (byte) ALPHABET[mod];
        }

        // Strip extra '1' if any
        while (j < temp.length && temp[j] == ALPHABET[0]) {
            ++j;
        }

        // Add as many leading '1' as there were leading zeros.
        while (--zeroCount >= 0) {
            temp[--j] = (byte) ALPHABET[0];
        }

        byte[] output = Arrays.copyOfRange(temp, j, temp.length);
        return new String(output);
    }

    public static byte[] decode(String input) {
        if (input.length() == 0) {
            return new byte[0];
        }

        byte[] input58 = new byte[input.length()];
        for (int i = 0; i < input.length(); ++i) {
            char c = input.charAt(i);

            int digit58 = -1;
            if (c >= '1' && c <= '9') {
                digit58 = c - '1';
            } else if (c >= 'A' && c <= 'H') {
                digit58 = c - 'A' + 9;
            } else if (c >= 'J' && c <= 'N') {
                digit58 = c - 'J' + 17;
            } else if (c >= 'P' && c <= 'Z') {
                digit58 = c - 'P' + 22;
            } else if (c >= 'a' && c <= 'k') {
                digit58 = c - 'a' + 33;
            } else if (c >= 'm' && c <= 'z') {
                digit58 = c - 'm' + 44;
            }
            if (digit58 < 0) {
                throw new IllegalArgumentException("Illegal character " + c + " at " + i);
            }

            input58[i] = (byte) digit58;
        }

        // Count leading zeroes.
        int zeroCount = 0;
        while (zeroCount < input58.length && input58[zeroCount] == 0) {
            ++zeroCount;
        }

        // The encoding.
        byte[] temp = new byte[input.length()];
        int j = temp.length;

        int startAt = zeroCount;
        while (startAt < input58.length) {
            byte mod = divmod256(input58, startAt);
            if (input58[startAt] == 0) {
                ++startAt;
            }
            temp[--j] = mod;
        }

        // Do no add extra leading zeroes, move j to first non null byte.
        while (j < temp.length && temp[j] == 0) {
            ++j;
        }

        return Arrays.copyOfRange(temp, j - zeroCount, temp.length);
    }

    private static byte divmod58(byte[] number, int startAt) {
        int remainder = 0;
        for (int i = startAt; i < number.length; i++) {
            int digit256 = (int) number[i] & 0xFF;
            int temp = remainder * BASE_256 + digit256;

            number[i] = (byte) (temp / BASE_58);
            remainder = temp % BASE_58;
        }

        return (byte) remainder;
    }

    private static byte divmod256(byte[] number58, int startAt) {
        int remainder = 0;
        for (int i = startAt; i < number58.length; i++) {
            int digit58 = (int) number58[i] & 0xFF;
            int temp = remainder * BASE_58 + digit58;

            number58[i] = (byte) (temp / BASE_256);
            remainder = temp % BASE_256;
        }

        return (byte) remainder;
    }
}
