import { BrowserRouter, Routes, Route } from "react-router-dom";

import MainLayout from "./layout/MainLayout";

import HomePage from "./pages/HomePage";
import ConnextionUser from "./Pages/ConnextionUser";
import InscriptionUser from "./Pages/InscriptionUser";
import DetailsPage from "./Pages/DetailsPage";
import AdminDashboard from "./Pages/AdminDashboard";
import AdminUsers from "./Pages/AdminUsers";

// 🔵 Import de ton CRUD
import CrudHotel from "./pages/CrudHotel";

function App() {
    return (
        <BrowserRouter>
            <Routes>
                {/* Pages AVEC Layout */}
                <Route element={<MainLayout />}>
                    <Route path="/" element={<HomePage />} />
                    <Route path="/hotel/:id" element={<DetailsPage />} />
                </Route>

                {/* Pages SANS layout */}
                <Route path="/Connexion" element={<ConnextionUser />} />
                <Route path="/Inscription" element={<InscriptionUser />} />

                {/* CRUD Admin */}
                <Route path="/admin/hotels" element={<CrudHotel />} />

                {/* Page 404 */}
                <Route path="*" element={<h1>404</h1>} />
                {/* Dashboard Admin */}
                <Route path="/admin/users" element={<AdminUsers />} />
                <Route path="/admin" element={<AdminDashboard />} />
            </Routes>


        </BrowserRouter>
    );
}

export default App;
