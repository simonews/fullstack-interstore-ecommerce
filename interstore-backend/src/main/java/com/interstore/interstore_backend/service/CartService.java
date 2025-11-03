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

import java.util.*;

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
    public void clearCartByUserId(Long userId) {
        Optional<Cart> cartOpt = cartRepository.findByUserId(userId);
        cartOpt.ifPresent(cart -> {
            cartItemRepository.deleteByCartId(cart.getId());
        });
    }

    @Transactional
    public CartResponse addProductToCart(Long userId, Long productId, int quantity, String attributeName, String attributeValue) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(userRepository.findById(userId).orElseThrow());
                    return cartRepository.save(newCart);
                });

        if (cart.getItems() == null) cart.setItems(new ArrayList<>());

        Prodotto product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Prodotto non trovato"));

        //controllo la disponibilità
        int stockDisponibile = product.getAttributi() != null && !product.getAttributi().isEmpty()
                ? product.getAttributi().stream().mapToInt(a -> a.getQuantita() != null ? a.getQuantita() : 0).sum()
                : 9999; //fallback se non ha attributi

        int quantitaAttuale = cart.getItems().stream()
                .filter(i -> i.getProdotto().getId().equals(productId))
                .mapToInt(CartItem::getQuantity)
                .sum();

        if (quantitaAttuale + quantity > stockDisponibile) {
            throw new RuntimeException("Quantità massima raggiunta per questo prodotto");
        }

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProdotto().getId().equals(productId) &&
                        Objects.equals(item.getAttributeName(), attributeName) &&
                        Objects.equals(item.getAttributeValue(), attributeValue)
                )
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + quantity);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProdotto(product);
            newItem.setQuantity(quantity);
            newItem.setAttributeName(attributeName);
            newItem.setAttributeValue(attributeValue);
            cart.getItems().add(newItem);
        }

        Cart savedCart = cartRepository.save(cart);
        return toResponse(savedCart);
    }


    @Transactional
    public CartResponse removeProductFromCart(Long userId, Long productId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Carrello non trovato"));

        // rimuovo tutte le righe con quel productId
        cart.getItems().removeIf(item -> item.getProdotto() != null && item.getProdotto().getId().equals(productId));

        return toResponse(cartRepository.save(cart));
    }

    @Transactional
    public CartResponse removeOneFromCart(Long userId, Long productId, int quantity) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Carrello non trovato"));

        if (cart.getItems() == null) {
            return toResponse(cart);
        }

        Iterator<CartItem> it = cart.getItems().iterator();
        while (it.hasNext()) {
            CartItem item = it.next();
            if (item.getProdotto() != null && item.getProdotto().getId().equals(productId)) {
                int newQty = item.getQuantity() - quantity;
                if (newQty <= 0) {
                    // cancello la riga
                    it.remove();
                    cartItemRepository.delete(item);
                } else {
                    item.setQuantity(newQty);
                }
                break; //solo prima occorrenza
            }
        }

        Cart saved = cartRepository.save(cart);
        return toResponse(saved);
    }

    @Transactional
    public CartResponse updateProductQuantity(Long userId, Long productId, int quantity) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Carrello non trovato"));

        if (cart.getItems() != null) {
            cart.getItems().forEach(item -> {
                if (item.getProdotto() != null && item.getProdotto().getId().equals(productId)) {
                    item.setQuantity(quantity);
                }
            });
        }

        return toResponse(cartRepository.save(cart));
    }

    //trasformo cart in cartresponse
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
            cir.setAttributeName(item.getAttributeName());
            cir.setAttributeValue(item.getAttributeValue());
            //prezzo (BigDecimal -> double)
            if (item.getProdotto().getPrezzo() != null) {
                cir.setPrice(item.getProdotto().getPrezzo().doubleValue());
            } else {
                cir.setPrice(0.0);
            }
            //prendo la prima immagine se presente
            if (item.getProdotto().getImmagini() != null && !item.getProdotto().getImmagini().isEmpty()) {
                cir.setImageUrl(item.getProdotto().getImmagini().get(0));
            } else {
                cir.setImageUrl("/assets/default.png");
            }


            return cir;
        }).toList();

        dto.setItems(items);
        return dto;
    }
}
