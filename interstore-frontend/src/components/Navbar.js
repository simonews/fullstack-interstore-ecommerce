import React from "react";
import "bootstrap/dist/css/bootstrap.min.css";
import { useNavigate, useLocation, Link } from "react-router-dom";
import { useCart } from "../context/CartContext";

function Navbar() {
  const isLoggedIn = !!localStorage.getItem("token");
  const navigate = useNavigate();
  const location = useLocation();
  const { cart } = useCart();

  const handleCartClick = (e) => {
    e.preventDefault();

    if (!isLoggedIn) {
      if (location.pathname === "/login" || location.pathname === "/register") {
        alert("Devi essere loggato per poter aprire il carrello!");
      } else {
        alert("Effettua il login per accedere al carrello!");
        navigate("/login");
      }
    } else {
      navigate("/cart");
    }
  };

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("userId");
    localStorage.removeItem("cart");
    navigate("/login");
    window.location.reload();
  };

  return (
    <nav className="navbar navbar-expand-lg navbar-dark bg-dark fixed-top shadow-sm">
      <div className="container">
        <Link className="navbar-brand d-flex align-items-center" to="/products">
          <img
            src="/assets/inter-logo.png"
            alt="Inter Logo"
            width="40"
            height="40"
            className="me-2"
          />
          <span className="fw-bold">InterStore</span>
        </Link>

        <button
          className="navbar-toggler"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#navbarNav"
          aria-controls="navbarNav"
          aria-expanded="false"
          aria-label="Toggle navigation"
        >
          <span className="navbar-toggler-icon"></span>
        </button>

        <div
          className="collapse navbar-collapse justify-content-end"
          id="navbarNav"
        >
          <ul className="navbar-nav">
            <li className="nav-item">
              <Link className="nav-link" to="/chi-siamo">
                Chi siamo
              </Link>
            </li>

            <li className="nav-item">
              <Link className="nav-link" to="/contattaci">
                Contattaci
              </Link>
            </li>

            <li className="nav-item">
              <button
                className="btn btn-link nav-link position-relative"
                onClick={handleCartClick}
              >
                Carrello 🛒
                {cart.length > 0 && (
                  <span
                    className="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger"
                    style={{ fontSize: "0.7rem" }}
                  >
                    {cart.length}
                  </span>
                )}
              </button>
            </li>

            {isLoggedIn && (
              <li className="nav-item">
                <button className="btn btn-outline-light ms-3" onClick={handleLogout}>
                  Logout
                </button>
              </li>
            )}
          </ul>
        </div>
      </div>
    </nav>
  );
}

export default Navbar;
