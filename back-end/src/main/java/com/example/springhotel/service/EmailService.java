package com.example.springhotel.service;

import com.example.springhotel.entity.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void envoyerEmailConfirmation(Reservation reservation) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(reservation.getEmailClient());
            helper.setSubject("✅ Confirmation de réservation - " + reservation.getChambre().getHotel().getNom());
            helper.setText(construireEmailHTML(reservation), true);

            mailSender.send(message);
            System.out.println("✅ Email envoyé à : " + reservation.getEmailClient());
        } catch (MessagingException e) {
            System.err.println("❌ Erreur envoi email : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String construireEmailHTML(Reservation reservation) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Calculer le nombre de nuits
        long nombreNuits = ChronoUnit.DAYS.between(
                reservation.getDateDebut(),
                reservation.getDateFin()
        );

        // Récupérer les informations de l'hôtel
        String hotelNom = reservation.getChambre().getHotel().getNom();
        String hotelAdresse = reservation.getChambre().getHotel().getAdresse();
        String hotelVille = reservation.getChambre().getHotel().getVille();
        String hotelPays = reservation.getChambre().getHotel().getPays();
        String hotelImageUrl = reservation.getChambre().getHotel().getImageUrl();

        // Récupérer les informations de la chambre
        String chambreNom = reservation.getChambre().getNom();
        Integer chambreCapacite = reservation.getChambre().getCapacite();
        Integer chambreSuperficie = reservation.getChambre().getSuperficie();

        // Dates formatées
        String dateDebut = reservation.getDateDebut().format(formatter);
        String dateFin = reservation.getDateFin().format(formatter);

        // Prix total
        Double prixTotal = reservation.getPrixTotal();

        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    body { 
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; 
                        line-height: 1.6; 
                        color: #333; 
                        margin: 0;
                        padding: 0;
                        background-color: #f4f4f4;
                    }
                    .container { 
                        max-width: 600px; 
                        margin: 20px auto; 
                        background: white;
                        border-radius: 12px;
                        overflow: hidden;
                        box-shadow: 0 4px 6px rgba(0,0,0,0.1);
                    }
                    .header { 
                        background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); 
                        color: white; 
                        padding: 40px 30px; 
                        text-align: center;
                    }
                    .header h1 {
                        margin: 0;
                        font-size: 28px;
                        font-weight: bold;
                    }
                    .header p {
                        margin: 10px 0 0 0;
                        opacity: 0.9;
                    }
                    .content { 
                        padding: 30px;
                    }
                    .code-box { 
                        background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                        color: white; 
                        padding: 20px; 
                        text-align: center; 
                        border-radius: 10px; 
                        margin: 25px 0;
                    }
                    .code-box .label {
                        font-size: 12px;
                        text-transform: uppercase;
                        letter-spacing: 1px;
                        opacity: 0.9;
                        margin-bottom: 5px;
                    }
                    .code-box .code { 
                        font-size: 32px; 
                        font-weight: bold; 
                        letter-spacing: 4px;
                        font-family: 'Courier New', monospace;
                    }
                    .info-section {
                        background: #f9fafb;
                        border-radius: 10px;
                        padding: 20px;
                        margin: 20px 0;
                    }
                    .info-section h2 {
                        margin: 0 0 15px 0;
                        font-size: 18px;
                        color: #667eea;
                        border-bottom: 2px solid #667eea;
                        padding-bottom: 8px;
                    }
                    .info-row {
                        display: flex;
                        justify-content: space-between;
                        padding: 10px 0;
                        border-bottom: 1px solid #e5e7eb;
                    }
                    .info-row:last-child {
                        border-bottom: none;
                    }
                    .info-label {
                        font-weight: 600;
                        color: #6b7280;
                    }
                    .info-value {
                        color: #111827;
                        text-align: right;
                    }
                    .hotel-image {
                        width: 100%%;
                        height: 200px;
                        object-fit: cover;
                        border-radius: 10px;
                        margin-bottom: 20px;
                    }
                    .price-total {
                        background: #ecfdf5;
                        border: 2px solid #10b981;
                        border-radius: 10px;
                        padding: 20px;
                        text-align: center;
                        margin: 20px 0;
                    }
                    .price-total .label {
                        color: #059669;
                        font-size: 14px;
                        margin-bottom: 5px;
                    }
                    .price-total .amount {
                        color: #047857;
                        font-size: 36px;
                        font-weight: bold;
                    }
                    .footer {
                        background: #f9fafb;
                        padding: 20px 30px;
                        text-align: center;
                        color: #6b7280;
                        font-size: 14px;
                    }
                    .important-note {
                        background: #fef3c7;
                        border-left: 4px solid #f59e0b;
                        padding: 15px;
                        margin: 20px 0;
                        border-radius: 5px;
                    }
                    .important-note strong {
                        color: #d97706;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <!-- Header -->
                    <div class="header">
                        <h1>🎉 Réservation Confirmée !</h1>
                        <p>Merci pour votre confiance</p>
                    </div>

                    <!-- Content -->
                    <div class="content">
                        <p>Bonjour <strong>%s</strong>,</p>
                        <p>Nous avons le plaisir de confirmer votre réservation. Voici les détails :</p>

                        <!-- Code de confirmation -->
                        <div class="code-box">
                            <div class="label">Code de confirmation</div>
                            <div class="code">%s</div>
                        </div>

                        <div class="important-note">
                            <strong>⚠️ Important :</strong> Conservez ce code de confirmation. Il vous sera demandé lors de votre arrivée à l'hôtel et pour toute modification ou annulation de votre réservation.
                        </div>

                        <!-- Image de l'hôtel -->
                        %s

                        <!-- Informations Hôtel -->
                        <div class="info-section">
                            <h2>🏨 Hôtel</h2>
                            <div class="info-row">
                                <span class="info-label">Nom</span>
                                <span class="info-value">%s</span>
                            </div>
                            <div class="info-row">
                                <span class="info-label">Adresse</span>
                                <span class="info-value">%s</span>
                            </div>
                            <div class="info-row">
                                <span class="info-label">Ville</span>
                                <span class="info-value">%s, %s</span>
                            </div>
                        </div>

                        <!-- Informations Chambre -->
                        <div class="info-section">
                            <h2>🛏️ Chambre</h2>
                            <div class="info-row">
                                <span class="info-label">Type</span>
                                <span class="info-value">%s</span>
                            </div>
                            <div class="info-row">
                                <span class="info-label">Capacité</span>
                                <span class="info-value">%s personne(s)</span>
                            </div>
                            <div class="info-row">
                                <span class="info-label">Surface</span>
                                <span class="info-value">%s m²</span>
                            </div>
                        </div>

                        <!-- Informations Séjour -->
                        <div class="info-section">
                            <h2>📅 Période de séjour</h2>
                            <div class="info-row">
                                <span class="info-label">Arrivée</span>
                                <span class="info-value">%s</span>
                            </div>
                            <div class="info-row">
                                <span class="info-label">Départ</span>
                                <span class="info-value">%s</span>
                            </div>
                            <div class="info-row">
                                <span class="info-label">Durée</span>
                                <span class="info-value">%s nuit(s)</span>
                            </div>
                            <div class="info-row">
                                <span class="info-label">Nombre de personnes</span>
                                <span class="info-value">%s</span>
                            </div>
                        </div>

                        <!-- Prix Total -->
                        <div class="price-total">
                            <div class="label">Prix total</div>
                            <div class="amount">%s €</div>
                        </div>

                        <!-- Informations pratiques -->
                        <div class="info-section">
                            <h2>ℹ️ Informations pratiques</h2>
                            <div class="info-row">
                                <span class="info-label">Check-in</span>
                                <span class="info-value">À partir de 14h00</span>
                            </div>
                            <div class="info-row">
                                <span class="info-label">Check-out</span>
                                <span class="info-value">Avant 11h00</span>
                            </div>
                        </div>

                        <p style="margin-top: 30px;">
                            Nous vous souhaitons un excellent séjour ! 🌟
                        </p>
                    </div>

                    <!-- Footer -->
                    <div class="footer">
                        <p>Cet email a été envoyé automatiquement, merci de ne pas y répondre.</p>
                        <p>Pour toute question, contactez-nous à support@hotel.com</p>
                        <p style="margin-top: 15px; font-size: 12px;">
                            © 2026 Hotel Management System. Tous droits réservés.
                        </p>
                    </div>
                </div>
            </body>
            </html>
            """,
                // Paramètres (tous en String pour éviter les erreurs de formatage)
                reservation.getNomClient(),
                reservation.getCodeConfirmation(),
                hotelImageUrl != null && !hotelImageUrl.isEmpty()
                        ? "<img src=\"" + hotelImageUrl + "\" alt=\"" + hotelNom + "\" class=\"hotel-image\">"
                        : "",
                hotelNom,
                hotelAdresse,
                hotelVille,
                hotelPays != null ? hotelPays : "Non spécifié",
                chambreNom,
                chambreCapacite != null ? chambreCapacite.toString() : "Non spécifié",
                chambreSuperficie != null ? chambreSuperficie.toString() : "Non spécifié",
                dateDebut,
                dateFin,
                String.valueOf(nombreNuits),
                String.valueOf(reservation.getNombrePersonnes()),
                prixTotal != null ? String.format("%.2f", prixTotal) : "0.00"
        );
    }
}