-- SEED DATA - SQL Server - plain INSERTS with default ; separator
-- Duplicate-key errors on re-run are ignored by continue-on-error=true
-- SEED DATA - SQL Server - plain INSERTs with default ; separator
-- Duplicate-key errors on re-run are ignored by continue-on-error=true.
-- The user seed below uses MERGE so password hashes are refreshed on restart.

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
UPDATE activities SET image_url = 'https://images.pexels.com/photos/1697076/pexels-photo-1697076.jpeg' WHERE id = 2;
UPDATE activities SET image_url = 'https://images.pexels.com/photos/262978/pexels-photo-262978.jpeg' WHERE id = 3;
UPDATE activities SET image_url = 'https://images.pexels.com/photos/417173/pexels-photo-417173.jpeg' WHERE id = 4;
UPDATE activities SET image_url = 'https://images.pexels.com/photos/2702805/pexels-photo-2702805.jpeg' WHERE id = 5;
UPDATE activities SET image_url = 'https://images.pexels.com/photos/27727948/pexels-photo-27727948.jpeg' WHERE id = 6;
UPDATE activities SET image_url = 'https://images.pexels.com/photos/13858336/pexels-photo-13858336.jpeg' WHERE id = 7;
UPDATE activities SET image_url = 'https://images.pexels.com/photos/29714312/pexels-photo-29714312.jpeg' WHERE id = 8;
UPDATE activities SET image_url = 'https://images.pexels.com/photos/10553977/pexels-photo-10553977.jpeg' WHERE id = 9;
UPDATE activities SET image_url = 'https://images.pexels.com/photos/26654240/pexels-photo-26654240.jpeg' WHERE id = 10;
UPDATE activities SET image_url = 'https://images.pexels.com/photos/210186/pexels-photo-210186.jpeg' WHERE id = 11;
UPDATE activities SET image_url = 'https://images.pexels.com/photos/3889843/pexels-photo-3889843.jpeg' WHERE id = 12;

-- 3b. ACTIVIDADES ADICIONALES (cupos disponibles, imagen completa)
SET IDENTITY_INSERT activities ON;
INSERT INTO activities (id, name, image_url, description, includes_text, meeting_point, duration_minutes, language, cancellation_policy, category, base_price, currency, featured, destination_id, guide_id) VALUES (13, 'Trekking al Cerro Llao Llao', 'https://images.pexels.com/photos/1761279/pexels-photo-1761279.jpeg', 'Senderismo de media jornada hasta la cima del Cerro Llao Llao con vistas panoramicas al lago Nahuel Huapi.', 'Guia certificado, bastones, snack energetico', 'Estacionamiento Hotel Llao Llao', 210, 'ES', 'Cancelacion gratuita hasta 24 h antes.', 'AVENTURA', 16000.00, 'ARS', 0, 3, 4);
INSERT INTO activities (id, name, image_url, description, includes_text, meeting_point, duration_minutes, language, cancellation_policy, category, base_price, currency, featured, destination_id, guide_id) VALUES (14, 'City Tour Buenos Aires', 'https://images.pexels.com/photos/3889843/pexels-photo-3889843.jpeg', 'Recorrido en bus panoramico por los barrios mas emblematicos: La Boca, San Telmo, Puerto Madero y Recoleta.', 'Bus panoramico, audio guia, mapa, agua', 'Obelisco, Av. Corrientes 1', 180, 'ES', 'Cancelacion gratuita hasta 24 h antes.', 'VISITA_GUIADA', 12000.00, 'ARS', 1, 1, 1);
INSERT INTO activities (id, name, image_url, description, includes_text, meeting_point, duration_minutes, language, cancellation_policy, category, base_price, currency, featured, destination_id, guide_id) VALUES (15, 'Safari fotografico en la selva misionera', 'https://images.pexels.com/photos/975771/pexels-photo-975771.jpeg', 'Exploracion fotografica guiada por senderos internos del Parque Nacional Iguazu. Avistaje de fauna y flora.', 'Guia fotografo, entrada al parque, agua', 'Centro de Visitantes, Parque Nacional Iguazu', 240, 'ES', 'Cancelacion gratuita hasta 48 h antes.', 'AVENTURA', 22000.00, 'ARS', 0, 4, 2);
INSERT INTO activities (id, name, image_url, description, includes_text, meeting_point, duration_minutes, language, cancellation_policy, category, base_price, currency, featured, destination_id, guide_id) VALUES (16, 'Sandboarding en dunas de Cafayate', 'https://images.pexels.com/photos/1001682/pexels-photo-1001682.jpeg', 'Deslizate por las dunas rojizas de los alrededores de Cafayate. Apto para todos los niveles.', 'Tabla de sandboarding, casco, transporte desde Salta', 'Terminal de omnibus de Salta', 300, 'ES', 'Cancelacion gratuita hasta 24 h antes.', 'AVENTURA', 19000.00, 'ARS', 0, 5, 5);
INSERT INTO activities (id, name, image_url, description, includes_text, meeting_point, duration_minutes, language, cancellation_policy, category, base_price, currency, featured, destination_id, guide_id) VALUES (17, 'Glaciar Martial y teleferico - Ushuaia', 'https://images.pexels.com/photos/3369569/pexels-photo-3369569.jpeg', 'Sube en teleferico hasta el Glaciar Martial y disfruta de vistas unicas sobre Ushuaia y el Canal Beagle.', 'Teleferico, guia, chocolate caliente al regreso', 'Base del teleferico Glaciar Martial, Ushuaia', 150, 'ES', 'Cancelacion gratuita hasta 24 h antes.', 'EXCURSION', 14000.00, 'ARS', 0, 6, 4);
SET IDENTITY_INSERT activities OFF;

