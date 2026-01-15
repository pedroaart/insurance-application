
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TABLE IF NOT EXISTS insurance_policies (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    policy_number VARCHAR(50) UNIQUE NOT NULL,
    policy_type VARCHAR(20) NOT NULL,
    coverage_amount DECIMAL(15, 2) NOT NULL,
    monthly_premium DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    idempotency_key VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    version INTEGER DEFAULT 0,
    CONSTRAINT chk_policy_type CHECK (policy_type IN ('BRONZE', 'SILVER', 'GOLD')),
    CONSTRAINT chk_status CHECK (status IN ('ACTIVE', 'CANCELLED', 'EXPIRED', 'PENDING'))
);

CREATE TABLE IF NOT EXISTS policy_simulations (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    policy_type VARCHAR(20) NOT NULL,
    coverage_amount DECIMAL(15, 2) NOT NULL,
    monthly_premium DECIMAL(10, 2) NOT NULL,
    simulated_at TIMESTAMP NOT NULL,
    CONSTRAINT chk_sim_policy_type CHECK (policy_type IN ('BRONZE', 'SILVER', 'GOLD'))
);

CREATE INDEX IF NOT EXISTS idx_insurance_policies_customer_id ON insurance_policies(customer_id);
CREATE INDEX IF NOT EXISTS idx_insurance_policies_policy_number ON insurance_policies(policy_number);
CREATE INDEX IF NOT EXISTS idx_insurance_policies_idempotency ON insurance_policies(idempotency_key);
CREATE INDEX IF NOT EXISTS idx_policy_simulations_customer_id ON policy_simulations(customer_id);

CREATE TRIGGER update_insurance_policies_updated_at
    BEFORE UPDATE ON insurance_policies
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();
