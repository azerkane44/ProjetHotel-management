// front-end/src/Pages/HomePage.jsx (version complète)
import { useEffect, useState } from "react";
import BarRecherche from "../components/BarRecherche";
import CardHotel from "../components/CardHotel";
import HotelMap from "../components/HotelMap";
import { useHotelSearch } from "../hooks/useHotelSearch";
import Filter from "../components/FilterPro.jsx";

export default function HomePage() {
  const [allHotels, setAllHotels] = useState([]);
  const [displayedHotels, setDisplayedHotels] = useState([]);
  const [showMap, setShowMap] = useState(false);
  const [selectedHotelId, setSelectedHotelId] = useState(null);
  const { hotels, loading, error, searchHotels } = useHotelSearch();

  // Charger tous les hôtels au démarrage
  useEffect(() => {
    fetch("http://localhost:8080/api/hotels")
      .then((response) => response.json())
      .then((data) => {
        setAllHotels(data);
        setDisplayedHotels(data);
      })
      .catch((error) => console.error("Erreur:", error));
  }, []);

  // Mettre à jour les hôtels affichés après une recherche
  useEffect(() => {
    if (hotels.length > 0) {
      setDisplayedHotels(hotels);
    }
  }, [hotels]);

  const handleSearch = async (searchParams) => {
    await searchHotels(searchParams);
  };

  const handleFilterChange = async (filters) => {
    // Combiner les filtres avec les résultats de recherche actuels
    const baseHotels = hotels.length > 0 ? hotels : allHotels;

    const filtered = baseHotels.filter(hotel => {
      // Filtre prix
      if (filters.prixMax && hotel.prixMoyenNuit > filters.prixMax) return false;
      if (filters.prixMin && hotel.prixMoyenNuit < filters.prixMin) return false;

      // Filtre catégorie
      if (filters.categories && filters.categories.length > 0) {
        if (!filters.categories.includes(hotel.categorie)) return false;
      }

      // Filtre notation
      if (filters.notationMin && hotel.noteMoyenne < filters.notationMin) return false;

      // Filtre équipements
      if (filters.equipments && filters.equipments.length > 0) {
        const hotelEquipements = hotel.equipments || [];
        const hasAllEquipements = filters.equipments.every(eq =>
          hotelEquipements.includes(eq)
        );
        if (!hasAllEquipements) return false;
      }

      return true;
    });

    setDisplayedHotels(filtered);
  };

  const handleResetFilters = () => {
    setDisplayedHotels(allHotels);
  };

  const handleHotelClick = (hotel) => {
    setSelectedHotelId(hotel.id);
    // Scroll vers la carte ou la card
    const element = document.getElementById(`hotel-${hotel.id}`);
    if (element) {
      element.scrollIntoView({ behavior: 'smooth', block: 'center' });
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <BarRecherche onSearch={handleSearch} />

      <div className="w-[90%] mx-auto mt-10">
        {/* Bouton pour basculer entre liste et carte */}
        <div className="flex justify-end mb-4 gap-3">
          <button
            onClick={() => setShowMap(false)}
            className={`px-6 py-2 rounded-lg font-semibold transition ${
              !showMap
                ? "bg-blue-600 text-white"
                : "bg-white text-gray-700 border border-gray-300"
            }`}
          >
            📋 Liste
          </button>
          <button
            onClick={() => setShowMap(true)}
            className={`px-6 py-2 rounded-lg font-semibold transition ${
              showMap
                ? "bg-blue-600 text-white"
                : "bg-white text-gray-700 border border-gray-300"
            }`}
          >
            🗺️ Carte
          </button>
        </div>

        <div className="flex gap-6">
          <Filter
            onFilterChange={handleFilterChange}
            onReset={handleResetFilters}
          />

          <div className="flex-1">
            {loading && (
              <div className="flex justify-center items-center h-64">
                <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
              </div>
            )}

            {error && (
              <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
                {error}
              </div>
            )}

            {!loading && !error && displayedHotels.length === 0 && (
              <div className="text-center py-20">
                <p className="text-gray-500 text-xl">
                  😔 Aucun hôtel ne correspond à vos critères
                </p>
                <button
                  onClick={handleResetFilters}
                  className="mt-4 bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700"
                >
                  Réinitialiser les filtres
                </button>
              </div>
            )}

            {!loading && !error && displayedHotels.length > 0 && (
              <>
                {!showMap ? (
                  // Vue en grille
                  <div>
                    <p className="text-gray-600 mb-4">
                      {displayedHotels.length} hôtel(s) trouvé(s)
                    </p>
                    <div className="grid grid-cols-3 gap-6">
                      {displayedHotels.map((hotel) => (
                        <div
                          key={hotel.id}
                          id={`hotel-${hotel.id}`}
                          className={`transition-all ${
                            selectedHotelId === hotel.id
                              ? "ring-4 ring-blue-500 rounded-lg"
                              : ""
                          }`}
                        >
                          <CardHotel hotel={hotel} />
                        </div>
                      ))}
                    </div>
                  </div>
                ) : (
                  // Vue carte
                  <div className="h-[700px]">
                    <HotelMap
                      hotels={displayedHotels}
                      onHotelClick={handleHotelClick}
                      selectedHotelId={selectedHotelId}
                    />
                  </div>
                )}
              </>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}