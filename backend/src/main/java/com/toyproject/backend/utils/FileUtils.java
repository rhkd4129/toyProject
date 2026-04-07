package com.toyproject.backend.utils;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileUtils {
//    public static String getTransFormImgName(TransformLevel  transformLevel,FileType fileType){
//        return transformLevel.getTransformLevel()+"."+fileType.getExtension();
//    }




    public static void uploadFile(String originalName, byte[] fileData, String path) throws IOException {
        File fileDirectory = new File(path);
        if (!fileDirectory.exists()) {
            fileDirectory.mkdirs();
        }
        File target = new File(path, originalName);
        FileCopyUtils.copy(fileData, target);
    }

    public static Resource getResource(String filePath , String fileName) throws MalformedURLException {
        Path path = Paths.get(filePath, fileName);
        return new UrlResource(path.toUri());
    }



//    public static File[] searchFile(String realPath){
//        File dir = new File(realPath);
//
//        if (!dir.exists() || !dir.isDirectory()) return new File[0];
//
//
//
//    }






}
