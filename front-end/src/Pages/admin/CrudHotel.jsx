import { useEffect, useState } from "react";

const API_BASE = "http://localhost:8080/api/hotels";

export default function CrudHotel() {
  const [hotels, setHotels] = useState([]);
  const [editingId, setEditingId] = useState(null);
  const [error, setError] = useState(null);
  const [imageFile, setImageFile] = useState(null);

  const [form, setForm] = useState({
    nom: "",
    adresse: "",
    ville: "",
    pays: "",
    description: "",
    noteMoyenne: 0
  });

  const loadHotels = async () => {
    try {
      const res = await fetch(API_BASE);
      if (!res.ok) throw new Error(`Erreur ${res.status}`);
      const data = await res.json();

      if (Array.isArray(data)) {
        setHotels(data);
        setError(null);
      } else {
        console.error("La réponse n'est pas un tableau:", data);
        setHotels([]);
        setError("Format de données invalide");
      }
    } catch (err) {
      console.error("Erreur chargement:", err);
      setHotels([]);
      setError(err.message);
    }
  };

  useEffect(() => {
    loadHotels();
  }, []);

  const resetForm = () => {
    setEditingId(null);
    setImageFile(null);
    setForm({
      nom: "",
      adresse: "",
      ville: "",
      pays: "",
      description: "",
      noteMoyenne: 0
    });
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: value }));
  };

  const handleEdit = (hotel) => {
    setEditingId(hotel.id);
    setImageFile(null);
    setForm({
      nom: hotel.nom || "",
      adresse: hotel.adresse || "",
      ville: hotel.ville || "",
      pays: hotel.pays || "",
      description: hotel.description || "",
      noteMoyenne: hotel.noteMoyenne || 0
    });
    window.scrollTo({ top: 0, behavior: "smooth" });
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Supprimer cet hôtel ?")) return;
    try {
      const res = await fetch(`${API_BASE}/${id}`, { method: "DELETE" });
      if (!res.ok) throw new Error(`Erreur ${res.status}`);
      loadHotels();
      alert("Hôtel supprimé avec succès !");
    } catch (err) {
      console.error("Erreur suppression:", err);
      alert("Erreur lors de la suppression");
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const formData = new FormData();
    formData.append("nom", form.nom);
    formData.append("adresse", form.adresse);
    formData.append("ville", form.ville);
    formData.append("pays", form.pays);
    formData.append("description", form.description);
    formData.append("noteMoyenne", form.noteMoyenne);

    if (imageFile) {
      formData.append("image", imageFile);
    }

    try {
      let res;
      if (editingId) {
        // Update
        res = await fetch(`${API_BASE}/${editingId}`, {
          method: "PUT",
          body: formData
        });
      } else {
        // Create
        res = await fetch(API_BASE, {
          method: "POST",
          body: formData
        });
      }

      if (!res.ok) {
        const errorText = await res.text();
        throw new Error(`Erreur ${res.status}: ${errorText}`);
      }

      alert(editingId ? "Hôtel modifié avec succès !" : "Hôtel ajouté avec succès !");
      resetForm();
      loadHotels();
    } catch (err) {
      console.error("Erreur soumission:", err);
      alert(`Erreur: ${err.message}`);
    }
  };

  return (
    <div className="w-[90%] mx-auto mt-10">
      <h1 className="text-3xl font-bold mb-6">🏨 Gestion des hôtels</h1>

      {error && (
        <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
          ⚠️ Erreur: {error}
        </div>
      )}

      {/* Formulaire */}
      <form onSubmit={handleSubmit} className="bg-white p-6 shadow-lg rounded-lg mb-8">
        <h2 className="text-2xl font-semibold mb-4 text-blue-600">
          {editingId ? "✏️ Modifier l'hôtel" : "➕ Ajouter un hôtel"}
        </h2>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Nom de l'hôtel *
            </label>
            <input
              name="nom"
              value={form.nom}
              onChange={handleChange}
              placeholder="Ex: Hôtel Royal"
              className="border border-gray-300 p-2 rounded w-full focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              required
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Ville *
            </label>
            <input
              name="ville"
              value={form.ville}
              onChange={handleChange}
              placeholder="Ex: Paris"
              className="border border-gray-300 p-2 rounded w-full focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              required
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Pays *
            </label>
            <input
              name="pays"
              value={form.pays}
              onChange={handleChange}
              placeholder="Ex: France"
              className="border border-gray-300 p-2 rounded w-full focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              required
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Adresse
            </label>
            <input
              name="adresse"
              value={form.adresse}
              onChange={handleChange}
              placeholder="Ex: 123 Rue de la Paix"
              className="border border-gray-300 p-2 rounded w-full focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Note moyenne (0-5)
            </label>
            <input
              name="noteMoyenne"
              type="number"
              step="0.1"
              min="0"
              max="5"
              value={form.noteMoyenne}
              onChange={handleChange}
              placeholder="Ex: 4.5"
              className="border border-gray-300 p-2 rounded w-full focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Image de l'hôtel
            </label>
            <input
              type="file"
              accept="image/*"
              onChange={(e) => setImageFile(e.target.files[0])}
              className="border border-gray-300 p-2 rounded w-full focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
            {imageFile && (
              <p className="text-sm text-green-600 mt-1">
                ✅ {imageFile.name}
              </p>
            )}
          </div>
        </div>

        <div className="mt-4">
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Description
          </label>
          <textarea
            name="description"
            value={form.description}
            onChange={handleChange}
            placeholder="Décrivez l'hôtel..."
            rows="4"
            className="border border-gray-300 p-2 rounded w-full focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
        </div>

        <div className="flex gap-4 mt-6">
          <button
            type="submit"
            className="bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700 transition font-medium"
          >
            {editingId ? "💾 Mettre à jour" : "➕ Ajouter"}
          </button>
          <button
            type="button"
            onClick={resetForm}
            className="bg-gray-400 text-white px-6 py-2 rounded-lg hover:bg-gray-500 transition font-medium"
          >
            ❌ Annuler
          </button>
        </div>
      </form>

      {/* Liste */}
      <div className="bg-white shadow-lg rounded-lg overflow-hidden">
        <div className="bg-gradient-to-r from-blue-600 to-blue-800 p-4">
          <h2 className="text-xl font-bold text-white">
            📋 Liste des hôtels ({hotels.length})
          </h2>
        </div>

        {hotels.length === 0 ? (
          <p className="text-gray-500 p-10 text-center">Aucun hôtel trouvé</p>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead>
                <tr className="bg-gray-100 border-b-2 border-gray-300">
                  <th className="p-3 text-left font-semibold text-gray-700">ID</th>
                  <th className="p-3 text-left font-semibold text-gray-700">Image</th>
                  <th className="p-3 text-left font-semibold text-gray-700">Nom</th>
                  <th className="p-3 text-left font-semibold text-gray-700">Ville</th>
                  <th className="p-3 text-left font-semibold text-gray-700">Pays</th>
                  <th className="p-3 text-left font-semibold text-gray-700">Note</th>
                  <th className="p-3 text-center font-semibold text-gray-700">Actions</th>
                </tr>
              </thead>
              <tbody>
                {hotels.map(h => (
                  <tr key={h.id} className="border-b hover:bg-gray-50 transition">
                    <td className="p-3 text-gray-600">{h.id}</td>
                    <td className="p-3">
                      {h.imageUrl ? (
                        <img
                          src={`http://localhost:8080${h.imageUrl}`}
                          alt={h.nom}
                          className="w-24 h-16 object-cover rounded"
                          onError={(e) => {
                            e.target.src = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="96" height="64"%3E%3Crect width="96" height="64" fill="%23e5e7eb"/%3E%3Ctext x="50%25" y="50%25" dominant-baseline="middle" text-anchor="middle" font-size="24"%3E🏨%3C/text%3E%3C/svg%3E';
                          }}
                        />
                      ) : (
                        <div className="w-24 h-16 bg-gray-200 rounded flex items-center justify-center text-2xl">
                          🏨
                        </div>
                      )}
                    </td>
                    <td className="p-3 font-medium text-gray-800">{h.nom}</td>
                    <td className="p-3 text-gray-600">{h.ville}</td>
                    <td className="p-3 text-gray-600">{h.pays || "—"}</td>
                    <td className="p-3">
                      <span className="text-yellow-500 font-bold">
                        ⭐ {h.noteMoyenne}
                      </span>
                    </td>
                    <td className="p-3">
                      <div className="flex gap-2 justify-center">
                        <button
                          onClick={() => handleEdit(h)}
                          className="bg-yellow-500 text-white px-4 py-2 rounded hover:bg-yellow-600 transition font-medium"
                        >
                          ✏️ Modifier
                        </button>
                        <button
                          onClick={() => handleDelete(h.id)}
                          className="bg-red-600 text-white px-4 py-2 rounded hover:bg-red-700 transition font-medium"
                        >
                          🗑️ Supprimer
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}