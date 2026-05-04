package com.toyproject.backend.domain.pdf.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PdfRedisRequest {

    public PdfRedisRequest( String taskId,String filePath, String originalFileName) {
        this.taskId = taskId;
        this.filePath = filePath;
        this.originalFileName = originalFileName;
    }

    public static PdfRedisRequest to(String taskId,String filePath , String originalFileName){
        return  new PdfRedisRequest(taskId,filePath,originalFileName);
    }

    private String taskId;
    private String filePath;
    private String originalFileName;


}
