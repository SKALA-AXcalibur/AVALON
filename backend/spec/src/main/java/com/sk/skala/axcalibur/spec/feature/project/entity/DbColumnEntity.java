package com.sk.skala.axcalibur.spec.feature.project.entity;

import com.sk.skala.axcalibur.spec.global.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "db_column")
public class DbColumnEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`key`")
    private Integer key;

    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "type", nullable = false, length = 20)
    private String type;

    @Column(name = "length")
    private Integer length;

    @Column(name = "is_pk", nullable = false)
    private Boolean isPk;

    @Column(name = "fk", length = 30)
    private String fk;

    @Column(name = "is_null", nullable = false)
    private Boolean isNull;

    @Column(name = "constraint", length = 255)
    private String constraint;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "db_design_key", nullable = false)
    private DbDesignEntity dbDesignKey;
    

}
