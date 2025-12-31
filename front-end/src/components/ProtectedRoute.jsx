import { Navigate } from "react-router-dom";

export default function ProtectedRoute({ children, allowedRoles }) {
    const user = JSON.parse(localStorage.getItem("user"));

    // ❌ Pas connecté
    if (!user) {
        return <Navigate to="/Connexion" replace />;
    }

    // ❌ Pas le bon rôle
    if (allowedRoles && !allowedRoles.includes(user.role)) {
        return <Navigate to="/" replace />;
    }

    // ✅ Autorisé
    return children;
}
