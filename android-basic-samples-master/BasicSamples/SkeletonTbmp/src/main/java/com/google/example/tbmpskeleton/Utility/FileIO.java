package Utility;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Nathan on 1/20/2015.
 */
public class FileIO {
    public static String readFileToString(File file) throws IOException{
        int len;
        char[] chr = new char[4096];
        StringBuffer buffer = new StringBuffer();
        FileReader reader = null;

        try {
            reader = new FileReader(file);
        }
        catch(IOException e){
            Log.d("FileIO", "Failed to open file");
        }
        if(reader != null){
            try {
                while ((len = reader.read(chr)) > 0) {
                    buffer.append(chr, 0, len);
                }
            } finally {
                reader.close();
            }
        }
        return buffer.toString();
    }
}
