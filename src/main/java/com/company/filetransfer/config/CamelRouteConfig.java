package com.company.filetransfer.config;

import com.company.filetransfer.routes.FileTransferRoute;
import com.company.filetransfer.service.DirectoryConfigService;
import com.company.filetransfer.service.DirectoryConfigService.RouteMapping;
import org.apache.camel.CamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Configuration class that dynamically creates Apache Camel routes
 * based on the directory mappings defined in application properties.
 *
 * Each source-destination mapping results in a separate Camel route
 * that monitors the source directory and processes files independently.
 */
@Configuration
public class CamelRouteConfig {

    private static final Logger log = LoggerFactory.getLogger(CamelRouteConfig.class);

    private final CamelContext camelContext;
    private final DirectoryConfigService directoryConfigService;

    @Value("${api.endpoint.url}")
    private String apiEndpointUrl;

    @Value("${file.error.directory:/data/error}")
    private String errorDirectory;

    public CamelRouteConfig(CamelContext camelContext, DirectoryConfigService directoryConfigService) {
        this.camelContext = camelContext;
        this.directoryConfigService = directoryConfigService;
    }

    @PostConstruct
    public void configureRoutes() throws Exception {
        List<RouteMapping> routes = directoryConfigService.getRoutes();

        if (routes.isEmpty()) {
            log.warn("No directory route mappings configured. No Camel routes will be created.");
            return;
        }

        log.info("Creating {} dynamic Camel route(s)...", routes.size());

        for (int i = 0; i < routes.size(); i++) {
            RouteMapping mapping = routes.get(i);
            String routeId = "file-transfer-route-" + (i + 1);

            log.info("Creating route '{}': {} -> {}", routeId, mapping.getSource(), mapping.getDestination());

            FileTransferRoute route = new FileTransferRoute(
                    routeId,
                    mapping.getSource(),
                    mapping.getDestination(),
                    apiEndpointUrl,
                    errorDirectory
            );

            camelContext.addRoutes(route);
            log.info("Route '{}' created successfully", routeId);
        }

        log.info("All dynamic Camel routes configured successfully");
    }
}
