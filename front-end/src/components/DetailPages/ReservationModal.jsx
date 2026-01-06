// front-end/src/components/DetailPages/ReservationModal.jsx (amélioré)
import { useState } from 'react';

export default function ReservationModal({ isOpen, onClose, room, hotelName }) {
  const [formData, setFormData] = useState({
    dateArrivee: '',
    dateDepart: '',
    nombrePersonnes: 1,
    nom: '',
    prenom: '',
    email: '',
    telephone: '',
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);

  const calculateNights = () => {
    if (!formData.dateArrivee || !formData.dateDepart) return 0;
    const start = new Date(formData.dateArrivee);
    const end = new Date(formData.dateDepart);
    const diffTime = Math.abs(end - start);
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    return diffDays;
  };

  const calculateTotal = () => {
    const nights = calculateNights();
    return nights * (room?.prix || 0);
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      const token = localStorage.getItem('token');

      const reservationData = {
        chambreId: room.id,
        dateArrivee: formData.dateArrivee,
        dateDepart: formData.dateDepart,
        nombrePersonnes: formData.nombrePersonnes,
        prixTotal: calculateTotal(),
      };

      const response = await fetch('http://localhost:8080/api/reservations', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify(reservationData),
      });

      if (!response.ok) {
        throw new Error('Erreur lors de la réservation');
      }

      setSuccess(true);
      setTimeout(() => {
        onClose();
        setSuccess(false);
      }, 2000);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-2xl max-w-2xl w-full max-h-[90vh] overflow-y-auto shadow-2xl">
        <div className="sticky top-0 bg-gradient-to-r from-blue-600 to-blue-800 text-white p-6 rounded-t-2xl">
          <div className="flex justify-between items-center">
            <div>
              <h2 className="text-2xl font-bold">Réserver votre séjour</h2>
              <p className="text-blue-100 mt-1">{hotelName}</p>
            </div>
            <button
              onClick={onClose}
              className="text-white hover:bg-white hover:bg-opacity-20 rounded-full p-2 transition"
            >
              ✕
            </button>
          </div>
        </div>

        <div className="p-6">
          {success ? (
            <div className="text-center py-10">
              <div className="text-6xl mb-4">✅</div>
              <h3 className="text-2xl font-bold text-green-600 mb-2">
                Réservation confirmée !
              </h3>
              <p className="text-gray-600">
                Vous allez recevoir un email de confirmation
              </p>
            </div>
          ) : (
            <>
              {/* Détails de la chambre */}
              <div className="bg-blue-50 rounded-xl p-4 mb-6">
                <h3 className="font-bold text-lg mb-2">{room?.type}</h3>
                <div className="grid grid-cols-2 gap-4 text-sm">
                  <div>
                    <span className="text-gray-600">Prix par nuit:</span>
                    <span className="font-bold ml-2">{room?.prix}€</span>
                  </div>
                  <div>
                    <span className="text-gray-600">Capacité:</span>
                    <span className="font-bold ml-2">{room?.capacite} pers.</span>
                  </div>
                </div>
              </div>

              <form onSubmit={handleSubmit} className="space-y-4">
                {/* Dates */}
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Date d'arrivée *
                    </label>
                    <input
                      type="date"
                      name="dateArrivee"
                      value={formData.dateArrivee}
                      onChange={handleChange}
                      required
                      min={new Date().toISOString().split('T')[0]}
                      className="w-full border border-gray-300 rounded-lg p-3 focus:ring-2 focus:ring-blue-500"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Date de départ *
                    </label>
                    <input
                      type="date"
                      name="dateDepart"
                      value={formData.dateDepart}
                      onChange={handleChange}
                      required
                      min={formData.dateArrivee || new Date().toISOString().split('T')[0]}
                      className="w-full border border-gray-300 rounded-lg p-3 focus:ring-2 focus:ring-blue-500"
                    />
                  </div>
                </div>

                {/* Nombre de personnes */}
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Nombre de personnes *
                  </label>
                  <input
                    type="number"
                    name="nombrePersonnes"
                    value={formData.nombrePersonnes}
                    onChange={handleChange}
                    required
                    min="1"
                    max={room?.capacite}
                    className="w-full border border-gray-300 rounded-lg p-3 focus:ring-2 focus:ring-blue-500"
                  />
                </div>

                {/* Récapitulatif */}
                {calculateNights() > 0 && (
                  <div className="bg-gray-50 rounded-xl p-4 space-y-2">
                    <div className="flex justify-between">
                      <span className="text-gray-600">Nombre de nuits:</span>
                      <span className="font-semibold">{calculateNights()}</span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-gray-600">Prix par nuit:</span>
                      <span className="font-semibold">{room?.prix}€</span>
                    </div>
                    <div className="border-t pt-2 flex justify-between text-lg">
                      <span className="font-bold">Total:</span>
                      <span className="font-bold text-blue-600">
                        {calculateTotal()}€
                      </span>
                    </div>
                  </div>
                )}

                {error && (
                  <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-lg">
                    {error}
                  </div>
                )}

                {/* Boutons */}
                <div className="flex gap-3 pt-4">
                  <button
                    type="button"
                    onClick={onClose}
                    className="flex-1 bg-gray-200 text-gray-700 py-3 rounded-lg font-semibold hover:bg-gray-300 transition"
                  >
                    Annuler
                  </button>
                  <button
                    type="submit"
                    disabled={loading || calculateNights() === 0}
                    className="flex-1 bg-blue-600 text-white py-3 rounded-lg font-semibold hover:bg-blue-700 transition disabled:bg-gray-400 disabled:cursor-not-allowed"
                  >
                    {loading ? 'Réservation...' : 'Confirmer la réservation'}
                  </button>
                </div>
              </form>
            </>
          )}
        </div>
      </div>
    </div>
  );
}