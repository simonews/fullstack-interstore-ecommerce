package com.interstore.interstore_backend.service;

import com.interstore.interstore_backend.entity.Prodotto;
import com.interstore.interstore_backend.entity.ProdottoAttributo;
import com.interstore.interstore_backend.repository.CartItemRepository;
import com.interstore.interstore_backend.repository.ProdottoRepository;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProdottoService {

    private final ProdottoRepository prodottoRepository;
    private final CartItemRepository cartItemRepository;

    public ProdottoService(ProdottoRepository prodottoRepository, CartItemRepository cartItemRepository) {
        this.prodottoRepository = prodottoRepository;
        this.cartItemRepository = cartItemRepository;
    }

    public List<Prodotto> getAllProdotti() {
        return prodottoRepository.findAll();
    }

    public Optional<Prodotto> getProdottoById(Long id) {
        return prodottoRepository.findById(id);
    }

    public Prodotto saveProdotto(Prodotto prodotto) {
        if (prodotto.getAttributi() != null) {
            for (ProdottoAttributo att : prodotto.getAttributi()) {
                att.setProdotto(prodotto); //collego l'attributo al prodotto
            }
        }
        return prodottoRepository.save(prodotto);
    }


    @Transactional
    public void deleteProdotto(Long id) {
        // rimuov riferimenti dal carrello
        cartItemRepository.deleteByProdottoId(id);
        prodottoRepository.deleteById(id);
    }

        @Transactional
        public Prodotto updateProdotto(Long id, Prodotto prodottoDetails) {
            Prodotto prodottoEsistente = prodottoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Prodotto non trovato"));

            //Aggiorna campi base
            prodottoEsistente.setNome(prodottoDetails.getNome());
            prodottoEsistente.setDescrizione(prodottoDetails.getDescrizione());
            prodottoEsistente.setPrezzo(prodottoDetails.getPrezzo());
            prodottoEsistente.setImmagini(prodottoDetails.getImmagini());

            //Aggiorna attributi
            Map<Long, ProdottoAttributo> mappaEsistenti = prodottoEsistente.getAttributi().stream()
                    .collect(Collectors.toMap(ProdottoAttributo::getId, a -> a));

            List<ProdottoAttributo> nuoviAttributi = new ArrayList<>();
            for (ProdottoAttributo attDettagli : prodottoDetails.getAttributi()) {
                if (attDettagli.getId() != null && mappaEsistenti.containsKey(attDettagli.getId())) {
                    // Attributo già esistente: aggiorno mantenendo la versione
                    ProdottoAttributo attEsistente = mappaEsistenti.get(attDettagli.getId());
                    attEsistente.setNome(attDettagli.getNome());
                    attEsistente.setValore(attDettagli.getValore());
                    attEsistente.setQuantita(attDettagli.getQuantita());
                    nuoviAttributi.add(attEsistente);
                } else {
                    //Nuovo attributo: assegno il prodotto e aggiungo
                    attDettagli.setProdotto(prodottoEsistente);
                    nuoviAttributi.add(attDettagli);
                }
            }

            // ostituisco la lista preservando le versioni corrette
            prodottoEsistente.getAttributi().clear();
            prodottoEsistente.getAttributi().addAll(nuoviAttributi);

            return prodottoRepository.save(prodottoEsistente);
        }


    public Optional<ProdottoAttributo> compraProdotto(Long prodottoId, String attributoNome, String attributoValore, int quantita) {
        try {
            Optional<Prodotto> prodottoOpt = prodottoRepository.findById(prodottoId);

            if (prodottoOpt.isPresent()) {
                Prodotto prodotto = prodottoOpt.get();

                for (ProdottoAttributo attr : prodotto.getAttributi()) {
                    if (attr.getNome().equalsIgnoreCase(attributoNome) &&
                            attr.getValore().equalsIgnoreCase(attributoValore)) {

                        if (attr.getQuantita() >= quantita) {
                            attr.setQuantita(attr.getQuantita() - quantita);
                            prodottoRepository.save(prodotto); // aggiorna DB
                            return Optional.of(attr);
                        } else {
                            throw new RuntimeException("Quantità non disponibile per questo attributo");
                        }
                    }
                }
            }
            return Optional.empty();
        }catch(OptimisticLockException e) {
            throw new RuntimeException("Quantità aggiornata da un altro utente, riprova l'acquisto.");
        }
    }
}

