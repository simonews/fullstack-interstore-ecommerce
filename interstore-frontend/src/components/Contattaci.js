import React, { useState } from "react";
import "bootstrap/dist/css/bootstrap.min.css";

function Contattaci() {
  const [email, setEmail] = useState("");
  const [messaggio, setMessaggio] = useState("");
  const [inviato, setInviato] = useState(false);

  const handleSubmit = (e) => {
    e.preventDefault();
    setInviato(true);
    setEmail("");
    setMessaggio("");
    setTimeout(() => setInviato(false), 3000);
  };

  return (
    <div
      className="d-flex justify-content-center align-items-center vh-100"
      style={{
        backgroundImage: "url('/assets/background.jpg')",
        backgroundSize: "cover",
        backgroundPosition: "center",
      }}
    >
      <div className="card p-4 shadow-lg" style={{ width: "400px" }}>
        <h3 className="mb-3 text-center">Contattaci</h3>

        {inviato && (
          <div className="alert alert-success text-center" role="alert">
            Messaggio inviato ✅
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="mb-3">
            <label htmlFor="formEmail" className="form-label">
              Email
            </label>
            <input
              id="formEmail"
              type="email"
              className="form-control"
              placeholder="Inserisci la tua email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>

          <div className="mb-3">
            <label htmlFor="formMessaggio" className="form-label">
              Messaggio
            </label>
            <textarea
              id="formMessaggio"
              className="form-control"
              rows={4}
              placeholder="Scrivi il tuo messaggio"
              value={messaggio}
              onChange={(e) => setMessaggio(e.target.value)}
              required
            />
          </div>

          <button type="submit" className="btn btn-dark w-100">
            Invia
          </button>
        </form>
      </div>
    </div>
  );
}

export default Contattaci;
