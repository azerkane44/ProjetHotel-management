function Equipement({ label, desc }) {
  return (
    <div className="border rounded-lg p-4 flex gap-3">
      <span className="text-blue-500">✔</span>
      <div>
        <p className="font-medium">{label}</p>
        <p className="text-sm text-gray-500">{desc}</p>
      </div>
    </div>
  );
}

export default Equipement;
