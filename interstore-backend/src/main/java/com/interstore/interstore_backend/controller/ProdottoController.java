package com.interstore.interstore_backend.controller;

import com.interstore.interstore_backend.entity.Prodotto;
import com.interstore.interstore_backend.entity.ProdottoAttributo;
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
    public ResponseEntity<Prodotto> updateProdotto(@PathVariable Long id,
                                                   @RequestBody Prodotto prodottoDetails) {
        Prodotto updatedProdotto = prodottoService.updateProdotto(id, prodottoDetails);
        return ResponseEntity.ok(updatedProdotto);
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

    @PostMapping("/{id}/compra")
    public ResponseEntity<ProdottoAttributo> compraProdotto(
            @PathVariable Long id,
            @RequestParam String attributoNome,
            @RequestParam String attributoValore,
            @RequestParam int quantita
    ) {
        try {
            Optional<ProdottoAttributo> attr = prodottoService.compraProdotto(id, attributoNome, attributoValore, quantita);
            return attr.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
