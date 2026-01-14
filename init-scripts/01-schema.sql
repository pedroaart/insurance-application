CREATE TABLE IF NOT EXISTS customers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cpf VARCHAR(11) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    birth_date DATE NOT NULL,
    phone VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version INTEGER DEFAULT 0,
    CONSTRAINT chk_cpf_length CHECK (LENGTH(cpf) = 11)
    );

CREATE TABLE IF NOT EXISTS addresses (
                                         id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id UUID NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    street VARCHAR(255) NOT NULL,
    number VARCHAR(20) NOT NULL,
    complement VARCHAR(255),
    neighborhood VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(2) NOT NULL,
    zip_code VARCHAR(8) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_zip_code_length CHECK (LENGTH(zip_code) = 8),
    CONSTRAINT chk_state_length CHECK (LENGTH(state) = 2)
    );

-- Schema for Insurance Service
CREATE TABLE IF NOT EXISTS insurance_policies (
                                                  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id UUID NOT NULL,
    policy_number VARCHAR(50) UNIQUE NOT NULL,
    policy_type VARCHAR(20) NOT NULL,
    coverage_amount DECIMAL(15, 2) NOT NULL,
    monthly_premium DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version INTEGER DEFAULT 0,
    idempotency_key VARCHAR(255) UNIQUE,
    CONSTRAINT chk_policy_type CHECK (policy_type IN ('BRONZE', 'SILVER', 'GOLD')),
    CONSTRAINT chk_status CHECK (status IN ('ACTIVE', 'CANCELLED', 'EXPIRED'))
    );

CREATE TABLE IF NOT EXISTS policy_simulations (
                                                  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id UUID NOT NULL,
    policy_type VARCHAR(20) NOT NULL,
    coverage_amount DECIMAL(15, 2) NOT NULL,
    monthly_premium DECIMAL(10, 2) NOT NULL,
    simulated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_sim_policy_type CHECK (policy_type IN ('BRONZE', 'SILVER', 'GOLD'))
    );

-- Outbox pattern for cache invalidation
CREATE TABLE IF NOT EXISTS outbox_events (
                                             id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    aggregate_id UUID NOT NULL,
    aggregate_type VARCHAR(50) NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    payload JSONB NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP,
    retry_count INTEGER DEFAULT 0,
    status VARCHAR(20) DEFAULT 'PENDING',
    CONSTRAINT chk_outbox_status CHECK (status IN ('PENDING', 'PROCESSED', 'FAILED'))
    );

-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_customers_cpf ON customers(cpf);
CREATE INDEX IF NOT EXISTS idx_addresses_customer_id ON addresses(customer_id);
CREATE INDEX IF NOT EXISTS idx_insurance_policies_customer_id ON insurance_policies(customer_id);
CREATE INDEX IF NOT EXISTS idx_insurance_policies_policy_number ON insurance_policies(policy_number);
CREATE INDEX IF NOT EXISTS idx_insurance_policies_idempotency ON insurance_policies(idempotency_key);
CREATE INDEX IF NOT EXISTS idx_policy_simulations_customer_id ON policy_simulations(customer_id);
CREATE INDEX IF NOT EXISTS idx_outbox_events_status ON outbox_events(status, created_at);

-- Function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Triggers for updated_at
CREATE TRIGGER update_customers_updated_at BEFORE UPDATE ON customers
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_addresses_updated_at BEFORE UPDATE ON addresses
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_insurance_policies_updated_at BEFORE UPDATE ON insurance_policies
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();