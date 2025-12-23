import HotelHeader from "../components/DetailPages/HotelHeader";
import HotelEquipments from "../components/DetailPages/HotelEquipments";
import HotelRooms from "../components/DetailPages/HotelRooms";
import HotelSummaryCard from "../components/DetailPages/HotelSummaryCard";
import HotelDescription from "../components/DetailPages/HotelDescription";

export default function DetailsPage() {
  return (
    <div className="w-full min-h-screen bg-gray-50">
      {/* HEADER IMAGE FULLSCREEN */}
      <section className="relative h-[60vh] w-full">
        {/* Image principale */}
        <img
          src="https://digital.ihg.com/is/image/ihg/intercontinental-tegucigalpa-6071105521-2x1"
          alt="Hotel"
          className="w-full h-full object-cover"
        />

        {/* Bouton retour */}
        <button
          className="
          absolute top-6 left-6
          bg-white/90 backdrop-blur
          px-4 py-2 rounded-lg shadow
          text-sm font-medium
        "
        >
          ← Retour
        </button>
      </section>

      {/* CONTENU PRINCIPAL */}
      <section className="max-w-7xl mx-auto px-6 py-10 grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* COLONNE GAUCHE */}
        <div className="lg:col-span-2 space-y-10">
          {/* INFOS HOTEL */}
          <HotelHeader />

          {/* ÉQUIPEMENTS */}
          <HotelEquipments />

          {/* DESCRIPTION */}
          <HotelDescription />

          {/* CHAMBRES */}
          <HotelRooms />
        </div>

        {/* COLONNE DROITE */}
        <div className="lg:col-span-1">
          <HotelSummaryCard />
        </div>
      </section>

      {/* INFOS COMPLEMENTAIRES
      <HotelPolicies /> */}
    </div>
  );
}
