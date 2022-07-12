package com.taogen.commons.io;

import com.taogen.commons.datatypes.datetime.DateFormatters;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author Taogen
 */
public class FileUtils {
    public static final List<String> BROWSER_PREVIEW_FILE_SUFFIXES = Arrays.asList(".pdf", ".png", ".jpg", ".jpeg", ".gif", ".txt");

    /**
     * For example: text.txt -> text_2022-06-23_16-01-01.txt, text -> text_2022-06-23_16-01-01
     *
     * @param fileName
     * @return
     */
    public static String appendDateTimeToFileName(String fileName) {
        int index = fileName.lastIndexOf(".");
        if (index != -1) {
            return new StringBuilder()
                    .append(fileName, 0, index)
                    .append("_")
                    .append(DateFormatters.yyyy_MM_dd_HH_mm_ss_SSS_2.format(new Date()))
                    .append(fileName.substring(index))
                    .toString();
        } else {
            return new StringBuilder()
                    .append(fileName)
                    .append("_")
                    .append(DateFormatters.yyyy_MM_dd_HH_mm_ss_SSS_2.format(new Date()))
                    .toString();
        }
    }

    public static void ensureFileExists(String dirOrFilePath) throws FileNotFoundException {
        File file = new File(dirOrFilePath);
        if (!file.exists()) {
            throw new FileNotFoundException("Not found " + dirOrFilePath);
        }
    }

    /**
     * Windows 10: C:\Users\{user}\AppData\Local\Temp\
     * Debian: /tmp
     *
     * @return
     */
    public static String getTempDir() {
        return System.getProperty("java.io.tmpdir");
    }

    public static String getDirPathByFilePath(String filePath) {
        if (filePath == null) {
            return null;
        }
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            return file.getParentFile().getAbsolutePath();
        }
        return null;
    }

    public static String getFileNameByFilePath(String filePath) {
        if (filePath == null) {
            return null;
        }
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            return file.getName();
        }
        return null;
    }

    /**
     * the root of file is 'src/main/resources' or 'src/test/resources'
     *
     * @param fileClassPath For example: application.yml, i18n/test.properties
     * @return
     * @throws IOException
     */
    public static String getFilePathByFileClassPath(String fileClassPath) throws URISyntaxException {
        URL url = FileUtils.class.getClassLoader()
                .getResource(fileClassPath);
        return Paths.get(url.toURI()).toFile().getAbsolutePath();
    }

    public static String getFileNameByFileUrl(String fileUrl) {
        if (fileUrl == null) {
            return null;
        }
        String fileName = null;
        if (fileUrl.indexOf("/") > 0) {
            fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        }
        if (fileName.indexOf("?") > 0) {
            fileName = fileName.substring(0, fileName.indexOf("?"));
        }
        return fileName;
    }

    public static String getTextFromFile(String textFilePath) {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = getBufferedReaderWithCharset(textFilePath, StandardCharsets.UTF_8)) {
            int len;
            char[] buf = new char[1024];
            while ((len = reader.read(buf)) != -1) {
                stringBuilder.append(buf, 0, len);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    /**
     * The one-arguments constructors of FileReader always use the platform default encoding which is generally a bad idea.
     * Since Java 11 FileReader has also gained constructors that accept an encoding: new FileReader(file, charset) and new FileReader(fileName, charset).
     * In earlier versions of java, you need to use new InputStreamReader(new FileInputStream(pathToFile), <encoding>).
     *
     * @param filePath
     * @param charset
     * @return
     * @throws FileNotFoundException
     */
    public static BufferedReader getBufferedReaderWithCharset(String filePath,
                                                              Charset charset) throws FileNotFoundException {
        return new BufferedReader(new InputStreamReader(
                new FileInputStream(filePath), charset));
    }

    public static String getFileExtension(String fileName) {
        if (fileName == null) {
            return null;
        }
        int index = fileName.lastIndexOf(".");
        if (index == -1) {
            return null;
        }
        return fileName.substring(index + 1);
    }

    public static String removeIllegalCharactersFromFileName(String fileName) {
        List<Character> windowsReservedChars = Arrays.asList('\\', '/', ':', '*', '?', '"', '<', '>', '|');
        List<Character> otherChars = Arrays.asList('\r', '\n', '\t');
        if (fileName == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fileName.length(); i++) {
            char c = fileName.charAt(i);
            if (windowsReservedChars.contains(c) || otherChars.contains(c)) {
                sb.append('-');
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static boolean isPreviewFileOnBrowser(String fileName) {
        for (String suffix : BROWSER_PREVIEW_FILE_SUFFIXES) {
            if (fileName.toLowerCase().contains(suffix)) {
                return true;
            }
        }
        return false;
    }

    public static String unifyFileSeparatorOfFilePath(String filepath) {
        return filepath.replace("\\", File.separator).replace("/", File.separator);
    }
}