-- 3c. GALERIA DE IMAGENES (carrusel en detalle)
-- Nota: requiere la entidad ActivityGalleryImageEntity (JPA crea la tabla).
DELETE FROM activity_gallery_images WHERE activity_id IN (1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17);

-- Activity 1
INSERT INTO activity_gallery_images (activity_id, position, image_url) VALUES (1, 1, 'https://images.pexels.com/photos/2101187/pexels-photo-2101187.jpeg');
INSERT INTO activity_gallery_images (activity_id, position, image_url) VALUES (1, 2, 'https://images.pexels.com/photos/1540406/pexels-photo-1540406.jpeg');
INSERT INTO activity_gallery_images (activity_id, position, image_url) VALUES (1, 3, 'https://images.pexels.com/photos/3889843/pexels-photo-3889843.jpeg');

-- Activity 2
INSERT INTO activity_gallery_images (activity_id, position, image_url) VALUES (2, 1, 'https://images.pexels.com/photos/1697076/pexels-photo-1697076.jpeg');
INSERT INTO activity_gallery_images (activity_id, position, image_url) VALUES (2, 2, 'https://images.pexels.com/photos/2772698/pexels-photo-2772698.jpeg');
INSERT INTO activity_gallery_images (activity_id, position, image_url) VALUES (2, 3, 'https://images.pexels.com/photos/53213/pexels-photo-53213.jpeg');

-- Activity 3
INSERT INTO activity_gallery_images (activity_id, position, image_url) VALUES (3, 1, 'https://images.pexels.com/photos/262978/pexels-photo-262978.jpeg');
INSERT INTO activity_gallery_images (activity_id, position, image_url) VALUES (3, 2, 'https://images.pexels.com/photos/958545/pexels-photo-958545.jpeg');
INSERT INTO activity_gallery_images (activity_id, position, image_url) VALUES (3, 3, 'https://images.pexels.com/photos/3019019/pexels-photo-3019019.jpeg');

-- Activity 4
INSERT INTO activity_gallery_images (activity_id, position, image_url) VALUES (4, 1, 'https://images.pexels.com/photos/417173/pexels-photo-417173.jpeg');
INSERT INTO activity_gallery_images (activity_id, position, image_url) VALUES (4, 2, 'https://images.pexels.com/photos/672358/pexels-photo-672358.jpeg');
INSERT INTO activity_gallery_images (activity_id, position, image_url) VALUES (4, 3, 'https://images.pexels.com/photos/33448192/pexels-photo-33448192.jpeg');

-- Activity 5
INSERT INTO activity_gallery_images (activity_id, position, image_url) VALUES (5, 1, 'https://images.pexels.com/photos/434311/pexels-photo-434311.jpeg');
INSERT INTO activity_gallery_images (activity_id, position, image_url) VALUES (5, 2, 'https://images.pexels.com/photos/3019019/pexels-photo-3019019.jpeg');
INSERT INTO activity_gallery_images (activity_id, position, image_url) VALUES (5, 3, 'https://images.pexels.com/photos/2702805/pexels-photo-2702805.jpeg');

