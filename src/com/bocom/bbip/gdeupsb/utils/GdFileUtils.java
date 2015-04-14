/*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.

package com.bocom.bbip.gdeupsb.utils;

import com.bocom.bbip.utils.StringUtils;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class GdFileUtils
{

    public GdFileUtils()
    {
    }

    public static void copyFile(File srcFile, File dest)
    {
        try
        {
            if(dest.isDirectory())
                org.apache.commons.io.FileUtils.copyFile(srcFile, createFile(dest.getAbsolutePath(), srcFile.getName()));
            else
                org.apache.commons.io.FileUtils.copyFile(srcFile, dest);
        }
        catch(IOException e)
        {
            logger.error((new StringBuilder("Copy file:")).append(srcFile).append("  to file:").append(dest).toString(), e);
            throw new RuntimeException((new StringBuilder("a error happen when try to copy file from:")).append(srcFile).append("  ,to dest file:").append(dest).toString(), e);
        }
    }

    public static File createFile(String path, String name)
    {
        logger.debug((new StringBuilder("try to build a file:")).append(name).append(" in file:").append(path).toString());
        File file = new File(path);
        if(!file.exists() && !file.mkdirs())
            throw new RuntimeException((new StringBuilder("try to delete file :")).append(file.getAbsolutePath()).append(" not success!").toString());
        file = new File(file, name);
        if(file.exists() && !file.delete())
            throw new RuntimeException((new StringBuilder("try to delete file :")).append(file.getAbsolutePath()).append(" not success!").toString());
        try
        {
            file.createNewFile();
        }
        catch(IOException e)
        {
            logger.error((new StringBuilder("A unexpect error happen when to create a new file :")).append(name).append(" in path:").append(path).toString(), e);
            throw new RuntimeException("Unexpect runtime error when create file!", e);
        }
        logger.debug("create file success!");
        return file;
    }

    public static void checkPath(String directory)
    {
        File f = new File(directory);
        if(!f.exists())
            f.mkdirs();
    }

    public static InputStream zip(File files[])
        throws Exception
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        zipOut(files, bos);
        return new ByteArrayInputStream(bos.toByteArray());
    }

    public static void zipOut(File files[], OutputStream os)
        throws Exception
    {
        CheckedOutputStream csum = new CheckedOutputStream(os, new CRC32());
        ZipOutputStream zos = new ZipOutputStream(csum);
        BufferedOutputStream out = new BufferedOutputStream(zos);
        byte b[] = new byte[4096];
        File afile[];
        int k = (afile = files).length;
        for(int j = 0; j < k; j++)
        {
            File f = afile[j];
            zos.putNextEntry(new ZipEntry(f.getName()));
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(f));
            int i;
            while((i = in.read(b, 0, b.length)) != -1) 
            {
                out.write(b, 0, i);
                out.flush();
            }
            in.close();
            zos.closeEntry();
        }

        out.close();
        zos.close();
        csum.close();
    }

    public static InputStream zip(Map map)
        throws Exception
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        zipOut(map, bos);
        return new ByteArrayInputStream(bos.toByteArray());
    }

    public static void zipOut(Map map, OutputStream os)
        throws Exception
    {
        CheckedOutputStream csum = new CheckedOutputStream(os, new CRC32());
        ZipOutputStream zos = new ZipOutputStream(csum);
        BufferedOutputStream out = new BufferedOutputStream(zos);
        byte b[] = new byte[4096];
        for(Iterator iterator = map.keySet().iterator(); iterator.hasNext(); zos.closeEntry())
        {
            String name = (String)iterator.next();
            zos.putNextEntry(new ZipEntry(name));
            BufferedInputStream in = new BufferedInputStream((InputStream)map.get(name));
            int i;
            while((i = in.read(b, 0, b.length)) != -1) 
            {
                out.write(b, 0, i);
                out.flush();
            }
            in.close();
        }

        out.close();
        zos.close();
        csum.close();
    }

    public static List extractZipFile(String zipFile, String destination)
        throws IOException
    {
        ZipFile zip = new ZipFile(zipFile);
        Enumeration en = zip.entries();
        ZipEntry entry = null;
        byte buffer[] = new byte[8192];
        int length = -1;
        InputStream input = null;
        BufferedOutputStream bos = null;
        List extractFiles = new ArrayList();
        while(en.hasMoreElements()) 
        {
            entry = (ZipEntry)en.nextElement();
            if(!entry.isDirectory())
            {
                input = zip.getInputStream(entry);
                File file = new File(destination, entry.getName());
                if(!file.getParentFile().exists())
                    file.getParentFile().mkdirs();
                bos = new BufferedOutputStream(new FileOutputStream(file));
                do
                {
                    length = input.read(buffer);
                    if(length == -1)
                        break;
                    bos.write(buffer, 0, length);
                } while(true);
                bos.close();
                input.close();
                extractFiles.add(file);
            }
        }
        zip.close();
        return extractFiles;
    }

    public static void extractGZIPFile(String from, String to)
        throws Exception
    {
        GZIPInputStream zis = new GZIPInputStream(new FileInputStream(from));
        FileOutputStream fos = new FileOutputStream(to);
        for(int i = -1; (i = zis.read()) != -1;)
            fos.write(i);

        fos.flush();
        zis.close();
        fos.close();
    }

    public static void write(File file, String data)
        throws IOException
    {
        org.apache.commons.io.FileUtils.writeStringToFile(file, data);
    }

    public static void write(File file, String data, String encoding)
        throws IOException
    {
        org.apache.commons.io.FileUtils.writeStringToFile(file, data, encoding);
    }

    public static void writeStringToFile(File file, String data)
        throws IOException
    {
        org.apache.commons.io.FileUtils.writeStringToFile(file, data);
    }

    public static void writeStringToFile(File file, String data, String encoding)
        throws IOException
    {
        org.apache.commons.io.FileUtils.writeStringToFile(file, data, encoding);
    }

    public static List readLines(File file)
        throws IOException
    {
        return org.apache.commons.io.FileUtils.readLines(file);
    }

    public static List readLines(File file, String encoding)
        throws IOException
    {
        return org.apache.commons.io.FileUtils.readLines(file, encoding);
    }

    public static boolean deleteQuietly(File file)
    {
        return org.apache.commons.io.FileUtils.deleteQuietly(file);
    }

    public static String buildAbstractPath(String path, String fileName)
    {
        path = StringUtils.trimToEmpty(path);
        fileName = StringUtils.trimToEmpty(fileName);
        if(path.endsWith("/"))
            return (new StringBuilder(String.valueOf(path))).append(fileName).toString();
        else
            return (new StringBuilder(String.valueOf(path))).append("/").append(fileName).toString();
    }

    public static List list(String path, FilenameFilter filter)
    {
        File dir = new File(path);
        String fiels[] = dir.list(filter);
        if(fiels == null)
            return null;
        List fileList = new ArrayList();
        String as[];
        int j = (as = fiels).length;
        for(int i = 0; i < j; i++)
        {
            String s = as[i];
            fileList.add((new StringBuilder(path)).append(s).toString());
        }

        return fileList;
    }

    private static final Log logger = LogFactory.getLog(GdFileUtils.class);
    private static final String SLASH_CHAR = "/";

}


/*
	DECOMPILATION REPORT

	Decompiled from: D:\.m2\repository\com\bocom\bbip\bbip-comp-cpos\1.1.0-SNAPSHOT\bbip-comp-cpos-1.1.0-SNAPSHOT.jar
	Total time: 16 ms
	Jad reported messages/errors:
	Exit status: 0
	Caught exceptions:
*/