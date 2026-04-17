INSERT INTO CINEMA (ADRESS, CITY, NAME, POSTAL_CODE) VALUES
('Major,15', 'Tarragona', 'Oscars' , 43100);

INSERT INTO CINEMA (ADRESS, CITY, NAME, POSTAL_CODE) VALUES
('Major,30', 'Tarragona', 'JCS' , 43100);

INSERT INTO CINEMA (ADRESS, CITY, NAME, POSTAL_CODE) VALUES
('Major,40', 'Tarragona', 'Yelmus' , 43100);

INSERT INTO ROOM(NAME, CAPACITY, CINEMA_ID) VALUES('Sala 1',120,1);
INSERT INTO ROOM(NAME, CAPACITY, CINEMA_ID) VALUES('Sala 2',120,1);
INSERT INTO ROOM(NAME, CAPACITY, CINEMA_ID) VALUES('Sala 3',120,1);

INSERT INTO ROOM(NAME, CAPACITY, CINEMA_ID) VALUES('Sala 1',120,2);
INSERT INTO ROOM(NAME, CAPACITY, CINEMA_ID) VALUES('Sala 2',120,2);
INSERT INTO ROOM(NAME, CAPACITY, CINEMA_ID) VALUES('Sala 3',120,2);

INSERT INTO ROOM(NAME, CAPACITY, CINEMA_ID) VALUES('Sala 1',120,3);
INSERT INTO ROOM(NAME, CAPACITY, CINEMA_ID) VALUES('Sala 2',120,3);
INSERT INTO ROOM(NAME, CAPACITY, CINEMA_ID) VALUES('Sala 3',120,3);


INSERT INTO pelicula (Titol, Durada, sinopsi, estrena) VALUES
('Avatar', 162, 'Un exmarine viaja a Pandora y se integra en la cultura Na''vi.', '2009-12-18');

INSERT INTO pelicula (Titol, Durada, sinopsi, estrena) VALUES
('Titanic', 195, 'Historia de amor entre Jack y Rose a bordo del famoso transatlántico.', '1997-12-19');

INSERT INTO pelicula (Titol, Durada, sinopsi, estrena) VALUES
('Inception', 148, 'Un ladrón roba secretos entrando en los sueños de las personas.', '2010-07-16');

INSERT INTO pelicula (Titol, Durada, sinopsi, estrena) VALUES
('Batman', 152, 'Batman se enfrenta al Joker que quiere sembrar el caos en Gotham.', '2008-07-18');

INSERT INTO pelicula (Titol, Durada, sinopsi, estrena) VALUES
('Interstellar', 169, 'Un grupo de astronautas viaja a través de un agujero de gusano para salvar la humanidad.', '2014-11-07');

INSERT INTO pelicula (Titol, Durada, sinopsi, estrena) VALUES
('Jurassic Park', 127, 'Un parque temático de dinosaurios clonados se convierte en una pesadilla.', '1993-06-11');


INSERT INTO Screening (screening_date_time, price, movie_id, room_id)
VALUES -- Avatar (id=1)
    ('2026-04-30T18:00', 8.50, 1, 1),
    ('2026-04-30T21:30', 9.50, 1, 1),
    ('2026-05-01T17:00', 8.50, 1, 2),
    ('2026-05-01T20:30', 9.50, 1, 2),
    ('2026-05-02T18:30', 9.00, 1, 3),
    -- Titanic (id=2)
    ('2026-05-01T19:00', 7.50, 2, 2),
    ('2026-05-01T22:00', 8.50, 2, 2),
    ('2026-05-02T18:00', 7.50, 2, 3),
    ('2026-05-02T21:00', 8.50, 2, 3),
    ('2026-05-03T20:00', 8.00, 2, 1),
    -- Inception (id=3)
    ('2026-05-01T17:30', 8.50, 3, 3),
    ('2026-05-01T21:00', 9.50, 3, 3),
    ('2026-05-02T16:30', 8.50, 3, 1),
    ('2026-05-02T20:00', 9.50, 3, 1),
    ('2026-05-03T19:00', 9.00, 3, 2),
    -- Batman (id=4)
    ('2026-05-01T20:00', 8.00, 4, 1),
    ('2026-05-01T22:45', 9.00, 4, 1),
    ('2026-05-02T18:30', 8.50, 4, 2),
    ('2026-05-02T21:30', 9.00, 4, 2),
    ('2026-05-03T17:30', 8.00, 4, 3),
    -- Interstellar (id=5)
    ('2026-05-01T19:30', 9.00, 5, 3),
    ('2026-05-01T22:30', 10.00, 5, 3),
    ('2026-05-02T19:45', 9.50, 5, 1),
    ('2026-05-02T22:15', 10.00, 5, 1),
    ('2026-05-03T20:30', 9.50, 5, 2),
    -- Jurassic Park (id=6)
    ('2026-05-01T16:30', 7.50, 6, 2),
    ('2026-05-01T19:00', 8.00, 6, 2),
    ('2026-05-02T17:00', 7.50, 6, 3),
    ('2026-05-02T20:00', 8.50, 6, 3),
    ('2026-05-03T18:00', 8.00, 6, 1);
