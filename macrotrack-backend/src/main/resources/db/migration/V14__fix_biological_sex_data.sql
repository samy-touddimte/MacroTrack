UPDATE users SET sex = 'MALE' WHERE LOWER(sex) IN ('m', 'homme', 'male');
UPDATE users SET sex = 'FEMALE' WHERE LOWER(sex) IN ('f', 'femme', 'female');
