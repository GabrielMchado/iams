package com.gabrielmachado.iams.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Data
@Entity(name = "users")
public class UserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwd;
}
