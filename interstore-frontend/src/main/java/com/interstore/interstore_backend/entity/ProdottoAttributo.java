package com.interstore.interstore_backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "prodotto_attributo")
public class ProdottoAttributo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome; //taglia, colore...
    private String valore; //L, rosso...


    @ManyToOne
    @JoinColumn(name = "prodotto_id")
    private Prodotto prodotto;

    public String getValore() {
        return valore;
    }

    public void setValore(String valore) {
        this.valore = valore;
    }

    public Prodotto getProdotto() {
        return prodotto;
    }

    public void setProdotto(Prodotto prodotto) {
        this.prodotto = prodotto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Long getId() {
        return id;
    }
}
