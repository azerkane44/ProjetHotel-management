import { useEffect, useState } from "react";

const API_BASE = "http://localhost:8080/api/hotels";

export default function CrudHotel() {
    const [hotels, setHotels] = useState([]);
    const [editingId, setEditingId] = useState(null);

    const [form, setForm] = useState({
        nom: "",
        adresse: "",
        ville: "",
        description: "",
        noteMoyenne: 0,
        imageFile: null, // File object
        imagePreview: null // data URL or existing imageUrl
    });

    const loadHotels = async () => {
        const res = await fetch(API_BASE);
        const data = await res.json();
        setHotels(data);
    };

    useEffect(() => {
        loadHotels();
    }, []);

    const resetForm = () => {
        setEditingId(null);
        setForm({
            nom: "",
            adresse: "",
            ville: "",
            description: "",
            noteMoyenne: 0,
            imageFile: null,
            imagePreview: null
        });
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setForm(prev => ({ ...prev, [name]: value }));
    };

    const handleFileChange = (e) => {
        const file = e.target.files[0];
        if (!file) return;
        setForm(prev => ({ ...prev, imageFile: file }));

        // preview
        const reader = new FileReader();
        reader.onload = (ev) => {
            setForm(prev => ({ ...prev, imagePreview: ev.target.result }));
        };
        reader.readAsDataURL(file);
    };

    const handleEdit = async (hotel) => {
        setEditingId(hotel.id);
        setForm({
            nom: hotel.nom || "",
            adresse: hotel.adresse || "",
            ville: hotel.ville || "",
            description: hotel.description || "",
            noteMoyenne: hotel.noteMoyenne || 0,
            imageFile: null,
            imagePreview: hotel.imageUrl ? (`http://localhost:8080${hotel.imageUrl}`) : null
        });
        window.scrollTo({ top: 0, behavior: "smooth" });
    };

    const handleDelete = async (id) => {
        if (!window.confirm("Supprimer cet hôtel ?")) return;
        await fetch(`${API_BASE}/${id}`, { method: "DELETE" });
        loadHotels();
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        const fd = new FormData();
        fd.append("nom", form.nom);
        fd.append("adresse", form.adresse || "");
        fd.append("ville", form.ville || "");
        fd.append("description", form.description || "");
        fd.append("noteMoyenne", form.noteMoyenne || 0);

        if (form.imageFile) {
            fd.append("image", form.imageFile);
        }

        if (editingId) {
            // update (POST /{id} to accept multipart)
            await fetch(`${API_BASE}/${editingId}`, {
                method: "POST",
                body: fd
            });
        } else {
            // create
            await fetch(API_BASE, {
                method: "POST",
                body: fd
            });
        }

        resetForm();
        loadHotels();
    };

    return (
        <div className="w-[90%] mx-auto mt-10">
            <h1 className="text-3xl font-bold mb-6">Gestion des hôtels</h1>

            <form onSubmit={handleSubmit} className="bg-white p-6 shadow rounded mb-8 grid grid-cols-2 gap-4">
                <h2 className="col-span-2 text-xl font-semibold">
                    {editingId ? "Modifier l'hôtel" : "Ajouter un hôtel"}
                </h2>

                <input
                    name="nom"
                    value={form.nom}
                    onChange={handleChange}
                    placeholder="Nom"
                    className="border p-2 rounded"
                    required
                />

                <input
                    name="adresse"
                    value={form.adresse}
                    onChange={handleChange}
                    placeholder="Adresse"
                    className="border p-2 rounded"
                />

                <input
                    name="ville"
                    value={form.ville}
                    onChange={handleChange}
                    placeholder="Ville"
                    className="border p-2 rounded"
                />

                <input
                    name="noteMoyenne"
                    type="number"
                    value={form.noteMoyenne}
                    onChange={handleChange}
                    placeholder="Note moyenne"
                    className="border p-2 rounded"
                />

                <textarea
                    name="description"
                    value={form.description}
                    onChange={handleChange}
                    placeholder="Description"
                    className="border p-2 rounded col-span-2"
                />

                <div className="col-span-2">
                    <label className="block mb-2">Image (jpg/png)</label>
                    <input type="file" accept="image/*" onChange={handleFileChange} />
                </div>

                {form.imagePreview && (
                    <div className="col-span-2">
                        <p className="text-sm mb-2">Aperçu :</p>
                        <img src={form.imagePreview} alt="preview" style={{ maxWidth: 240, maxHeight: 160 }} />
                    </div>
                )}

                <div className="col-span-2 flex gap-4">
                    <button type="submit" className="bg-blue-600 text-white px-4 py-2 rounded">
                        {editingId ? "Mettre à jour" : "Ajouter"}
                    </button>

                    <button type="button" onClick={resetForm} className="bg-gray-300 px-4 py-2 rounded">
                        Annuler
                    </button>
                </div>
            </form>

            <h2 className="text-xl font-semibold mb-4">Liste des hôtels</h2>
            <table className="w-full border">
                <thead className="bg-gray-200">
                <tr>
                    <th className="p-2 border">ID</th>
                    <th className="p-2 border">Image</th>
                    <th className="p-2 border">Nom</th>
                    <th className="p-2 border">Ville</th>
                    <th className="p-2 border">Note</th>
                    <th className="p-2 border">Actions</th>
                </tr>
                </thead>

                <tbody>
                {hotels.map(h => (
                    <tr key={h.id}>
                        <td className="p-2 border">{h.id}</td>
                        <td className="p-2 border">
                            {h.imageUrl ? (
                                <img src={`http://localhost:8080${h.imageUrl}`} alt={h.nom} style={{ width: 120, height: 80, objectFit: "cover" }} />
                            ) : (
                                <div style={{ width: 120, height: 80, background: "#eee", display: "flex", alignItems: "center", justifyContent: "center" }}>No image</div>
                            )}
                        </td>
                        <td className="p-2 border">{h.nom}</td>
                        <td className="p-2 border">{h.ville}</td>
                        <td className="p-2 border">{h.noteMoyenne}</td>
                        <td className="p-2 border flex gap-2 justify-center">
                            <button onClick={() => handleEdit(h)} className="bg-yellow-500 text-white px-3 py-1 rounded">Modifier</button>
                            <button onClick={() => handleDelete(h.id)} className="bg-red-600 text-white px-3 py-1 rounded">Supprimer</button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
}
