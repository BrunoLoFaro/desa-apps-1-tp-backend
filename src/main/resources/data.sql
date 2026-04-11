-- SEED DATA - SQL Server - plain INSERTs with default ; separator
-- Duplicate-key errors on re-run are ignored by continue-on-error=true

-- 1. DESTINATIONS
SET IDENTITY_INSERT destinations ON;
INSERT INTO destinations (id, name, country, city) VALUES (1, 'Buenos Aires', 'Argentina', 'Buenos Aires');
INSERT INTO destinations (id, name, country, city) VALUES (2, 'Mendoza', 'Argentina', 'Mendoza');
INSERT INTO destinations (id, name, country, city) VALUES (3, 'Bariloche', 'Argentina', 'San Carlos de Bariloche');
INSERT INTO destinations (id, name, country, city) VALUES (4, N'Iguaz' + NCHAR(250), 'Argentina', N'Puerto Iguaz' + NCHAR(250));
INSERT INTO destinations (id, name, country, city) VALUES (5, 'Salta', 'Argentina', 'Salta');
INSERT INTO destinations (id, name, country, city) VALUES (6, 'Ushuaia', 'Argentina', 'Ushuaia');
INSERT INTO destinations (id, name, country, city) VALUES (7, N'C' + NCHAR(243) + N'rdoba', 'Argentina', N'C' + NCHAR(243) + N'rdoba');
SET IDENTITY_INSERT destinations OFF;

-- 2. GUIDES
SET IDENTITY_INSERT guides ON;
INSERT INTO guides (id, full_name, languages) VALUES (1, N'Carlos Garc' + NCHAR(237) + N'a', N'Espa' + NCHAR(241) + N'ol, Ingl' + NCHAR(233) + N's');
INSERT INTO guides (id, full_name, languages) VALUES (2, N'Luc' + NCHAR(237) + N'a Fern' + NCHAR(225) + N'ndez', N'Espa' + NCHAR(241) + N'ol, Portugu' + NCHAR(233) + N's');
INSERT INTO guides (id, full_name, languages) VALUES (3, N'Mart' + NCHAR(237) + N'n L' + NCHAR(243) + N'pez', N'Espa' + NCHAR(241) + N'ol, Ingl' + NCHAR(233) + N's, Franc' + NCHAR(233) + N's');
INSERT INTO guides (id, full_name, languages) VALUES (4, 'Valentina Ruiz', N'Espa' + NCHAR(241) + N'ol');
INSERT INTO guides (id, full_name, languages) VALUES (5, 'Diego Morales', N'Espa' + NCHAR(241) + N'ol, Ingl' + NCHAR(233) + N's');
SET IDENTITY_INSERT guides OFF;

