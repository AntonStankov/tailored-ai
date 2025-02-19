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

    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "thread_ids", joinColumns = @JoinColumn(name = "private_client_id"))
    @Column(nullable = false, length = 1000)
    private List<String> threadIds;

    @OneToOne
    private Client client;
}
