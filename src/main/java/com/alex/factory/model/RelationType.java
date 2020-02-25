package com.alex.factory.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class RelationType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "companyDescription_id")
    private CompanyDescription companyDescription;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "factoryDescription_id")
    private FactoryDescription factoryDescription;
}
