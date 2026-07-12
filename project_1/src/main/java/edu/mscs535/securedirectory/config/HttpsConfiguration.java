package edu.mscs535.securedirectory.config;

import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpsConfiguration {

    @Bean
    WebServerFactoryCustomizer<TomcatServletWebServerFactory> httpRedirectConnector(
            @Value("${app.http-port:8080}") int httpPort) {
        return factory -> factory.addAdditionalTomcatConnectors(httpConnector(httpPort));
    }

    private Connector httpConnector(int port) {
        Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
        connector.setScheme("http");
        connector.setPort(port);
        connector.setSecure(false);
        return connector;
    }
}
