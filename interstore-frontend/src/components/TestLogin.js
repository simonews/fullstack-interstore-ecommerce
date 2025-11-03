import React, { useState } from "react";
import { login } from "../api/auth";

function App() {

  const handleLogin = async () => {
    try {
      const response = await fetch("http://localhost:8080/api/auth/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({
          username: "testAdmin2",
          password: "1234"
        })
      });

      if (!response.ok) {
        throw new Error(`Errore HTTP! status: ${response.status}`);
      }

      const data = await response.json();
      console.log("✅ Login riuscito:", data);
      alert("Login riuscito! Token in console.");
      localStorage.setItem("token", data.token);

    } catch (error) {
      console.error("❌ Errore durante il login:", error);
      alert("Errore di connessione o CORS. Controlla la console!");
    }
  };

  return (
    <div>
      <h1>InterStore</h1>
      <h2>Test Login</h2>
      <button onClick={handleLogin}>Fai Login</button>
    </div>
  );
}

export default App;

