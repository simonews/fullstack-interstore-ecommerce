package com.interstore.interstore_backend.entity;


import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "prodotto")
public class Prodotto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String descrizione;
    private BigDecimal prezzo;
    private String tipo; //maglia, pantaloncino, sciarpa...

    @OneToMany(mappedBy = "prodotto", cascade = CascadeType.ALL, orphanRemoval = true,  fetch = FetchType.EAGER)
    private List<ProdottoAttributo> attributi;

    @ElementCollection
    @CollectionTable(name = "prodotto_immagini", joinColumns = @JoinColumn(name = "prodotto_id"))
    @Column(name = "url")
    private List<String> immagini;

    public List<String> getImmagini() {
        return immagini;
    }

    public void setImmagini(List<String> immagini) {
        this.immagini = immagini;
    }

    public List<ProdottoAttributo> getAttributi() {
        return attributi;
    }

    public void setAttributi(List<ProdottoAttributo> attributi) {
        this.attributi = attributi;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(BigDecimal prezzo) {
        this.prezzo = prezzo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
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
