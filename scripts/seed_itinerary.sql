-- Seed de itinerarios (Feature 10 - Punto 27)
-- Ejecutar contra una DB existente:
--   sqlcmd -S localhost -U sa -P "YourStrong!Passw0rd" -d desa_db -i scripts/seed_itinerary.sql

IF OBJECT_ID('activity_itinerary_points', 'U') IS NULL
BEGIN
    PRINT 'Tabla activity_itinerary_points no existe. Levantar la app para que JPA la cree (ddl-auto=update).';
    RETURN;
END

-- Activity 1 - Free Tour por San Telmo
IF NOT EXISTS (SELECT 1 FROM activity_itinerary_points WHERE activity_id = 1)
BEGIN
    INSERT INTO activity_itinerary_points (activity_id, position, name, address) VALUES
      (1, 1, 'Plaza Dorrego', 'Plaza Dorrego, San Telmo, Buenos Aires'),
      (1, 2, 'Mercado de San Telmo', 'Mercado de San Telmo, Bolivar 970, Buenos Aires'),
      (1, 3, 'Calle Defensa', 'Defensa, San Telmo, Buenos Aires');
END

-- Activity 4 - Excursion Alta Montana
IF NOT EXISTS (SELECT 1 FROM activity_itinerary_points WHERE activity_id = 4)
BEGIN
    INSERT INTO activity_itinerary_points (activity_id, position, name, address) VALUES
      (4, 1, 'Villavicencio', 'Villavicencio, Mendoza'),
      (4, 2, 'Uspallata', 'Uspallata, Mendoza'),
      (4, 3, 'Puente del Inca', 'Puente del Inca, Mendoza'),
      (4, 4, 'Mirador del Aconcagua', 'Parque Provincial Aconcagua, Mendoza');
END