-- Activity 6
INSERT INTO activity_gallery_images (activity_id, position, image_url) VALUES (6, 1, 'https://images.pexels.com/photos/27727948/pexels-photo-27727948.jpeg');
INSERT INTO activity_gallery_images (activity_id, position, image_url) VALUES (6, 2, 'https://images.pexels.com/photos/36975348/pexels-photo-36975348.jpeg');
INSERT INTO activity_gallery_images (activity_id, position, image_url) VALUES (6, 3, 'https://images.pexels.com/photos/1761279/pexels-photo-1761279.jpeg');

-- Activity 7
INSERT INTO activity_gallery_images (activity_id, position, image_url) VALUES (7, 1, 'https://images.pexels.com/photos/248797/pexels-photo-248797.jpeg');
INSERT INTO activity_gallery_images (activity_id, position, image_url) VALUES (7, 2, 'https://images.pexels.com/photos/753626/pexels-photo-753626.jpeg');
INSERT INTO activity_gallery_images (activity_id, position, image_url) VALUES (7, 3, 'https://images.pexels.com/photos/1295036/pexels-photo-1295036.jpeg');

-- Activity 8
INSERT INTO activity_gallery_images (activity_id, position, image_url) VALUES (8, 1, 'https://images.pexels.com/photos/3601425/pexels-photo-3601425.jpeg');
INSERT INTO activity_gallery_images (activity_id, position, image_url) VALUES (8, 2, 'https://images.pexels.com/photos/1295036/pexels-photo-1295036.jpeg');
INSERT INTO activity_gallery_images (activity_id, position, image_url) VALUES (8, 3, 'https://images.pexels.com/photos/29714312/pexels-photo-29714312.jpeg');

-- Activity 9
INSERT INTO activity_gallery_images (activity_id, position, image_url) VALUES (9, 1, 'https://images.pexels.com/photos/2901228/pexels-photo-2901228.jpeg');
INSERT INTO activity_gallery_images (activity_id, position, image_url) VALUES (9, 2, 'https://images.pexels.com/photos/301875/pexels-photo-301875.jpeg');
INSERT INTO activity_gallery_images (activity_id, position, image_url) VALUES (9, 3, 'https://images.pexels.com/photos/1295036/pexels-photo-1295036.jpeg');

-- Activity 10
INSERT INTO activity_gallery_images (activity_id, position, image_url) VALUES (10, 1, 'https://images.pexels.com/photos/1631678/pexels-photo-1631678.jpeg');
INSERT INTO activity_gallery_images (activity_id, position, image_url) VALUES (10, 2, 'https://images.pexels.com/photos/1271620/pexels-photo-1271620.jpeg');
INSERT INTO activity_gallery_images (activity_id, position, image_url) VALUES (10, 3, 'https://images.pexels.com/photos/1001682/pexels-photo-1001682.jpeg');

-- Activity 11
INSERT INTO activity_gallery_images (activity_id, position, image_url) VALUES (11, 1, 'https://images.pexels.com/photos/210186/pexels-photo-210186.jpeg');
INSERT INTO activity_gallery_images (activity_id, position, image_url) VALUES (11, 2, 'https://images.pexels.com/photos/53213/pexels-photo-53213.jpeg');
INSERT INTO activity_gallery_images (activity_id, position, image_url) VALUES (11, 3, 'https://images.pexels.com/photos/3369569/pexels-photo-3369569.jpeg');

-- Activity 12
INSERT INTO activity_gallery_images (activity_id, position, image_url) VALUES (12, 1, 'https://images.pexels.com/photos/3889843/pexels-photo-3889843.jpeg');
INSERT INTO activity_gallery_images (activity_id, position, image_url) VALUES (12, 2, 'https://images.pexels.com/photos/1761279/pexels-photo-1761279.jpeg');
INSERT INTO activity_gallery_images (activity_id, position, image_url) VALUES (12, 3, 'https://images.pexels.com/photos/1001682/pexels-photo-1001682.jpeg');

-- Activity 13
INSERT INTO activity_gallery_images (activity_id, position, image_url) VALUES (13, 1, 'https://images.pexels.com/photos/1761279/pexels-photo-1761279.jpeg');
INSERT INTO activity_gallery_images (activity_id, position, image_url) VALUES (13, 2, 'https://images.pexels.com/photos/2433353/pexels-photo-2433353.jpeg');
INSERT INTO activity_gallery_images (activity_id, position, image_url) VALUES (13, 3, 'https://images.pexels.com/photos/2387871/pexels-photo-2387871.jpeg');

