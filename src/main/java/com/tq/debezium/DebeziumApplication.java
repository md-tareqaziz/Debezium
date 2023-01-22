package com.tq.debezium;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.kafka.connect.storage.FileOffsetBackingStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootApplication
public class DebeziumApplication {
    @Autowired
    MyRouter myRouter;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(DebeziumApplication.class, args);
    }

    @Bean
    public void debezium() throws Exception {
        CamelContext camelContext = new DefaultCamelContext();
//        camelContext.addRoutes(new MyRouter(kafkaTemplate));
        camelContext.addRoutes(myRouter);
        // Start the routes
        camelContext.start();
    }

}
