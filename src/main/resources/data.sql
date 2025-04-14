-- User beszúrása
INSERT INTO user (id, name, date_of_birth) 
VALUES ('cfff8f37-5dae-47df-bf86-4e216fe3eaee', 'Kovacs Istvan', '1999-06-10');

-- Todo beszúrása (ehhez kapcsolódó user)
INSERT INTO todo (id, title, due_date, completed, user_id) 
VALUES 
  ('b3c517f5-e9bf-431b-a7f0-47f6a19825c9', 'Téma elkészítése', '2025-05-01', false, 'cfff8f37-5dae-47df-bf86-4e216fe3eaee'),
  ('e2b6197e-2e8a-4abf-a935-f0c5db46768a', 'Beadandó leadása', '2025-06-01', false, 'cfff8f37-5dae-47df-bf86-4e216fe3eaee');

