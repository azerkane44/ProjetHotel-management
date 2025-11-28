import { BrowserRouter, Routes, Route } from "react-router-dom";

import MainLayout from "./layout/MainLayout";

import HomePage from "./pages/HomePage";
import ConnextionUser from "./Pages/ConnextionUser";
import InscriptionUser from "./Pages/InscriptionUser";
import DetailsPage from "./Pages/DetailsPage";

function App() {
  return (
    <BrowserRouter>
      <Routes>

        {/* Toutes les pages qui gardent une navbar */}
        <Route element={<MainLayout />}>
          <Route path="/" element={<HomePage />} />
        </Route>

        {/* Pages SANS layout */}
        <Route path="/Connextion" element={<ConnextionUser />} />
        <Route path="/Inscription" element={<InscriptionUser />} />

        {/* Page détails → sans layout */}
        <Route path="/hotel/:id" element={<DetailsPage />} />

        {/* Page 404 */}
        <Route path="*" element={<h1>404</h1>} />

      </Routes>
    </BrowserRouter>
  );
}

export default App;
