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
        return readList(new File(path));
    }

    public static String readOneLine(File file) {
        BufferedReader bufferedReader = null;
        try {
            FileReader reader = new FileReader(file);
            bufferedReader = new BufferedReader(reader);

            return bufferedReader.readLine();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }

    public String readOneLine(String path) {
        return readOneLine(new File(path));
    }

    public static List<String> readList(File file) {
        List<String> list = new ArrayList<>();
        BufferedReader bufferedReader = null;
        try {
            FileReader reader = new FileReader(file);
            bufferedReader = new BufferedReader(reader);

            String s = bufferedReader.readLine();
            while (s != null) {
                list.add(s);
                s = bufferedReader.readLine();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
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
