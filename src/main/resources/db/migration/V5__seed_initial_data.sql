-- V5: Seed de dados iniciais e ajuste de sequências
-- Este script insere categorias, clientes, endereços, produtos e pedidos de exemplo
-- e depois ajusta todas as sequências BIGSERIAL para o último ID inserido.

-- Categorias primeiro
INSERT INTO categories (id, name, description) VALUES (1, 'Eletrônicos', 'Produtos como notebooks, smartphones, TVs e tablets') ON CONFLICT DO NOTHING;
INSERT INTO categories (id, name, description) VALUES (2, 'Eletrodomésticos', 'Geladeiras, fogões, micro-ondas e outros eletrodomésticos') ON CONFLICT DO NOTHING;
INSERT INTO categories (id, name, description) VALUES (3, 'Vestuário', 'Camisetas, calças, calçados e acessórios') ON CONFLICT DO NOTHING;
INSERT INTO categories (id, name, description) VALUES (4, 'Livros', 'Romances, livros técnicos e literatura infantil') ON CONFLICT DO NOTHING;
INSERT INTO categories (id, name, description) VALUES (5, 'Móveis', 'Sofás, camas, mesas e cadeiras') ON CONFLICT DO NOTHING;

-- Clientes
INSERT INTO customers (id, name, email) VALUES (1, 'João da Silva', 'joao@email.com') ON CONFLICT DO NOTHING;
INSERT INTO customers (id, name, email) VALUES (2, 'Maria Oliveira', 'maria@email.com') ON CONFLICT DO NOTHING;

-- Endereços (associa depois ao cliente)
INSERT INTO addresses (id, street, neighborhood, city, state, zip_code, country) VALUES (1, 'Rua das Flores', 'Centro', 'São Paulo', 'SP', '01001-000', 'Brasil') ON CONFLICT (id) DO NOTHING;
INSERT INTO addresses (id, street, neighborhood, city, state, zip_code, country) VALUES (2, 'Avenida Brasil', 'Jardins', 'Rio de Janeiro', 'RJ', '22041-001', 'Brasil') ON CONFLICT (id) DO NOTHING;
UPDATE addresses SET customer_id = 1 WHERE id = 1;
UPDATE addresses SET customer_id = 2 WHERE id = 2;

-- Produtos (inserindo price e unit_price para compatibilidade com V2)
INSERT INTO products (id, name, description, price, unit_price, stock, image_url, category_id)
VALUES (1, 'Notebook Dell', 'Notebook i7 16GB RAM SSD 512GB', 4500.00, 4500.00, 10, 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8', 1) ON CONFLICT (id) DO NOTHING;

INSERT INTO products (id, name, description, price, unit_price, stock, image_url, category_id)
VALUES (2, 'Smartphone Samsung', 'Galaxy S23 Ultra 256GB', 5200.00, 5200.00, 15, 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9', 1) ON CONFLICT (id) DO NOTHING;

INSERT INTO products (id, name, description, price, unit_price, stock, image_url, category_id)
VALUES (3, 'Fone JBL', 'Fone Bluetooth com cancelamento de ruído', 600.00, 600.00, 25, 'https://images.unsplash.com/photo-1519671482749-fd09be7ccebf', 1) ON CONFLICT (id) DO NOTHING;

-- Pedido (Order) e itens
INSERT INTO orders (id, customer_id, status, created_at, total) VALUES (1, 1, 'PAID', '2025-09-25 10:00:00', 4500.00) ON CONFLICT (id) DO NOTHING;
INSERT INTO order_items (id, order_id, product_id, quantity, unit_price) VALUES (1, 1, 1, 1, 4500.00) ON CONFLICT (id) DO NOTHING;

INSERT INTO orders (id, customer_id, status, created_at, total) VALUES (2, 1, 'PENDING', '2025-09-25 11:00:00', 5200.00) ON CONFLICT (id) DO NOTHING;
INSERT INTO order_items (id, order_id, product_id, quantity, unit_price) VALUES (2, 2, 2, 1, 5200.00) ON CONFLICT (id) DO NOTHING;

-- Carrinho (Cart) não é criado aqui para evitar conflito ao testar via API

-- Ajuste de sequências após inserts
SELECT setval('categories_id_seq', COALESCE((SELECT MAX(id) FROM categories), 1), true);
SELECT setval('customers_id_seq', COALESCE((SELECT MAX(id) FROM customers), 1), true);
SELECT setval('addresses_id_seq', COALESCE((SELECT MAX(id) FROM addresses), 1), true);
SELECT setval('products_id_seq', COALESCE((SELECT MAX(id) FROM products), 1), true);
SELECT setval('orders_id_seq', COALESCE((SELECT MAX(id) FROM orders), 1), true);
SELECT setval('order_items_id_seq', COALESCE((SELECT MAX(id) FROM order_items), 1), true);
SELECT setval('carts_id_seq', COALESCE((SELECT MAX(id) FROM carts), 1), true);
SELECT setval('cart_items_id_seq', COALESCE((SELECT MAX(id) FROM cart_items), 1), true);