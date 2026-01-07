import { useState } from "react";
import InputField from "../components/InputField";

export default function InscriptionUser() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [message, setMessage] = useState("");
    const [messageType, setMessageType] = useState(""); // "success" ou "error"
    const [debug, setDebug] = useState(null);

    const handleRegister = async (e) => {
        e.preventDefault();
        setMessage("");
        setMessageType("");
        setDebug(null);

        const userData = {
            email,
            password
        };

        console.log("📤 Données envoyées :", userData);

        try {
            const response = await fetch("http://localhost:8080/api/v1/register", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(userData)
            });

            console.log("📡 Status HTTP :", response.status);

            const contentType = response.headers.get("content-type");

            let responseBody;
            if (contentType && contentType.includes("application/json")) {
                responseBody = await response.json();
            } else {
                responseBody = await response.text();
            }

            console.log("📥 Réponse backend :", responseBody);

            // ✅ Gestion des erreurs HTTP
            if (!response.ok) {
                // Afficher le message d'erreur du backend
                const errorMessage = responseBody.error || responseBody.message || "Erreur inconnue";
                setMessage(errorMessage);
                setMessageType("error");
                setDebug({
                    status: response.status,
                    body: responseBody
                });
                return;
            }

            // ✅ Succès
            setMessage("✅ Inscription réussie ! Redirection...");
            setMessageType("success");
            setDebug(responseBody);

            setTimeout(() => {
                window.location.href = "/Connexion";
            }, 1500);

        } catch (error) {
            console.error("🚨 Erreur FETCH :", error);
            setMessage("❌ Erreur serveur / réseau");
            setMessageType("error");
            setDebug(error.message);
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-50">
            <div className="bg-white p-10 rounded-2xl shadow-xl w-[380px] space-y-6">
                <h2 className="text-3xl font-bold text-center text-gray-800">
                    Inscription
                </h2>

                {/* Message de succès ou d'erreur */}
                {message && (
                    <div
                        className={`p-4 rounded-lg text-center ${
                            messageType === "success"
                                ? "bg-green-100 text-green-800 border border-green-300"
                                : "bg-red-100 text-red-800 border border-red-300"
                        }`}
                    >
                        {message}
                    </div>
                )}

                {/* 🔍 DEBUG VISUEL (à retirer en production) */}
                {debug && (
                    <details className="bg-gray-100 p-3 rounded text-xs">
                        <summary className="cursor-pointer font-semibold text-gray-700">
                            🔍 Détails techniques
                        </summary>
                        <pre className="mt-2 text-red-700 overflow-auto">
                            {JSON.stringify(debug, null, 2)}
                        </pre>
                    </details>
                )}

                <form onSubmit={handleRegister} className="space-y-5">
                    <InputField
                        label="Email"
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                    />

                    <InputField
                        label="Mot de passe"
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                        minLength={6}
                    />

                    <button
                        type="submit"
                        className="w-full bg-black text-white py-3 rounded-lg hover:bg-gray-800 font-medium transition-colors"
                    >
                        Créer un compte
                    </button>
                </form>

                <p className="text-center text-sm text-gray-600">
                    Déjà un compte ?{" "}
                    <a
                        href="/Connexion"
                        className="text-blue-600 font-medium hover:underline"
                    >
                        Connecte-toi
                    </a>
                </p>
            </div>
        </div>
    );
}