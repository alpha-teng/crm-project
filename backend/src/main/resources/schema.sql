-- CRM Database Schema for PostgreSQL with pgvector

-- Enable pgvector extension
CREATE EXTENSION IF NOT EXISTS vector;

-- Customers table
CREATE TABLE IF NOT EXISTS customers (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    company VARCHAR(200),
    phone VARCHAR(20),
    email VARCHAR(100),
    address VARCHAR(500),
    longitude DOUBLE PRECISION,
    latitude DOUBLE PRECISION,
    status VARCHAR(20),
    source VARCHAR(50),
    remark VARCHAR(1000),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Leads table
CREATE TABLE IF NOT EXISTS leads (
    id SERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    source VARCHAR(50),
    status VARCHAR(20),
    score INTEGER,
    remark VARCHAR(1000),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Follow-ups table
CREATE TABLE IF NOT EXISTS follow_ups (
    id SERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    content VARCHAR(2000) NOT NULL,
    type VARCHAR(20),
    next_follow_up_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Opportunities table
CREATE TABLE IF NOT EXISTS opportunities (
    id SERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    amount DECIMAL(15, 2),
    stage VARCHAR(20),
    probability INTEGER,
    expected_close_date DATE,
    remark VARCHAR(1000),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Deals table
CREATE TABLE IF NOT EXISTS deals (
    id SERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    opportunity_id BIGINT,
    title VARCHAR(200) NOT NULL,
    amount DECIMAL(15, 2),
    contract_date DATE,
    status VARCHAR(20),
    remark VARCHAR(1000),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Knowledge docs table with vector support
CREATE TABLE IF NOT EXISTS knowledge_docs (
    id SERIAL PRIMARY KEY,
    content VARCHAR(5000) NOT NULL,
    customer_id BIGINT,
    embedding vector(1024),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index for vector similarity search
CREATE INDEX IF NOT EXISTS idx_knowledge_docs_embedding 
ON knowledge_docs 
USING ivfflat (embedding vector_cosine_ops) 
WITH (lists = 100);

-- Trigger to update updated_at column
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

DROP TRIGGER IF EXISTS update_customers_updated_at ON customers;
CREATE TRIGGER update_customers_updated_at
    BEFORE UPDATE ON customers
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();
