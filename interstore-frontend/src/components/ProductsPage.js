import React, { useEffect, useState } from "react";
import { jwtDecode } from "jwt-decode";
import { useCart } from "../context/CartContext";
import { useNavigate } from "react-router-dom";

function ProductsPage() {
  const [products, setProducts] = useState([]);
  const [role, setRole] = useState(null);
  const [showForm, setShowForm] = useState(false);
  const [editProduct, setEditProduct] = useState(null);

  const [newProduct, setNewProduct] = useState({
    nome: "",
    descrizione: "",
    prezzo: "",
    immagini: [],
    attributi: [],
  });

  const { cart, addToCart } = useCart();
  const navigate = useNavigate();

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

  // Decodifico ruolo
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

  // Fetch prodotti
  useEffect(() => {
    const token = localStorage.getItem("token");
    fetch("http://localhost:8080/api/prodotti", {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then(async (res) => {
        const text = await res.text();
        if (!res.ok) throw new Error(`Errore ${res.status}: ${text}`);
        return JSON.parse(text);
      })
      .then((data) => {
        console.log("📦 [ProductsPage] Prodotti caricati:", data);

        const prodottiNormalizzati = data.map((p) => ({
            ...p,
            immagini: normalizeImages(p.immagini),
            attributi: p.attributi?.map(att => ({
              ...att,
              version: att.version // 👈 IMPORTANTISSIMO
            })) || []
          }));
        setProducts(prodottiNormalizzati);
      })
      .catch((err) => console.error("❌ Errore caricamento prodotti:", err));
  }, []);

  // Elimina prodotto
  const handleDelete = async (id) => {
    try {
      const token = localStorage.getItem("token");
      const res = await fetch(`http://localhost:8080/api/prodotti/${id}`, {
        method: "DELETE",
        headers: { Authorization: `Bearer ${token}` },
      });

      if (res.ok) {
        setProducts(products.filter((p) => p.id !== id));
      } else {
        const text = await res.text();
        if (text.includes("viola il vincolo di chiave") || res.status === 403) {
          alert("Questo prodotto è già stato venduto almeno una volta. Non può essere eliminato per mantenere la cronologia degli ordini. Puoi comunque continuare a venderne le quantità disponibili o disattivarlo impostando la quantità a 0.");
        } else {
          alert(" Errore eliminazione: " + (text || `Codice ${res.status}`));
        }
        throw new Error(`Errore ${res.status}: ${text}`);
      }
    } catch (err) {
      console.error("❌ Errore eliminazione prodotto:", err);
    }
  };


  // Parsing attributi: "taglia:L:15,colore:Blu:10"
  const parseAttributi = (input) => {
    if (!input.trim()) return [];
    return input.split(",").map((attr) => {
      const [nome, valore, quantita] = attr.split(":");
      return {
        nome: nome?.trim(),
        valore: valore?.trim(),
        quantita: parseInt(quantita) || 0,
      };
    });
  };

  // Aggiungi nuovo prodotto
  const handleAddProduct = async (e) => {
    e.preventDefault();
    try {
      const prodottoDaInviare = {
        ...newProduct,
        prezzo: parseFloat(newProduct.prezzo),
      };

      const token = localStorage.getItem("token");
      const res = await fetch("http://localhost:8080/api/prodotti", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(prodottoDaInviare),
      });

      if (res.ok) {
        const prodottoCreato = await res.json();
        // normalizza la risposta prima di inserirla nello state
        prodottoCreato.immagini = normalizeImages(prodottoCreato.immagini);
        setProducts([...products, prodottoCreato]);
        setNewProduct({
          nome: "",
          descrizione: "",
          prezzo: "",
          immagini: [],
          attributi: [],
        });
        setShowForm(false);
      } else {
        const text = await res.text();
        throw new Error(`Errore ${res.status}: ${text}`);
      }
    } catch (err) {
      console.error("❌ Errore aggiunta prodotto:", err);
    }
  };

  // Modifica prodotto esistente
  const handleEditProduct = async (e) => {
    e.preventDefault();
    try {
      const prodottoDaInviare = {
        ...editProduct,
        prezzo: parseFloat(editProduct.prezzo),
        attributi: editProduct.attributi.map(att => ({
            ...att,
            version: att.version
          }))
      };

      const token = localStorage.getItem("token");
      const res = await fetch(
        `http://localhost:8080/api/prodotti/${editProduct.id}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify(prodottoDaInviare),
        }
      );

      if (res.ok) {
        const prodottoAggiornato = await res.json();
        // normalizza la risposta prima di aggiornare lo state
        prodottoAggiornato.immagini = normalizeImages(prodottoAggiornato.immagini);

        setProducts(
          products.map((p) =>
            p.id === prodottoAggiornato.id ? prodottoAggiornato : p
          )
        );
        setEditProduct(null);
      } else {
        const text = await res.text();
        throw new Error(`Errore ${res.status}: ${text}`);
      }
    } catch (err) {
      console.error("❌ Errore modifica prodotto:", err);
    }
  };

  return (
    <div
      className="container-fluid min-vh-100 d-flex flex-column align-items-center py-5"
      style={{
        backgroundImage: "url('/assets/background.jpg')",
        backgroundSize: "cover",
        backgroundPosition: "center",
        backgroundAttachment: "fixed",
      }}
    >
      <div className="container position-relative" style={{ zIndex: 1 }}>


        <div className="row w-100 justify-content-center">
          {products.map((product) => (
            <div
              key={product.id}
              className="col-md-4 mb-4 d-flex"
              style={{ cursor: "pointer" }}
              onClick={() => navigate(`/product/${product.id}`)}
            >
              <div className="card shadow-lg border-0 flex-fill">
                {product.immagini && product.immagini.length > 0 ? (
                  <img
                    src={product.immagini[0]}
                    className="d-block w-100"
                    style={{ height: "220px", objectFit: "contain" }}
                    alt="anteprima"
                  />
                ) : (
                  <img
                    src="https://via.placeholder.com/200"
                    className="card-img-top"
                    alt="Placeholder"
                  />
                )}

                <div className="card-body text-center">
                  <h5 className="card-title">{product.nome}</h5>
                  <p className="card-text">{product.descrizione}</p>
                  <p className="fw-bold">{product.prezzo} €</p>

                  {/* Varianti */}
                  {product.attributi?.length > 0 && (
                    <ul className="list-group mb-3">
                      {product.attributi.map((att, idx) => (
                        <li
                          key={idx}
                          className="list-group-item d-flex justify-content-between align-items-center"
                          onClick={(e) => e.stopPropagation()}
                        >
                          {att.nome}: {att.valore}
                          <span className="badge bg-secondary">
                            {att.quantita}
                          </span>
                          {role === "user" && (
                            <button
                              className="btn btn-sm btn-primary ms-2"
                              onClick={(e) => {
                                e.stopPropagation();

                                // controllo se prodotto esaurito
                                if (att.quantita <= 0) {
                                  alert("⚠️ Prodotto esaurito");
                                  return;
                                }

                                // controllo se già nel carrello alla quantità massima
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
                              {att.quantita > 0 ? "Aggiungi" : "Esaurito"}
                            </button>
                          )}

                        </li>
                      ))}
                    </ul>
                  )}

                  {/* Se non ha varianti */}
                  {role === "user" && !product.attributi?.length && (
                    <button
                      className="btn btn-primary"
                      onClick={(e) => {
                        e.stopPropagation();
                        addToCart(product);
                      }}
                    >
                      Aggiungi al carrello
                    </button>
                  )}

                  {role === "admin" && (
                    <>
                      <button
                        className="btn btn-danger me-2"
                        onClick={(e) => {
                          e.stopPropagation();
                          handleDelete(product.id);
                        }}
                      >
                        Elimina
                      </button>
                      <button
                        className="btn btn-warning"
                        onClick={(e) => {
                          e.stopPropagation();
                          setEditProduct(product);
                        }}
                      >
                        Modifica
                      </button>
                    </>
                  )}
                </div>
              </div>
            </div>
          ))}

          {/* Card aggiungi nuovo prodotto (solo admin) */}
          {role === "admin" && !editProduct && (
            <div className="col-md-4 mb-4 d-flex">
              <div className="card shadow-lg border-0 flex-fill d-flex align-items-center justify-content-center bg-light">
                {!showForm ? (
                  <div
                    className="card-body text-center"
                    style={{ cursor: "pointer" }}
                    onClick={() => setShowForm(true)}
                  >
                    <h5 className="card-title">➕ Aggiungi nuovo prodotto</h5>
                    <p className="card-text text-muted">
                      Inserisci un nuovo prodotto nello store
                    </p>
                  </div>
                ) : (
                  <form className="p-3 w-100" onSubmit={handleAddProduct}>
                    <div className="mb-2">
                      <input
                        type="text"
                        className="form-control"
                        placeholder="Nome"
                        value={newProduct.nome}
                        onChange={(e) =>
                          setNewProduct({ ...newProduct, nome: e.target.value })
                        }
                        required
                      />
                    </div>
                    <div className="mb-2">
                      <textarea
                        className="form-control"
                        placeholder="Descrizione"
                        value={newProduct.descrizione}
                        onChange={(e) =>
                          setNewProduct({
                            ...newProduct,
                            descrizione: e.target.value,
                          })
                        }
                        required
                      />
                    </div>
                    <div className="mb-2">
                      <input
                        type="number"
                        className="form-control"
                        placeholder="Prezzo"
                        value={newProduct.prezzo}
                        onChange={(e) =>
                          setNewProduct({
                            ...newProduct,
                            prezzo: e.target.value,
                          })
                        }
                        required
                      />
                    </div>
                    {/*  */}
                    <div className="mb-2">
                      <input
                        type="text"
                        className="form-control"
                        placeholder="URL immagini (separate da virgola)"
                        value={newProduct.immagini.join(",")}
                        onChange={(e) =>
                          setNewProduct({
                            ...newProduct,
                            immagini: e.target.value
                              .split(",")
                              .map((i) => i.trim()),
                          })
                        }
                      />
                    </div>
                    <div className="mb-2">
                      <input
                        type="text"
                        className="form-control"
                        placeholder="Varianti (es: taglia:L:15,colore:Blu:10)"
                        onChange={(e) =>
                          setNewProduct({
                            ...newProduct,
                            attributi: parseAttributi(e.target.value),
                          })
                        }
                      />
                    </div>
                    <button type="submit" className="btn btn-success w-100">
                      Salva
                    </button>
                    <button
                      type="button"
                      className="btn btn-secondary w-100 mt-2"
                      onClick={() => setShowForm(false)}
                    >
                      Annulla
                    </button>
                  </form>
                )}
              </div>
            </div>
          )}

          {/* Card modifica prodotto (solo admin) */}
          {role === "admin" && editProduct && (
            <div className="col-md-4 mb-4 d-flex">
              <div className="card shadow-lg border-0 flex-fill d-flex align-items-center justify-content-center bg-light">
                <form className="p-3 w-100" onSubmit={handleEditProduct}>
                  <div className="mb-2">
                    <input
                      type="text"
                      className="form-control"
                      placeholder="Nome"
                      value={editProduct.nome}
                      onChange={(e) =>
                        setEditProduct({ ...editProduct, nome: e.target.value })
                      }
                      required
                    />
                  </div>
                  <div className="mb-2">
                    <textarea
                      className="form-control"
                      placeholder="Descrizione"
                      value={editProduct.descrizione}
                      onChange={(e) =>
                        setEditProduct({
                          ...editProduct,
                          descrizione: e.target.value,
                        })
                      }
                      required
                    />
                  </div>
                  <div className="mb-2">
                    <input
                      type="number"
                      className="form-control"
                      placeholder="Prezzo"
                      value={editProduct.prezzo}
                      onChange={(e) =>
                        setEditProduct({
                          ...editProduct,
                          prezzo: e.target.value,
                        })
                      }
                      required
                    />
                  </div>
                  {/*  */}
                  <div className="mb-2">
                    <input
                      type="text"
                      className="form-control"
                      placeholder="URL immagini (separate da virgola)"
                      value={editProduct.immagini.join(",")}
                      onChange={(e) =>
                        setEditProduct({
                          ...editProduct,
                          immagini: e.target.value
                            .split(",")
                            .map((i) => i.trim()),
                        })
                      }
                    />
                  </div>
                  <div className="mb-2">
                    <input
                      type="text"
                      className="form-control"
                      placeholder="Varianti (es: taglia:L:15,colore:Blu:10)"
                      onChange={(e) =>
                        setEditProduct({
                          ...editProduct,
                          attributi: parseAttributi(e.target.value),
                        })
                      }
                    />
                  </div>
                  <button type="submit" className="btn btn-warning w-100">
                    Aggiorna
                  </button>
                  <button
                    type="button"
                    className="btn btn-secondary w-100 mt-2"
                    onClick={() => setEditProduct(null)}
                  >
                    Annulla
                  </button>
                </form>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default ProductsPage;
