package com.interstore.interstore_backend.service;

import com.interstore.interstore_backend.entity.*;
import com.interstore.interstore_backend.model.StatoOrdine;
import com.interstore.interstore_backend.repository.CartRepository;
import com.interstore.interstore_backend.repository.OrderRepository;
import com.interstore.interstore_backend.repository.OrderItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        CartRepository cartRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartRepository = cartRepository;
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

    //Checkout: genera un ordine dal carrello
    @Transactional
    public Ordine checkout(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Carrello non trovato"));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Il carrello è vuoto");
        }

        Ordine ordine = new Ordine();
        ordine.setUser(cart.getUser());
        ordine.setData(new Date());
        ordine.setStato(StatoOrdine.IN_CORSO);

        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrdine(ordine);
            orderItem.setProdotto(cartItem.getProdotto());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrezzoUnitario(cartItem.getProdotto().getPrezzo()); //prezzo congelato al momento dell'ordine
            ordine.getItems().add(orderItem);
        }

        //salva l'ordine
        Ordine savedOrder = orderRepository.save(ordine);

        //svuota il carrello dopo il checkout
        cart.getItems().clear();
        cartRepository.save(cart);

        return savedOrder;
    }
}

