import { useState } from "react";
import InputField from "../components/InputField";

export default function InscriptionUser() {

  const [fullName, setFullName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const handleRegister = (e) => {
    e.preventDefault();
    console.log("Inscription :", fullName, email, password);
    // plus tard : fetch("http://localhost:8080/api/register")
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50">

      <div className="bg-white p-10 rounded-2xl shadow-xl w-[380px] space-y-6">

        <h2 className="text-3xl font-bold text-center text-gray-800">
          Inscription
        </h2>

        <form onSubmit={handleRegister} className="space-y-5">

          <InputField
            label="Nom complet"
            type="text"
            value={fullName}
            onChange={(e) => setFullName(e.target.value)}
          />

          <InputField
            label="Email"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />

          <InputField
            label="Mot de passe"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />

          <button
            type="submit"
            className="w-full bg-black text-white py-3 rounded-lg hover:bg-gray-800 font-medium"
          >
            Créer un compte
          </button>
        </form>

        <p className="text-center text-sm text-gray-600">
          Déjà un compte ?{" "}
          <a href="/Connextion" className="text-blue-600 font-medium hover:underline">
            Connecte-toi
          </a>
        </p>

      </div>
    </div>
  );
}
