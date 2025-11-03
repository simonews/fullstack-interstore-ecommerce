import React, { useState } from "react";
import { useNavigate } from "react-router-dom";

function RegisterPage() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const navigate = useNavigate();

  const handleRegister = async (e) => {
    e.preventDefault();
    setError("");
    setSuccess("");

    try {
      const response = await fetch("http://localhost:8080/api/auth/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password }),
      });

      if (!response.ok) {
        throw new Error("Registrazione fallita");
      }

      setSuccess("✅ Registrazione completata!");

      // dopo 1 secondo, vai al login
      setTimeout(() => navigate("/login"), 1000);

    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div
      className="container-fluid d-flex flex-column align-items-center justify-content-center min-vh-100"
      style={{
        backgroundImage: "url('/assets/background.jpg')",
        backgroundSize: "cover",
        backgroundPosition: "center",
      }}
    >
      <img
        src="/assets/inter-logo.png"
        alt="Inter Logo"
        className="mb-4"
        style={{ width: "150px" }}
      />

      <div className="card shadow p-4" style={{ width: "22rem" }}>
        <h2 className="text-center mb-4">InterStore - Registrati</h2>

        <form onSubmit={handleRegister}>
          <div className="mb-3">
            <label className="form-label">Nome Utente</label>
            <input
              type="text"
              className="form-control"
              placeholder="Inserisci nome utente"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
            />
          </div>

          <div className="mb-3">
            <label className="form-label">Password</label>
            <input
              type="password"
              className="form-control"
              placeholder="Inserisci password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>

          <button type="submit" className="btn btn-success w-100">
            Registrati
          </button>
        </form>

        {error && <div className="alert alert-danger mt-3">{error}</div>}
        {success && <div className="alert alert-success mt-3">{success}</div>}

        <div className="text-center mt-3">
          <small>
            Hai già un account?{" "}
            <a href="/login" className="text-decoration-none">Accedi</a>
          </small>
        </div>
      </div>
    </div>
  );
}

export default RegisterPage;
