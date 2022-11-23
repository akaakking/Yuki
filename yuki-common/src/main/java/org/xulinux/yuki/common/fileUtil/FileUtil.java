package org.xulinux.yuki.common.fileUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * //TODO add class commment here
 *
 * @Author wfh
 * @Date 2022/11/8 下午9:01
 */
public class FileUtil {
    public static List<String> readList(String path) {
        List<String> list = new ArrayList<>();

        File file = new File(path);

        try {

            FileReader reader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(reader);
            list.add(bufferedReader.readLine());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static void writeLine(String path, String line) {
        File file = new File(path);

        writeLine(file,line);
    }

    public static void writeLine(File file, int num) {
        writeLine(file,String.valueOf(num));
    }


    public static void writeLine(File file, String line) {
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        try {
            fileWriter = new FileWriter(file,true);
            bufferedWriter = new BufferedWriter(fileWriter);

            bufferedWriter.write(line + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
