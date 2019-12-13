package com.example.diary;

import android.database.Cursor;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

public class FileIO {
    public static void exportItems (Cursor cursor, int year, int month, File path) {
        File file = new File (path.toString() + "/" + year + "-" + month + ".txt");
        Log.d("path", file.toString());
        StringBuilder writeText = new StringBuilder();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            writeText.append(year);
            writeText.append("/");
            writeText.append(month);
            writeText.append("/");
            writeText.append(cursor.getInt(0));
            writeText.append(System.lineSeparator());
            writeText.append(cursor.getString(1));
            writeText.append(System.lineSeparator());
            writeText.append(System.lineSeparator());
            cursor.moveToNext();
        }
        try {
            BufferedWriter bufferedWriter =
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    new FileOutputStream(file, true), "UTF-8"
                            )
                    );
            Log.d("in the string", writeText.toString());
            bufferedWriter.write(writeText.toString());
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (FileNotFoundException e) {
            Log.d("file output error.", e.toString());
        } catch (UnsupportedEncodingException e) {
            Log.d("file output error.", e.toString());
        } catch (IOException e) {
            Log.d("file output error.", e.toString());
        }
    }
}
