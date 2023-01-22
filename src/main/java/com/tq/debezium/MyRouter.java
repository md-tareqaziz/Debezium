package com.tq.debezium;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import org.apache.camel.Configuration;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spi.PropertiesComponent;
import org.apache.kafka.connect.data.Struct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.sql.Date;
import java.util.Map;

@Service
@Configurable
@Configuration
@NoArgsConstructor
public class MyRouter extends RouteBuilder {
    @Autowired
    AuditRepository auditRepository;
    private KafkaTemplate<String, String> kafkaTemplate;
    private static final String TOPIC = "quickstart-events";

    public MyRouter(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Autowired
    public MyRouter(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    @Override
    public void configure() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        PropertiesComponent pc = this.getContext().getPropertiesComponent();
        pc.setLocation("classpath:application.properties");

        from("debezium-postgres:my_postgress_endpoint?"
                + "databaseHostname={{database.hostname}}"
                + "&databasePort={{database.port}}"
                + "&databaseUser={{database.user}}"
                + "&databasePassword={{database.password}}"
                + "&databaseDbname={{database.dbname}}"
                + "&databaseServerName=postgress_server"
                + "&schemaWhitelist={{database.schema}}"
//                + "&tableWhitelist={{database.schema}}.{{database.table}}"
                + "&tableIncludeList={{database.schema}}.{{database.table}}"
                + "&offsetStorageFileName=D:\\offset.dat"
//                + "&offsetStorage=org.apache.kafka.connect.storage.FileOffsetBackingStore"
//                + "&offsetStorageTopic="+TOPIC
                + "&offsetFlushIntervalMs=1000"
                + "&pluginName=pgoutput")
                .log("---------------Event Log---------------")
//                .log("Event received from Debezium : ${body}")
//                .log("with this identifier ${headers.CamelDebeziumIdentifier}")
//                .log("with these source metadata ${headers.CamelDebeziumSourceMetadata}")
//                .log("the event occured upon this operation '${headers.CamelDebeziumOperation}'")
//                .log("on this database '${headers.CamelDebeziumSourceMetadata[db]}' and this table '${headers.CamelDebeziumSourceMetadata[table]}'")
//                .log("with the key ${headers.CamelDebeziumKey}")
//                .log("the previous value is ${headers.CamelDebeziumBefore}")
                .log("\"{\"server\":\"${headers.CamelDebeziumSourceMetadata[name]}\"," +
                        "\"db\":\"${headers.CamelDebeziumSourceMetadata[db]}\"," +
                        "\"schema\":\"${headers.CamelDebeziumSourceMetadata[schema]}\"," +
                        "\"table\":\"${headers.CamelDebeziumSourceMetadata[table]}\"," +
                        "\"operation\":\"${headers.CamelDebeziumOperation}\"," +
                        "\"time\":\"${headers.CamelDebeziumTimestamp}\"," +
                        "\"before\":\"${headers.CamelDebeziumBefore}\"," +
                        "\"after\":\"${headers.CamelDebeziumAfter}\"" +
                        "}\"")
                .process(exchange -> {
                    Map<String, Object> map = exchange.getMessage().getHeaders();
                    Map<String, Object> metaData = (Map<String, Object>) map.get("CamelDebeziumSourceMetadata");

                    Long time = (Long) map.get("CamelDebeziumTimestamp");
                    String db = (String) metaData.get("db");
                    String scheme = (String) metaData.get("schema");
                    String table = (String) metaData.get("table");
                    String operation = (String) map.get("CamelDebeziumOperation");
                    Struct after = exchange.getIn().getBody(Struct.class);
                    Struct before = (Struct) map.get("CamelDebeziumBefore");
                    Struct bodyValue = exchange.getIn().getBody(Struct.class);

                    Audit audit = new Audit();
                    audit.setDb(db);
                    audit.setTable(db);
                    audit.setBefore(before.toString());
                    audit.setAfter(after.toString());

                    auditRepository.save(audit);

                    String json = "{";
                    for (int i = 0; i < User.class.getDeclaredFields().length; i++) {
                        Field field = User.class.getDeclaredFields()[i];
                        String name = field.getName();
                        Object value = bodyValue.get(field.getName());
                        if (field.getType().equals(String.class) || field.getType().equals(Date.class)) {
                            json += String.format("\"%s\":\"%s\"", name, value);
                        } else {
                            json += String.format("\"%s\":%s", name, value);
                        }
                        json += (i != User.class.getDeclaredFields().length - 1) ? "," : "}";
                    }

//                    kafkaTemplate.send(TOPIC, json);
//                    BeanUtils.copyProperties(bodyValue,user);

                    log.info("Body value is :" + json);
//                    log.info("And fields:" + bodyValue.getStruct("type"));
                    log.info("And fields:" + bodyValue);
//                    log.info("And fields of :" + schemaValue.doc());
//                    log.info("And fields of :" + schemaValue.parameters());
//                    log.info("Field name has `" + schemaValue.field("name").schema() + "` type");
                });
    }

}