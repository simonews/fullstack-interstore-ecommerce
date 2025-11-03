import React from "react";
import { useCart } from "../context/CartContext";
import { useNavigate } from "react-router-dom";

function CartPage() {
  const { cart, removeFromCart, clearCart } = useCart();
  const navigate = useNavigate();

  const total = cart.reduce((sum, item) => sum + (item.prezzo || 0) * (item.quantita || 0), 0);

  return (
    <div className="container py-5">
      <h2>🛒 Il tuo carrello</h2>
      {cart.length === 0 ? (
        <p>Nessun prodotto nel carrello.</p>
      ) : (
        <>
          <ul className="list-group mb-3">
            {cart.map((item) => (
              <li
                key={`${item.id}-${item.attributo?.valore || "base"}`}
                className="list-group-item d-flex justify-content-between align-items-center"
              >
                <span>
                  {item.nome}{" "}
                  {item.attributo && `(${item.attributo.nome}: ${item.attributo.valore})`} ×{" "}
                  {item.quantita}
                </span>
                <div>
                  <strong>{((item.prezzo || 0) * (item.quantita || 0)).toFixed(2)} €</strong>
                  <button
                    className="btn btn-sm btn-outline-danger ms-2"
                    onClick={() => removeFromCart(item.id, item.attributo)}
                  >
                    Rimuovi
                  </button>
                </div>
              </li>
            ))}
          </ul>
          <h4>Totale: {total.toFixed(2)} €</h4>
          <div className="d-flex gap-3 mt-3">
            <button className="btn btn-danger" onClick={clearCart}>
              Svuota carrello
            </button>
            <button
              className="btn btn-success"
              onClick={() => navigate("/ordine")}
            >
              Procedi all’ordine
            </button>
          </div>
        </>
      )}
    </div>
  );
}

export default CartPage;
