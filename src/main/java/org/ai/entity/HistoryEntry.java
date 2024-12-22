package org.ai.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@ApplicationScoped
@Entity
@Table(name = "history")
public class HistoryEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String prompt;

    @Column(length = 1000)
    private String answer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    private Client client;
}
