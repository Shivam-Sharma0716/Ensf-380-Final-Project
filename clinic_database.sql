-- ==========================================
-- PostgreSQL setup for Veterinary Clinic DB
-- ==========================================

-- 1) Create the database (run from a superuser or a user with createdb)
-- If you're already inside the target DB, you can skip this and the \c line.
CREATE DATABASE vet_clinic;

-- 2) Connect to the database (psql command)
\c vet_clinic

-- (Optional) Create a dedicated schema
CREATE SCHEMA IF NOT EXISTS public;

-- ------------------------------------------
-- ENUM Types
-- ------------------------------------------
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'role_type') THEN
    CREATE TYPE role_type AS ENUM ('Vet', 'Receptionist');
  END IF;

  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'species_type') THEN
    CREATE TYPE species_type AS ENUM ('Dog', 'Cat');
  END IF;
END$$;

-- ------------------------------------------
-- TABLE: Staff
-- ------------------------------------------
CREATE TABLE IF NOT EXISTS staff (
    id          INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name        TEXT NOT NULL,
    role        role_type NOT NULL,
    specialization TEXT
);

-- Sample data for Staff
INSERT INTO staff (name, role, specialization) VALUES
('Dr. Test Vet 1',      'Vet', 'Surgery'),
('Dr. Test Vet 2',      'Vet', 'Dermatology'),
('Test Receptionist 1', 'Receptionist', NULL),
('Test Receptionist 2', 'Receptionist', NULL)
ON CONFLICT DO NOTHING;

-- ------------------------------------------
-- TABLE: Owners
-- ------------------------------------------
CREATE TABLE IF NOT EXISTS owners (
    id      INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name    TEXT NOT NULL,
    phone   TEXT NOT NULL,
    email   TEXT NOT NULL UNIQUE
);

-- Sample data for Owners
INSERT INTO owners (name, phone, email) VALUES
('Test Owner 1', '403-555-0101', 'test.owner1@email.com'),
('Test Owner 2', '403-555-0102', 'test.owner2@email.com'),
('Test Owner 3', '403-555-0103', 'test.owner3@email.com')
ON CONFLICT DO NOTHING;

-- ------------------------------------------
-- TABLE: Pets
-- ------------------------------------------
CREATE TABLE IF NOT EXISTS pets (
    id            INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name          TEXT NOT NULL,
    age           INT  NOT NULL CHECK (age >= 0),
    species       species_type NOT NULL,
    owner_id      INT NOT NULL REFERENCES owners(id) ON DELETE CASCADE,
    is_vaccinated BOOLEAN,
    is_indoor     BOOLEAN
    -- You can enforce cross-field rules with a CHECK if desired.
    -- Example (optional):
    -- CHECK (
    --   (species = 'Dog' AND is_indoor IS NULL) OR
    --   (species = 'Cat' AND is_vaccinated IS NULL) OR
    --   (species IN ('Dog','Cat'))
    -- )
);

-- Sample data for Pets
INSERT INTO pets (name, age, species, owner_id, is_vaccinated, is_indoor) VALUES
('Test Dog 1', 3, 'Dog', 1, TRUE,  NULL),
('Test Cat 1', 2, 'Cat', 2, NULL,  TRUE),
('Test Dog 2', 5, 'Dog', 3, FALSE, NULL),
('Test Cat 2', 4, 'Cat', 1, NULL,  FALSE)
ON CONFLICT DO NOTHING;

-- ------------------------------------------
-- TABLE: Appointments
-- ------------------------------------------
CREATE TABLE IF NOT EXISTS appointments (
    id        INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    pet_id    INT NOT NULL REFERENCES pets(id) ON DELETE CASCADE,
    vet_id    INT NOT NULL REFERENCES staff(id) ON DELETE CASCADE,
    date_time TIMESTAMP NOT NULL,
    notes     TEXT,
    -- Prevent double-booking a vet at the same timestamp
    CONSTRAINT uq_vet_datetime UNIQUE (vet_id, date_time)
);

-- Sample data for Appointments
-- Make sure vet_id refers to a Vet (id 1 or 2 from staff table)
INSERT INTO appointments (pet_id, vet_id, date_time, notes) VALUES
(1, 1, '2026-08-14 10:00:00', 'Vaccination test appointment'),
(2, 2, '2026-08-14 11:00:00', 'Allergy check test appointment'),
(3, 1, '2026-08-15 09:00:00', 'General check-up test'),
(4, 2, '2026-08-15 14:00:00', 'Behaviour consultation test')
ON CONFLICT DO NOTHING;

-- ------------------------------------------
-- Helpful Indexes (optional, improves lookups)
-- ------------------------------------------
CREATE INDEX IF NOT EXISTS idx_pets_owner_id ON pets(owner_id);
CREATE INDEX IF NOT EXISTS idx_appts_pet_id   ON appointments(pet_id);
CREATE INDEX IF NOT EXISTS idx_appts_vet_id   ON appointments(vet_id);
CREATE INDEX IF NOT EXISTS idx_appts_dt       ON appointments(date_time);
