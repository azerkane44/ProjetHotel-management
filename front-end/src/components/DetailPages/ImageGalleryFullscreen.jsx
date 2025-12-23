import { useNavigate, useParams } from "react-router-dom";

export default function ImageGalleryFullscreen() {
  const navigate = useNavigate();
  const { type, id } = useParams();

  // MOCK pour l’instant
  const images = [
    "https://picsum.photos/1200/800?1",
    "https://picsum.photos/1200/800?2",
    "https://picsum.photos/1200/800?3",
  ];

  return (
    <div className="fixed inset-0 bg-black z-50 flex items-center justify-center">
      {/* Bouton retour */}
      <button
        onClick={() => navigate(-1)}
        className="absolute top-6 left-6 bg-white/90 px-4 py-2 rounded-lg"
      >
        ← Retour
      </button>

      {/* Galerie */}
      <div className="w-full h-full flex overflow-x-scroll snap-x snap-mandatory">
        {images.map((img, index) => (
          <div
            key={index}
            className="min-w-full h-full flex items-center justify-center snap-center"
          >
            <img src={img} alt="" className="max-h-full object-contain" />
          </div>
        ))}
      </div>
    </div>
  );
}
