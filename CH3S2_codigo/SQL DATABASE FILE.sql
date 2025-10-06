USE scholar;

DROP TABLE IF EXISTS articles;

CREATE TABLE articles (
  id INT AUTO_INCREMENT PRIMARY KEY,
  author_query     VARCHAR(255) NOT NULL,
  article_id       VARCHAR(128) NULL,
  title            VARCHAR(512) NOT NULL,
  authors          VARCHAR(1024),
  publication_date VARCHAR(32),
  abstract         TEXT,
  link             VARCHAR(1024),
  keywords         VARCHAR(512),
  cited_by         INT,
  created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_author_title (author_query, title(255)),
  UNIQUE KEY uk_article_id (article_id)
);