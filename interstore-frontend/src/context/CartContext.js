import React, { createContext, useContext, useEffect, useState } from "react";

const CartContext = createContext();

export const CartProvider = ({ children }) => {
  const [cart, setCart] = useState([]);
  const token = localStorage.getItem("token");
  const userId = localStorage.getItem("userId");

  // carico carrello locale (fallback)
  useEffect(() => {
    const storedCart = localStorage.getItem("cart");
    if (storedCart) {
      try {
        setCart(JSON.parse(storedCart));
      } catch {
        setCart([]);
      }
    }
  }, []);

  // se loggato → carico cart dal server
  useEffect(() => {
    if (!userId || !token) {
      setCart([]);
      return;
    }

    const fetchCart = async () => {
      try {
        const res = await fetch(`http://localhost:8080/api/cart/${userId}`, {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        });
        if (res.ok) {
          const data = await res.json();
          const normalized = (data.items || []).map((it) => ({
            id: it.productId,
            nome: it.productName,
            prezzo:
              typeof it.price === "number"
                ? it.price
                : parseFloat(it.price || 0) || 0,
            immagine: it.imageUrl || "/assets/default.png",
            attributo: it.attribute || null,
            quantita: it.quantity || 0,
          }));
          setCart(normalized);
          localStorage.setItem("cart", JSON.stringify(normalized));
        }
      } catch (err) {
        console.error("Errore caricamento carrello:", err);
      }
    };

    fetchCart();
  }, [userId, token]);

  // salvo copia locale
  useEffect(() => {
    localStorage.setItem("cart", JSON.stringify(cart));
  }, [cart]);

  const normalizeItem = (product, attribute = null) => {
    return {
      id: product.id,
      nome: product.nome,
      prezzo: product.prezzo ?? product.price ?? 0,
      immagine: product.immagine || "/assets/default.png",
      attributo: attribute
        ? { nome: attribute.nome, valore: attribute.valore, quantita: attribute.quantita }
        : null,
    };
  };

  // aggiungo 1 unità
  const addToCart = async (product, attribute = null) => {
    const item = normalizeItem(product, attribute);

    try {
      if (userId && token) {
        const res = await fetch(
          `http://localhost:8080/api/cart/${encodeURIComponent(userId)}/add?` +
          `productId=${encodeURIComponent(item.id)}&quantity=1` +
          (item.attributo
            ? `&attributeName=${encodeURIComponent(item.attributo.nome)}&attributeValue=${encodeURIComponent(item.attributo.valore)}`
            : ""),
          {
            method: "POST",
            headers: { Authorization: `Bearer ${token}` },
          }
        );

        if (!res.ok) {
          const msg = await res.text();
          alert("⚠️ " + msg);
          return;
        }
      }

      setCart((prevCart) => {
        const existing = prevCart.find(
          (c) =>
            c.id === item.id &&
            ((!c.attributo && !item.attributo) ||
              (c.attributo &&
                item.attributo &&
                c.attributo.nome === item.attributo.nome &&
                c.attributo.valore === item.attributo.valore))
        );

        if (existing) {
          return prevCart.map((c) =>
            c === existing ? { ...c, quantita: (c.quantita || 0) + 1 } : c
          );
        } else {
          return [...prevCart, { ...item, quantita: 1 }];
        }
      });
    } catch (err) {
      console.error("Errore addToCart:", err);
    }
  };


  const removeFromCart = async (productId, attribute = null) => {
    try {
      if (userId && token) {
        await fetch(
          `http://localhost:8080/api/cart/${encodeURIComponent(
            userId
          )}/remove?productId=${encodeURIComponent(productId)}&quantity=1`,
          {
            method: "POST",
            headers: { Authorization: `Bearer ${token}` },
          }
        );
      }

      setCart((prevCart) =>
        prevCart
          .map((c) =>
            c.id === productId &&
            ((!c.attributo && !attribute) ||
              (c.attributo &&
                attribute &&
                c.attributo.nome === attribute.nome &&
                c.attributo.valore === attribute.valore))
              ? { ...c, quantita: (c.quantita || 0) - 1 }
              : c
          )
          .filter((c) => (c.quantita || 0) > 0)
      );
    } catch (err) {
      console.error("Errore removeFromCart:", err);
    }
  };

  const deleteFromCart = async (productId, attribute = null) => {
    try {
      if (userId && token) {
        await fetch(
          `http://localhost:8080/api/cart/${encodeURIComponent(
            userId
          )}/remove/${encodeURIComponent(productId)}`,
          {
            method: "DELETE",
            headers: { Authorization: `Bearer ${token}` },
          }
        );
      }

      setCart((prevCart) =>
        prevCart.filter(
          (c) =>
            !(
              c.id === productId &&
              ((!c.attributo && !attribute) ||
                (c.attributo &&
                  attribute &&
                  c.attributo.nome === attribute.nome &&
                  c.attributo.valore === attribute.valore))
            )
        )
      );
    } catch (err) {
      console.error("Errore deleteFromCart:", err);
    }
  };

  const clearCart = async () => {
    try {
      if (userId && token) {
        await fetch(`http://localhost:8080/api/cart/${encodeURIComponent(userId)}`, {
          method: "DELETE",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
      }
      setCart([]);
      localStorage.removeItem("cart");
    } catch (err) {
      console.error("Errore clearCart:", err);
    }
  };

  return (
    <CartContext.Provider
      value={{ cart, addToCart, removeFromCart, deleteFromCart, clearCart, setCart }}
    >
      {children}
    </CartContext.Provider>
  );
};

export const useCart = () => useContext(CartContext);

export default CartContext;
