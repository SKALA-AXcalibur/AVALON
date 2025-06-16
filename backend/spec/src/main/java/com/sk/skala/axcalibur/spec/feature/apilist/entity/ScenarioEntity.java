// package com.sk.skala.axcalibur.spec.feature.apilist.entity;

// import javax.persistence.Entity;
// import javax.persistence.GeneratedValue;
// import javax.persistence.GenerationType;
// import javax.persistence.Id;
// import javax.persistence.JoinColumn;
// import javax.persistence.ManyToOne;

// import com.sk.skala.axcalibur.global.entity.BaseTimeEntity;
// import com.sk.skala.axcalibur.spec.feature.apilist.entity.ProjectEntity;

// import lombok.Getter;

// @Getter
// @Entity
// public class ScenarioEntity extends BaseTimeEntity {
//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     @Column(name = "`key`")
//     private Integer key;

//     @Column(name = "id", nullable = false, length = 30, unique = true)
//     private String id;

//     @Column(name = "name", nullable = false, length = 100)
//     private String name;

//     @Column(name = "description", nullable = false, columnDefinition = "TEXT")
//     private String description;

//     @Column(name = "validation", nullable = true)
//     private String validation;

//     @Column(name = "flow_chart", nullable = true)
//     private String flowChart;

//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "project_key", nullable = false)
//     private ProjectEntity projectKey;
    
    
// }