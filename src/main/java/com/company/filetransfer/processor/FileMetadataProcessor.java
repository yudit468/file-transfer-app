package com.company.filetransfer.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

/**
 * Extracts metadata from the detected file and stores it in exchange headers.
 *
 * Metadata extracted:
 * - fileName
 * - sourcePath
 * - destinationPath
 * - timestamp
 */
public class FileMetadataProcessor implements Processor {

    private static final Logger log = LoggerFactory.getLogger(FileMetadataProcessor.class);

    public static final String HEADER_FILE_NAME = "transferFileName";
    public static final String HEADER_SOURCE_PATH = "transferSourcePath";
    public static final String HEADER_DESTINATION_PATH = "transferDestinationPath";
    public static final String HEADER_TIMESTAMP = "transferTimestamp";

    private final String sourcePath;
    private final String destinationPath;

    public FileMetadataProcessor(String sourcePath, String destinationPath) {
        this.sourcePath = sourcePath;
        this.destinationPath = destinationPath;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        String fileName = exchange.getIn().getHeader(Exchange.FILE_NAME, String.class);

        log.info("File detected: {}", fileName);
        log.info("Extracting metadata for file: {}", fileName);

        exchange.getIn().setHeader(HEADER_FILE_NAME, fileName);
        exchange.getIn().setHeader(HEADER_SOURCE_PATH, sourcePath);
        exchange.getIn().setHeader(HEADER_DESTINATION_PATH, destinationPath);
        exchange.getIn().setHeader(HEADER_TIMESTAMP, LocalDateTime.now().toString());

        log.info("Metadata extracted - fileName: {}, sourcePath: {}, destinationPath: {}, timestamp: {}",
                fileName, sourcePath, destinationPath,
                exchange.getIn().getHeader(HEADER_TIMESTAMP));
    }
}
