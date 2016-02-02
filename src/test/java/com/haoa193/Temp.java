package com.haoa193;

/**
 * Created by chenyong on 2016/1/31.
 */
public class Temp {

    private static int padding_len;
    public static void main(String[] args) {

        int i = 2;
        int size = 50;
        padding_len = 2;
        StringBuilder sb = new StringBuilder(size);
        for (; i <= size; i++) {
            sb.append("ftp://1:1@dz.dl1234.com:8006/秦时明月"+ padding(i) +".720p高清未删减[电影天堂www.dy2018.com].mp4\n");
        }

        System.out.println(sb.toString());
    }
    private static String padding(int i) {

        String rs = String.valueOf(i);
        while (rs.length() != padding_len) {
            rs = "0" + rs;
        }
        return rs;
    }

}
