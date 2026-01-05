import { useEffect, useState } from "react";
import BarRecherche from "../components/BarRecherche";
import CardHotel from "../components/CardHotel";
import Filter from "../components/filter";

export default function HomePage() {
    const [hotels, setHotels] = useState([]);

    useEffect(() => {
        fetch("http://localhost:8080/api/hotels") // PAS de retour à la ligne ici
            .then((response) => response.json())
            .then((data) => setHotels(data))
            .catch((error) => console.error("Erreur:", error));
    }, []);

    return (
        <div>
            <BarRecherche />

            <div className="flex w-[90%] mx-auto mt-10 gap-6">
                <Filter />

                <div className="flex-1">
                    {hotels.length === 0 ? (
                        <p className="text-center text-gray-500 mt-10">
                            Aucun hôtel disponible.
                        </p>
                    ) : (
                        <div className="grid grid-cols-3 gap-6">
                            {hotels.map((hotel) => (
                                <CardHotel key={hotel.id} hotel={hotel} />
                            ))}
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}
