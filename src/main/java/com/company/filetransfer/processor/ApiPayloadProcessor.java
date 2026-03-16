package com.company.filetransfer.processor;

import com.company.filetransfer.model.FileTransferRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

/**
 * Builds the REST API payload from the file metadata stored in exchange headers.
 */
public class ApiPayloadProcessor implements Processor {

    private static final Logger log = LoggerFactory.getLogger(ApiPayloadProcessor.class);

    private final ObjectMapper objectMapper;

    public ApiPayloadProcessor() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        String fileName = exchange.getIn().getHeader(FileMetadataProcessor.HEADER_FILE_NAME, String.class);
        String sourcePath = exchange.getIn().getHeader(FileMetadataProcessor.HEADER_SOURCE_PATH, String.class);
        String destinationPath = exchange.getIn().getHeader(FileMetadataProcessor.HEADER_DESTINATION_PATH, String.class);
        String timestampStr = exchange.getIn().getHeader(FileMetadataProcessor.HEADER_TIMESTAMP, String.class);

        LocalDateTime timestamp = LocalDateTime.parse(timestampStr);

        FileTransferRequest request = new FileTransferRequest(fileName, sourcePath, destinationPath, timestamp);

        String jsonPayload = objectMapper.writeValueAsString(request);

        exchange.getIn().setBody(jsonPayload);
        exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "application/json");
        exchange.getIn().setHeader(Exchange.HTTP_METHOD, "POST");

        log.info("API request payload built for file: {}", fileName);
        log.debug("Payload: {}", jsonPayload);
    }
}
