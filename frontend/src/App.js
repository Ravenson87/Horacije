import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import HomePage from "./pages/HomePage";
import AddBillPage from "./pages/AddBillPage";
import CheckPurchasesPage from "./pages/CheckPurchasesPage";

function App() {
  return (
      <Router>
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/add-bill" element={<AddBillPage />} />
          <Route path="/check-purchases" element={<CheckPurchasesPage />} />
        </Routes>
      </Router>
  );
}

export default App;
