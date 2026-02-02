-- Perfiles
INSERT INTO perfil_usuario (nombre) SELECT 'ADMIN' WHERE NOT EXISTS (SELECT 1 FROM perfil_usuario WHERE nombre = 'ADMIN');
INSERT INTO perfil_usuario (nombre) SELECT 'USER' WHERE NOT EXISTS (SELECT 1 FROM perfil_usuario WHERE nombre = 'USER');

-- Medios de Pago
INSERT INTO medio_pago (nombre) SELECT 'EFECTIVO' WHERE NOT EXISTS (SELECT 1 FROM medio_pago WHERE nombre = 'EFECTIVO');
INSERT INTO medio_pago (nombre) SELECT 'CHEQUE' WHERE NOT EXISTS (SELECT 1 FROM medio_pago WHERE nombre = 'CHEQUE');
INSERT INTO medio_pago (nombre) SELECT 'TRANSFERENCIA' WHERE NOT EXISTS (SELECT 1 FROM medio_pago WHERE nombre = 'TRANSFERENCIA');
INSERT INTO medio_pago (nombre) SELECT 'DEPOSITO' WHERE NOT EXISTS (SELECT 1 FROM medio_pago WHERE nombre = 'DEPOSITO');

-- Tipos de Finanza
INSERT INTO tipo_finanza (nombre) SELECT 'INGRESO' WHERE NOT EXISTS (SELECT 1 FROM tipo_finanza WHERE nombre = 'INGRESO');
INSERT INTO tipo_finanza (nombre) SELECT 'EGRESO' WHERE NOT EXISTS (SELECT 1 FROM tipo_finanza WHERE nombre = 'EGRESO');

-- Conceptos
INSERT INTO concepto (nombre) SELECT 'Sueldo' WHERE NOT EXISTS (SELECT 1 FROM concepto WHERE nombre = 'Sueldo');
INSERT INTO concepto (nombre) SELECT 'Arriendo' WHERE NOT EXISTS (SELECT 1 FROM concepto WHERE nombre = 'Arriendo');
INSERT INTO concepto (nombre) SELECT 'Servicios' WHERE NOT EXISTS (SELECT 1 FROM concepto WHERE nombre = 'Servicios');
INSERT INTO concepto (nombre) SELECT 'Comida' WHERE NOT EXISTS (SELECT 1 FROM concepto WHERE nombre = 'Comida');
INSERT INTO concepto (nombre) SELECT 'Transporte' WHERE NOT EXISTS (SELECT 1 FROM concepto WHERE nombre = 'Transporte');
INSERT INTO concepto (nombre) SELECT 'Otros' WHERE NOT EXISTS (SELECT 1 FROM concepto WHERE nombre = 'Otros');

-- Usuarios (Passwords: admin1234 and kate1234)
INSERT INTO usuario (username, email, password, perfil_id) 
SELECT 'admin', 'admin@easysave.com', '$2a$10$agBYblLkr1gMP6IjZhyoC.nrhou9Jl5.mmtGMCs7xzml0XHGQpGce', 1 
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE username = 'admin');

INSERT INTO usuario (username, email, password, perfil_id) 
SELECT 'katherin', 'katherin@easysave.com', '$2a$10$3eEdw45mZHhWWVtguQ9rgOanUkZg9Dj4zs.UE2zzubWeA1hrsdNvK', 2 
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE username = 'katherin');

-- Finanzas
INSERT INTO finanza (concepto_id, cantidad, fecha, tipo_id, medio_id, usuario_username)
SELECT (SELECT id FROM concepto WHERE nombre = 'Sueldo'), 5000, CURRENT_DATE, 1, 3, 'admin'
WHERE NOT EXISTS (SELECT 1 FROM finanza WHERE concepto_id = (SELECT id FROM concepto WHERE nombre = 'Sueldo') AND usuario_username = 'admin');

INSERT INTO finanza (concepto_id, cantidad, fecha, tipo_id, medio_id, usuario_username)
SELECT (SELECT id FROM concepto WHERE nombre = 'Arriendo'), 1500, CURRENT_DATE, 2, 3, 'admin'
WHERE NOT EXISTS (SELECT 1 FROM finanza WHERE concepto_id = (SELECT id FROM concepto WHERE nombre = 'Arriendo') AND usuario_username = 'admin');

