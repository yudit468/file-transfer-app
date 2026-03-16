package com.company.filetransfer.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Model representing the file transfer REST API payload.
 */
public class FileTransferRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String fileName;
    private String sourcePath;
    private String destinationPath;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    public FileTransferRequest() {
    }

    public FileTransferRequest(String fileName, String sourcePath, String destinationPath, LocalDateTime timestamp) {
        this.fileName = fileName;
        this.sourcePath = sourcePath;
        this.destinationPath = destinationPath;
        this.timestamp = timestamp;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public String getDestinationPath() {
        return destinationPath;
    }

    public void setDestinationPath(String destinationPath) {
        this.destinationPath = destinationPath;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "FileTransferRequest{" +
                "fileName='" + fileName + '\'' +
                ", sourcePath='" + sourcePath + '\'' +
                ", destinationPath='" + destinationPath + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
