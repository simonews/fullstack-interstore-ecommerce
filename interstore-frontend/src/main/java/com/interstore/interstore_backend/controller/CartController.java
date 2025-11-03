package com.interstore.interstore_backend.controller;

import com.interstore.interstore_backend.dto.CartResponse;
import com.interstore.interstore_backend.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<CartResponse> getCartByUserId(@PathVariable Long userId) {
        Optional<CartResponse> cart = cartService.getCartResponseByUserId(userId);
        return cart.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CartResponse> saveCart(@RequestBody CartResponse cartDto) {
        CartResponse saved = cartService.saveCart(cartDto);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<Void> clearCart(@PathVariable Long cartId) {
        cartService.clearCart(cartId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/add")
    public ResponseEntity<CartResponse> addProductToCart(
            @PathVariable Long userId,
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") int quantity) {

        CartResponse updatedCart = cartService.addProductToCart(userId, productId, quantity);
        return ResponseEntity.ok(updatedCart);
    }

    @DeleteMapping("/{userId}/remove/{productId}")
    public ResponseEntity<CartResponse> removeProductFromCart(
            @PathVariable Long userId,
            @PathVariable Long productId) {

        CartResponse updatedCart = cartService.removeProductFromCart(userId, productId);
        return ResponseEntity.ok(updatedCart);
    }

    @PutMapping("/{userId}/update/{productId}")
    public ResponseEntity<CartResponse> updateProductQuantity(
            @PathVariable Long userId,
            @PathVariable Long productId,
            @RequestParam int quantity) {

        CartResponse updatedCart = cartService.updateProductQuantity(userId, productId, quantity);
        return ResponseEntity.ok(updatedCart);
    }
}