-- Finanzas OCTUBRE 2025 para katherin (7 movimientos)
INSERT INTO finanza (concepto_id, cantidad, fecha, tipo_id, medio_id, usuario_username)
SELECT (SELECT id FROM concepto WHERE nombre = 'Sueldo'), 3500, '2025-10-01', 1, 3, 'katherin'
WHERE NOT EXISTS (SELECT 1 FROM finanza WHERE concepto_id = (SELECT id FROM concepto WHERE nombre = 'Sueldo') AND fecha = '2025-10-01' AND usuario_username = 'katherin');

INSERT INTO finanza (concepto_id, cantidad, fecha, tipo_id, medio_id, usuario_username)
SELECT (SELECT id FROM concepto WHERE nombre = 'Arriendo'), 1200, '2025-10-05', 2, 3, 'katherin'
WHERE NOT EXISTS (SELECT 1 FROM finanza WHERE concepto_id = (SELECT id FROM concepto WHERE nombre = 'Arriendo') AND fecha = '2025-10-05' AND usuario_username = 'katherin');

INSERT INTO finanza (concepto_id, cantidad, fecha, tipo_id, medio_id, usuario_username)
SELECT (SELECT id FROM concepto WHERE nombre = 'Servicios'), 150, '2025-10-08', 2, 1, 'katherin'
WHERE NOT EXISTS (SELECT 1 FROM finanza WHERE concepto_id = (SELECT id FROM concepto WHERE nombre = 'Servicios') AND fecha = '2025-10-08' AND usuario_username = 'katherin');

INSERT INTO finanza (concepto_id, cantidad, fecha, tipo_id, medio_id, usuario_username)
SELECT (SELECT id FROM concepto WHERE nombre = 'Comida'), 450, '2025-10-12', 2, 1, 'katherin'
WHERE NOT EXISTS (SELECT 1 FROM finanza WHERE concepto_id = (SELECT id FROM concepto WHERE nombre = 'Comida') AND fecha = '2025-10-12' AND usuario_username = 'katherin');

INSERT INTO finanza (concepto_id, cantidad, fecha, tipo_id, medio_id, usuario_username)
SELECT (SELECT id FROM concepto WHERE nombre = 'Transporte'), 80, '2025-10-15', 2, 1, 'katherin'
WHERE NOT EXISTS (SELECT 1 FROM finanza WHERE concepto_id = (SELECT id FROM concepto WHERE nombre = 'Transporte') AND fecha = '2025-10-15' AND usuario_username = 'katherin');

INSERT INTO finanza (concepto_id, cantidad, fecha, tipo_id, medio_id, usuario_username)
SELECT (SELECT id FROM concepto WHERE nombre = 'Otros'), 1800, '2025-10-20', 1, 3, 'katherin'
WHERE NOT EXISTS (SELECT 1 FROM finanza WHERE concepto_id = (SELECT id FROM concepto WHERE nombre = 'Otros') AND fecha = '2025-10-20' AND usuario_username = 'katherin');

INSERT INTO finanza (concepto_id, cantidad, fecha, tipo_id, medio_id, usuario_username)
SELECT (SELECT id FROM concepto WHERE nombre = 'Otros'), 50, '2025-10-28', 2, 4, 'katherin'
WHERE NOT EXISTS (SELECT 1 FROM finanza WHERE concepto_id = (SELECT id FROM concepto WHERE nombre = 'Otros') AND fecha = '2025-10-28' AND usuario_username = 'katherin');


-- Finanzas NOVIEMBRE 2025 para katherin (9 movimientos)
INSERT INTO finanza (concepto_id, cantidad, fecha, tipo_id, medio_id, usuario_username)
SELECT (SELECT id FROM concepto WHERE nombre = 'Sueldo'), 3500, '2025-11-01', 1, 3, 'katherin'
WHERE NOT EXISTS (SELECT 1 FROM finanza WHERE concepto_id = (SELECT id FROM concepto WHERE nombre = 'Sueldo') AND fecha = '2025-11-01' AND usuario_username = 'katherin');

INSERT INTO finanza (concepto_id, cantidad, fecha, tipo_id, medio_id, usuario_username)
SELECT (SELECT id FROM concepto WHERE nombre = 'Arriendo'), 1200, '2025-11-05', 2, 3, 'katherin'
WHERE NOT EXISTS (SELECT 1 FROM finanza WHERE concepto_id = (SELECT id FROM concepto WHERE nombre = 'Arriendo') AND fecha = '2025-11-05' AND usuario_username = 'katherin');

