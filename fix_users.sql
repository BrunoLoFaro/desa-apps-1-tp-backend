-- Fix seeded users with correct BCrypt hash
SET IDENTITY_INSERT users ON;

UPDATE users 
SET password_hash = '$2a$10$Zq.580gBFNWXppKFYRXJouckqEUPAqP469tPSTGV83yv5yRcXz8t6'
WHERE id IN (1, 2);

SET IDENTITY_INSERT users OFF;

-- Verify
SELECT id, email, password_hash FROM users WHERE id IN (1, 2);
