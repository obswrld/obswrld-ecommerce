CREATE TABLE payments (
    id UUID PRIMARY KEY,
    status VARCHAR(255) NOT NULL,
    method VARCHAR(255) NOT NULL,
    order_id UUID UNIQUE NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    transaction_reference VARCHAR(255) UNIQUE NOT NULL,
    gateway_reference VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id)
);