INSERT INTO finanza (concepto_id, cantidad, fecha, tipo_id, medio_id, usuario_username)
SELECT (SELECT id FROM concepto WHERE nombre = 'Servicios'), 165, '2025-11-07', 2, 1, 'katherin'
WHERE NOT EXISTS (SELECT 1 FROM finanza WHERE concepto_id = (SELECT id FROM concepto WHERE nombre = 'Servicios') AND fecha = '2025-11-07' AND usuario_username = 'katherin');

INSERT INTO finanza (concepto_id, cantidad, fecha, tipo_id, medio_id, usuario_username)
SELECT (SELECT id FROM concepto WHERE nombre = 'Comida'), 520, '2025-11-10', 2, 1, 'katherin'
WHERE NOT EXISTS (SELECT 1 FROM finanza WHERE concepto_id = (SELECT id FROM concepto WHERE nombre = 'Comida') AND fecha = '2025-11-10' AND usuario_username = 'katherin');

INSERT INTO finanza (concepto_id, cantidad, fecha, tipo_id, medio_id, usuario_username)
SELECT (SELECT id FROM concepto WHERE nombre = 'Transporte'), 90, '2025-11-12', 2, 1, 'katherin'
WHERE NOT EXISTS (SELECT 1 FROM finanza WHERE concepto_id = (SELECT id FROM concepto WHERE nombre = 'Transporte') AND fecha = '2025-11-12' AND usuario_username = 'katherin');

INSERT INTO finanza (concepto_id, cantidad, fecha, tipo_id, medio_id, usuario_username)
SELECT (SELECT id FROM concepto WHERE nombre = 'Otros'), 2200, '2025-11-15', 1, 3, 'katherin'
WHERE NOT EXISTS (SELECT 1 FROM finanza WHERE concepto_id = (SELECT id FROM concepto WHERE nombre = 'Otros') AND fecha = '2025-11-15' AND usuario_username = 'katherin');

INSERT INTO finanza (concepto_id, cantidad, fecha, tipo_id, medio_id, usuario_username)
SELECT (SELECT id FROM concepto WHERE nombre = 'Otros'), 200, '2025-11-18', 2, 1, 'katherin'
WHERE NOT EXISTS (SELECT 1 FROM finanza WHERE concepto_id = (SELECT id FROM concepto WHERE nombre = 'Otros') AND fecha = '2025-11-18' AND usuario_username = 'katherin');

INSERT INTO finanza (concepto_id, cantidad, fecha, tipo_id, medio_id, usuario_username)
SELECT (SELECT id FROM concepto WHERE nombre = 'Otros'), 50, '2025-11-22', 2, 4, 'katherin'
WHERE NOT EXISTS (SELECT 1 FROM finanza WHERE concepto_id = (SELECT id FROM concepto WHERE nombre = 'Otros') AND fecha = '2025-11-22' AND usuario_username = 'katherin');

INSERT INTO finanza (concepto_id, cantidad, fecha, tipo_id, medio_id, usuario_username)
SELECT (SELECT id FROM concepto WHERE nombre = 'Comida'), 380, '2025-11-28', 2, 1, 'katherin'
WHERE NOT EXISTS (SELECT 1 FROM finanza WHERE concepto_id = (SELECT id FROM concepto WHERE nombre = 'Comida') AND fecha = '2025-11-28' AND usuario_username = 'katherin');


-- Finanzas DICIEMBRE 2025 para katherin (11 movimientos)
INSERT INTO finanza (concepto_id, cantidad, fecha, tipo_id, medio_id, usuario_username)
SELECT (SELECT id FROM concepto WHERE nombre = 'Sueldo'), 3500, '2025-12-01', 1, 3, 'katherin'
WHERE NOT EXISTS (SELECT 1 FROM finanza WHERE concepto_id = (SELECT id FROM concepto WHERE nombre = 'Sueldo') AND fecha = '2025-12-01' AND usuario_username = 'katherin');

INSERT INTO finanza (concepto_id, cantidad, fecha, tipo_id, medio_id, usuario_username)
SELECT (SELECT id FROM concepto WHERE nombre = 'Arriendo'), 1200, '2025-12-05', 2, 3, 'katherin'
WHERE NOT EXISTS (SELECT 1 FROM finanza WHERE concepto_id = (SELECT id FROM concepto WHERE nombre = 'Arriendo') AND fecha = '2025-12-05' AND usuario_username = 'katherin');

