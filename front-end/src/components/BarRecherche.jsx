export default function BarRecherche() {
  return (
    <div className="w-full bg-blue-600 text-white p-10 flex flex-col items-center">
      <h2 className="text-3xl font-semibold mb-2">Trouvez votre hôtel idéal</h2>
      <p className="text-lg mb-6">Des milliers d'hôtels au meilleur prix</p>

      <div className="bg-white w-[90%] max-w-5xl rounded-xl shadow-lg p-6 grid grid-cols-4 gap-4 text-black">
        <input
          type="text"
          placeholder="Destination"
          className="border rounded-lg p-3"
        />

        <input
          type="date"
          placeholder="Arrivée"
          className="border rounded-lg p-3"
        />

        <input
          type="date"
          placeholder="Départ"
          className="border rounded-lg p-3"
        />

        <input
          type="number"
          placeholder="Voyageurs"
          className="border rounded-lg p-3"
        />

        <button className="col-span-4 bg-black text-white py-3 rounded-lg hover:bg-gray-800 mt-2">
          Rechercher
        </button>
      </div>
    </div>
  );
}
