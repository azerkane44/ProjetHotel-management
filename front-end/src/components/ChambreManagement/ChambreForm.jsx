import React, { useState, useEffect } from 'react';

export default function ChambreForm({ chambre, onSubmit, onCancel }) {
  const [formData, setFormData] = useState({
    nom: '',
    prixParNuit: '',
    capacite: 1,
    superficie: '',
    typeLit: '',
    description: '',
    equipements: [],
    hotelId: ''
  });

  const [equipementInput, setEquipementInput] = useState('');
  const [imageFiles, setImageFiles] = useState([]);

  useEffect(() => {
    if (chambre) {
      setFormData(chambre);
    }
  }, [chambre]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleAddEquipement = () => {
    if (equipementInput.trim()) {
      setFormData({
        ...formData,
        equipements: [...formData.equipements, equipementInput.trim()]
      });
      setEquipementInput('');
    }
  };

  const handleRemoveEquipement = (index) => {
    setFormData({
      ...formData,
      equipements: formData.equipements.filter((_, i) => i !== index)
    });
  };

  const handleImageChange = (e) => {
    const files = Array.from(e.target.files);
    setImageFiles(files);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const formDataToSend = new FormData();
    formDataToSend.append('nom', formData.nom);
    formDataToSend.append('prixParNuit', formData.prixParNuit);
    formDataToSend.append('capacite', formData.capacite);
    formDataToSend.append('superficie', formData.superficie);
    formDataToSend.append('typeLit', formData.typeLit);
    formDataToSend.append('description', formData.description || '');
    formDataToSend.append('hotelId', formData.hotelId);

    // Ajouter les équipements
    formData.equipements.forEach(eq => {
      formDataToSend.append('equipements', eq);
    });

    // Ajouter les images
    imageFiles.forEach(file => {
      formDataToSend.append('images', file);
    });

    onSubmit(formDataToSend);
  };

  return (
    <div className="bg-white p-6 rounded-lg shadow">
      <h2 className="text-xl font-bold mb-4">
        {chambre ? '✏️ Modifier' : '➕ Créer'} une chambre
      </h2>

      <form onSubmit={handleSubmit} className="space-y-4">
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium mb-1">Nom de la chambre *</label>
            <input
              type="text"
              name="nom"
              value={formData.nom}
              onChange={handleChange}
              required
              placeholder="Ex: Suite Deluxe"
              className="w-full border rounded-lg px-3 py-2"
            />
          </div>

          <div>
            <label className="block text-sm font-medium mb-1">Prix par nuit (€) *</label>
            <input
              type="number"
              name="prixParNuit"
              value={formData.prixParNuit}
              onChange={handleChange}
              required
              step="0.01"
              placeholder="Ex: 120.00"
              className="w-full border rounded-lg px-3 py-2"
            />
          </div>

          <div>
            <label className="block text-sm font-medium mb-1">Capacité (personnes) *</label>
            <input
              type="number"
              name="capacite"
              value={formData.capacite}
              onChange={handleChange}
              required
              min="1"
              placeholder="Ex: 2"
              className="w-full border rounded-lg px-3 py-2"
            />
          </div>

          <div>
            <label className="block text-sm font-medium mb-1">Superficie (m²) *</label>
            <input
              type="number"
              name="superficie"
              value={formData.superficie}
              onChange={handleChange}
              required
              placeholder="Ex: 25"
              className="w-full border rounded-lg px-3 py-2"
            />
          </div>

          <div>
            <label className="block text-sm font-medium mb-1">Type de lit *</label>
            <input
              type="text"
              name="typeLit"
              value={formData.typeLit}
              onChange={handleChange}
              required
              placeholder="Ex: Lit Queen"
              className="w-full border rounded-lg px-3 py-2"
            />
          </div>

          <div>
            <label className="block text-sm font-medium mb-1">ID Hôtel *</label>
            <input
              type="number"
              name="hotelId"
              value={formData.hotelId}
              onChange={handleChange}
              required
              placeholder="Ex: 1"
              className="w-full border rounded-lg px-3 py-2"
            />
          </div>
        </div>

        <div>
          <label className="block text-sm font-medium mb-1">Description</label>
          <textarea
            name="description"
            value={formData.description}
            onChange={handleChange}
            rows="3"
            placeholder="Décrivez la chambre..."
            className="w-full border rounded-lg px-3 py-2"
          />
        </div>

        {/* Équipements */}
        <div>
          <label className="block text-sm font-medium mb-1">Équipements</label>
          <div className="flex gap-2 mb-2">
            <input
              type="text"
              value={equipementInput}
              onChange={(e) => setEquipementInput(e.target.value)}
              placeholder="Ex: Climatisation"
              className="flex-1 border rounded-lg px-3 py-2"
            />
            <button
              type="button"
              onClick={handleAddEquipement}
              className="bg-green-600 text-white px-4 py-2 rounded-lg hover:bg-green-700"
            >
              Ajouter
            </button>
          </div>
          <div className="flex flex-wrap gap-2">
            {formData.equipements.map((eq, idx) => (
              <span
                key={idx}
                className="bg-gray-100 px-3 py-1 rounded-full text-sm flex items-center gap-2"
              >
                {eq}
                <button
                  type="button"
                  onClick={() => handleRemoveEquipement(idx)}
                  className="text-red-600 font-bold"
                >
                  ×
                </button>
              </span>
            ))}
          </div>
        </div>

        {/* Images */}
        <div>
          <label className="block text-sm font-medium mb-1">Images de la chambre</label>
          <input
            type="file"
            accept="image/*"
            multiple
            onChange={handleImageChange}
            className="w-full border rounded-lg px-3 py-2"
          />
          {imageFiles.length > 0 && (
            <p className="text-sm text-green-600 mt-2">
              ✅ {imageFiles.length} image(s) sélectionnée(s)
            </p>
          )}
        </div>

        <div className="flex gap-2">
          <button
            type="submit"
            className="flex-1 bg-blue-600 text-white py-2 rounded-lg hover:bg-blue-700"
          >
            {chambre ? '💾 Modifier' : '➕ Créer'}
          </button>
          <button
            type="button"
            onClick={onCancel}
            className="flex-1 bg-gray-300 text-gray-700 py-2 rounded-lg hover:bg-gray-400"
          >
            ❌ Annuler
          </button>
        </div>
      </form>
    </div>
  );
}
