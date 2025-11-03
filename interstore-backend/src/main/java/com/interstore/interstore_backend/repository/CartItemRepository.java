package com.interstore.interstore_backend.repository;


import com.interstore.interstore_backend.entity.CartItem;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @Transactional
    void deleteByProdottoId(Long prodottoId);

    @Transactional
    void deleteByCartId(Long cartId);

    List<CartItem> findByCartId(Long cartId);
}

