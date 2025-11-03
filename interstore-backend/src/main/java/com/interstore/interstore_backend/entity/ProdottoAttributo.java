package com.interstore.interstore_backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "prodotto_attributo")
public class ProdottoAttributo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;   // es "taglia"
    private String valore; // es "L"

    private Integer quantita;

    @ManyToOne
    @JoinColumn(name = "prodotto_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Prodotto prodotto;

    @Version
    private long version;



    // Getter e Setter
    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getValore() {
        return valore;
    }

    public void setValore(String valore) {
        this.valore = valore;
    }

    public Integer getQuantita() {
        return quantita;
    }

    public void setQuantita(Integer quantita) {
        this.quantita = quantita;
    }

    public Prodotto getProdotto() {
        return prodotto;
    }

    public void setProdotto(Prodotto prodotto) {
        this.prodotto = prodotto;
    }
}
