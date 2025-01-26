package org.ai.entity;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "private_clients")
@ApplicationScoped
public class PrivateClient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String privateUsername;
    private String privatePassword;
    private String assistantId;

    @OneToOne
    private Client client;
}
