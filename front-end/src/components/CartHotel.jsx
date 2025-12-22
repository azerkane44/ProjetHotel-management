import { Link } from "react-router-dom";

export default function CardHotel({ hotel }) {
  // Fonction étoiles modernes
  const renderStars = (count) => {
    const filled = "text-yellow-400";
    const empty = "text-gray-300";

    return (
      <div className="flex text-sm">
        {Array.from({ length: count }).map((_, i) => (
          <span key={"filled-" + i} className={filled}>
            ★
          </span>
        ))}
        {Array.from({ length: 5 - count }).map((_, i) => (
          <span key={"empty-" + i} className={empty}>
            ★
          </span>
        ))}
      </div>
    );
  };

  return (
    <div
      className="
      w-64 
      h-92               /* HAUTEUR FIXE */
      bg-white 
      shadow-md 
      rounded-xl 
      overflow-hidden     /* MASQUE ce qui dépasse */
      hover:shadow-xl 
      transition
      relative
    "
    >
      {/* Image */}
      <div className="relative h-32">
        <img
          src={hotel.imageUrl}
          alt={hotel.name}
          className="w-full h-full object-cover"
        />
        <div className="absolute top-2 right-2 bg-white/90 px-2 py-1 text-xs rounded-md shadow">
          ★ {hotel.stars}
        </div>
      </div>

      {/* Contenu compact */}
      <div className="p-3 space-y-1 text-sm">
        <h3 className="font-bold text-gray-800 line-clamp-1">{hotel.name}</h3>

        <p className="text-gray-500 line-clamp-1 text-xs">
          📍 {hotel.city}, {hotel.country}
        </p>

        <div className="flex items-center gap-2">
          {renderStars(hotel.stars)}
          <span className="text-gray-400 text-xs">({hotel.reviews})</span>
        </div>

        <p className="text-gray-600 text-xs line-clamp-1">
          {hotel.equipments.join(" • ")}
        </p>

        <p className="text-gray-500 text-xs line-clamp-2">
          {hotel.description}
        </p>

        <div className="flex justify-between items-center pt-1">
          <p className="text-blue-600 font-bold">{hotel.price}€</p>
          <Link
            to={`/hotels/${hotel.id}`}
            className="bg-black text-white px-3 py-1.5 text-xs rounded-md hover:bg-gray-800"
          >
            Voir détails
          </Link>
        </div>
      </div>
    </div>
  );
}
