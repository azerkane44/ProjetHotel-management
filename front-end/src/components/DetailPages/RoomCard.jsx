import { Link } from "react-router-dom";

export default function RoomCard({ room }) {

  return (
    <div
      className="
      bg-white rounded-xl shadow
      grid grid-cols-1 md:grid-cols-3
      overflow-hidden
    "
    >
      {/* Image chambre */}
      <div className="md:col-span-1">
        <img
          src={room.imageUrl}
          alt={room.name}
          className="w-full h-full object-cover"
        />
      </div>

      {/* Infos */}
      <div className="md:col-span-2 p-5 flex flex-col justify-between">
        <div>
          <h3 className="font-bold text-lg">{room.name}</h3>
          <p className="text-sm text-gray-500">
            {room.capacity} personnes • {room.size}m² • {room.bedType}
          </p>

          <ul className="mt-3 text-sm text-gray-600 space-y-1">
            <li>{room.amenities.map((amenitie) => (
              <span key={amenitie}>• {amenitie} </span>
            ))}</li>
          </ul>
        </div>

        <div className="flex justify-between items-end mt-4">
          <div>
            <p className="text-blue-600 font-bold text-lg">{room.pricePerNight}€ / nuit</p>
            <p className="text-xs text-gray-400">Taxes incluses</p>
          </div>

          <button className="bg-black text-white px-4 py-2 rounded-lg">
            Réserver
          </button>
        </div>
      </div>
    </div>
  );
}
