import React, { useState } from "react";

function TestCart() {
  const [cart, setCart] = useState(null);
  const [error, setError] = useState(null);

  const fetchCart = async () => {
    try {
      const token = localStorage.getItem("token");
      if (!token) {
        alert("⚠️ Devi fare login prima!");
        return;
      }

      const response = await fetch("http://localhost:8080/api/cart/1", {
        method: "GET",
        headers: {
          "Authorization": `Bearer ${token}`,
          "Content-Type": "application/json"
        }
      });

      if (!response.ok) {
        throw new Error(`Errore HTTP! status: ${response.status}`);
      }

      const data = await response.json();
      setCart(data);
      setError(null);

    } catch (err) {
      console.error("❌ Errore nel fetch del carrello:", err);
      setError(err.message);
    }
  };

  return (
    <div>
      <h2>Test Carrello</h2>
      <button onClick={fetchCart}>Mostra Carrello</button>

      {error && <p style={{color:"red"}}>Errore: {error}</p>}

      {cart && (
        <pre>{JSON.stringify(cart, null, 2)}</pre>
      )}
    </div>
  );
}

export default TestCart;
