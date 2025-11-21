
import BarRecherche from "../components/BarRecherche";
import CardHotel from "../components/CartHotel";
import Filter from "../components/filter";
import Navbar from "../components/NavBar";

export default function HomePage() {

  // TEMP data — remplacée plus tard par l’API Spring Boot
const hotels = [
  {
    id: 1,
    name: "Grand Hôtel de Paris",
    city: "Paris",
    country: "France",
    stars: 4,
    price: 245,
    reviews: 350,
    equipments: ["wifi", "restaurant", "parking", "piscine"],
    imageUrl: "https://picsum.photos/400/300",
    description: "Hôtel de luxe au cœur de Paris avec vue panoramique."
  },
  {
    id: 2,
    name: "Hôtel Côte d'Azur",
    city: "Nice",
    country: "France",
    stars: 5,
    price: 320,
    reviews: 190,
    equipments: ["wifi", "spa", "piscine"],
    imageUrl: "https://picsum.photos/400/301",
    description: "Chambres modernes avec accès direct à la plage."
  },
  {
    id: 3,
    name: "Mountain Retreat",
    city: "Chamonix",
    country: "France",
    stars: 3,
    price: 150,
    reviews: 85,
    equipments: ["wifi", "parking"],
    imageUrl: "https://picsum.photos/400/302",
    description: "Charmant hôtel au pied des montagnes avec vue imprenable."
  }
];
    // Duplique pour tester

  return (
    <div>
      <Navbar />
      <BarRecherche />

      <div className="flex w-[90%] mx-auto mt-10 gap-6">
        <Filter />

        <div className="grid grid-cols-3 gap-6 flex-1">
          {hotels.map((hotel) => (
            <CardHotel key={hotel.id} hotel={hotel} />
          ))}
        </div>
      </div>
    </div>
  );
}
