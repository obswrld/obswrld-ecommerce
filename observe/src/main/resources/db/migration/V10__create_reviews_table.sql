CREATE TABLE reviews (
    id UUID PRIMARY KEY,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT ,
    user_id UUID NOT NULL,
    product_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT uq_user_product_review UNIQUE (user_id, product_id)
);