-- 3. ACTIVITIES
SET IDENTITY_INSERT activities ON;
INSERT INTO activities (id, name, description, includes_text, meeting_point, duration_minutes, language, cancellation_policy, category, base_price, currency, featured, destination_id, guide_id) VALUES (1, 'Free Tour por San Telmo', 'Recorrido a pie por el barrio mas antiguo de Buenos Aires. Visitamos la Plaza Dorrego, el Mercado de San Telmo y la famosa Defensa.', 'Guia en espanol, mapa del barrio, agua mineral', 'Plaza Dorrego, San Telmo', 120, 'ES', 'Cancelacion gratuita hasta 24 h antes.', 'FREE_TOUR', 0.00, 'ARS', 1, 1, 1);
INSERT INTO activities (id, name, description, includes_text, meeting_point, duration_minutes, language, cancellation_policy, category, base_price, currency, featured, destination_id, guide_id) VALUES (2, 'Visita guiada al Teatro Colon', 'Descubri la historia y la arquitectura del Teatro Colon con un guia experto. Recorremos el foyer, la sala principal y los talleres.', 'Entrada al teatro, guia bilingue, auriculares', 'Entrada principal del Teatro Colon, Tucuman 1171', 90, 'ES', 'Cancelacion gratuita hasta 48 h antes.', 'VISITA_GUIADA', 8500.00, 'ARS', 1, 1, 1);
INSERT INTO activities (id, name, description, includes_text, meeting_point, duration_minutes, language, cancellation_policy, category, base_price, currency, featured, destination_id, guide_id) VALUES (3, 'Tour gastronomico por Palermo', 'Proba lo mejor de la gastronomia portena: empanadas, pizza, helado artesanal y vinos en las mejores paradas de Palermo Soho.', '5 degustaciones, 2 bebidas, guia foodie', 'Esquina de Honduras y Thames, Palermo', 150, 'ES', 'Cancelacion gratuita hasta 24 h antes.', 'GASTRONOMIA', 15000.00, 'ARS', 0, 1, 2);
INSERT INTO activities (id, name, description, includes_text, meeting_point, duration_minutes, language, cancellation_policy, category, base_price, currency, featured, destination_id, guide_id) VALUES (4, 'Excursion Alta Montana', 'Salida de dia completo por la Ruta 7 hasta el Puente del Inca y mirador del Aconcagua. Paradas en Villavicencio y Uspallata.', 'Transporte, almuerzo, guia, seguro', 'Hotel NH Mendoza, Av. Espana 1210', 600, 'ES', 'Cancelacion gratuita hasta 72 h antes.', 'EXCURSION', 35000.00, 'ARS', 1, 2, 3);
INSERT INTO activities (id, name, description, includes_text, meeting_point, duration_minutes, language, cancellation_policy, category, base_price, currency, featured, destination_id, guide_id) VALUES (5, 'Degustacion de vinos en Lujan de Cuyo', 'Visitamos 3 bodegas boutique con degustacion de Malbec y blend premium. Incluye maridaje con quesos regionales.', 'Transporte, 3 bodegas, degustaciones, tabla de quesos', 'Plaza Independencia, Mendoza', 300, 'ES', 'Cancelacion gratuita hasta 48 h antes.', 'GASTRONOMIA', 22000.00, 'ARS', 0, 2, 3);
INSERT INTO activities (id, name, description, includes_text, meeting_point, duration_minutes, language, cancellation_policy, category, base_price, currency, featured, destination_id, guide_id) VALUES (6, 'Circuito Chico en bici', 'Pedalea por el famoso Circuito Chico: lago Moreno, Punto Panoramico, Colonia Suiza y Cerro Campanario.', 'Bicicleta mountain bike, casco, mapa, snack', 'Base Cerro Campanario', 240, 'ES', 'Cancelacion gratuita hasta 24 h antes.', 'AVENTURA', 18000.00, 'ARS', 1, 3, 4);
INSERT INTO activities (id, name, description, includes_text, meeting_point, duration_minutes, language, cancellation_policy, category, base_price, currency, featured, destination_id, guide_id) VALUES (7, 'Kayak en el Nahuel Huapi', 'Navega en kayak por el brazo Blest del lago Nahuel Huapi. Apto para principiantes.', 'Kayak doble, chaleco, remo, instructor, snack', 'Puerto Panuelo, Llao Llao', 180, 'ES', 'Cancelacion gratuita hasta 48 h antes.', 'AVENTURA', 25000.00, 'ARS', 0, 3, 5);
INSERT INTO activities (id, name, description, includes_text, meeting_point, duration_minutes, language, cancellation_policy, category, base_price, currency, featured, destination_id, guide_id) VALUES (8, 'Cataratas lado argentino', 'Recorrido completo por los circuitos Superior, Inferior y Garganta del Diablo. Incluye tren ecologico.', 'Entrada al parque, guia, tren ecologico', 'Centro de Visitantes, Parque Nacional Iguazu', 300, 'ES', 'Cancelacion gratuita hasta 48 h antes.', 'VISITA_GUIADA', 20000.00, 'ARS', 1, 4, 2);
INSERT INTO activities (id, name, description, includes_text, meeting_point, duration_minutes, language, cancellation_policy, category, base_price, currency, featured, destination_id, guide_id) VALUES (9, 'Aventura Gran Aventura (lancha bajo las cataratas)', 'Emocionante paseo en lancha que te lleva bajo los saltos de las Cataratas del Iguazu. Te mojas seguro!', 'Lancha, chaleco salvavidas, bolsa estanca para celular', 'Puerto Macuco, dentro del Parque Nacional', 60, 'ES', 'No reembolsable.', 'AVENTURA', 30000.00, 'ARS', 0, 4, 2);
INSERT INTO activities (id, name, description, includes_text, meeting_point, duration_minutes, language, cancellation_policy, category, base_price, currency, featured, destination_id, guide_id) VALUES (10, 'Tren a las Nubes (excursion completa)', 'Viaje en el legendario Tren a las Nubes desde Salta hasta el viaducto La Polvorilla a 4.220 m.s.n.m.', 'Pasaje de tren, almuerzo, guia, seguro de altura', 'Estacion Salta, Av. Ameghino 50', 900, 'ES', 'Cancelacion con 50% de penalidad hasta 7 dias antes.', 'EXCURSION', 65000.00, 'ARS', 1, 5, 5);
INSERT INTO activities (id, name, description, includes_text, meeting_point, duration_minutes, language, cancellation_policy, category, base_price, currency, featured, destination_id, guide_id) VALUES (11, 'Navegacion por el Canal Beagle', 'Navegamos por el Canal Beagle visitando la Isla de los Lobos, Isla de los Pajaros y el Faro Les Eclaireurs.', 'Navegacion, guia, cafe y medialunas a bordo', 'Puerto turistico de Ushuaia, Muelle Comercial', 180, 'ES', 'Cancelacion gratuita hasta 48 h antes.', 'EXCURSION', 28000.00, 'ARS', 1, 6, 4);
INSERT INTO activities (id, name, description, includes_text, meeting_point, duration_minutes, language, cancellation_policy, category, base_price, currency, featured, destination_id, guide_id) VALUES (12, 'Free Tour por la Manzana Jesuitica', 'Recorrido historico por la Manzana Jesuitica, Patrimonio de la Humanidad: Iglesia de la Compania, Universidad y Colegio Monserrat.', 'Guia en espanol, folleteria', 'Plaza San Martin, Cordoba', 90, 'ES', 'Cancelacion gratuita hasta 12 h antes.', 'FREE_TOUR', 0.00, 'ARS', 0, 7, 1);
SET IDENTITY_INSERT activities OFF;

