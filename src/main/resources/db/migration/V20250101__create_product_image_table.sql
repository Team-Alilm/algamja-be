/* =========================================================
   PRODUCT_IMAGE
   ========================================================= */
create table if not exists `product_image` (
    id                   bigint auto_increment primary key,
    is_delete            boolean not null default false,
    created_date         bigint not null,
    last_modified_date   bigint not null,
    
    image_url            varchar(512) not null,
    product_id           bigint not null,
    
    constraint `ux_product_image_url` unique (`image_url`)
);

-- 조회/정렬 최적화 인덱스
create index `idx_product_image_product_id` on `product_image` (`product_id`);
create index `idx_product_image_product_id_id` on `product_image` (`product_id`, `id`);