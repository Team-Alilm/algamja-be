-- 기존 unique index 제거
ALTER TABLE product_image DROP INDEX ux_product_image_url;

-- productId + imageUrl 복합 unique index 추가
-- 같은 상품에는 동일한 이미지가 한 번만, 다른 상품은 같은 이미지 사용 가능
ALTER TABLE product_image ADD UNIQUE INDEX ux_product_image_product_url (product_id, image_url);

-- 이미지 URL 조회 최적화를 위한 일반 인덱스 추가
ALTER TABLE product_image ADD INDEX idx_product_image_url (image_url);