-- 4. ACTIVITY SESSIONS (fechas futuras)
SET IDENTITY_INSERT activity_sessions ON;
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (1, 1, '2026-04-01 10:00:00', 25, 12, NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (2, 1, '2026-04-01 15:00:00', 25, 5, NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (3, 1, '2026-04-02 10:00:00', 25, 0, NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (4, 1, '2026-04-03 10:00:00', 25, 0, NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (5, 2, '2026-04-01 11:00:00', 20, 18, NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (6, 2, '2026-04-01 14:00:00', 20, 8, NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (7, 2, '2026-04-02 11:00:00', 20, 3, 9000.00);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (8, 2, '2026-04-03 11:00:00', 20, 0, NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (9, 3, '2026-04-01 12:30:00', 12, 10, NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (10, 3, '2026-04-02 12:30:00', 12, 2, NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (11, 3, '2026-04-04 12:30:00', 12, 0, 13500.00);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (12, 4, '2026-04-02 07:00:00', 15, 8, NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (13, 4, '2026-04-04 07:00:00', 15, 0, NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (14, 4, '2026-04-07 07:00:00', 15, 0, 32000.00);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (15, 5, '2026-04-01 10:00:00', 10, 7, NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (16, 5, '2026-04-03 10:00:00', 10, 0, NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (17, 6, '2026-04-01 09:00:00', 8, 6, NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (18, 6, '2026-04-02 09:00:00', 8, 0, NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (19, 6, '2026-04-03 09:00:00', 8, 0, 16500.00);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (20, 7, '2026-04-02 10:00:00', 6, 4, NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (21, 7, '2026-04-03 10:00:00', 6, 0, NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (22, 8, '2026-04-01 08:00:00', 30, 25, NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (23, 8, '2026-04-02 08:00:00', 30, 5, NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (24, 8, '2026-04-03 08:00:00', 30, 0, NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (25, 9, '2026-04-01 09:00:00', 12, 12, NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (26, 9, '2026-04-01 14:00:00', 12, 3, NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (27, 9, '2026-04-02 09:00:00', 12, 0, 28000.00);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (28, 10, '2026-04-03 07:00:00', 40, 35, NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (29, 10, '2026-04-10 07:00:00', 40, 0, NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (30, 11, '2026-04-02 09:30:00', 20, 12, NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (31, 11, '2026-04-03 09:30:00', 20, 0, NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (32, 11, '2026-04-04 09:30:00', 20, 0, 26000.00);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (33, 12, '2026-04-01 10:00:00', 20, 15, NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (34, 12, '2026-04-02 10:00:00', 20, 0, NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (35, 12, '2026-04-03 16:00:00', 20, 0, NULL);
SET IDENTITY_INSERT activity_sessions OFF;

-- 5. USERS DE PRUEBA (password = "123456" con BCrypt)
SET IDENTITY_INSERT users ON;
INSERT INTO users (id, email, password_hash, first_name, last_name, dni, phone, profile_photo_url, enabled, created_at) VALUES (1, 'test@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Test', 'User', '11222333', '+5491123456789', 'https://i.pravatar.cc/300?u=test', 1, '2026-01-01 00:00:00');
INSERT INTO users (id, email, password_hash, first_name, last_name, dni, phone, profile_photo_url, enabled, created_at) VALUES (2, 'maria@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Maria', 'Lopez', '22333444', '+5491198765432', 'https://i.pravatar.cc/300?u=maria', 1, '2026-01-15 00:00:00');
SET IDENTITY_INSERT users OFF;

-- 6. USER PREFERENCES (para /activities/recommended)
SET IDENTITY_INSERT user_preferred_categories ON;
INSERT INTO user_preferred_categories (id, user_id, category) VALUES (1, 1, 'AVENTURA');
INSERT INTO user_preferred_categories (id, user_id, category) VALUES (2, 1, 'GASTRONOMIA');
SET IDENTITY_INSERT user_preferred_categories OFF;

SET IDENTITY_INSERT user_preferred_destinations ON;
INSERT INTO user_preferred_destinations (id, user_id, destination_id) VALUES (1, 1, 1);
INSERT INTO user_preferred_destinations (id, user_id, destination_id) VALUES (2, 1, 3);
INSERT INTO user_preferred_destinations (id, user_id, destination_id) VALUES (3, 1, 4);
SET IDENTITY_INSERT user_preferred_destinations OFF;

-- 7. BOOKINGS
SET IDENTITY_INSERT bookings ON;
-- User 1: completados (sesiones pasadas del 1-abr)
INSERT INTO bookings (id, user_id, session_id, participants, total_price, status, created_at, cancelled_at) VALUES (1, 1, 1, 2, 0.00, 'COMPLETED', '2026-03-25 10:00:00', NULL);
INSERT INTO bookings (id, user_id, session_id, participants, total_price, status, created_at, cancelled_at) VALUES (2, 1, 5, 1, 8500.00, 'COMPLETED', '2026-03-26 12:00:00', NULL);
INSERT INTO bookings (id, user_id, session_id, participants, total_price, status, created_at, cancelled_at) VALUES (3, 1, 9, 2, 30000.00, 'COMPLETED', '2026-03-27 09:00:00', NULL);
INSERT INTO bookings (id, user_id, session_id, participants, total_price, status, created_at, cancelled_at) VALUES (4, 1, 17, 1, 18000.00, 'COMPLETED', '2026-03-28 08:00:00', NULL);
INSERT INTO bookings (id, user_id, session_id, participants, total_price, status, created_at, cancelled_at) VALUES (5, 1, 22, 3, 60000.00, 'COMPLETED', '2026-03-29 07:00:00', NULL);
-- User 1: confirmados (sesiones futuras)
INSERT INTO bookings (id, user_id, session_id, participants, total_price, status, created_at, cancelled_at) VALUES (6, 1, 13, 2, 70000.00, 'CONFIRMED', '2026-04-01 10:00:00', NULL);
INSERT INTO bookings (id, user_id, session_id, participants, total_price, status, created_at, cancelled_at) VALUES (7, 1, 18, 1, 18000.00, 'CONFIRMED', '2026-04-01 11:00:00', NULL);
-- User 1: cancelado
INSERT INTO bookings (id, user_id, session_id, participants, total_price, status, created_at, cancelled_at) VALUES (8, 1, 3, 2, 0.00, 'CANCELLED', '2026-03-30 14:00:00', '2026-03-31 08:00:00');
-- User 2: completados
INSERT INTO bookings (id, user_id, session_id, participants, total_price, status, created_at, cancelled_at) VALUES (9, 2, 1, 1, 0.00, 'COMPLETED', '2026-03-25 11:00:00', NULL);
INSERT INTO bookings (id, user_id, session_id, participants, total_price, status, created_at, cancelled_at) VALUES (10, 2, 22, 2, 40000.00, 'COMPLETED', '2026-03-29 07:30:00', NULL);
-- User 2: confirmado
INSERT INTO bookings (id, user_id, session_id, participants, total_price, status, created_at, cancelled_at) VALUES (11, 2, 29, 2, 130000.00, 'CONFIRMED', '2026-04-02 09:00:00', NULL);
SET IDENTITY_INSERT bookings OFF;

-- 8. REVIEWS (sobre bookings COMPLETED)
SET IDENTITY_INSERT reviews ON;
INSERT INTO reviews (id, booking_id, user_id, activity_id, guide_id, activity_rating, guide_rating, comment, created_at) VALUES (1, 1, 1, 1, 1, 5, 5, 'Excelente recorrido por San Telmo, el guia fue muy amable y conocedor.', '2026-04-01 14:00:00');
INSERT INTO reviews (id, booking_id, user_id, activity_id, guide_id, activity_rating, guide_rating, comment, created_at) VALUES (2, 2, 1, 2, 1, 4, 5, 'El Teatro Colon es impresionante. Muy recomendable.', '2026-04-01 16:00:00');
INSERT INTO reviews (id, booking_id, user_id, activity_id, guide_id, activity_rating, guide_rating, comment, created_at) VALUES (3, 3, 1, 3, 2, 5, 4, 'Las empanadas y el helado estaban increibles. Gran experiencia gastronomica.', '2026-04-02 10:00:00');
INSERT INTO reviews (id, booking_id, user_id, activity_id, guide_id, activity_rating, guide_rating, comment, created_at) VALUES (4, 5, 1, 8, 2, 5, 5, 'Las cataratas son impresionantes, una experiencia unica en la vida.', '2026-04-01 18:00:00');
INSERT INTO reviews (id, booking_id, user_id, activity_id, guide_id, activity_rating, guide_rating, comment, created_at) VALUES (5, 9, 2, 1, 1, 4, 4, 'Lindo paseo, el barrio tiene mucha historia.', '2026-04-01 15:00:00');
INSERT INTO reviews (id, booking_id, user_id, activity_id, guide_id, activity_rating, guide_rating, comment, created_at) VALUES (6, 10, 2, 8, 2, 5, 5, 'Espectacular! La Garganta del Diablo te deja sin palabras.', '2026-04-01 17:00:00');
SET IDENTITY_INSERT reviews OFF;