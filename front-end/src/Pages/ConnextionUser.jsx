import { useState } from "react";
import InputField from "../components/InputField";

export default function ConnextionUser() {

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const handleLogin = (e) => {
    e.preventDefault();
    console.log("Login avec :", email, password);
    // plus tard : fetch("http://localhost:8080/api/login")
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50">

      <div className="bg-white p-10 rounded-2xl shadow-xl w-[380px] space-y-6">

        <h2 className="text-3xl font-bold text-center text-gray-800">
          Connexion
        </h2>

        <form onSubmit={handleLogin} className="space-y-5">

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
            className="w-full bg-blue-600 text-white py-3 rounded-lg hover:bg-blue-700 font-medium"
          >
            Se connecter
          </button>
        </form>

        <p className="text-center text-sm text-gray-600">
          Pas de compte ?{" "}
          <a href="/Inscription" className="text-blue-600 font-medium hover:underline">
            Créer un compte
          </a>
        </p>

      </div>
    </div>
  );
}
