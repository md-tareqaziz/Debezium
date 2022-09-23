package com.tq.debezium;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
public class User implements Serializable {
    Integer id;
    String name;
    Integer type;
    Double taka;
    Date time;
    Timestamp stamp;
}
