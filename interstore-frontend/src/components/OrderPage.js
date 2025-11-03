import React, { useState } from "react";
import { useCart } from "../context/CartContext";
import { useNavigate } from "react-router-dom";

function OrderPage() {
  const { cart, clearCart } = useCart();
  const navigate = useNavigate();

  const [form, setForm] = useState({
    nome: "",
    cognome: "",
    indirizzo: "",
    citta: "",
    cap: "",
    telefono: "",
    pagamento: "carta",
  });

  const total = cart.reduce((sum, item) => sum + (item.prezzo || 0) * (item.quantita || 0), 0);

  const handleSubmit = async (e) => {
    e.preventDefault();
    const token = localStorage.getItem("token");
    const userId = localStorage.getItem("userId");

    try {
      const response = await fetch(
        `http://localhost:8080/api/orders/checkout/${userId}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify({
            items: cart.map((item) => ({
              productId: item.id,
              quantity: item.quantita || 0,
            })),
            nome: form.nome,
            cognome: form.cognome,
            indirizzo: form.indirizzo,
            citta: form.citta,
            cap: form.cap,
            telefono: form.telefono,
            pagamento: form.pagamento,
          }),
        }
      );

      if (response.status === 409) {
        const msg = await response.text();
        alert("⚠️ " + msg);
        return;
      }

      if (!response.ok) {
        throw new Error("Errore server");
      }

      await clearCart(); // svuota anche lato backend
      alert("✅ Ordine confermato! Grazie per l’acquisto.");
      navigate("/products");
    } catch (err) {
      console.error("❌ Errore durante l’ordine:", err);
      alert("Errore durante l’ordine. Riprova.");
    }
  };

  if (cart.length === 0) {
    return (
      <div className="container py-5">
        <h2>📦 Nessun articolo da ordinare</h2>
        <button
          className="btn btn-primary mt-3"
          onClick={() => navigate("/products")}
        >
          Torna ai prodotti
        </button>
      </div>
    );
  }

  return (
    <div className="container py-5">
      <h2>📦 Conferma ordine</h2>

      <ul className="list-group mb-3">
        {cart.map((item) => (
          <li
            key={`${item.id}-${item.attributo?.valore || "base"}`}
            className="list-group-item d-flex justify-content-between align-items-center"
          >
            <span>
              {item.nome}{" "}
              {item.attributo &&
                `(${item.attributo.nome}: ${item.attributo.valore})`}{" "}
              × {item.quantita}
            </span>
            <strong>{((item.prezzo || 0) * (item.quantita || 0)).toFixed(2)} €</strong>
          </li>
        ))}
      </ul>
      <h4 className="mb-4">Totale: {total.toFixed(2)} €</h4>

      <form onSubmit={handleSubmit}>
        <div className="row">
          <div className="col-md-6 mb-3">
            <label className="form-label">Nome</label>
            <input
              type="text"
              className="form-control"
              value={form.nome}
              onChange={(e) => setForm({ ...form, nome: e.target.value })}
              required
            />
          </div>
          <div className="col-md-6 mb-3">
            <label className="form-label">Cognome</label>
            <input
              type="text"
              className="form-control"
              value={form.cognome}
              onChange={(e) => setForm({ ...form, cognome: e.target.value })}
              required
            />
          </div>
        </div>

        <div className="mb-3">
          <label className="form-label">Indirizzo</label>
          <input
            type="text"
            className="form-control"
            value={form.indirizzo}
            onChange={(e) => setForm({ ...form, indirizzo: e.target.value })}
            required
          />
        </div>
        <div className="row">
          <div className="col-md-6 mb-3">
            <label className="form-label">Città</label>
            <input
              type="text"
              className="form-control"
              value={form.citta}
              onChange={(e) => setForm({ ...form, citta: e.target.value })}
              required
            />
          </div>
          <div className="col-md-3 mb-3">
            <label className="form-label">CAP</label>
            <input
              type="text"
              className="form-control"
              value={form.cap}
              onChange={(e) => setForm({ ...form, cap: e.target.value })}
              required
            />
          </div>
          <div className="col-md-3 mb-3">
            <label className="form-label">Telefono</label>
            <input
              type="tel"
              className="form-control"
              value={form.telefono}
              onChange={(e) => setForm({ ...form, telefono: e.target.value })}
              required
            />
          </div>
        </div>

        <div className="mb-3">
          <label className="form-label">Metodo di pagamento</label>
          <select
            className="form-select"
            value={form.pagamento}
            onChange={(e) => setForm({ ...form, pagamento: e.target.value })}
          >
            <option value="carta">Carta di credito</option>
            <option value="paypal">PayPal</option>
            <option value="bonifico">Bonifico bancario</option>
            <option value="contrassegno">Contrassegno</option>
          </select>
        </div>

        <button type="submit" className="btn btn-success w-100">
          Conferma ordine
        </button>
      </form>
    </div>
  );
}

export default OrderPage;
