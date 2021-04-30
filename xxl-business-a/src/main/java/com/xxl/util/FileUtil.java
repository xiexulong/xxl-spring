package com.xxl.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class FileUtil {

    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    /**
     * Write file.
     * @param fileName write file path.
     * @param content write context.
     * @param append  boolean if <code>true</code>, then data will be written to the end of the file
     *                rather than the beginning.
     */
    public static boolean writeFile(String fileName, String content, boolean append) {

        logger.debug("write file, append:{}, fileName:{}, content:{}", append, fileName, content);
        try (FileWriter writer = new FileWriter(fileName, append)) {
            writer.write(content);
            return true;
        } catch (IOException e) {
            logger.error("write file failed, fileName:{}, msg:{}",fileName, e.getMessage());
            return false;
        }
    }

    /**
     * Write file.
     */
    public static boolean writeFile(String fileName, InputStream in, boolean saveEmptyFile) {
        FileOutputStream fileout = null;
        File file = new File(fileName);
        boolean flag = false;
        try {
            fileout = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int ch;
            while ((ch = in.read(buffer)) != -1) {
                flag = true;
                fileout.write(buffer, 0, ch);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.error("write file :" + fileName + " error:" + e.getMessage());
            close(in);
            return false;
        } finally {
            close(fileout);
        }

        if (!saveEmptyFile && !flag) {
            repeatDeleteFile(fileName);
        }

        return true;
    }

    /**
     * Write file.
     */
    public static boolean writeFile(String filePath, StringBuffer buffer) {
        PrintWriter p = null;
        try {
            File newFile = new File(filePath);
            if (!repeatDeleteFile(newFile)) {
                logger.error("delete file" + newFile + "failed!");
                return false;
            }
            if (newFile.createNewFile()) {
                p = new PrintWriter(new FileOutputStream(newFile.getAbsolutePath()));
                p.write(buffer.toString());

            } else {
                logger.error("create file" + newFile + "failed!");
                return false;
            }
        } catch (Exception e) {
            logger.error("writeFile exception", e);
        } finally {
            close(p);
        }
        return true;
    }

    /**
     * repeat delete file.
     * @param fileName file name.
     */
    public static boolean repeatDeleteFile(String fileName) {
        for (int i = 3; i > 0; i--) {
            //don't know why sometime delete file failed
            //to be insure, create a new File object before delete it.
            File f = new File(fileName);
            if (!f.exists() || f.delete()) {
                return true;
            } else {
                logger.error("repeat delete file failed {} delete num {}", fileName, i);
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                logger.error("repeatDeleteFile exception",e);
                Thread.currentThread().interrupt();
            }
        }
        return false;
    }

    private static boolean repeatDeleteFile(File file) {
        return repeatDeleteFile(file.getAbsolutePath());
    }

    /**
     * Move file.
     */
    public static String moveFile(String srcFile, String destFolder) {
        if (FileUtil.createFolder(destFolder)) {
            File file = new File(srcFile);
            String newFilePath = destFolder + "/" + file.getName();

            if(file.renameTo(new File(newFilePath))) {
                return null;
            }

            if ((new File(newFilePath)).exists()) {
                return newFilePath;
            } else {
                return null;
            }
        }

        return null;
    }

    public static void close(Closeable inputSteam) {
        if (inputSteam != null) {
            try {
                inputSteam.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                logger.error(e.getMessage());
            }
        }
    }

    /**
     * Create folder.
     * See {@link #mkdirs(String)} for mkdirs with retries.
     */
    public static boolean createFolder(String path) {
        File dir = new File(path);
        if (dir.exists() && dir.isDirectory()) {
            logger.debug("dir " + path + "is exist");
            return true;
        }

        if (dir.mkdirs() || dir.exists()) {
            logger.debug("createFolder  " + path + "sucessful");
            return true;
        } else {
            logger.debug("createFolder " + path + "fail");
            return false;
        }
    }

    /**
     * Read file content.
     */
    public static String readFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            logger.error("readFile() error, filePath is empty");
            return null;
        }
        File file = new File(filePath);
        StringBuilder sb = new StringBuilder();
        String result;
        try (FileReader fileReader = new FileReader(file);
             BufferedReader br = new BufferedReader(fileReader)) {
            String tempString = br.readLine();
            if (tempString != null) {
                sb.append(tempString);
            }
            while ((tempString = br.readLine()) != null) {
                sb.append(System.getProperty("line.separator"));
                sb.append(tempString);
            }
            result = sb.toString();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage());
            return null;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage());
            return null;
        }
        return result;
    }

    public static String readOneLine(String filePath, String lineKey) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(new File(filePath)))) {
            String tempString;
            while ((tempString = br.readLine()) != null) {
                if (tempString.startsWith(lineKey)) {
                    return tempString;
                }
            }
        }
        throw new IOException(String.format("read file {} get line key {} failed", lineKey, filePath));
    }

    /**
     * Ungz file.
     */
    public static String unGzFile(String gzFile, String destDir) {
        if (!gzFile.endsWith(".gz")) {
            logger.debug("ungz failed: " + gzFile + " is not a .gz file");
            return null;
        }

        String destFileName = null;
        File gz = new File(gzFile);
        String gzFileName = gz.getName();
        mkdirs(destDir);

        try (InputStream gis = new GZIPInputStream(new BufferedInputStream(new FileInputStream(gz)))) {
            String outFileName = destDir + File.separator + gzFileName.substring(0, gzFileName.length() - 3);
            File outFile = new File(outFileName);
            try (OutputStream bos = new BufferedOutputStream(new FileOutputStream(outFile))) {
                int count;
                byte[] data = new byte[1024];
                while ((count = gis.read(data, 0, 1024)) != -1) {
                    bos.write(data, 0, count);
                }
                bos.flush();
                destFileName = outFile.getAbsolutePath();
            }
        } catch (IOException e) {
            logger.error("ungz failed: " + e.toString());
        }

        return destFileName;
    }

    /**
     * The deference between {@link #createFolder(String)} is this method will retry for three times.
     */
    public static boolean mkdirs(String dir) {
        for (int i = 2; i >= 0; i--) {
            File f = new File(dir);
            if (f.isDirectory() || f.mkdirs()) {
                return true;
            }
            if (i != 0) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    logger.warn("mkdirs interrupted", e);
                    Thread.currentThread().interrupt();
                }
            }
        }
        return false;
    }

    private static boolean delFile(String filePath, boolean delDir) {
        if (null == filePath) {
            return true;
        }
        File file = new File(filePath);
        if (file == null || !file.exists()) {
            return true;
        }
        
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (null != files) {
                for (int i = 0; i < files.length; i++) {
                    boolean deleted;
                    if (files[i].isDirectory()) {
                        deleted = delFile(files[i].getAbsolutePath(), delDir);
                    } else {
                        deleted = repeatDeleteFile(files[i]);
                    }
                    if (!deleted) {
                        return false;
                    }
                }
            }
            if (delDir) {
                boolean deleted = repeatDeleteFile(file);
                if (!deleted) {
                    return false;
                }
            }
        } else {
            boolean deleted = repeatDeleteFile(file);
            if (!deleted) {
                return false;
            }
        }
        return true;
    }

    public static boolean delFile(String filePath) {
        logger.info("delFile " + filePath);
        return delFile(filePath, false);
    }

    public static boolean delDir(String filePath) {
        return delFile(filePath, true);
    }

    /**
     * Move file.
     */
    public static boolean move(String from, String to) {
        try {
            File fromfile = new File(from);
            File tofile = new File(to);

            if (!fromfile.exists()) {
                return false;
            }

            repeatDeleteFile(tofile);

            if (fromfile.renameTo(tofile)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * get file md5 string.
     */
    public static String getFileMD5(File file) throws IOException {

        try (InputStream inputStream = new FileInputStream(file)) {
            return DigestUtils.md5Hex(inputStream);
        }
    }

    /**
     * get file sha256 string.
     */
    public static String getFileSha256Hex(File file) {
        InputStream inputStream = null;
        String sha256 = "";
        try {
            inputStream = new FileInputStream(file);
            sha256 = DigestUtils.sha256Hex(inputStream);
        } catch (IOException e) {
            logger.error(e.toString());
        } finally {
            FileUtil.close(inputStream);
        }
        return sha256;
    }

    /**
     * get file sha256 string.
     */
    public static String getFileSha256Hex(String filePath) {
        InputStream inputStream = null;
        String sha256 = "";
        try {
            inputStream = new FileInputStream(new File(filePath));
            sha256 = DigestUtils.sha256Hex(inputStream);
        } catch (IOException e) {
            logger.error(e.toString());
        } finally {
            FileUtil.close(inputStream);
        }
        return sha256;
    }

    /**
     * Copy file.
     */
    public static boolean copyFile(String srcFile, String destFile) {
        String inputFile = srcFile;
        String outputFile = destFile;

        try (FileOutputStream fos = new FileOutputStream(outputFile);
             FileInputStream fis = new FileInputStream(inputFile);
             BufferedInputStream bufis = new BufferedInputStream(fis);
             BufferedOutputStream bufos = new BufferedOutputStream(fos)) {
            int len;
            byte[] buffer = new byte[1024];
            while ((len = bufis.read(buffer)) != -1) {
                bufos.write(buffer, 0, len);
            }
        } catch (Exception e) {
            logger.error("load file:" + outputFile + "error:" + e.getMessage());
            return false;
        }
        return true;
    }


    /**
     * Copy the whole directory from src to des. And filter the files to copy
     * by {@code after}parameter - only copy the files whose lastmodified time is
     * greater than the value of {@code after} in the specific directory.
     * @param src
     *  The path of source directory.
     * @param des
     *  The path of destination
     * @param after
     *  Timestamp for filter files.
     * @param before
     *  Timestamp for filter files.
     * @return
     *  {@code true} if successful, otherwise {@code false}
     */
    public static boolean copyDir(String src, String des, long after, long before) {
        //logger.info("copyDir src = {}, des = {}", src, des);
        File file1 = new File(src);
        File[] fs = file1.listFiles();
        if (fs == null) {
            logger.error("srcFile is empty");
            return true;
        }
        if (FileUtil.createFolder(des)) {
            for (File f : fs) {
                if (f.isFile() && f.lastModified() >= after && f.lastModified() < before) {
                    boolean copyResult = copyFile(f.getPath(), des + File.separator + f.getName());
                    if (!copyResult) {
                        return false;
                    }
                } else if (f.isDirectory()) {
                    boolean copyResult = copyDir(f.getPath(), des + File.separator + f.getName(),
                            after, before);
                    if (!copyResult) {
                        return false;
                    }
                }
            }

            return true;
        }
        return false;
    }

    /**
     * Copy the whole directory from src to des.
     * @param src The path of source directory.
     * @param des The path of destination
     * @return {@code true} if successful, otherwise {@code false}
     */
    public static boolean copyDir(String src, String des) {
        return copyDir(src, des, null);
    }

    /**
     * Copy the whole directory from src to des.
     * @param src The path of source directory.
     * @param des The path of destination
     * @param endStr filer. only copy file endsWith input parameter.
     * @return {@code true} if successful, otherwise {@code false}
     */
    public static boolean copyDir(String src, String des, String endStr) {
        logger.info("copyDir src = {}, des = {}", src, des);
        File file1 = new File(src);
        File[] fs = file1.listFiles();
        if (fs == null) {
            logger.error("srcFile is empty");
            return true;
        }

        for (File f : fs) {
            if (f.isFile()) {
                if (endStr == null || endStr.isEmpty() || f.getName().endsWith(endStr)) {
                    if (FileUtil.createFolder(des)) {
                        boolean copyResult = copyFile(f.getPath(), des + File.separator + f.getName());
                        if (!copyResult) {
                            return false;
                        }
                    }
                }
            } else {
                boolean copyResult = copyDir(f.getPath(),
                        des + File.separator + f.getName(), endStr);
                if (!copyResult) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Get all children folders.
     */
    public static List<String> getChildrenFolder(String path) {
        List<String> fileList = new ArrayList<String>();
        File file = new File(path);

        if (!file.exists() || !file.isDirectory()) {
            return fileList;
        }

        File[] tempList = file.listFiles();
        if (null != tempList) {
            for (int i = 0; i < tempList.length; i++) {
                if (tempList[i].isDirectory()) {

                    fileList.add(tempList[i].getName());
                }
                if (tempList[i].isFile()) {
                    continue;
                }
            }
        }

        return fileList;
    }

    /**
     * Get all children files.
     */
    public static List<String> getChildrenFile(String path) {
        List<String> fileList = new ArrayList<String>();
        File file = new File(path);

        if (!file.exists() || !file.isDirectory()) {
            return fileList;
        }

        File[] tempList = file.listFiles();
        if (null != tempList) {
            for (int i = 0; i < tempList.length; i++) {
                if (tempList[i].isDirectory()) {
                    continue;
                }
                if (tempList[i].isFile()) {
                    fileList.add(tempList[i].getName());
                }
            }
        }

        return fileList;
    }

    /**
     * Assert that it exists, or throw exception.
     */
    public static File assertExist(File file) throws FileNotFoundException {
        if (!file.exists()) {
            throw new FileNotFoundException(file.getAbsolutePath() + " not exists");
        }
        return file;
    }

    /**
     * Assert that it is a normal file, or throw exception.
     */
    public static File assertFile(File file) throws FileNotFoundException {
        if (!assertExist(file).isFile()) {
            throw new FileNotFoundException(file.getAbsolutePath() + " is not a file");
        }
        return file;
    }

    /**
     * Assert that it is directory, or throw exception.
     */
    public static File assertDirectory(File file) throws FileNotFoundException {
        if (!assertExist(file).isDirectory()) {
            throw new FileNotFoundException(file.getAbsolutePath() + " is not a directory");
        }
        return file;
    }

    /**
     * This is used to replace the usage of {@link File#listFiles()} in JDK because it may return null.
     */
    public static List<File> listFiles(File dir) {
        File[] children = dir.listFiles();
        if (children == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(children);
    }

    /**
     * Get the absolute path list of folders in specified directory recursively.
     */
    public static List<String> getAllFoldersRecursively(String dir) {
        List<String> ret = new ArrayList<>();
        LinkedList<File> files = new LinkedList<>();
        File parent = new File(dir);
        files.add(parent);
        while (!files.isEmpty()) {
            File f = files.pop();
            if (f.isDirectory()) {
                files.addAll(listFiles(f));
                if (f != parent) {
                    ret.add(f.getAbsolutePath());
                }
            }
        }
        return ret;
    }

    /**
     * Get the absolute path list of normal files in specified directory recursively.
     */
    public static List<String> getAllFilesRecursively(String dir) {
        List<String> ret = new ArrayList<>();
        LinkedList<File> files = new LinkedList<>();
        files.add(new File(dir));
        while (!files.isEmpty()) {
            File f = files.pop();
            if (f.isDirectory()) {
                files.addAll(listFiles(f));
            } else if (f.isFile()) {
                ret.add(f.getAbsolutePath());
            }
        }
        return ret;
    }

    public static void createNewFile(String file) {
        try {
            String fileName = file;
            File myFile = new File(fileName);
            if (!myFile.createNewFile()) {
                logger.debug("can not create file, file exists");
            }
        } catch (Exception e) {
            logger.debug("can not create file :" + file + " error:" + e.getMessage());
        }
    }

    public static boolean exists(String path) {
        File file = new File(path);
        return file.exists();
    }

    /**
     * Delete specified directory.
     */
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            if (null == children) {
                return false;
            }
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    logger.error("delete file failed!");
                    return false;
                }
            }
        }
        return repeatDeleteFile(dir);
    }

    /**
     * Save String content as a file.
     */
    public static boolean saveFile(String content, String path) {
        if (content == null || content.isEmpty()) {
            return false;
        }

        boolean result = true;
        byte[] b = content.getBytes();

        File file = new File(path);
        try (FileOutputStream fstream = new FileOutputStream(file);
             BufferedOutputStream stream = new BufferedOutputStream(fstream)) {
            stream.write(b);
        } catch (Exception e) {
            logger.error("save file failed",e);
            result = false;
        }
        return result;
    }

    /**
     * get segment path according to segmentID.
     */
    public static String getSegmentPath(String baseDir, int segId) {
        String firstSegId = Integer.toHexString((segId >> 24) & 0xff);
        String secondSegId = Integer.toHexString((segId >> 16) & 0xff);
        String thirdSegId = Integer.toHexString((segId >> 8) & 0xff);
        String fourthSegId = Integer.toHexString(segId & 0xff);

        if (firstSegId.length() == 1) {
            firstSegId = "0" + firstSegId;
        }
        if (secondSegId.length() == 1) {
            secondSegId = "0" + secondSegId;
        }
        if (thirdSegId.length() == 1) {
            thirdSegId = "0" + thirdSegId;
        }
        if (fourthSegId.length() == 1) {
            fourthSegId = "0" + fourthSegId;
        }
        String segmentPath = baseDir + File.separator + firstSegId + File.separator + secondSegId + File.separator
                + thirdSegId + File.separator + fourthSegId;
        // createFolder(segmentPath);
        return segmentPath;
    }

    public static String getDivisionPath(String baseDir, long divisionId) {
        int segId = (int)(divisionId >> 32);
        return getSegmentPath(baseDir, segId);
    }

    public static boolean reName(String path, String oldname, String newname) {
        if (!oldname.equals(newname)) {
            File oldfile = new File(path + "/" + oldname);
            File newfile = new File(path + "/" + newname);
            if (!oldfile.exists()) {
                return false;
            }
            if (!newfile.exists()) {
                if (!oldfile.renameTo(newfile)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * un zip all files in the folder.
     * @param folder zip files in this folder.
     * @param delGzFile if true, delete zip file after unzip successful.
     * @return true or false.
     */
    public static boolean unGzFilesInFolder(String folder, boolean delGzFile) {
        File gzfilesFolder = new File(folder);
        File[] gzfiles = gzfilesFolder.listFiles();
        for (File file : gzfiles) {
            String absolutePath = file.getAbsolutePath();
            if (!absolutePath.endsWith(".gz")) {
                continue;
            }
            if (unGzFile(absolutePath, folder) != null) {
                if (delGzFile) {
                    if (!repeatDeleteFile(file)) {
                        logger.warn("ungiz file {} successful, but delete it fail.", absolutePath);
                        return false;
                    }
                }
            } else {
                logger.error("ungiz file {} fail.", absolutePath);
                return false;
            }
        }
        return true;
    }

    /**
     * get file name by file path.
     * @param filePath file path.
     * @return file name.
     */
    public static String getFileName(String filePath) {
        if (null == filePath) {
            return null;
        }
        return filePath.substring(filePath.lastIndexOf(File.separator) + 1);
    }
}