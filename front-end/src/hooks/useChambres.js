import { useState, useEffect } from 'react';
import chambreService from '../services/chambreService';

export const useChambres = (hotelId = null) => {
  const [chambres, setChambres] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    // ✅ CORRECTION : Charger TOUJOURS les chambres
    fetchChambres();
  }, [hotelId]);

  const fetchChambres = async () => {
    try {
      console.log('🔄 Chargement des chambres... hotelId:', hotelId);
      setLoading(true);
      setError(null);

      const data = hotelId
        ? await chambreService.getChambresByHotel(hotelId)
        : await chambreService.getAllChambres();

      console.log('✅ Chambres récupérées:', data);
      console.log('📊 Nombre de chambres:', data?.length || 0);
      setChambres(data || []);
    } catch (err) {
      console.error('❌ Erreur chargement chambres:', err);
      setError(err.response?.data?.message || 'Erreur lors du chargement des chambres');
      setChambres([]);
    } finally {
      setLoading(false);
    }
  };

  const creerChambre = async (chambre) => {
    try {
      console.log('➕ Création chambre:', chambre);
      const nouvelle = await chambreService.creerChambre(chambre);
      console.log('✅ Chambre créée:', nouvelle);

      // Recharger la liste complète
      await fetchChambres();

      return { success: true, data: nouvelle };
    } catch (err) {
      console.error('❌ Erreur création:', err);
      return {
        success: false,
        error: err.response?.data?.message || 'Erreur lors de la création'
      };
    }
  };

  const updateChambre = async (id, chambre) => {
    try {
      console.log('✏️ Modification chambre:', id, chambre);
      const updated = await chambreService.updateChambre(id, chambre);
      console.log('✅ Chambre modifiée:', updated);

      // Recharger la liste complète
      await fetchChambres();

      return { success: true, data: updated };
    } catch (err) {
      console.error('❌ Erreur modification:', err);
      return {
        success: false,
        error: err.response?.data?.message || 'Erreur lors de la modification'
      };
    }
  };

  const deleteChambre = async (id) => {
    try {
      console.log('🗑️ Suppression chambre:', id);
      await chambreService.deleteChambre(id);
      console.log('✅ Chambre supprimée');

      // Recharger la liste complète
      await fetchChambres();

      return { success: true };
    } catch (err) {
      console.error('❌ Erreur suppression:', err);
      return {
        success: false,
        error: err.response?.data?.message || 'Erreur lors de la suppression'
      };
    }
  };

  return {
    chambres,
    loading,
    error,
    fetchChambres,
    creerChambre,
    updateChambre,
    deleteChambre
  };
};