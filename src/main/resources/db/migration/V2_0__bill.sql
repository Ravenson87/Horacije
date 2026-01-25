CREATE TABLE bill (
                      id INT NOT NULL AUTO_INCREMENT,
                      bill_date DATE NOT NULL,
                      article_name VARCHAR(255) NOT NULL,
                      article_price DECIMAL(10, 2) NOT NULL,
                      article_type VARCHAR(255),
                      brand_name VARCHAR(255),
                      PRIMARY KEY (id) USING BTREE,
                      INDEX idx_article_name (article_name),
                      INDEX idx_brand_name (brand_name)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;
