package com.interstore.interstore_backend.repository;

import com.interstore.interstore_backend.entity.Prodotto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdottoRepository extends JpaRepository<Prodotto, Long> {
}