INSERT INTO finanza (concepto_id, cantidad, fecha, tipo_id, medio_id, usuario_username)
SELECT (SELECT id FROM concepto WHERE nombre = 'Servicios'), 180, '2025-12-07', 2, 1, 'katherin'
WHERE NOT EXISTS (SELECT 1 FROM finanza WHERE concepto_id = (SELECT id FROM concepto WHERE nombre = 'Servicios') AND fecha = '2025-12-07' AND usuario_username = 'katherin');

INSERT INTO finanza (concepto_id, cantidad, fecha, tipo_id, medio_id, usuario_username)
SELECT (SELECT id FROM concepto WHERE nombre = 'Comida'), 600, '2025-12-10', 2, 1, 'katherin'
WHERE NOT EXISTS (SELECT 1 FROM finanza WHERE concepto_id = (SELECT id FROM concepto WHERE nombre = 'Comida') AND fecha = '2025-12-10' AND usuario_username = 'katherin');

INSERT INTO finanza (concepto_id, cantidad, fecha, tipo_id, medio_id, usuario_username)
SELECT (SELECT id FROM concepto WHERE nombre = 'Transporte'), 95, '2025-12-12', 2, 1, 'katherin'
WHERE NOT EXISTS (SELECT 1 FROM finanza WHERE concepto_id = (SELECT id FROM concepto WHERE nombre = 'Transporte') AND fecha = '2025-12-12' AND usuario_username = 'katherin');

INSERT INTO finanza (concepto_id, cantidad, fecha, tipo_id, medio_id, usuario_username)
SELECT (SELECT id FROM concepto WHERE nombre = 'Otros'), 2500, '2025-12-14', 1, 3, 'katherin'
WHERE NOT EXISTS (SELECT 1 FROM finanza WHERE concepto_id = (SELECT id FROM concepto WHERE nombre = 'Otros') AND fecha = '2025-12-14' AND usuario_username = 'katherin');

INSERT INTO finanza (concepto_id, cantidad, fecha, tipo_id, medio_id, usuario_username)
SELECT (SELECT id FROM concepto WHERE nombre = 'Otros'), 450, '2025-12-16', 2, 2, 'katherin'
WHERE NOT EXISTS (SELECT 1 FROM finanza WHERE concepto_id = (SELECT id FROM concepto WHERE nombre = 'Otros') AND fecha = '2025-12-16' AND usuario_username = 'katherin');

INSERT INTO finanza (concepto_id, cantidad, fecha, tipo_id, medio_id, usuario_username)
SELECT (SELECT id FROM concepto WHERE nombre = 'Otros'), 50, '2025-12-20', 2, 4, 'katherin'
WHERE NOT EXISTS (SELECT 1 FROM finanza WHERE concepto_id = (SELECT id FROM concepto WHERE nombre = 'Otros') AND fecha = '2025-12-20' AND usuario_username = 'katherin');

INSERT INTO finanza (concepto_id, cantidad, fecha, tipo_id, medio_id, usuario_username)
SELECT (SELECT id FROM concepto WHERE nombre = 'Comida'), 420, '2025-12-22', 2, 1, 'katherin'
WHERE NOT EXISTS (SELECT 1 FROM finanza WHERE concepto_id = (SELECT id FROM concepto WHERE nombre = 'Comida') AND fecha = '2025-12-22' AND usuario_username = 'katherin');

INSERT INTO finanza (concepto_id, cantidad, fecha, tipo_id, medio_id, usuario_username)
SELECT (SELECT id FROM concepto WHERE nombre = 'Otros'), 280, '2025-12-26', 2, 1, 'katherin'
WHERE NOT EXISTS (SELECT 1 FROM finanza WHERE concepto_id = (SELECT id FROM concepto WHERE nombre = 'Otros') AND fecha = '2025-12-26' AND usuario_username = 'katherin');

INSERT INTO finanza (concepto_id, cantidad, fecha, tipo_id, medio_id, usuario_username)
SELECT (SELECT id FROM concepto WHERE nombre = 'Otros'), 1500, '2025-12-30', 1, 3, 'katherin'
WHERE NOT EXISTS (SELECT 1 FROM finanza WHERE concepto_id = (SELECT id FROM concepto WHERE nombre = 'Otros') AND fecha = '2025-12-30' AND usuario_username = 'katherin');


