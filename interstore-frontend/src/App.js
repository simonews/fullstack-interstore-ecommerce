import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import AuthPage from "./components/AuthPage.js";
import RegisterPage from "./components/RegisterPage.js";
import ProductsPage from "./components/ProductsPage.js";
import ProductDetailPage from "./components/ProductDetailPage.js";
import CartPage from "./components/CartPage.js";
import OrderPage from "./components/OrderPage.js";
import Navbar from "./components/Navbar.js";
import ChiSiamo from "./components/ChiSiamo.js";
import Contattaci from "./components/Contattaci.js";
import { CartProvider } from "./context/CartContext";
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap/dist/js/bootstrap.bundle.min.js";


function App() {
  return (
    <CartProvider>
      <Router>
        <Navbar />
        <div className="mt-7 pt-4">
          <Routes>
            {/* Login */}
            <Route path="/login" element={<AuthPage />} />

            {/* Registrazione */}
            <Route path="/register" element={<RegisterPage />} />

            {/* Prodotti */}
            <Route path="/products" element={<ProductsPage />} />

            {/* Dettaglio prodotto */}
            <Route path="/product/:id" element={<ProductDetailPage />} />

            {/* Carrello */}
            <Route path="/cart" element={<CartPage />} />

            {/* Pagina ordine */}
            <Route path="/ordine" element={<OrderPage />} />

            {/* Chi siamo */}
            <Route path="/chi-siamo" element={<ChiSiamo />} />

            {/* Contattaci */}
            <Route path="/contattaci" element={<Contattaci />} />

            {/* Redirect default */}
            <Route path="*" element={<Navigate to="/login" />} />
          </Routes>
        </div>
      </Router>
    </CartProvider>
  );
}

export default App;