-- Activity 14
INSERT INTO activity_gallery_images (activity_id, position, image_url) VALUES (14, 1, 'https://images.pexels.com/photos/3889843/pexels-photo-3889843.jpeg');
INSERT INTO activity_gallery_images (activity_id, position, image_url) VALUES (14, 2, 'https://images.pexels.com/photos/53213/pexels-photo-53213.jpeg');
INSERT INTO activity_gallery_images (activity_id, position, image_url) VALUES (14, 3, 'https://images.pexels.com/photos/2101187/pexels-photo-2101187.jpeg');

-- Activity 15
INSERT INTO activity_gallery_images (activity_id, position, image_url) VALUES (15, 1, 'https://images.pexels.com/photos/975771/pexels-photo-975771.jpeg');
INSERT INTO activity_gallery_images (activity_id, position, image_url) VALUES (15, 2, 'https://images.pexels.com/photos/247600/pexels-photo-247600.jpeg');
INSERT INTO activity_gallery_images (activity_id, position, image_url) VALUES (15, 3, 'https://images.pexels.com/photos/2558605/pexels-photo-2558605.jpeg');

-- Activity 16
INSERT INTO activity_gallery_images (activity_id, position, image_url) VALUES (16, 1, 'https://images.pexels.com/photos/1001682/pexels-photo-1001682.jpeg');
INSERT INTO activity_gallery_images (activity_id, position, image_url) VALUES (16, 2, 'https://images.pexels.com/photos/315191/pexels-photo-315191.jpeg');
INSERT INTO activity_gallery_images (activity_id, position, image_url) VALUES (16, 3, 'https://images.pexels.com/photos/753626/pexels-photo-753626.jpeg');

-- Activity 17
INSERT INTO activity_gallery_images (activity_id, position, image_url) VALUES (17, 1, 'https://images.pexels.com/photos/3369569/pexels-photo-3369569.jpeg');
INSERT INTO activity_gallery_images (activity_id, position, image_url) VALUES (17, 2, 'https://images.pexels.com/photos/1761279/pexels-photo-1761279.jpeg');
INSERT INTO activity_gallery_images (activity_id, position, image_url) VALUES (17, 3, 'https://images.pexels.com/photos/1001682/pexels-photo-1001682.jpeg');



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

