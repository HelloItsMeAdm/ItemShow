package com.helloitsmeadm.itemshow;

import java.util.HashMap;
import java.util.Map;

public class RomanNumeralConverter {
    private static final Map<Integer, String> map = new HashMap<>();

    static {
        map.put(1000, "M");
        map.put(900, "CM");
        map.put(500, "D");
        map.put(400, "CD");
        map.put(100, "C");
        map.put(90, "XC");
        map.put(50, "L");
        map.put(40, "XL");
        map.put(10, "X");
        map.put(9, "IX");
        map.put(5, "V");
        map.put(4, "IV");
        map.put(1, "I");
    }

    public static String convertToRoman(int num) {
        if (num > 10) {
            return num + "";
        }

        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (num > 0) {
            int times = num / RomanNumeralConverter.map.keySet().toArray(new Integer[0])[i];
            num -= times * RomanNumeralConverter.map.keySet().toArray(new Integer[0])[i];
            while (times > 0) {
                sb.append(RomanNumeralConverter.map.get(RomanNumeralConverter.map.keySet().toArray(new Integer[0])[i]));
                times--;
            }
            i++;
        }
        return sb.toString();
    }
}
