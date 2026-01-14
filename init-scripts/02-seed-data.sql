-- Clientes de teste
INSERT INTO customers (id, cpf, name, birth_date, phone, version) VALUES
  ('123e4567-e89b-12d3-a456-426614174000', '12345678901', 'João Silva Santos', '1985-03-15', '11987654321', 0),
  ('223e4567-e89b-12d3-a456-426614174001', '23456789012', 'Maria Oliveira Costa', '1990-07-22', '21976543210', 0),
  ('323e4567-e89b-12d3-a456-426614174002', '34567890123', 'Pedro Henrique Souza', '1988-11-30', '11965432109', 0),
  ('423e4567-e89b-12d3-a456-426614174003', '45678901234', 'Ana Paula Ferreira', '1992-05-18', '11954321098', 0),
  ('523e4567-e89b-12d3-a456-426614174004', '56789012345', 'Carlos Eduardo Lima', '1987-09-25', '11943210987', 0),
  ('623e4567-e89b-12d3-a456-426614174005', '67890123456', 'Juliana Mendes Rocha', '1995-01-10', '11932109876', 0),
  ('723e4567-e89b-12d3-a456-426614174006', '78901234567', 'Roberto Alves Pereira', '1983-08-05', '11921098765', 0),
  ('823e4567-e89b-12d3-a456-426614174007', '89012345678', 'Fernanda Costa Lima', '1991-12-20', '11910987654', 0)
ON CONFLICT (cpf) DO NOTHING;

-- Endereços
INSERT INTO addresses (customer_id, street, number, complement, neighborhood, city, state, zip_code) VALUES
 ('123e4567-e89b-12d3-a456-426614174000', 'Rua das Flores', '123', 'Apto 45', 'Jardim Paulista', 'São Paulo', 'SP', '01310100'),
 ('223e4567-e89b-12d3-a456-426614174001', 'Avenida Atlântica', '1000', 'Cobertura', 'Copacabana', 'Rio de Janeiro', 'RJ', '22021001'),
 ('323e4567-e89b-12d3-a456-426614174002', 'Rua Augusta', '500', NULL, 'Consolação', 'São Paulo', 'SP', '01305000'),
 ('423e4567-e89b-12d3-a456-426614174003', 'Avenida Boa Viagem', '250', 'Apto 1502', 'Boa Viagem', 'Recife', 'PE', '51020000'),
 ('523e4567-e89b-12d3-a456-426614174004', 'Avenida Brigadeiro Faria Lima', '1500', 'Andar 10', 'Pinheiros', 'São Paulo', 'SP', '01451000'),
 ('623e4567-e89b-12d3-a456-426614174005', 'Rua dos Caetés', '789', 'Casa', 'Floresta', 'Belo Horizonte', 'MG', '31015000'),
 ('723e4567-e89b-12d3-a456-426614174006', 'Avenida Getúlio Vargas', '333', 'Sala 501', 'Centro', 'Curitiba', 'PR', '80020100'),
 ('823e4567-e89b-12d3-a456-426614174007', 'Rua da Praia', '456', 'Apto 302', 'Centro Histórico', 'Porto Alegre', 'RS', '90010000')
ON CONFLICT DO NOTHING;

-- Apólices de seguro já contratadas
INSERT INTO insurance_policies (id, customer_id, policy_number, policy_type, coverage_amount, monthly_premium, status, start_date, end_date, version) VALUES
  ('a23e4567-e89b-12d3-a456-426614174001', '123e4567-e89b-12d3-a456-426614174000', 'POL-2024-0001', 'BRONZE', 50000.00, 150.00, 'ACTIVE', '2024-01-15', '2025-01-15', 0),
  ('b23e4567-e89b-12d3-a456-426614174002', '223e4567-e89b-12d3-a456-426614174001', 'POL-2024-0002', 'SILVER', 100000.00, 300.00, 'ACTIVE', '2024-02-01', '2025-02-01', 0),
  ('c23e4567-e89b-12d3-a456-426614174003', '323e4567-e89b-12d3-a456-426614174002', 'POL-2024-0003', 'GOLD', 200000.00, 500.00, 'ACTIVE', '2024-03-10', '2025-03-10', 0),
  ('d23e4567-e89b-12d3-a456-426614174004', '523e4567-e89b-12d3-a456-426614174004', 'POL-2024-0004', 'SILVER', 100000.00, 300.00, 'ACTIVE', '2024-04-05', '2025-04-05', 0)
ON CONFLICT (policy_number) DO NOTHING;

-- Simulações realizadas
INSERT INTO policy_simulations (customer_id, policy_type, coverage_amount, monthly_premium, simulated_at) VALUES
  ('423e4567-e89b-12d3-a456-426614174003', 'BRONZE', 50000.00, 150.00, NOW() - INTERVAL '5 days'),
  ('423e4567-e89b-12d3-a456-426614174003', 'SILVER', 100000.00, 300.00, NOW() - INTERVAL '5 days'),
  ('423e4567-e89b-12d3-a456-426614174003', 'GOLD', 200000.00, 500.00, NOW() - INTERVAL '4 days'),
  ('623e4567-e89b-12d3-a456-426614174005', 'SILVER', 100000.00, 300.00, NOW() - INTERVAL '2 days'),
  ('723e4567-e89b-12d3-a456-426614174006', 'GOLD', 200000.00, 500.00, NOW() - INTERVAL '1 day'),
  ('823e4567-e89b-12d3-a456-426614174007', 'BRONZE', 50000.00, 150.00, NOW() - INTERVAL '3 hours')
ON CONFLICT DO NOTHING;