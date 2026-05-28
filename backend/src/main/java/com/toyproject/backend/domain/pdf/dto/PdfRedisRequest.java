package com.toyproject.backend.domain.pdf.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PdfRedisRequest {

    public PdfRedisRequest(String taskId, String filePath, String originalFileName,String pdfType) {
        this.taskId = taskId;
        this.filePath = filePath;
        this.originalFileName = originalFileName;
        this.pdfType = pdfType;
    }

    public static PdfRedisRequest to(String taskId,String filePath , String originalFileName,String pdfType){
        return  new PdfRedisRequest(taskId,filePath,originalFileName, pdfType);
    }

    private String taskId;
    private String pdfType;
    private String filePath;
    private String originalFileName;


}
