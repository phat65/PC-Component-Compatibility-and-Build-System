UPDATE external_product_source
SET provider_code = 'algolia'
WHERE LOWER(provider_code) = 'corsair';
