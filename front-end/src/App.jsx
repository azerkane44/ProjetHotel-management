import { BrowserRouter, Routes, Route } from "react-router-dom";

// Layout
import MainLayout from "./layout/MainLayout";

// Pages publiques
import HomePage from "./Pages/HomePage";
import ConnexionUser from "./Pages/ConnexionUser";
import InscriptionUser from "./Pages/InscriptionUser";
import DetailsPage from "./Pages/DetailsPage";

// ADMIN
import AdminDashboard from "./Pages/admin/AdminDashboard";
import AdminUsers from "./Pages/admin/AdminUsers";
import CrudHotel from "./Pages/admin/CrudHotel";

// EMPLOYE
import EmployeDashboard from "./Pages/employe/EmployeDashboard";

// Composant de protection de route
import ProtectedRoute from "./components/ProtectedRoute";

function App() {
    return (
        <BrowserRouter>
            <Routes>

                {/* Pages publiques avec layout */}
                <Route element={<MainLayout />}>
                    <Route path="/" element={<HomePage />} />
                    <Route path="/hotel/:id" element={<DetailsPage />} />
                </Route>

                {/* Auth */}
                <Route path="/Connexion" element={<ConnexionUser />} />
                <Route path="/Inscription" element={<InscriptionUser />} />

                {/* ADMIN - protégées */}
                <Route element={<ProtectedRoute requiredRole="ADMIN" />}>
                    <Route path="/admin" element={<AdminDashboard />} />
                    <Route path="/admin/add-user" element={<AdminUsers />} />
                    <Route path="/admin/add-hotel" element={<CrudHotel />} />
                </Route>

                {/* EMPLOYE - protégées */}
                <Route element={<ProtectedRoute requiredRole="EMPLOYE" />}>
                    <Route path="/employe" element={<EmployeDashboard />} />
                </Route>

                {/* Page 404 */}
                <Route path="*" element={<h1>404 - Page introuvable</h1>} />

            </Routes>
        </BrowserRouter>
    );
}

export default App;
