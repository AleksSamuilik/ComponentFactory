package com.alex.factory.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class UserDescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
    @OneToOne(optional = false, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = " relationType_id", nullable = false)
    private RelationType relationType;
}
