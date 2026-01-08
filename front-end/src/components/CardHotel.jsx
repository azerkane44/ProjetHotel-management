import React from "react";
import { Link } from "react-router-dom";

export default function CardHotel({ hotel }) {
  if (!hotel) return null;

  const imageSrc = hotel.imageUrl
    ? `http://localhost:8080${hotel.imageUrl}`
    : null;

  return (
    <div className="bg-white shadow rounded overflow-hidden hover:shadow-lg transition-shadow">
      {imageSrc ? (
        <img
          src={imageSrc}
          alt={hotel.nom || "Hôtel"}
          className="w-full h-48 object-cover"
          onError={(e) => {
            e.target.style.display = 'none';
            e.target.nextSibling.style.display = 'flex';
          }}
        />
      ) : null}

      {/* Placeholder SVG si pas d'image */}
      <div
        style={{
          display: imageSrc ? 'none' : 'flex',
          width: '100%',
          height: '192px',
          background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
          alignItems: 'center',
          justifyContent: 'center',
          color: 'white',
          fontSize: '48px'
        }}
      >
        🏨
      </div>

      <div className="p-4">
        <h3 className="text-lg font-semibold">{hotel.nom || "Nom non disponible"}</h3>

        {/* Ville et Pays */}
        <p className="text-gray-500 text-sm">
          📍 {hotel.ville || "Ville non disponible"}
          {hotel.pays && `, ${hotel.pays}`}
        </p>

        {/* Description (limitée à 100 caractères) */}
        {hotel.description && (
          <p className="text-gray-600 text-sm mt-2 line-clamp-2">
            {hotel.description.length > 100
              ? hotel.description.substring(0, 100) + "..."
              : hotel.description}
          </p>
        )}

        <div className="flex justify-between items-center mt-3">
          <span className="text-yellow-500 font-bold">
            ⭐ {hotel.noteMoyenne || "N/A"}
          </span>

          <Link
            to={`/hotel/${hotel.id}`}
            className="text-blue-600 hover:underline font-medium"
          >
            Voir détails
          </Link>
        </div>
      </div>
    </div>
  );
}