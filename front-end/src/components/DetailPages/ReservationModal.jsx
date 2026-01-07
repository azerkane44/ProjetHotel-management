import { useState, useEffect } from "react";

export default function ReservationModal({ chambre, onClose }) {
    const [dateDebut, setDateDebut] = useState("");
    const [dateFin, setDateFin] = useState("");
    const [nomClient, setNomClient] = useState("");
    const [telephoneClient, setTelephoneClient] = useState("");
    const [nombrePersonnes, setNombrePersonnes] = useState(1);
    const [message, setMessage] = useState("");
    const [messageType, setMessageType] = useState("");
    const [loading, setLoading] = useState(false);
    const [isConnected, setIsConnected] = useState(false);
    const [checkingAuth, setCheckingAuth] = useState(true);

    // ✅ Vérifier si l'utilisateur est connecté au chargement
    useEffect(() => {
        const checkAuth = () => {
            const token = localStorage.getItem("token");
            console.log("🔍 Vérification token:", token ? "✅ Présent" : "❌ Absent");
            console.log("🔍 Token complet:", token);

            setIsConnected(!!token);
            setCheckingAuth(false);
        };

        checkAuth();
    }, []);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setMessage("");
        setMessageType("");
        setLoading(true);

        // ✅ Vérifier si l'utilisateur est connecté
        const token = localStorage.getItem("token");

        console.log("🔐 Token lors de la soumission:", token ? "✅ Présent" : "❌ Absent");

        if (!token) {
            setMessage("⚠️ Veuillez vous connecter pour réserver");
            setMessageType("error");
            setLoading(false);

            // Rediriger vers la page de connexion après 2 secondes
            setTimeout(() => {
                window.location.href = "/Connexion";
            }, 2000);
            return;
        }

        const reservationData = {
            chambreId: chambre.id,
            dateDebut,
            dateFin,
            nomClient,
            telephoneClient,
            nombrePersonnes: parseInt(nombrePersonnes)
        };

        console.log("📤 Envoi réservation:", reservationData);

        try {
            const response = await fetch("http://localhost:8080/api/reservations", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify(reservationData)
            });

            const data = await response.json();
            console.log("📥 Réponse:", data);

            if (!response.ok) {
                if (response.status === 401) {
                    setMessage("⚠️ Session expirée. Veuillez vous reconnecter");
                    setMessageType("error");
                    setTimeout(() => {
                        localStorage.removeItem("token");
                        window.location.href = "/Connexion";
                    }, 2000);
                } else {
                    setMessage(data.error || "Erreur lors de la réservation");
                    setMessageType("error");
                }
                setLoading(false);
                return;
            }

            setMessage(`✅ Réservation confirmée ! Code : ${data.codeConfirmation}\n📧 Un email de confirmation a été envoyé`);
            setMessageType("success");

            setTimeout(() => {
                onClose();
                window.location.reload();
            }, 3000);

        } catch (error) {
            console.error("❌ Erreur:", error);
            setMessage("❌ Erreur serveur");
            setMessageType("error");
        } finally {
            setLoading(false);
        }
    };

    // Calculer le prix total
    const calculateTotal = () => {
        if (!dateDebut || !dateFin) return 0;
        const debut = new Date(dateDebut);
        const fin = new Date(dateFin);
        const nights = Math.ceil((fin - debut) / (1000 * 60 * 60 * 24));
        return nights > 0 ? nights * chambre.prixParNuit : 0;
    };

    // ✅ Afficher un loader pendant la vérification
    if (checkingAuth) {
        return (
            <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
                <div className="bg-white rounded-2xl p-8 max-w-md w-full shadow-2xl text-center">
                    <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
                    <p className="mt-4 text-gray-600">Vérification...</p>
                </div>
            </div>
        );
    }

    // ✅ Si l'utilisateur n'est pas connecté, afficher un message
    if (!isConnected) {
        return (
            <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
                <div className="bg-white rounded-2xl p-8 max-w-md w-full shadow-2xl text-center">
                    <div className="mb-6">
                        <svg className="mx-auto h-16 w-16 text-yellow-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
                        </svg>
                    </div>
                    <h2 className="text-2xl font-bold text-gray-800 mb-4">
                        Connexion requise
                    </h2>
                    <p className="text-gray-600 mb-6">
                        Vous devez être connecté pour effectuer une réservation.
                    </p>
                    <div className="flex gap-3">
                        <button
                            onClick={() => window.location.href = "/Connexion"}
                            className="flex-1 bg-blue-600 text-white py-3 rounded-lg hover:bg-blue-700 font-medium transition-colors"
                        >
                            Se connecter
                        </button>
                        <button
                            onClick={() => window.location.href = "/Inscription"}
                            className="flex-1 bg-green-600 text-white py-3 rounded-lg hover:bg-green-700 font-medium transition-colors"
                        >
                            S'inscrire
                        </button>
                    </div>
                    <button
                        onClick={onClose}
                        className="mt-4 text-gray-500 hover:text-gray-700 underline"
                    >
                        Annuler
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
            <div className="bg-white rounded-2xl p-8 max-w-md w-full shadow-2xl max-h-[90vh] overflow-y-auto">
                <div className="flex justify-between items-center mb-6">
                    <h2 className="text-2xl font-bold text-gray-800">
                        Réserver {chambre.nom}
                    </h2>
                    <button
                        onClick={onClose}
                        className="text-gray-500 hover:text-gray-700 text-3xl leading-none"
                    >
                        ×
                    </button>
                </div>

                {message && (
                    <div
                        className={`p-4 rounded-lg mb-4 text-center whitespace-pre-line ${
                            messageType === "success"
                                ? "bg-green-100 text-green-800 border border-green-300"
                                : "bg-red-100 text-red-800 border border-red-300"
                        }`}
                    >
                        {message}
                    </div>
                )}

                <form onSubmit={handleSubmit} className="space-y-4">
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Nom complet *
                        </label>
                        <input
                            type="text"
                            value={nomClient}
                            onChange={(e) => setNomClient(e.target.value)}
                            required
                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                            placeholder="Jean Dupont"
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Téléphone *
                        </label>
                        <input
                            type="tel"
                            value={telephoneClient}
                            onChange={(e) => setTelephoneClient(e.target.value)}
                            required
                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                            placeholder="+33 6 12 34 56 78"
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Date d'arrivée *
                        </label>
                        <input
                            type="date"
                            value={dateDebut}
                            onChange={(e) => setDateDebut(e.target.value)}
                            required
                            min={new Date().toISOString().split('T')[0]}
                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Date de départ *
                        </label>
                        <input
                            type="date"
                            value={dateFin}
                            onChange={(e) => setDateFin(e.target.value)}
                            required
                            min={dateDebut || new Date().toISOString().split('T')[0]}
                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Nombre de personnes *
                        </label>
                        <input
                            type="number"
                            value={nombrePersonnes}
                            onChange={(e) => setNombrePersonnes(e.target.value)}
                            required
                            min="1"
                            max={chambre.capacite}
                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                        />
                        <p className="text-xs text-gray-500 mt-1">
                            Capacité maximale : {chambre.capacite} personne(s)
                        </p>
                    </div>

                    <div className="bg-gradient-to-r from-blue-50 to-indigo-50 p-4 rounded-lg border border-blue-200">
                        <div className="flex justify-between items-center mb-2">
                            <span className="text-sm text-gray-600">Prix par nuit</span>
                            <span className="text-lg font-semibold text-gray-800">
                                {chambre.prixParNuit} €
                            </span>
                        </div>
                        {dateDebut && dateFin && (
                            <div className="flex justify-between items-center pt-2 border-t border-blue-200">
                                <span className="text-sm font-medium text-gray-700">Prix total</span>
                                <span className="text-2xl font-bold text-blue-600">
                                    {calculateTotal()} €
                                </span>
                            </div>
                        )}
                    </div>

                    <button
                        type="submit"
                        disabled={loading}
                        className="w-full bg-blue-600 text-white py-3 rounded-lg hover:bg-blue-700 font-medium transition-colors disabled:bg-gray-400 disabled:cursor-not-allowed shadow-lg hover:shadow-xl"
                    >
                        {loading ? "Réservation en cours..." : "Confirmer la réservation"}
                    </button>

                    <p className="text-xs text-gray-500 text-center">
                        * Champs obligatoires | Un email de confirmation sera envoyé
                    </p>
                </form>
            </div>
        </div>
    );
}
