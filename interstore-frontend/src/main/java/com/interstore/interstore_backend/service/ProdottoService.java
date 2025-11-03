package com.interstore.interstore_backend.service;

import com.interstore.interstore_backend.entity.Prodotto;
import com.interstore.interstore_backend.repository.ProdottoRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProdottoService {

    private final ProdottoRepository prodottoRepository;

    public ProdottoService(ProdottoRepository prodottoRepository) {
        this.prodottoRepository = prodottoRepository;
    }

    public List<Prodotto> getAllProdotti() {
        return prodottoRepository.findAll();
    }

    public Optional<Prodotto> getProdottoById(Long id) {
        return prodottoRepository.findById(id);
    }

    public Prodotto saveProdotto(Prodotto prodotto) {
        return prodottoRepository.save(prodotto);
    }

    public void deleteProdotto(Long id) {
        prodottoRepository.deleteById(id);
    }
}

