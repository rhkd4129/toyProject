package com.toyproject.backend.domain.pdf.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PdfRedisRequest {

    public PdfRedisRequest(String taskId, String inputPath,String pdfType) {
        this.taskId = taskId;
        this.inputPath = inputPath;
        this.pdfType = pdfType;
    }

    public static PdfRedisRequest to(String taskId,String inputPath ,String pdfType){
        return  new PdfRedisRequest(taskId,inputPath, pdfType);
    }

    private String taskId;
    private String pdfType;
    private String inputPath;
//    private String presignedDownloadUrl;
//    private String presignedUploadUrl;
//    private String resultDownloadUrl;


}
