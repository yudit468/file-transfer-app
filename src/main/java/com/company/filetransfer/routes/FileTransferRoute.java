package com.company.filetransfer.routes;

import com.company.filetransfer.processor.ApiPayloadProcessor;
import com.company.filetransfer.processor.ApiResponseProcessor;
import com.company.filetransfer.processor.FileCopyProcessor;
import com.company.filetransfer.processor.FileMetadataProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Apache Camel route that monitors a source directory for new files
 * and processes them through the file transfer pipeline.
 *
 * Processing flow:
 * 1. Detect file from source directory
 * 2. Extract file metadata
 * 3. Copy file to destination directory
 * 4. Build REST API payload
 * 5. Send POST request to configured API endpoint
 * 6. Process API response
 */
public class FileTransferRoute extends RouteBuilder {

    private static final Logger log = LoggerFactory.getLogger(FileTransferRoute.class);

    private final String routeId;
    private final String sourcePath;
    private final String destinationPath;
    private final String apiUrl;
    private final String errorDirectory;

    public FileTransferRoute(String routeId, String sourcePath, String destinationPath,
                             String apiUrl, String errorDirectory) {
        this.routeId = routeId;
        this.sourcePath = sourcePath;
        this.destinationPath = destinationPath;
        this.apiUrl = apiUrl;
        this.errorDirectory = errorDirectory;
    }

    @Override
    public void configure() throws Exception {
        log.info("Configuring route '{}': source={}, destination={}, apiUrl={}",
                routeId, sourcePath, destinationPath, apiUrl);

        onException(Exception.class)
                .handled(true)
                .log("Error processing file ${file:name}: ${exception.message}")
                .to("file:" + errorDirectory + "?fileName=${file:name}")
                .log("File ${file:name} moved to error directory: " + errorDirectory);

        from("file:" + sourcePath + "?include=.*&noop=true")
                .routeId(routeId)
                .log("File detected in source directory: ${file:name}")
                .process(new FileMetadataProcessor(sourcePath, destinationPath))
                .process(new FileCopyProcessor())
                .process(new ApiPayloadProcessor())
                .log("API request sent for file: ${file:name}")
                .to(apiUrl)
                .process(new ApiResponseProcessor())
                .log("File transfer completed: ${file:name}");
    }
}
