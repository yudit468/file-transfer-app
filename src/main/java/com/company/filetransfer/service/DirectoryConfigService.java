package com.company.filetransfer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Service that reads directory mapping configuration from application properties.
 * Supports N number of source-to-destination directory mappings.
 */
@Service
@ConfigurationProperties(prefix = "file")
public class DirectoryConfigService {

    private static final Logger log = LoggerFactory.getLogger(DirectoryConfigService.class);

    private List<RouteMapping> routes = new ArrayList<>();

    @PostConstruct
    public void init() {
        log.info("Loaded {} directory route mapping(s)", routes.size());
        for (RouteMapping mapping : routes) {
            log.info("  Source: {} -> Destination: {}", mapping.getSource(), mapping.getDestination());
        }
    }

    public List<RouteMapping> getRoutes() {
        return Collections.unmodifiableList(routes);
    }

    public void setRoutes(List<RouteMapping> routes) {
        this.routes = routes;
    }

    /**
     * Represents a single source-to-destination directory mapping.
     */
    public static class RouteMapping {

        private String source;
        private String destination;

        public RouteMapping() {
        }

        public RouteMapping(String source, String destination) {
            this.source = source;
            this.destination = destination;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getDestination() {
            return destination;
        }

        public void setDestination(String destination) {
            this.destination = destination;
        }

        @Override
        public String toString() {
            return "RouteMapping{source='" + source + "', destination='" + destination + "'}";
        }
    }
}
