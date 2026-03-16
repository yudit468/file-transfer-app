package com.company.filetransfer.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Processes the REST API response after the file transfer notification is sent.
 * Logs the response status and body.
 */
public class ApiResponseProcessor implements Processor {

    private static final Logger log = LoggerFactory.getLogger(ApiResponseProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        String fileName = exchange.getIn().getHeader(FileMetadataProcessor.HEADER_FILE_NAME, String.class);
        Integer responseCode = exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class);
        String responseBody = exchange.getIn().getBody(String.class);

        log.info("API response received for file: {}", fileName);
        log.info("API response code: {}", responseCode);
        log.debug("API response body: {}", responseBody);

        if (responseCode != null && responseCode >= 200 && responseCode < 300) {
            log.info("File transfer notification successful for file: {}", fileName);
        } else {
            log.warn("File transfer notification returned non-success status {} for file: {}", responseCode, fileName);
        }
    }
}
