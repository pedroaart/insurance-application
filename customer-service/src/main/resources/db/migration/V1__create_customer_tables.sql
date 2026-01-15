-- Customers table
CREATE TABLE IF NOT EXISTS customers (
    id UUID PRIMARY KEY,
    cpf VARCHAR(11) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    birth_date DATE NOT NULL,
    phone VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    version INTEGER DEFAULT 0,
    CONSTRAINT chk_cpf_length CHECK (LENGTH(cpf) = 11)
);

-- Addresses table
CREATE TABLE IF NOT EXISTS addresses (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    street VARCHAR(255) NOT NULL,
    number VARCHAR(20) NOT NULL,
    complement VARCHAR(255),
    neighborhood VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(2) NOT NULL,
    zip_code VARCHAR(8) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    CONSTRAINT chk_zip_code_length CHECK (LENGTH(zip_code) = 8),
    CONSTRAINT chk_state_length CHECK (LENGTH(state) = 2)
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_customers_cpf ON customers(cpf);
CREATE INDEX IF NOT EXISTS idx_addresses_customer_id ON addresses(customer_id);

-- Function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Triggers
CREATE TRIGGER update_customers_updated_at 
    BEFORE UPDATE ON customers
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_addresses_updated_at 
    BEFORE UPDATE ON addresses
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();
