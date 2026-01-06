// front-end/src/components/HotelMap.jsx
import { useEffect, useRef } from 'react';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';

// Fix pour les icônes Leaflet
delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon-2x.png',
  iconUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon.png',
  shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png',
});

export default function HotelMap({ hotels, onHotelClick, selectedHotelId }) {
  const mapRef = useRef(null);
  const mapInstanceRef = useRef(null);
  const markersRef = useRef([]);

  useEffect(() => {
    if (!mapRef.current) return;

    // Initialiser la carte
    if (!mapInstanceRef.current) {
      mapInstanceRef.current = L.map(mapRef.current).setView([48.8566, 2.3522], 6);

      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '© OpenStreetMap contributors',
        maxZoom: 19,
      }).addTo(mapInstanceRef.current);
    }

    // Nettoyer les anciens marqueurs
    markersRef.current.forEach(marker => marker.remove());
    markersRef.current = [];

    // Ajouter les nouveaux marqueurs
    if (hotels && hotels.length > 0) {
      const bounds = [];

      hotels.forEach(hotel => {
        if (hotel.latitude && hotel.longitude) {
          const isSelected = hotel.id === selectedHotelId;

          const customIcon = L.divIcon({
            className: 'custom-marker',
            html: `
              <div class="relative">
                <div class="${isSelected ? 'bg-red-600' : 'bg-blue-600'} text-white rounded-full w-10 h-10 flex items-center justify-center shadow-lg border-2 border-white transform ${isSelected ? 'scale-125' : 'scale-100'} transition-transform">
                  <span class="text-lg">🏨</span>
                </div>
                <div class="absolute -bottom-1 left-1/2 transform -translate-x-1/2 w-0 h-0 border-l-4 border-r-4 border-t-4 ${isSelected ? 'border-t-red-600' : 'border-t-blue-600'} border-l-transparent border-r-transparent"></div>
              </div>
            `,
            iconSize: [40, 40],
            iconAnchor: [20, 40],
          });

          const marker = L.marker([hotel.latitude, hotel.longitude], { icon: customIcon })
            .addTo(mapInstanceRef.current);

          marker.bindPopup(`
            <div class="p-2">
              <h3 class="font-bold text-lg">${hotel.nom}</h3>
              <p class="text-gray-600">${hotel.ville}</p>
              <p class="text-yellow-500 font-semibold">⭐ ${hotel.noteMoyenne || 'N/A'}</p>
              <p class="text-blue-600 font-bold">${hotel.prixMoyenNuit || 'N/A'}€ / nuit</p>
            </div>
          `);

          marker.on('click', () => {
            if (onHotelClick) {
              onHotelClick(hotel);
            }
          });

          markersRef.current.push(marker);
          bounds.push([hotel.latitude, hotel.longitude]);
        }
      });

      // Ajuster la vue pour afficher tous les marqueurs
      if (bounds.length > 0) {
        mapInstanceRef.current.fitBounds(bounds, { padding: [50, 50] });
      }
    }

    return () => {
      // Cleanup si nécessaire
    };
  }, [hotels, selectedHotelId, onHotelClick]);

  return (
    <div className="w-full h-full rounded-xl overflow-hidden shadow-lg">
      <div ref={mapRef} className="w-full h-full min-h-[500px]" />
    </div>
  );
}