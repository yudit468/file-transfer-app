package com.company.filetransfer.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Copies the detected file from the source directory to the configured destination directory.
 * Creates the destination directory if it does not exist.
 */
public class FileCopyProcessor implements Processor {

    private static final Logger log = LoggerFactory.getLogger(FileCopyProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        String fileName = exchange.getIn().getHeader(FileMetadataProcessor.HEADER_FILE_NAME, String.class);
        String sourcePath = exchange.getIn().getHeader(FileMetadataProcessor.HEADER_SOURCE_PATH, String.class);
        String destinationPath = exchange.getIn().getHeader(FileMetadataProcessor.HEADER_DESTINATION_PATH, String.class);

        Path sourceFile = Paths.get(sourcePath, fileName);
        Path destinationDir = Paths.get(destinationPath);
        Path destinationFile = destinationDir.resolve(fileName);

        log.info("Copy started - source: {}, destination: {}", sourceFile, destinationFile);

        try {
            if (!Files.exists(destinationDir)) {
                Files.createDirectories(destinationDir);
                log.info("Created destination directory: {}", destinationDir);
            }

            Files.copy(sourceFile, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            log.info("Copy completed - file: {} successfully copied to {}", fileName, destinationFile);
        } catch (IOException e) {
            log.error("Copy failed - file: {}, error: {}", fileName, e.getMessage(), e);
            throw e;
        }
    }
}
