import { useState } from "react";

export default function Filter() {
  const [price, setPrice] = useState(250); // valeur par défaut au milieu
  
  return (
    <div className="w-72 bg-white shadow-xl rounded-2xl p-6 space-y">
      
      {/* Titre */}
      <h2 className="text-2xl font-semibold text-gray-800">Filtres</h2>

      {/* Prix */}
      <div className="space-y-3">
        <label className="font-medium text-gray-700">Prix par nuit</label>
        
        <div className="flex justify-between text-sm text-gray-500">
          <span>20€</span>
          <span>500€</span>
        </div>

        {/* je veux mettre le prix au dessus du point qui coulisse */}
        
        <input
          type="range"
          min="20"
          max="500"
          value={price}
          onChange={(e) => setPrice(e.target.value)}
          className="w-full accent-blue-600"
        />

        <p className="text-center text-blue-600 font-semibold">
          {price} € / nuit max
        </p>
      </div>

      {/* Catégorie */}
      <div className="space-y-3">
        <label className="font-medium text-gray-700">Catégorie</label>

        <div className="flex flex-col space-y-2 text-gray-700">
          <label className="flex items-center gap-3">
            <input type="checkbox" className="h-4 w-4" /> 5 étoiles
          </label>
          <label className="flex items-center gap-3">
            <input type="checkbox" className="h-4 w-4" /> 4 étoiles
          </label>
          <label className="flex items-center gap-3">
            <input type="checkbox" className="h-4 w-4" /> 3 étoiles
          </label>
        </div>
      </div>

      {/* Équipements */}
      <div className="space-y-3">
        <label className="font-medium text-gray-700">Équipements</label>

        <div className="flex flex-col space-y-2 text-gray-700">
          <label className="flex items-center gap-3">
            <input type="checkbox" className="h-4 w-4" /> Wifi
          </label>
          <label className="flex items-center gap-3">
            <input type="checkbox" className="h-4 w-4" /> Restaurant
          </label>
          <label className="flex items-center gap-3">
            <input type="checkbox" className="h-4 w-4" /> Parking
          </label>
          <label className="flex items-center gap-3">
            <input type="checkbox" className="h-4 w-4" /> Piscine
          </label>
          <label className="flex items-center gap-3">
            <input type="checkbox" className="h-4 w-4" /> Spa
          </label>
        </div>
      </div>

      {/* Boutons */}
      <div className="space-y-3 pt-4">
        <button className="w-full bg-blue-600 text-white py-3 rounded-xl font-semibold hover:bg-blue-700 transition">
          Appliquer les filtres
        </button>

        <button className="w-full bg-gray-100 py-3 rounded-xl text-gray-600 hover:bg-gray-200 transition">
          Réinitialiser
        </button>
      </div>
    </div>
  );
}
