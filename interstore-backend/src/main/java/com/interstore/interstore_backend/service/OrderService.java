package com.interstore.interstore_backend.service;

import com.interstore.interstore_backend.dto.CheckoutRequest;
import com.interstore.interstore_backend.entity.*;
import com.interstore.interstore_backend.model.StatoOrdine;
import com.interstore.interstore_backend.repository.CartItemRepository;
import com.interstore.interstore_backend.repository.CartRepository;
import com.interstore.interstore_backend.repository.OrderRepository;
import com.interstore.interstore_backend.repository.OrderItemRepository;
import jakarta.persistence.OptimisticLockException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        CartRepository cartRepository, CartItemRepository cartItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
    }

    public List<Ordine> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Ordine> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public Ordine saveOrder(Ordine order) {
        return orderRepository.save(order);
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    @Transactional
    public Ordine checkout(CheckoutRequest checkoutRequest) {
        try {
            Long userId = checkoutRequest.getUserId();

            Cart cart = cartRepository.findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("Carrello non trovato"));

            //controllo carrello db con carrello d'ac1uisto
            if (!areCartsEqual(cart.getItems(), checkoutRequest.getItems())) {
                throw new RuntimeException("Il carrello è stato aggiornato. Ricarica la pagina.");
            }

            if (cart.getItems().isEmpty()) {
                throw new RuntimeException("Il carrello è vuoto");
            }

            Ordine ordine = new Ordine();
            ordine.setUser(cart.getUser());
            ordine.setData(new Date());
            ordine.setStato(StatoOrdine.IN_CORSO);
            ordine.setNome(checkoutRequest.getNome());
            ordine.setCognome(checkoutRequest.getCognome());
            ordine.setIndirizzo(checkoutRequest.getIndirizzo());
            ordine.setCitta(checkoutRequest.getCitta());
            ordine.setCap(checkoutRequest.getCap());
            ordine.setTelefono(checkoutRequest.getTelefono());
            ordine.setPagamento(checkoutRequest.getPagamento());

            for (CartItem cartItem : cart.getItems()) {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrdine(ordine);
                orderItem.setProdotto(cartItem.getProdotto());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setPrezzoUnitario(cartItem.getProdotto().getPrezzo());
                ordine.getItems().add(orderItem);

                if (cartItem.getProdotto().getAttributi() != null) {
                    for (ProdottoAttributo attr : cartItem.getProdotto().getAttributi()) {
                        if (Objects.equals(attr.getNome(), cartItem.getAttributeName()) &&
                                Objects.equals(attr.getValore(), cartItem.getAttributeValue())) {

                            if (attr.getQuantita() != null && attr.getQuantita() >= cartItem.getQuantity()) {
                                attr.setQuantita(attr.getQuantita() - cartItem.getQuantity());
                            } else {
                                throw new RuntimeException("Quantità non disponibile per " +
                                        attr.getNome() + " " + attr.getValore());
                            }
                        }
                    }
                }
            }

            Ordine savedOrder = orderRepository.save(ordine);

            cartItemRepository.deleteByCartId(cart.getId());
            cart.getItems().clear();
            cartRepository.save(cart);

            return savedOrder;
    } catch(OptimisticLockException e) {
            throw new RuntimeException("Quantità aggiornata da un altro utente, riprova l'acquisto.");
        }
    }

    private boolean areCartsEqual(List<CartItem> serverItems, List<CheckoutRequest.CartItemDTO> clientItems) {
        if (serverItems == null) serverItems = List.of();
        if (clientItems == null) clientItems = List.of();

        if (serverItems.size() != clientItems.size()) return false;

        Map<Long, Integer> serverMap = new HashMap<>();
        for (CartItem si : serverItems) {
            Long pid = si.getProdotto() != null ? si.getProdotto().getId() : null;
            if (pid == null) return false;
            serverMap.put(pid, serverMap.getOrDefault(pid, 0) + si.getQuantity());
        }

        Map<Long, Integer> clientMap = new HashMap<>();
        for (CheckoutRequest.CartItemDTO ci : clientItems) {
            if (ci.getProductId() == null) return false;
            clientMap.put(ci.getProductId(), clientMap.getOrDefault(ci.getProductId(), 0) + ci.getQuantity());
        }

        return serverMap.equals(clientMap);
    }


}

