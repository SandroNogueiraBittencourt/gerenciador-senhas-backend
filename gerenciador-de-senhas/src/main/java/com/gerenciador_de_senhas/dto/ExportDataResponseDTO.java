package com.gerenciador_de_senhas.dto;

public class ExportDataResponseDTO {

    private String fileName;
    private String content;

    public ExportDataResponseDTO() {
    }

    public ExportDataResponseDTO(String fileName, String content) {
        this.fileName = fileName;
        this.content = content;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
