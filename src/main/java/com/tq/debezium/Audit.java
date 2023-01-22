package com.tq.debezium;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
@Entity
@Table(name = "audit")
@Getter
@Setter
@NoArgsConstructor
public class Audit implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    @Column(name = "db")
    String db;
    @Column(name = "table_name")
    String table;
    @Column(name = "before")
    String before;
    @Column(name = "after")
    String after;
}
