import React from "react";
import { Link } from "react-router-dom";

export default function DetailsPage() {
  return (
    <div className="w-full min-h-screen bg-white">
      
      {/* ===== Header minimal : retour ===== */}
      <div className="fixed top-0 left-0 w-full z-50 bg-white/80 backdrop-blur">
        <div className="max-w-7xl mx-auto px-6 py-4">
          <Link
            to="/"
            className="text-sm font-medium text-gray-700 hover:text-black flex items-center gap-2"
          >
            ← Retour aux résultats
          </Link>
        </div>
      </div>

      {/* ===== Galerie image / Hero ===== */}
      <section className="w-full h-[70vh] relative">
        <img
          src="https://images.unsplash.com/photo-1542314831-068cd1dbfeeb"
          alt="Hotel"
          className="w-full h-full object-cover"
        />

        {/* Navigation images (optionnel plus tard) */}
        <button className="absolute left-4 top-1/2 -translate-y-1/2 bg-white/80 rounded-full p-2">
          ‹
        </button>
        <button className="absolute right-4 top-1/2 -translate-y-1/2 bg-white/80 rounded-full p-2">
          ›
        </button>
      </section>

      {/* ===== Contenu principal ===== */}
      <main className="max-w-7xl mx-auto px-6 py-12 grid grid-cols-1 lg:grid-cols-3 gap-10">
        
        {/* ===== Colonne gauche (infos) ===== */}
        <div className="lg:col-span-2 space-y-10">

          {/* Infos générales */}
          <section>
            <div className="flex items-center gap-3 mb-2">
              <span className="bg-yellow-500 text-white text-xs px-2 py-1 rounded">
                5★ Luxe
              </span>
              <span className="border text-xs px-2 py-1 rounded">
                Recommandé
              </span>
            </div>

            <h1 className="text-3xl font-bold mb-2">
              Grand Hôtel de Paris
            </h1>

            <div className="flex items-center gap-3 text-sm text-gray-600">
              <span>📍 Paris, France</span>
              <span>⭐⭐⭐⭐⭐</span>
              <span>4.8 (350 avis)</span>
            </div>
          </section>

          {/* Équipements & services */}
          <section>
            <h2 className="text-xl font-semibold mb-4">
              Équipements & Services
            </h2>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div className="border rounded-lg p-4">
                📶 WiFi gratuit
              </div>
              <div className="border rounded-lg p-4">
                🍽 Restaurant gastronomique
              </div>
              <div className="border rounded-lg p-4">
                🚗 Parking avec voiturier
              </div>
              <div className="border rounded-lg p-4">
                🧘 Spa & wellness
              </div>
            </div>
          </section>

          {/* Description */}
          <section>
            <h2 className="text-xl font-semibold mb-4">
              À propos de l’hôtel
            </h2>

            <p className="text-gray-700 leading-relaxed">
              Le Grand Hôtel de Paris est un établissement cinq étoiles
              emblématique situé au cœur de la capitale française...
            </p>
          </section>

        </div>

        {/* ===== Colonne droite (sidebar) ===== */}
        <aside className="space-y-6">

          {/* Carte prix */}
          <div className="border rounded-xl p-6 shadow-sm sticky top-28">
            <p className="text-sm text-gray-600 mb-1">
              À partir de
            </p>
            <p className="text-3xl font-bold text-blue-600 mb-2">
              245€ <span className="text-sm text-gray-600">/ nuit</span>
            </p>

            <p className="text-xs text-gray-500 mb-4">
              Taxes et frais inclus
            </p>

            <button className="w-full bg-black text-white py-3 rounded-lg hover:bg-gray-800">
              Voir les disponibilités
            </button>
          </div>

          {/* Contact */}
          <div className="border rounded-xl p-6">
            <h3 className="font-semibold mb-3">Contact</h3>
            <p className="text-sm">📞 +33 1 23 45 67 89</p>
            <p className="text-sm">✉ contact@grandhotel.fr</p>
          </div>

        </aside>
      </main>
    </div>
  );
}
