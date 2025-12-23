import RoomCard from "./RoomCard";

function HotelRooms() {
    const rooms = [
    {
      id: 1,
      name: "Chambre Classique",
      capacity: 2,
      size: 25,
      bedType: "Lit Queen",
      amenities: ["Climatisation", "Minibar", "Télévision écran plat"],
      pricePerNight: 245,
      imageUrl: "https://picsum.photos/400/200",
    },
    {
      id: 2,
      name: "Suite Deluxe",
      capacity: 4,
      size: 45,
      bedType: "2 Lits King",
      amenities: ["Climatisation", "Minibar", "Télévision écran plat", "Jacuzzi"],
      pricePerNight: 450,
      imageUrl: "https://picsum.photos/400/201",
    },
    {
      id: 3,
      name: "Chambre Familiale",
      capacity: 5,
      size: 35,
      bedType: "1 Lit King + 1 Canapé-lit",
      amenities: ["Climatisation", "Minibar", "Télévision écran plat", "Kitchenette"],
      pricePerNight: 320,
      imageUrl: "https://picsum.photos/400/202",
    },
  ]
  return (
    <div>
      <h2 className="text-xl font-semibold mb-6">Nos chambres & suites</h2>

      <div className="space-y-6">
        <div className="grid grid-cols gap-6 flex-1">
          {rooms.map((room) => (
            <RoomCard key={room.id} room={room} />
          ))}
        </div>
      </div>
    </div>
  );
}
export default HotelRooms;
