package org.ai.entity;


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
@Table(name = "clients")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String companyEmail;

    private String companyPhone;

    private String companyActivity;

    private int aiHistory;

    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "ai-instructions", joinColumns = @JoinColumn(name = "client_id"))
    @Column(nullable = false)
    private List<String> aiInstructions;
}
