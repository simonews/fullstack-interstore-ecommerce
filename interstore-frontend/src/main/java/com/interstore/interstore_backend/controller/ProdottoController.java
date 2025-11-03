package com.interstore.interstore_backend.controller;
import com.interstore.interstore_backend.entity.Prodotto;
import com.interstore.interstore_backend.service.ProdottoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/prodotti")
public class ProdottoController {

    private final ProdottoService prodottoService;
    public ProdottoController(ProdottoService prodottoService) {
        this.prodottoService = prodottoService;
    }

    @GetMapping
    public List<Prodotto> getAllProdotti() {
        return prodottoService.getAllProdotti();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Prodotto> getProdottoById(@PathVariable Long id) {
        Optional<Prodotto> prodotto = prodottoService.getProdottoById(id);
        return prodotto.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Prodotto createProdotto(@RequestBody Prodotto prodotto) {
        return prodottoService.saveProdotto(prodotto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Prodotto> updateProdotto(@PathVariable Long id, @RequestBody Prodotto prodottoDetails) {
        Optional<Prodotto> prodotto = prodottoService.getProdottoById(id);
        if (prodotto.isPresent()) {
            Prodotto updatedProdotto = prodotto.get();
            updatedProdotto.setNome(prodottoDetails.getNome());
            updatedProdotto.setPrezzo(prodottoDetails.getPrezzo());
            updatedProdotto.setDescrizione(prodottoDetails.getDescrizione());
            return ResponseEntity.ok(prodottoService.saveProdotto(updatedProdotto));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProdotto(@PathVariable Long id) {
        if (prodottoService.getProdottoById(id).isPresent()) {
            prodottoService.deleteProdotto(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
