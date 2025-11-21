import { Box, Flex } from '@chakra-ui/react';
import React from 'react';

export default function Navbar() {
  return (
    <nav className="w-full bg-white shadow-sm p-4 flex items-center justify-between">
      <div className="text-2xl font-bold text-blue-600">
        HotelBooking
      </div>

      <ul className="flex items-center gap-6 text-gray-700 font-medium">
        <li className="cursor-pointer hover:text-blue-600">Accueil</li>
        <li className="cursor-pointer hover:text-blue-600">Destinations</li>
        <li className="cursor-pointer hover:text-blue-600">Mes réservations</li>
      </ul>

      <div className="flex items-center gap-4">
        <button className="px-4 py-2 rounded-lg text-gray-700 hover:bg-gray-100">
          Connexion
        </button>
        <button className="px-4 py-2 rounded-lg bg-black text-white hover:bg-gray-800">
          Inscription
        </button>
      </div>
    </nav>
  );
}
