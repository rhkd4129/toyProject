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
import java.util.UUID;

public class FileUtils {
//    public static String getTransFormImgName(TransformLevel  transformLevel,FileType fileType){
//        return transformLevel.getTransformLevel()+"."+fileType.getExtension();
//    }

//    public static void uploadFile(String fileName, byte[] fileData, String path) throws IOException {
//        File fileDirectory = new File(path);
//        if (!fileDirectory.exists()) {
//            fileDirectory.mkdirs();
//        }
//        File target = new File(path, fileName);
//        FileCopyUtils.copy(fileData, target);
//    }

//    public static String buildFileName(String taskId, String originalFilename) {
//        int dotIndex = originalFilename.lastIndexOf(".");
//        String baseName = originalFilename.substring(0, dotIndex);  // "20260331133042"
//        String ext = originalFilename.substring(dotIndex);           // ".pdf"
//        return baseName + "_" + taskId + ext;  // "20260331133042_73809edf-....pdf"
//    }
//
    public static String buildFileName(String taskId, String originalFilename){
        String ext = originalFilename.substring(originalFilename.lastIndexOf(".")); // ".pdf"
        return  taskId + ext;

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
