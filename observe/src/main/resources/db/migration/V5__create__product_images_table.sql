CREATE TABLE product_images (
    id UUID PRIMARY KEY,
    url VARCHAR(500) NOT NULL,
    alt_text VARCHAR(150),
    is_primary BOOLEAN NOT NULL,
    display_order INTEGER NOT NULL,
    product_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (product_id) REFERENCES products(id)
);