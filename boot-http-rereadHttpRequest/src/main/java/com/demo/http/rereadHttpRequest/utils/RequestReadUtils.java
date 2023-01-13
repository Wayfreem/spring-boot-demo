package com.demo.http.rereadHttpRequest.utils;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.Enumeration;

/**
 * 将 HttpServletRequest 中的内容转为 String
 *
 * @author wuq
 * @Time 2023-1-13 13:52
 * @Description
 */
public class RequestReadUtils {
    private static final int BUFFER_SIZE = 1024 * 8;

    public static String read(HttpServletRequest request) throws IOException {
        BufferedReader bufferedReader = request.getReader();
        // 获取 head 信息
        for (Enumeration<String> iterator = request.getHeaderNames(); iterator.hasMoreElements(); ) {
            String type = iterator.nextElement();
            System.out.println(type + " = " + request.getHeader(type));
        }
        System.out.println();
        StringWriter writer = new StringWriter();
        write(bufferedReader, writer);
        return writer.getBuffer().toString();
    }

    public static long write(Reader reader, Writer writer) throws IOException {
        return write(reader, writer, BUFFER_SIZE);
    }

    public static long write(Reader reader, Writer writer, int bufferSize) throws IOException {
        int read;
        long total = 0;
        char[] buf = new char[bufferSize];
        while ((read = reader.read(buf)) != -1) {
            writer.write(buf, 0, read);
            total += read;
        }
        return total;
    }
}
