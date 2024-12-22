package org.ai.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.security.jpa.Password;
import io.quarkus.security.jpa.Roles;
import io.quarkus.security.jpa.UserDefinition;
import io.quarkus.security.jpa.Username;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ApplicationScoped
@Entity
@Table(name = "clients", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@UserDefinition
public class Client {

    @Username
    private String username;
    @Password
    private String password;
    @Roles
    private String role;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String companyEmail;

    private String companyPhone;

    private String companyActivity;

    private String aiTitleMessage;

    private int aiHistory;

    private int promptsSent;

    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "ai-instructions", joinColumns = @JoinColumn(name = "client_id"))
    @Column(nullable = false, length = 1000)
    @JsonIgnore
    private List<String> aiInstructions;

    @OneToMany(mappedBy = "client",
            cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonIgnore
    private List<HistoryEntry> historyEntries;
}