-- Sesiones futuras (hoy ~ 2026-05-02) para asegurar cupos y "next session" en Home/promos
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (46, 1,  '2026-05-06 11:00:00', 25,  5,  NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (47, 2,  '2026-05-07 15:00:00', 20,  6,  NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (48, 3,  '2026-05-08 19:00:00', 16,  4,  NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (49, 4,  '2026-05-09 07:00:00', 15,  7,  NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (50, 5,  '2026-05-10 10:00:00', 10,  4,  NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (51, 6,  '2026-05-10 09:00:00', 8,   4,  NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (52, 7,  '2026-05-11 10:00:00', 6,   2,  NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (53, 8,  '2026-05-12 08:00:00', 30,  8,  NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (54, 9,  '2026-05-12 12:00:00', 18,  6,  NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (55, 10, '2026-05-11 07:00:00', 40, 12,  NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (56, 11, '2026-05-08 09:30:00', 20,  6,  NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (57, 12, '2026-05-09 10:00:00', 30,  9,  NULL);

-- Sesiones pasadas para reviews de actividades 15-17 (mantener historial realista)
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (58, 15, '2026-04-20 07:30:00', 15, 10, NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (59, 16, '2026-04-18 06:00:00', 12,  9, NULL);
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (60, 17, '2026-04-19 10:00:00', 20, 14, NULL);
SET IDENTITY_INSERT activity_sessions OFF;

-- 5. USERS DE PRUEBA (password = "123456" con BCrypt)
SET IDENTITY_INSERT users ON;
DELETE FROM users WHERE id IN (1,2);
INSERT INTO users (id, email, password_hash, first_name, last_name, phone, profile_photo_url, enabled, created_at) VALUES
  (1, 'test@example.com',  '$2a$10$Zq.580gBFNWXppKFYRXJouckqEUPAqP469tPSTGV83yv5yRcXz8t6', 'Test',  'User',  '+5491123456789', 'https://i.pravatar.cc/300?u=test',  1, '2026-01-01 00:00:00'),
  (2, 'maria@example.com', '$2a$10$Zq.580gBFNWXppKFYRXJouckqEUPAqP469tPSTGV83yv5yRcXz8t6', 'Maria', 'Lopez', '+5491198765432', 'https://i.pravatar.cc/300?u=maria', 1, '2026-01-15 00:00:00');
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
INSERT INTO bookings (id, user_id, session_id, participants, total_price, status, created_at, cancelled_at, voucher_code) VALUES (1, 1, 1, 2, 0.00, 'COMPLETED', '2026-03-25 10:00:00', NULL, 'XPN-BOOK1');
INSERT INTO bookings (id, user_id, session_id, participants, total_price, status, created_at, cancelled_at, voucher_code) VALUES (2, 1, 5, 1, 8500.00, 'COMPLETED', '2026-03-26 12:00:00', NULL, 'XPN-BOOK2');
INSERT INTO bookings (id, user_id, session_id, participants, total_price, status, created_at, cancelled_at, voucher_code) VALUES (3, 1, 9, 2, 30000.00, 'COMPLETED', '2026-03-27 09:00:00', NULL, 'XPN-BOOK3');
INSERT INTO bookings (id, user_id, session_id, participants, total_price, status, created_at, cancelled_at, voucher_code) VALUES (4, 1, 17, 1, 18000.00, 'COMPLETED', '2026-03-28 08:00:00', NULL, 'XPN-BOOK4');
INSERT INTO bookings (id, user_id, session_id, participants, total_price, status, created_at, cancelled_at, voucher_code) VALUES (5, 1, 22, 3, 60000.00, 'COMPLETED', '2026-03-29 07:00:00', NULL, 'XPN-BOOK5');
INSERT INTO bookings (id, user_id, session_id, participants, total_price, status, created_at, cancelled_at, voucher_code) VALUES (6, 1, 13, 2, 70000.00, 'CONFIRMED', '2026-04-01 10:00:00', NULL, 'XPN-BOOK6');
INSERT INTO bookings (id, user_id, session_id, participants, total_price, status, created_at, cancelled_at, voucher_code) VALUES (7, 1, 18, 1, 18000.00, 'CONFIRMED', '2026-04-01 11:00:00', NULL, 'XPN-BOOK7');
INSERT INTO bookings (id, user_id, session_id, participants, total_price, status, created_at, cancelled_at, voucher_code) VALUES (8, 1, 3, 2, 0.00, 'CANCELLED', '2026-03-30 14:00:00', '2026-03-31 08:00:00', 'XPN-BOOK8');
INSERT INTO bookings (id, user_id, session_id, participants, total_price, status, created_at, cancelled_at, voucher_code) VALUES (9, 2, 1, 1, 0.00, 'COMPLETED', '2026-03-25 11:00:00', NULL, 'XPN-BOOK9');
INSERT INTO bookings (id, user_id, session_id, participants, total_price, status, created_at, cancelled_at, voucher_code) VALUES (10, 2, 22, 2, 40000.00, 'COMPLETED', '2026-03-29 07:30:00', NULL, 'XPN-BOOK10');
INSERT INTO bookings (id, user_id, session_id, participants, total_price, status, created_at, cancelled_at, voucher_code) VALUES (11, 2, 29, 2, 130000.00, 'CONFIRMED', '2026-04-02 09:00:00', NULL, 'XPN-BOOK11');

-- COMPLETED adicionales para poblar reviews (evitar "sin reseñas" en Home)
INSERT INTO bookings (id, user_id, session_id, participants, total_price, status, created_at, cancelled_at, voucher_code) VALUES (12, 1, 12, 2, 70000.00, 'COMPLETED', '2026-04-10 09:00:00', NULL, 'XPN-BOOK12'); -- Act 4
INSERT INTO bookings (id, user_id, session_id, participants, total_price, status, created_at, cancelled_at, voucher_code) VALUES (13, 1, 15, 1, 22000.00, 'COMPLETED', '2026-04-12 10:00:00', NULL, 'XPN-BOOK13'); -- Act 5
INSERT INTO bookings (id, user_id, session_id, participants, total_price, status, created_at, cancelled_at, voucher_code) VALUES (14, 1, 17, 1, 18000.00, 'COMPLETED', '2026-04-15 12:00:00', NULL, 'XPN-BOOK14'); -- Act 6
INSERT INTO bookings (id, user_id, session_id, participants, total_price, status, created_at, cancelled_at, voucher_code) VALUES (15, 1, 20, 2, 50000.00, 'COMPLETED', '2026-03-28 08:00:00', NULL, 'XPN-BOOK15'); -- Act 7
INSERT INTO bookings (id, user_id, session_id, participants, total_price, status, created_at, cancelled_at, voucher_code) VALUES (16, 1, 25, 2, 60000.00, 'COMPLETED', '2026-03-29 09:00:00', NULL, 'XPN-BOOK16'); -- Act 9
INSERT INTO bookings (id, user_id, session_id, participants, total_price, status, created_at, cancelled_at, voucher_code) VALUES (17, 1, 28, 1, 65000.00, 'COMPLETED', '2026-03-30 07:00:00', NULL, 'XPN-BOOK17'); -- Act 10
INSERT INTO bookings (id, user_id, session_id, participants, total_price, status, created_at, cancelled_at, voucher_code) VALUES (18, 1, 30, 1, 28000.00, 'COMPLETED', '2026-03-30 09:30:00', NULL, 'XPN-BOOK18'); -- Act 11
INSERT INTO bookings (id, user_id, session_id, participants, total_price, status, created_at, cancelled_at, voucher_code) VALUES (19, 1, 33, 2, 0.00,  'COMPLETED', '2026-03-31 10:00:00', NULL, 'XPN-BOOK19'); -- Act 12
INSERT INTO bookings (id, user_id, session_id, participants, total_price, status, created_at, cancelled_at, voucher_code) VALUES (20, 1, 38, 1, 12000.00, 'COMPLETED', '2026-05-01 09:30:00', NULL, 'XPN-BOOK20'); -- Act 14
INSERT INTO bookings (id, user_id, session_id, participants, total_price, status, created_at, cancelled_at, voucher_code) VALUES (21, 1, 58, 1, 22000.00, 'COMPLETED', '2026-04-18 08:00:00', NULL, 'XPN-BOOK21'); -- Act 15
INSERT INTO bookings (id, user_id, session_id, participants, total_price, status, created_at, cancelled_at, voucher_code) VALUES (22, 1, 59, 1, 19000.00, 'COMPLETED', '2026-04-16 06:30:00', NULL, 'XPN-BOOK22'); -- Act 16
INSERT INTO bookings (id, user_id, session_id, participants, total_price, status, created_at, cancelled_at, voucher_code) VALUES (23, 1, 60, 2, 28000.00, 'COMPLETED', '2026-04-17 10:30:00', NULL, 'XPN-BOOK23'); -- Act 17
SET IDENTITY_INSERT bookings OFF;

-- FAVORITES TEST DATA
-- Casos para verificar los flags sinCupos, cambioPrecio y cuposLiberados

-- Sesiones futuras para los 4 casos de prueba
-- (hoy = 2026-04-26; estas fechas son futuras)
SET IDENTITY_INSERT activity_sessions ON;
INSERT INTO activity_sessions (id, activity_id, start_time, capacity, booked_count, price_override) VALUES (100, 6,  '2026-05-16 09:00:00', 8,  8,  NULL);       -- Caso 1: sin cupos (lleno)
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

-- Reviews adicionales (objetivo: ~85%+ de actividades con rating)
INSERT INTO reviews (id, booking_id, user_id, activity_id, guide_id, activity_rating, guide_rating, comment, created_at) VALUES (7,  12, 1, 4, 3, 5, 5, 'Paisajes increibles y un recorrido muy completo. Recomiendo llevar abrigo.', '2026-04-21 18:00:00');
INSERT INTO reviews (id, booking_id, user_id, activity_id, guide_id, activity_rating, guide_rating, comment, created_at) VALUES (8,  13, 1, 5, 3, 4, 4, 'Excelente degustacion, muy buena seleccion de vinos y bodega hermosa.', '2026-04-20 15:00:00');
INSERT INTO reviews (id, booking_id, user_id, activity_id, guide_id, activity_rating, guide_rating, comment, created_at) VALUES (9,  14, 1, 6, 4, 5, 5, 'Circuito espectacular en bici, vistas increibles y ritmo tranquilo.', '2026-04-20 14:00:00');
INSERT INTO reviews (id, booking_id, user_id, activity_id, guide_id, activity_rating, guide_rating, comment, created_at) VALUES (10, 15, 1, 7, 5, 4, 4, 'Muy divertido y seguro para principiantes. Terminamos cansados pero felices.', '2026-04-02 13:00:00');
INSERT INTO reviews (id, booking_id, user_id, activity_id, guide_id, activity_rating, guide_rating, comment, created_at) VALUES (11, 16, 1, 9, 2, 5, 5, 'Adrenalina total, te mojas seguro. Inolvidable experiencia bajo los saltos.', '2026-04-01 16:30:00');
INSERT INTO reviews (id, booking_id, user_id, activity_id, guide_id, activity_rating, guide_rating, comment, created_at) VALUES (12, 17, 1, 10, 5, 4, 4, 'Una excursion larga pero vale la pena. El paisaje es impresionante.', '2026-04-10 20:00:00');
INSERT INTO reviews (id, booking_id, user_id, activity_id, guide_id, activity_rating, guide_rating, comment, created_at) VALUES (13, 18, 1, 11, 4, 5, 5, 'Navegacion hermosa, vimos fauna y el faro. Muy buen guia.', '2026-04-02 13:00:00');
INSERT INTO reviews (id, booking_id, user_id, activity_id, guide_id, activity_rating, guide_rating, comment, created_at) VALUES (14, 19, 1, 12, 1, 4, 5, 'Recorrido corto y muy interesante. Ideal para conocer historia de la ciudad.', '2026-04-01 12:30:00');
INSERT INTO reviews (id, booking_id, user_id, activity_id, guide_id, activity_rating, guide_rating, comment, created_at) VALUES (15, 20, 1, 14, 1, 4, 4, 'Buen resumen de la ciudad, comodo y con paradas lindas para fotos.', '2026-05-01 17:00:00');
INSERT INTO reviews (id, booking_id, user_id, activity_id, guide_id, activity_rating, guide_rating, comment, created_at) VALUES (16, 21, 1, 15, 2, 4, 4, 'Muy buena salida para sacar fotos y ver fauna. Guía paciente y claro.', '2026-04-20 13:00:00');
INSERT INTO reviews (id, booking_id, user_id, activity_id, guide_id, activity_rating, guide_rating, comment, created_at) VALUES (17, 22, 1, 16, 5, 5, 5, 'Sandboarding muy divertido, buena organizacion y equipo en buen estado.', '2026-04-18 13:00:00');
INSERT INTO reviews (id, booking_id, user_id, activity_id, guide_id, activity_rating, guide_rating, comment, created_at) VALUES (18, 23, 1, 17, 4, 4, 4, 'Vistas increibles desde el teleferico. Recomendado para una tarde distinta.', '2026-04-19 16:00:00');
SET IDENTITY_INSERT reviews OFF;

-- =========================================================
-- Itinerarios (Punto 27 - Feature 10)
-- Nota: requiere la tabla activity_itinerary_points (JPA ddl-auto=update)
-- =========================================================
INSERT INTO activity_itinerary_points (activity_id, position, name, address) VALUES
  (1, 1, 'Plaza Dorrego', 'Plaza Dorrego, San Telmo, Buenos Aires'),
  (1, 2, 'Mercado de San Telmo', 'Mercado de San Telmo, Bolivar 970, Buenos Aires'),
  (1, 3, 'Calle Defensa', 'Defensa, San Telmo, Buenos Aires');

INSERT INTO activity_itinerary_points (activity_id, position, name, address) VALUES
  (4, 1, 'Villavicencio', 'Villavicencio, Mendoza'),
  (4, 2, 'Uspallata', 'Uspallata, Mendoza'),
  (4, 3, 'Puente del Inca', 'Puente del Inca, Mendoza'),
  (4, 4, 'Mirador del Aconcagua', 'Parque Provincial Aconcagua, Mendoza');
-- 9. ADD DISCOUNTS TO PROMOTIONAL ACTIVITIES
-- Add 20% discount to Tour gastronomico por Palermo
UPDATE activities SET discount_percentage = 20 WHERE id = 3;
-- Add 15% discount to Excursion Alta Montana
UPDATE activities SET discount_percentage = 15 WHERE id = 4;
-- Add 25% discount to Degustacion de vinos en Lujan de Cuyo
UPDATE activities SET discount_percentage = 25 WHERE id = 5;
-- Add 10% discount to Circuito Chico en bici
UPDATE activities SET discount_percentage = 10 WHERE id = 6;
-- Add 30% discount to Kayak en el Nahuel Huapi
UPDATE activities SET discount_percentage = 30 WHERE id = 7;

-- 10. NEWS ARTICLES
SET IDENTITY_INSERT news ON;
INSERT INTO news (id, title, description, full_content, image_url, type, related_activity_id, published_at, valid_until, cta_text, cta_link) VALUES (1, N'Nuevas rutas de trekking en Bariloche', N'Se inauguraron 3 senderos nuevos en el Parque Nacional Nahuel Huapi con vistas panoramicas al lago.', N'Se inauguraron 3 senderos nuevos en el Parque Nacional Nahuel Huapi con vistas panoramicas al lago. Los senderos estan habilitados para todos los niveles de dificultad y cuentan con senalizacion bilingue. El acceso es gratuito con reserva previa.', 'https://images.pexels.com/photos/33448192/pexels-photo-33448192.jpeg', 'NEWS', NULL, '2026-04-15 10:00:00', NULL, N'Explorar actividades', NULL);
INSERT INTO news (id, title, description, full_content, image_url, type, related_activity_id, published_at, valid_until, cta_text, cta_link) VALUES (2, N'Temporada de ballenas en Puerto Madryn', N'Comenzo la temporada de avistaje de ballenas francas en la costa patagonica.', N'Comenzo la temporada de avistaje de ballenas francas en la costa patagonica. Las excursiones salen diariamente desde Puerto Piramides y la temporada se extiende hasta diciembre. Se recomienda reservar con anticipacion.', 'https://images.pexels.com/photos/7974939/pexels-photo-7974939.jpeg', 'NEWS', NULL, '2026-04-10 08:00:00', NULL, N'Ver excursiones', NULL);
INSERT INTO news (id, title, description, full_content, image_url, type, related_activity_id, published_at, valid_until, cta_text, cta_link) VALUES (3, N'Carnaval de Humahuaca 2026', N'El famoso Carnaval de Humahuaca celebra su edicion 2026 con mas de 50 comparsas.', N'El famoso Carnaval de Humahuaca celebra su edicion 2026 con mas de 50 comparsas. El evento se realiza en febrero y atrae a miles de turistas. Incluye musica folklorica, danzas tradicionales y la tradicional desentierra del diablo.', 'https://images.unsplash.com/photo-1530104685750-4217b1e0f9d6', 'NEWS', NULL, '2026-03-01 12:00:00', NULL, NULL, NULL);
INSERT INTO news (id, title, description, full_content, image_url, type, related_activity_id, published_at, valid_until, cta_text, cta_link) VALUES (4, N'Jujuy entre los 10 destinos emergentes del mundo', N'La revista Travel+ reconocio a Jujuy como uno de los destinos emergentes mas destacados del 2026.', N'La revista Travel+ reconocio a Jujuy como uno de los destinos emergentes mas destacados del 2026. La Quebrada de Humahuaca, las Salinas Grandes y la Puna fueron los atractivos que mas destacaron los editores.', 'https://images.unsplash.com/photo-1473496025427-9b7e1f62b4a1', 'NEWS', NULL, '2026-04-01 09:00:00', NULL, N'Descubrir Jujuy', NULL);
INSERT INTO news (id, title, description, full_content, image_url, type, related_activity_id, published_at, valid_until, cta_text, cta_link) VALUES (5, N'Reabren las Cataratas del Iguazu tras renovacion', N'Los circuitos del Parque Nacional Iguazu reabren con pasarelas renovadas y nuevas miradores.', N'Los circuitos del Parque Nacional Iguazu reabren con pasarelas renovadas y nuevas miradores. La Garganta del Diablo cuenta ahora con una pasarela de 1.2 km con vista panoramica de 360 grados.', 'https://images.unsplash.com/photo-1505761671935-60b3a7427bad', 'NEWS', 8, '2026-04-05 11:00:00', NULL, N'Ver actividad', NULL);
SET IDENTITY_INSERT news OFF;

-- Idempotente: asegura imÃ¡genes en news existentes (para no depender de recrear la DB)
UPDATE news SET image_url = 'https://images.pexels.com/photos/33448192/pexels-photo-33448192.jpeg' WHERE id = 1;
UPDATE news SET image_url = 'https://images.pexels.com/photos/7974939/pexels-photo-7974939.jpeg' WHERE id = 2;

-- 11. ITINERARIOS (Feature 10)
-- Mantenerlo simple (sin bloques BEGIN/END) para que Spring ejecute el script sin rollback.
DELETE FROM activity_itinerary_points WHERE activity_id IN (1,4);

INSERT INTO activity_itinerary_points (activity_id, position, name, address) VALUES
  (1, 1, 'Plaza Dorrego', 'Plaza Dorrego, San Telmo, Buenos Aires'),
  (1, 2, 'Mercado de San Telmo', 'Mercado de San Telmo, Bolivar 970, Buenos Aires'),
  (1, 3, 'Calle Defensa', 'Defensa, San Telmo, Buenos Aires');

INSERT INTO activity_itinerary_points (activity_id, position, name, address) VALUES
  (4, 1, 'Villavicencio', 'Villavicencio, Mendoza'),
  (4, 2, 'Uspallata', 'Uspallata, Mendoza'),
  (4, 3, 'Puente del Inca', 'Puente del Inca, Mendoza'),
  (4, 4, 'Mirador del Aconcagua', 'Parque Provincial Aconcagua, Mendoza');

