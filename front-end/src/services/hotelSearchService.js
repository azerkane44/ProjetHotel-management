import { useState } from 'react';

const API_BASE = "http://localhost:8080/api/hotels";

export default function useHotelSearch() {
  const [hotels, setHotels] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // Charger tous les hôtels
  const loadAllHotels = async () => {
    setLoading(true);
    setError(null);

    try {
      const response = await fetch(API_BASE); // ✅ CHANGÉ ICI

      if (!response.ok) {
        throw new Error(`Erreur ${response.status}`);
      }

      const data = await response.json();
      setHotels(Array.isArray(data) ? data : []);
    } catch (err) {
      console.error("Erreur chargement hôtels:", err);
      setError(err.message);
      setHotels([]);
    } finally {
      setLoading(false);
    }
  };

  // Recherche avec filtres
  const searchHotels = async (filters) => {
    setLoading(true);
    setError(null);

    try {
      const params = new URLSearchParams();

      if (filters.ville) params.append('ville', filters.ville);
      if (filters.dateDebut) params.append('dateDebut', filters.dateDebut);
      if (filters.dateFin) params.append('dateFin', filters.dateFin);
      if (filters.nombrePersonnes) params.append('nombrePersonnes', filters.nombrePersonnes);
      if (filters.prixMin) params.append('prixMin', filters.prixMin);
      if (filters.prixMax) params.append('prixMax', filters.prixMax);
      if (filters.categorie) params.append('categorie', filters.categorie);
      if (filters.equipements && filters.equipements.length > 0) {
        params.append('equipements', filters.equipements.join(','));
      }

      const url = `${API_BASE}/search?${params.toString()}`;
      const response = await fetch(url);

      if (!response.ok) {
        throw new Error(`Erreur ${response.status}`);
      }

      const data = await response.json();
      setHotels(Array.isArray(data) ? data : []);
    } catch (err) {
      console.error("Erreur recherche:", err);
      setError(err.message);
      setHotels([]);
    } finally {
      setLoading(false);
    }
  };

  return {
    hotels,
    loading,
    error,
    loadAllHotels,
    searchHotels
  };
}