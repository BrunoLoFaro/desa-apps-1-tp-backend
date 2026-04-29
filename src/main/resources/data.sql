-- SEED DATA - SQL Server - plain INSERTS with default ; separator
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
INSERT INTO activities (id, name, image_url, description, includes_text, meeting_point, duration_minutes, language, cancellation_policy, category, base_price, currency, featured, destination_id, guide_id) VALUES (1, 'Free Tour por San Telmo', 'https://images.unsplash.com/photo-1544986581-efac024faf62', 'Recorrido a pie por el barrio mas antiguo de Buenos Aires. Visitamos la Plaza Dorrego, el Mercado de San Telmo y la famosa Defensa.', 'Guia en espanol, mapa del barrio, agua mineral', 'Plaza Dorrego, San Telmo', 120, 'ES', 'Cancelacion gratuita hasta 24 h antes.', 'FREE_TOUR', 0.00, 'ARS', 1, 1, 1);
INSERT INTO activities (id, name, image_url, description, includes_text, meeting_point, duration_minutes, language, cancellation_policy, category, base_price, currency, featured, destination_id, guide_id) VALUES (2, 'Visita guiada al Teatro Colon', 'https://images.unsplash.com/photo-1520637836862-4d197d17c93a', 'Descubri la historia y la arquitectura del Teatro Colon con un guia experto. Recorremos el foyer, la sala principal y los talleres.', 'Entrada al teatro, guia bilingue, auriculares', 'Entrada principal del Teatro Colon, Tucuman 1171', 90, 'ES', 'Cancelacion gratuita hasta 48 h antes.', 'VISITA_GUIADA', 8500.00, 'ARS', 1, 1, 1);
INSERT INTO activities (id, name, image_url, description, includes_text, meeting_point, duration_minutes, language, cancellation_policy, category, base_price, currency, featured, destination_id, guide_id) VALUES (3, 'Tour gastronomico por Palermo', 'https://images.unsplash.com/photo-1504674900247-0877df9cc836', 'Proba lo mejor de la gastronomia portena: empanadas, pizza, helado artesanal y vinos en las mejores paradas de Palermo Soho.', '5 degustaciones, 2 bebidas, guia foodie', 'Esquina de Honduras y Thames, Palermo', 150, 'ES', 'Cancelacion gratuita hasta 24 h antes.', 'GASTRONOMIA', 15000.00, 'ARS', 0, 1, 2);
INSERT INTO activities (id, name, image_url, description, includes_text, meeting_point, duration_minutes, language, cancellation_policy, category, base_price, currency, featured, destination_id, guide_id) VALUES (4, 'Excursion Alta Montana', 'https://images.unsplash.com/photo-1501785888041-af3ef285b470', 'Salida de dia completo por la Ruta 7 hasta el Puente del Inca y mirador del Aconcagua. Paradas en Villavicencio y Uspallata.', 'Transporte, almuerzo, guia, seguro', 'Hotel NH Mendoza, Av. Espana 1210', 600, 'ES', 'Cancelacion gratuita hasta 72 h antes.', 'EXCURSION', 35000.00, 'ARS', 1, 2, 3);
INSERT INTO activities (id, name, image_url, description, includes_text, meeting_point, duration_minutes, language, cancellation_policy, category, base_price, currency, featured, destination_id, guide_id) VALUES (5, 'Degustacion de vinos en Lujan de Cuyo', 'https://images.unsplash.com/photo-1519681393784-d120267933ba', 'Visitamos 3 bodegas boutique con degustacion de Malbec y blend premium. Incluye maridaje con quesos regionales.', 'Transporte, 3 bodegas, degustaciones, tabla de quesos', 'Plaza Independencia, Mendoza', 300, 'ES', 'Cancelacion gratuita hasta 48 h antes.', 'GASTRONOMIA', 22000.00, 'ARS', 0, 2, 3);
INSERT INTO activities (id, name, image_url, description, includes_text, meeting_point, duration_minutes, language, cancellation_policy, category, base_price, currency, featured, destination_id, guide_id) VALUES (6, 'Circuito Chico en bici', 'https://images.unsplash.com/photo-1500530855697-b586d89ba3ee', 'Pedalea por el famoso Circuito Chico: lago Moreno, Punto Panoramico, Colonia Suiza y Cerro Campanario.', 'Bicicleta mountain bike, casco, mapa, snack', 'Base Cerro Campanario', 240, 'ES', 'Cancelacion gratuita hasta 24 h antes.', 'AVENTURA', 18000.00, 'ARS', 1, 3, 4);
INSERT INTO activities (id, name, image_url, description, includes_text, meeting_point, duration_minutes, language, cancellation_policy, category, base_price, currency, featured, destination_id, guide_id) VALUES (7, 'Kayak en el Nahuel Huapi', 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e', 'Navega en kayak por el brazo Blest del lago Nahuel Huapi. Apto para principiantes.', 'Kayak doble, chaleco, remo, instructor, snack', 'Puerto Panuelo, Llao Llao', 180, 'ES', 'Cancelacion gratuita hasta 48 h antes.', 'AVENTURA', 25000.00, 'ARS', 0, 3, 5);
INSERT INTO activities (id, name, image_url, description, includes_text, meeting_point, duration_minutes, language, cancellation_policy, category, base_price, currency, featured, destination_id, guide_id) VALUES (8, 'Cataratas lado argentino', 'https://images.unsplash.com/photo-1505761671935-60b3a7427bad', 'Recorrido completo por los circuitos Superior, Inferior y Garganta del Diablo. Incluye tren ecologico.', 'Entrada al parque, guia, tren ecologico', 'Centro de Visitantes, Parque Nacional Iguazu', 300, 'ES', 'Cancelacion gratuita hasta 48 h antes.', 'VISITA_GUIADA', 20000.00, 'ARS', 1, 4, 2);
INSERT INTO activities (id, name, image_url, description, includes_text, meeting_point, duration_minutes, language, cancellation_policy, category, base_price, currency, featured, destination_id, guide_id) VALUES (9, 'Aventura Gran Aventura (lancha bajo las cataratas)', 'https://images.unsplash.com/photo-1491553895911-0055eca6402d', 'Emocionante paseo en lancha que te lleva bajo los saltos de las Cataratas del Iguazu. Te mojas seguro!', 'Lancha, chaleco salvavidas, bolsa estanca para celular', 'Puerto Macuco, dentro del Parque Nacional', 60, 'ES', 'No reembolsable.', 'AVENTURA', 30000.00, 'ARS', 0, 4, 2);
INSERT INTO activities (id, name, image_url, description, includes_text, meeting_point, duration_minutes, language, cancellation_policy, category, base_price, currency, featured, destination_id, guide_id) VALUES (10, 'Tren a las Nubes (excursion completa)', 'https://images.unsplash.com/photo-1526772662000-3f88f10405ff', 'Viaje en el legendario Tren a las Nubes desde Salta hasta el viaducto La Polvorilla a 4.220 m.s.n.m.', 'Pasaje de tren, almuerzo, guia, seguro de altura', 'Estacion Salta, Av. Ameghino 50', 900, 'ES', 'Cancelacion con 50% de penalidad hasta 7 dias antes.', 'EXCURSION', 65000.00, 'ARS', 1, 5, 5);
INSERT INTO activities (id, name, image_url, description, includes_text, meeting_point, duration_minutes, language, cancellation_policy, category, base_price, currency, featured, destination_id, guide_id) VALUES (11, 'Navegacion por el Canal Beagle', 'https://images.unsplash.com/photo-1519817650390-64a93db511aa', 'Navegamos por el Canal Beagle visitando la Isla de los Lobos, Isla de los Pajaros y el Faro Les Eclaireurs.', 'Navegacion, guia, cafe y medialunas a bordo', 'Puerto turistico de Ushuaia, Muelle Comercial', 180, 'ES', 'Cancelacion gratuita hasta 48 h antes.', 'EXCURSION', 28000.00, 'ARS', 1, 6, 4);
INSERT INTO activities (id, name, image_url, description, includes_text, meeting_point, duration_minutes, language, cancellation_policy, category, base_price, currency, featured, destination_id, guide_id) VALUES (12, 'Free Tour por la Manzana Jesuitica', 'https://images.unsplash.com/photo-1470770841072-f978cf4d019e', 'Recorrido historico por la Manzana Jesuitica, Patrimonio de la Humanidad: Iglesia de la Compania, Universidad y Colegio Monserrat.', 'Guia en espanol, folleteria', 'Plaza San Martin, Cordoba', 90, 'ES', 'Cancelacion gratuita hasta 12 h antes.', 'FREE_TOUR', 0.00, 'ARS', 0, 7, 1);
SET IDENTITY_INSERT activities OFF;

-- Actualiza image_url en filas existentes (se ejecuta siempre, idempotente)
UPDATE activities SET image_url = 'https://images.pexels.com/photos/17914080/pexels-photo-17914080.jpeg' WHERE id = 1;
UPDATE activities SET image_url = 'https://images.unsplash.com/photo-1503095396549-807759245b35' WHERE id = 2;
UPDATE activities SET image_url = 'https://images.unsplash.com/photo-1504674900247-0877df9cc836' WHERE id = 3;
UPDATE activities SET image_url = 'https://images.unsplash.com/photo-1501785888041-af3ef285b470' WHERE id = 4;
UPDATE activities SET image_url = 'https://images.pexels.com/photos/2702805/pexels-photo-2702805.jpeg' WHERE id = 5;
UPDATE activities SET image_url = 'https://images.unsplash.com/photo-1500530855697-b586d89ba3ee' WHERE id = 6;
UPDATE activities SET image_url = 'https://images.pexels.com/photos/13858336/pexels-photo-13858336.jpeg' WHERE id = 7;
UPDATE activities SET image_url = 'https://images.pexels.com/photos/29714312/pexels-photo-29714312.jpeg' WHERE id = 8;
UPDATE activities SET image_url = 'https://images.pexels.com/photos/10553977/pexels-photo-10553977.jpeg' WHERE id = 9;
UPDATE activities SET image_url = 'https://images.pexels.com/photos/26654240/pexels-photo-26654240.jpeg' WHERE id = 10;
UPDATE activities SET image_url = 'https://images.unsplash.com/photo-1470770841072-f978cf4d019e' WHERE id = 11;
UPDATE activities SET image_url = 'https://images.unsplash.com/photo-1470770841072-f978cf4d019e' WHERE id = 12;

-- 3b. ACTIVIDADES ADICIONALES (cupos disponibles, imagen completa)
SET IDENTITY_INSERT activities ON;
INSERT INTO activities (id, name, image_url, description, includes_text, meeting_point, duration_minutes, language, cancellation_policy, category, base_price, currency, featured, destination_id, guide_id) VALUES (13, 'Trekking al Cerro Llao Llao', 'https://images.pexels.com/photos/1761279/pexels-photo-1761279.jpeg', 'Senderismo de media jornada hasta la cima del Cerro Llao Llao con vistas panoramicas al lago Nahuel Huapi.', 'Guia certificado, bastones, snack energetico', 'Estacionamiento Hotel Llao Llao', 210, 'ES', 'Cancelacion gratuita hasta 24 h antes.', 'AVENTURA', 16000.00, 'ARS', 0, 3, 4);
INSERT INTO activities (id, name, image_url, description, includes_text, meeting_point, duration_minutes, language, cancellation_policy, category, base_price, currency, featured, destination_id, guide_id) VALUES (14, 'City Tour Buenos Aires', 'https://images.pexels.com/photos/3889843/pexels-photo-3889843.jpeg', 'Recorrido en bus panoramico por los barrios mas emblematicos: La Boca, San Telmo, Puerto Madero y Recoleta.', 'Bus panoramico, audio guia, mapa, agua', 'Obelisco, Av. Corrientes 1', 180, 'ES', 'Cancelacion gratuita hasta 24 h antes.', 'VISITA_GUIADA', 12000.00, 'ARS', 1, 1, 1);
INSERT INTO activities (id, name, image_url, description, includes_text, meeting_point, duration_minutes, language, cancellation_policy, category, base_price, currency, featured, destination_id, guide_id) VALUES (15, 'Safari fotografico en la selva misionera', 'https://images.pexels.com/photos/975771/pexels-photo-975771.jpeg', 'Exploracion fotografica guiada por senderos internos del Parque Nacional Iguazu. Avistaje de fauna y flora.', 'Guia fotografo, entrada al parque, agua', 'Centro de Visitantes, Parque Nacional Iguazu', 240, 'ES', 'Cancelacion gratuita hasta 48 h antes.', 'AVENTURA', 22000.00, 'ARS', 0, 4, 2);
INSERT INTO activities (id, name, image_url, description, includes_text, meeting_point, duration_minutes, language, cancellation_policy, category, base_price, currency, featured, destination_id, guide_id) VALUES (16, 'Sandboarding en dunas de Cafayate', 'https://images.pexels.com/photos/1001682/pexels-photo-1001682.jpeg', 'Deslizate por las dunas rojizas de los alrededores de Cafayate. Apto para todos los niveles.', 'Tabla de sandboarding, casco, transporte desde Salta', 'Terminal de omnibus de Salta', 300, 'ES', 'Cancelacion gratuita hasta 24 h antes.', 'AVENTURA', 19000.00, 'ARS', 0, 5, 5);
INSERT INTO activities (id, name, image_url, description, includes_text, meeting_point, duration_minutes, language, cancellation_policy, category, base_price, currency, featured, destination_id, guide_id) VALUES (17, 'Glaciar Martial y teleferico - Ushuaia', 'https://images.pexels.com/photos/3369569/pexels-photo-3369569.jpeg', 'Sube en teleferico hasta el Glaciar Martial y disfruta de vistas unicas sobre Ushuaia y el Canal Beagle.', 'Teleferico, guia, chocolate caliente al regreso', 'Base del teleferico Glaciar Martial, Ushuaia', 150, 'ES', 'Cancelacion gratuita hasta 24 h antes.', 'EXCURSION', 14000.00, 'ARS', 0, 6, 4);
SET IDENTITY_INSERT activities OFF;

-- 4. ACTIVITY SESSIONS (fechas futuras)
SET IDENTITY_INSERT activity_sessions ON;
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (1, 1, '2026-04-20 10:00:00', 25, 12, NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (2, 1, '2026-04-20 15:00:00', 25, 5, NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (3, 1, '2026-04-21 10:00:00', 25, 0, NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (4, 1, '2026-04-22 10:00:00', 25, 0, NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (5, 2, '2026-04-20 11:00:00', 20, 18, NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (6, 2, '2026-04-20 14:00:00', 20, 8, NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (7, 2, '2026-04-21 11:00:00', 20, 3, 9000.00);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (8, 2, '2026-04-22 11:00:00', 20, 0, NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (9, 3, '2026-04-20 12:30:00', 12, 10, NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (10, 3, '2026-04-21 12:30:00', 12, 2, NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (11, 3, '2026-04-23 12:30:00', 12, 0, 13500.00);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (12, 4, '2026-04-21 07:00:00', 15, 8, NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (13, 4, '2026-04-23 07:00:00', 15, 0, NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (14, 4, '2026-04-26 07:00:00', 15, 0, 32000.00);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (15, 5, '2026-04-20 10:00:00', 10, 7, NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (16, 5, '2026-04-22 10:00:00', 10, 0, NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (17, 6, '2026-04-20 09:00:00', 8, 6, NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (18, 6, '2026-04-21 09:00:00', 8, 0, NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (19, 6, '2026-04-22 09:00:00', 8, 0, 16500.00);
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
-- Sesiones para actividades nuevas 13-17 (fechas futuras, cupos disponibles)
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (36, 13, '2026-05-02 08:00:00', 10, 3, NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (37, 13, '2026-05-09 08:00:00', 10, 0, NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (38, 14, '2026-05-01 09:00:00', 30, 10, NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (39, 14, '2026-05-08 09:00:00', 30, 0, NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (40, 15, '2026-05-04 07:30:00', 15, 5, NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (41, 15, '2026-05-11 07:30:00', 15, 0, NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (42, 16, '2026-05-05 06:00:00', 12, 4, NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (43, 16, '2026-05-12 06:00:00', 12, 0, NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (44, 17, '2026-05-03 10:00:00', 20, 7, NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (45, 17, '2026-05-10 10:00:00', 20, 0, NULL);
SET IDENTITY_INSERT activity_sessions OFF;

-- 5. USERS DE PRUEBA (password = "123456" con BCrypt)
SET IDENTITY_INSERT users ON;
INSERT INTO users (id, email, password_hash, first_name, last_name, phone, profile_photo_url, enabled, created_at) VALUES (1, 'test@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Test', 'User', '+5491123456789', 'https://i.pravatar.cc/300?u=test', 1, '2026-01-01 00:00:00');
INSERT INTO users (id, email, password_hash, first_name, last_name, phone, profile_photo_url, enabled, created_at) VALUES (2, 'maria@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Maria', 'Lopez', '+5491198765432', 'https://i.pravatar.cc/300?u=maria', 1, '2026-01-15 00:00:00');
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

-- FAVORITES TEST DATA
-- Casos para verificar los flags sinCupos, cambioPrecio y cuposLiberados

-- Sesiones futuras para los 4 casos de prueba
-- (hoy = 2026-04-26; estas fechas son futuras)
SET IDENTITY_INSERT activity_sessions ON;
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (100, 6,  '2026-05-10 09:00:00', 8,  8,  NULL);       -- Caso 1: sin cupos (lleno)
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (101, 2,  '2026-05-15 11:00:00', 20, 5,  10000.00);   -- Caso 2: cambio de precio (8500 -> 10000)
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (102, 7,  '2026-05-20 10:00:00', 6,  3,  NULL);       -- Caso 3: cupos liberados (0 -> 3)
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (103, 11, '2026-05-25 09:30:00', 20, 10, 35000.00);   -- Caso 4: cambio precio + cupos liberados
SET IDENTITY_INSERT activity_sessions OFF;

SET IDENTITY_INSERT favorites ON;
-- Caso 1: sin cupos
--   activity 6 (Circuito Chico en bici, Bariloche, 18000 ARS, guia Valentina Ruiz)
--   snapshot_slots=0 -> cupos actuales=0 -> sinCupos=true, cuposLiberados=false, cambioPrecio=false
INSERT INTO favorites (id, user_id, activity_id, created_at, snapshot_price, snapshot_slots) VALUES (1, 1, 6,  '2026-04-20 10:00:00', 18000.00, 0);

-- Caso 2: cambio de precio
--   activity 2 (Teatro Colon, Buenos Aires, guia Carlos Garcia)
--   snapshot_price=8500, precio actual=10000 -> cambioPrecio=true
--   snapshot_slots=20, cupos actuales=15 -> cuposLiberados=(15>20)=false
INSERT INTO favorites (id, user_id, activity_id, created_at, snapshot_price, snapshot_slots) VALUES (2, 1, 2,  '2026-04-20 11:00:00', 8500.00,  20);

-- Caso 3: cupos liberados
--   activity 7 (Kayak Nahuel Huapi, Bariloche, guia Diego Morales)
--   snapshot_slots=0, cupos actuales=3 -> cuposLiberados=true, cambioPrecio=false
INSERT INTO favorites (id, user_id, activity_id, created_at, snapshot_price, snapshot_slots) VALUES (3, 1, 7,  '2026-04-20 12:00:00', 25000.00, 0);

-- Caso 4: cambio de precio + cupos liberados
--   activity 11 (Canal Beagle, Ushuaia, guia Valentina Ruiz)
--   snapshot_price=28000, precio actual=35000 -> cambioPrecio=true
--   snapshot_slots=0, cupos actuales=10 -> cuposLiberados=true
INSERT INTO favorites (id, user_id, activity_id, created_at, snapshot_price, snapshot_slots) VALUES (4, 1, 11, '2026-04-20 13:00:00', 28000.00, 0);
SET IDENTITY_INSERT favorites OFF;

-- 8. REVIEWS (sobre bookings COMPLETED)
SET IDENTITY_INSERT reviews ON;
INSERT INTO reviews (id, booking_id, user_id, activity_id, guide_id, activity_rating, guide_rating, comment, created_at) VALUES (1, 1, 1, 1, 1, 5, 5, 'Excelente recorrido por San Telmo, el guia fue muy amable y conocedor.', '2026-04-01 14:00:00');
INSERT INTO reviews (id, booking_id, user_id, activity_id, guide_id, activity_rating, guide_rating, comment, created_at) VALUES (2, 2, 1, 2, 1, 4, 5, 'El Teatro Colon es impresionante. Muy recomendable.', '2026-04-01 16:00:00');
INSERT INTO reviews (id, booking_id, user_id, activity_id, guide_id, activity_rating, guide_rating, comment, created_at) VALUES (3, 3, 1, 3, 2, 5, 4, 'Las empanadas y el helado estaban increibles. Gran experiencia gastronomica.', '2026-04-02 10:00:00');
INSERT INTO reviews (id, booking_id, user_id, activity_id, guide_id, activity_rating, guide_rating, comment, created_at) VALUES (4, 5, 1, 8, 2, 5, 5, 'Las cataratas son impresionantes, una experiencia unica en la vida.', '2026-04-01 18:00:00');
INSERT INTO reviews (id, booking_id, user_id, activity_id, guide_id, activity_rating, guide_rating, comment, created_at) VALUES (5, 9, 2, 1, 1, 4, 4, 'Lindo paseo, el barrio tiene mucha historia.', '2026-04-01 15:00:00');
INSERT INTO reviews (id, booking_id, user_id, activity_id, guide_id, activity_rating, guide_rating, comment, created_at) VALUES (6, 10, 2, 8, 2, 5, 5, 'Espectacular! La Garganta del Diablo te deja sin palabras.', '2026-04-01 17:00:00');
SET IDENTITY_INSERT reviews OFF;