package com.toyproject.backend.domain.pdf.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PdfRedisRequest {

    public PdfRedisRequest( String filePath, String fileName) {
//        this.taskId = taskId;
        this.filePath = filePath;
        this.fileName = fileName;
    }

    public static PdfRedisRequest to(String taskId,String filePath , String fileName){
        return  new PdfRedisRequest(filePath,fileName);
    }

//    private String taskId;
    private String filePath;
    private String fileName;


}
