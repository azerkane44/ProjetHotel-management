import { useState } from "react";
import InputField from "../components/InputField";

export default function ConnextionUser() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [message, setMessage] = useState("");
    const [messageType, setMessageType] = useState(""); // "success" ou "error"
    const [loading, setLoading] = useState(false);

    const handleLogin = async (e) => {
        e.preventDefault();
        setLoading(true);
        setMessage("");
        setMessageType("");

        const loginData = { email, password };

        try {
            const response = await fetch("http://localhost:8080/api/v1/login", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(loginData)
            });

            if (response.ok) {
                const data = await response.json();

                console.log("📥 Réponse de connexion:", data);

                // ✅ VÉRIFIER SI LE TOKEN EST PRÉSENT
                if (!data.token) {
                    console.error("❌ Pas de token dans la réponse !");
                    setMessage("Erreur : Le serveur n'a pas renvoyé de token d'authentification");
                    setMessageType("error");
                    setLoading(false);
                    return;
                }

                // ✅ Stocker le TOKEN (TRÈS IMPORTANT)
                localStorage.setItem("token", data.token);
                localStorage.setItem("id", data.id);
                localStorage.setItem("email", data.email);
                localStorage.setItem("role", data.roles[0]);

                console.log("✅ Token stocké:", data.token);
                console.log("✅ Rôle:", data.roles[0]);

                setMessage("Connexion réussie ! Redirection...");
                setMessageType("success");

                // ✅ Redirection selon le rôle après 1 seconde
                setTimeout(() => {
                    if (data.roles[0] === "ROLE_ADMIN") {
                        window.location.href = "/admin/dashboard";
                    } else if (data.roles[0] === "ROLE_EMPLOYE") {
                        window.location.href = "/employe/dashboard";
                    } else {
                        window.location.href = "/";
                    }
                }, 1000);

            } else {
                const errorText = await response.text();
                console.error("❌ Erreur de connexion:", errorText);
                setMessage("Erreur : " + errorText);
                setMessageType("error");
                setLoading(false);
            }
        } catch (error) {
            console.error("❌ Erreur fetch :", error);
            setMessage("Erreur serveur : Impossible de se connecter");
            setMessageType("error");
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 to-indigo-100">
            <div className="bg-white p-10 rounded-2xl shadow-2xl w-[400px] space-y-6">
                <div className="text-center">
                    <h2 className="text-3xl font-bold text-gray-800 mb-2">
                        Connexion
                    </h2>
                    <p className="text-gray-600 text-sm">
                        Connectez-vous pour accéder à votre compte
                    </p>
                </div>

                {message && (
                    <div
                        className={`p-4 rounded-lg text-center text-sm font-medium ${
                            messageType === "success"
                                ? "bg-green-100 text-green-800 border border-green-300"
                                : "bg-red-100 text-red-800 border border-red-300"
                        }`}
                    >
                        {message}
                    </div>
                )}

                <form onSubmit={handleLogin} className="space-y-5">
                    <InputField
                        label="Email"
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                        placeholder="exemple@email.com"
                    />

                    <InputField
                        label="Mot de passe"
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                        placeholder="••••••••"
                    />

                    <button
                        type="submit"
                        disabled={loading}
                        className="w-full bg-blue-600 text-white py-3 rounded-lg hover:bg-blue-700 font-medium transition-colors disabled:bg-gray-400 disabled:cursor-not-allowed shadow-lg hover:shadow-xl"
                    >
                        {loading ? (
                            <span className="flex items-center justify-center">
                                <svg className="animate-spin h-5 w-5 mr-2" viewBox="0 0 24 24">
                                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" fill="none"></circle>
                                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                                </svg>
                                Connexion en cours...
                            </span>
                        ) : (
                            "Se connecter"
                        )}
                    </button>
                </form>

                <div className="text-center pt-4 border-t border-gray-200">
                    <p className="text-sm text-gray-600">
                        Pas de compte ?{" "}
                        <a
                            href="/Inscription"
                            className="text-blue-600 font-medium hover:underline hover:text-blue-700"
                        >
                            Créer un compte
                        </a>
                    </p>
                </div>

                {/* ✅ Zone de debug (à retirer en production) */}
                <div className="mt-4 p-3 bg-gray-50 rounded text-xs text-gray-600">
                    <p><strong>🔍 Debug:</strong></p>
                    <p>Token présent: {localStorage.getItem("token") ? "✅ OUI" : "❌ NON"}</p>
                    <p>Email: {localStorage.getItem("email") || "Non connecté"}</p>
                    <p>Rôle: {localStorage.getItem("role") || "Aucun"}</p>
                </div>
            </div>
        </div>
    );
}