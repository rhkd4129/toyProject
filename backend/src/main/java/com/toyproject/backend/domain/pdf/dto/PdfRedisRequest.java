package com.toyproject.backend.domain.pdf.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PdfRedisRequest {

    public PdfRedisRequest(String taskId, String filePath,String pdfType) {
        this.taskId = taskId;
        this.filePath = filePath;
        this.pdfType = pdfType;
    }

    public static PdfRedisRequest to(String taskId,String filePath ,String pdfType){
        return  new PdfRedisRequest(taskId,filePath, pdfType);
    }

    private String taskId;
    private String pdfType;
    private String filePath;



}
