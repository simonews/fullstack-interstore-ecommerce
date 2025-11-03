package com.interstore.interstore_backend.repository;

import com.interstore.interstore_backend.entity.Ordine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Ordine, Long> {
}

