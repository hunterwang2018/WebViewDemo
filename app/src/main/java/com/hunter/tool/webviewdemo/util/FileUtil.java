package com.hunter.tool.webviewdemo.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;


/**
 * Created by Administrator on 2017/12/5 0005.
 */

public class FileUtil {
    private final static String TAG = "FileUtil";
    public final static String FileSavePath = Environment
            .getExternalStorageDirectory().getPath();//+ File.separator + "WEB_SEARCH" + File.separator;


    public static void writeFileInit(Context context) {
        String path = FileSavePath + "/web_Data.txt"; //context.getFilesDir().getAbsolutePath()

        try {
            File file = new File(path);
            if (!file.exists()) {
                boolean flag = file.createNewFile();
                //boolean flag = file.mkdirs();
                Log.i(TAG, "mkdirs = " + flag);
            }

            RandomAccessFile raf = new RandomAccessFile(path, "rw");
            FileChannel fc = raf.getChannel();
            //将文件大小截为0
            fc.truncate(0);

            raf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeFile(Context context, String str) {
        String path = FileSavePath + "/web_Data.txt"; //context.getFilesDir().getAbsolutePath()

        try {
            File file = new File(path);
            if (!file.exists()) {
                boolean flag = file.createNewFile();
                //boolean flag = file.mkdirs();
                Log.i(TAG, "mkdirs = " + flag);
            }

            RandomAccessFile raf = new RandomAccessFile(path, "rw");
//            FileChannel fc = raf.getChannel();
//            //将文件大小截为0
//            fc.truncate(0);
            raf.seek(file.length());
            raf.write(str.getBytes());
            raf.writeChars("*");
//            raf.writeChars("*\r\n");

            raf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String readerFile(Context context) {
        StringBuffer sb = new StringBuffer();
        String path = FileSavePath + "/web_Data.txt";

        try {
            File file = new File(path);
            if (file.exists()) {
                RandomAccessFile raf = new RandomAccessFile(path, "r");

                byte[] rd = new byte[1024];
                int len = -1;
                //循环读取，读到结束后len被重新赋值为-1
                while((len = raf.read(rd))!= -1) {
                    String value = new String(rd);
                    sb.append(value);
                }
            }
        } catch (Exception e) {
        }

        return sb.toString();

    }
}
