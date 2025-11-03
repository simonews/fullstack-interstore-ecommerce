package com.interstore.interstore_backend.service;

import com.interstore.interstore_backend.dto.CartItemResponse;
import com.interstore.interstore_backend.dto.CartResponse;
import com.interstore.interstore_backend.entity.Cart;
import com.interstore.interstore_backend.entity.CartItem;
import com.interstore.interstore_backend.entity.Prodotto;
import com.interstore.interstore_backend.repository.CartRepository;
import com.interstore.interstore_backend.repository.CartItemRepository;
import com.interstore.interstore_backend.repository.ProdottoRepository;
import com.interstore.interstore_backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProdottoRepository productRepository;
    private final UserRepository userRepository;

    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       ProdottoRepository productRepository,
                       UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public Optional<CartResponse> getCartResponseByUserId(Long userId) {
        return cartRepository.findByUserId(userId).map(this::toResponse);
    }

    public CartResponse saveCart(CartResponse cartDto) {
        Cart cart = new Cart();
        cart.setUser(userRepository.findById(cartDto.getUserId())
                .orElseThrow(() -> new RuntimeException("Utente non trovato")));
        Cart saved = cartRepository.save(cart);
        return toResponse(saved);
    }

    @Transactional
    public void clearCart(Long cartId) {
        cartItemRepository.deleteByCartId(cartId);
    }

    @Transactional
    public CartResponse addProductToCart(Long userId, Long productId, int quantity) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(userRepository.findById(userId).orElseThrow());
                    return cartRepository.save(newCart);
                });

        if (cart.getItems() == null) {
            cart.setItems(new ArrayList<>());
        }

        Prodotto product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Prodotto non trovato"));

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProdotto().getId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + quantity);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProdotto(product);
            newItem.setQuantity(quantity);
            cart.getItems().add(newItem);
        }

        Cart savedCart = cartRepository.save(cart);
        return toResponse(savedCart);
    }

    @Transactional
    public CartResponse removeProductFromCart(Long userId, Long productId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Carrello non trovato"));

        cart.getItems().removeIf(item -> item.getProdotto().getId().equals(productId));

        return toResponse(cartRepository.save(cart));
    }

    @Transactional
    public CartResponse updateProductQuantity(Long userId, Long productId, int quantity) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Carrello non trovato"));

        cart.getItems().forEach(item -> {
            if (item.getProdotto().getId().equals(productId)) {
                item.setQuantity(quantity);
            }
        });

        return toResponse(cartRepository.save(cart));
    }

    private CartResponse toResponse(Cart cart) {
        CartResponse dto = new CartResponse();
        dto.setId(cart.getId());
        dto.setUserId(cart.getUser().getId());

        List<CartItemResponse> items = cart.getItems().stream().map(item -> {
            CartItemResponse cir = new CartItemResponse();
            cir.setId(item.getId());
            cir.setProductId(item.getProdotto().getId());
            cir.setProductName(item.getProdotto().getNome());
            cir.setQuantity(item.getQuantity());
            return cir;
        }).toList();

        dto.setItems(items);
        return dto;
    }
}
