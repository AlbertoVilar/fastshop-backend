-- Categorias primeiro
INSERT INTO categories (id, name, description) VALUES (1, 'Eletrônicos', 'Produtos como notebooks, smartphones, TVs e tablets');
INSERT INTO categories (id, name, description) VALUES (2, 'Eletrodomésticos', 'Geladeiras, fogões, micro-ondas e outros eletrodomésticos');
INSERT INTO categories (id, name, description) VALUES (3, 'Vestuário', 'Camisetas, calças, calçados e acessórios');
INSERT INTO categories (id, name, description) VALUES (4, 'Livros', 'Romances, livros técnicos e literatura infantil');
INSERT INTO categories (id, name, description) VALUES (5, 'Móveis', 'Sofás, camas, mesas e cadeiras');

-- Endereços
INSERT INTO addresses (street, neighborhood, city, state, zip_code, country) VALUES ('Rua das Flores', 'Centro', 'São Paulo', 'SP', '01001-000', 'Brasil');
INSERT INTO addresses (street, neighborhood, city, state, zip_code, country) VALUES ('Avenida Brasil', 'Jardins', 'Rio de Janeiro', 'RJ', '22041-001', 'Brasil');

-- Produtos depois
INSERT INTO products (name, description, price, stock, image_url, category_id) VALUES ('Notebook Dell', 'Notebook i7 16GB RAM SSD 512GB', 4500.00, 10, 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8', 1);
INSERT INTO products (name, description, price, stock, image_url, category_id) VALUES ('Smartphone Samsung', 'Galaxy S23 Ultra 256GB', 5200.00, 15, 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9', 1);
INSERT INTO products (name, description, price, stock, image_url, category_id) VALUES ('TV LG 55"', 'Smart TV 4K UHD com AI ThinQ', 3200.00, 8, 'https://images.unsplash.com/photo-1587825140708-dfaf72ae4b04', 1);
INSERT INTO products (name, description, price, stock, image_url, category_id) VALUES ('Fone JBL', 'Fone Bluetooth com cancelamento de ruído', 600.00, 25, 'https://images.unsplash.com/photo-1519671482749-fd09be7ccebf', 1);

-- Clientes (sem address_id)
INSERT INTO customers (name, email) VALUES ('João da Silva', 'joao@email.com');
INSERT INTO customers (name, email) VALUES ('Maria Oliveira', 'maria@email.com');

-- Atualize os endereços para associar ao cliente correto
UPDATE addresses SET customer_id = 1 WHERE id = 1;
UPDATE addresses SET customer_id = 2 WHERE id = 2;

-- Pedido (Order)
INSERT INTO orders (customer_id, status, created_at, total) VALUES (1, 'PAID', '2025-09-25T10:00:00', 4500.00);
-- Item do pedido (OrderItem)
INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (1, 1, 1, 4500.00);
INSERT INTO orders (customer_id, status, created_at, total) VALUES (1, 'PENDING', '2025-09-25T11:00:00', 5200.00);
INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (2, 2, 1, 5200.00);

-- Carrinho (Cart) para testes de CartItem
-- (Removido para evitar conflito de chave única ao criar pelo Postman)
