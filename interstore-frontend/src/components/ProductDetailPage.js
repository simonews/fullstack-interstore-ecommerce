import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { jwtDecode } from "jwt-decode";
import { useCart } from "../context/CartContext";

function ProductDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [product, setProduct] = useState(null);
  const [role, setRole] = useState(null);
  const { cart, addToCart } = useCart();

  //normalizzo immagini in Array<string>
  const normalizeImages = (raw) => {
    if (!raw) return [];
    if (Array.isArray(raw)) {
      return raw
        .flatMap((item) =>
          typeof item === "string" ? item.split(",") : item
        )
        .map((s) => (s || "").trim())
        .filter(Boolean);
    }
    if (typeof raw === "string") {
      return raw.split(",").map((s) => s.trim()).filter(Boolean);
    }
    return [];
  };

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (token) {
      try {
        const decoded = jwtDecode(token);
        setRole(decoded.role?.toLowerCase());
      } catch (err) {
        console.error("❌ Errore decoding JWT:", err);
      }
    }
  }, []);

  useEffect(() => {
    const token = localStorage.getItem("token");
    fetch(`http://localhost:8080/api/prodotti/${id}`, {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then(async (res) => {
        const text = await res.text();
        if (!res.ok) throw new Error(`Errore ${res.status}: ${text}`);
        return JSON.parse(text);
      })
      .then((data) => {
        console.log("📦 [ProductDetailPage] Prodotto caricato:", data);

        // 🔧 Normalizza immagini (robusto)
        const prodottoNormalizzato = {
          ...data,
          immagini: normalizeImages(data.immagini),
        };

        setProduct(prodottoNormalizzato);
      })
      .catch((err) => console.error("❌ Errore caricamento prodotto:", err));
  }, [id]);

  if (!product) return <p className="text-center mt-5">⏳ Caricamento...</p>;

  return (
    <div className="container py-5">
      <div className="card shadow-lg border-0">
        <div className="card-body">
          <h2>{product.nome}</h2>
          <p>{product.descrizione}</p>
          <h4 className="fw-bold">{product.prezzo} €</h4>

          {/* carosello immagini */}
          {product.immagini && product.immagini.length > 0 && (
            <div
              id={`carousel-detail-${product.id}`}
              className="carousel slide mb-3"
              data-bs-ride="carousel"
            >
              <div className="carousel-inner">
                {product.immagini.map((img, index) => (
                  <div
                    key={index}
                    className={`carousel-item ${index === 0 ? "active" : ""}`}
                  >
                    <img
                      src={img}
                      className="d-block w-100"
                      style={{ maxHeight: "400px", objectFit: "contain" }}
                      alt={`img-${index}`}
                    />
                  </div>
                ))}
              </div>
              {product.immagini.length > 1 && (
                <>
                  <button
                    className="carousel-control-prev"
                    type="button"
                    data-bs-target={`#carousel-detail-${product.id}`}
                    data-bs-slide="prev"
                  >
                    <span className="carousel-control-prev-icon"></span>
                  </button>
                  <button
                    className="carousel-control-next"
                    type="button"
                    data-bs-target={`#carousel-detail-${product.id}`}
                    data-bs-slide="next"
                  >
                    <span className="carousel-control-next-icon"></span>
                  </button>
                </>
              )}
            </div>
          )}

          {/* Varianti */}
          {product.attributi?.length > 0 && (
            <>
              <h5>Varianti disponibili</h5>
              <ul className="list-group mb-3">
                {product.attributi.map((att, idx) => (
                  <li
                    key={idx}
                    className="list-group-item d-flex justify-content-between align-items-center"
                  >
                    {att.nome}: {att.valore}
                    <span className="badge bg-secondary">{att.quantita}</span>
                    {role === "user" && (
                     <button
                       className="btn btn-sm btn-primary ms-2"
                       onClick={() => {
                         // controllo esaurito
                         if (att.quantita <= 0) {
                           alert("⚠️ Prodotto esaurito");
                           return;
                         }

                         // controllo limite max
                         const esistente = cart.find(
                           (c) =>
                             c.id === product.id &&
                             c.attributo?.nome === att.nome &&
                             c.attributo?.valore === att.valore
                         );

                         if (esistente && esistente.quantita >= att.quantita) {
                           alert("⚠️ Hai raggiunto il limite massimo per questa variante");
                           return;
                         }

                         // se ok → aggiungi
                         addToCart(product, att);
                       }}
                       disabled={att.quantita <= 0}
                     >
                       {att.quantita > 0 ? "Aggiungi al carrello" : "Esaurito"}
                     </button>
                    )}
                  </li>
                ))}
              </ul>
            </>
          )}

          {/* Nessuna variante */}
          {role === "user" && !product.attributi?.length && (
            <button
              className="btn btn-primary me-2"
              onClick={() => addToCart(product)}
            >
              Aggiungi al carrello
            </button>
          )}

          {/* Admin: elimina prodotto */}
          {role === "admin" && (
            <button
              className="btn btn-danger"
              onClick={async () => {
                const token = localStorage.getItem("token");
                const res = await fetch(
                  `http://localhost:8080/api/prodotti/${id}`,
                  {
                    method: "DELETE",
                    headers: { Authorization: `Bearer ${token}` },
                  }
                );
                if (res.ok) {
                  alert("✅ Prodotto eliminato!");
                  navigate("/products");
                }
              }}
            >
              Elimina prodotto
            </button>
          )}
        </div>
      </div>
    </div>
  );
}

export default ProductDetailPage;
