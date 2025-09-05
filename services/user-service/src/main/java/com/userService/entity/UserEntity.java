package com.userService.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable =false,unique = true)
    private String fullName;

    @Column(nullable =false,unique = true)
    private String userName;

    @Column(nullable =false)
    private String password;

    @Column(nullable =false,unique = true)
    private String email;

    @Column(nullable =false)
    private String address;

    @Column
    private String roleName;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}