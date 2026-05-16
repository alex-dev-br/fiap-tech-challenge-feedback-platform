package br.com.fiap.techchallenge.feedbackplatform.infrastructure.persistence.entity;

import br.com.fiap.techchallenge.feedbackplatform.domain.enums.Urgencia;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "feedback")
public class FeedbackEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "descricao", nullable = false, length = 2000)
    private String descricao;

    @Column(name = "nota", nullable = false)
    private int nota;

    @Enumerated(EnumType.STRING)
    @Column(name = "urgencia", nullable = false, length = 20)
    private Urgencia urgencia;

    @Column(name = "data_criacao", nullable = false)
    private OffsetDateTime dataCriacao;

    public FeedbackEntity() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getNota() {
        return nota;
    }

    public void setNota(int nota) {
        this.nota = nota;
    }

    public Urgencia getUrgencia() {
        return urgencia;
    }

    public void setUrgencia(Urgencia urgencia) {
        this.urgencia = urgencia;
    }

    public OffsetDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(OffsetDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
}
