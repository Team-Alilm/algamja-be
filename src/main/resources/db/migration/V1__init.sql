/* =========================================================
   MEMBER
   ========================================================= */
create table if not exists `member` (
                                        id                  bigint auto_increment primary key,
                                        is_delete           boolean not null default false,
                                        created_date        bigint not null,
                                        last_modified_date  bigint not null,

                                        provider            varchar(20)  not null,
    provider_id         varchar(64)  not null,
    email               varchar(255) not null,
    nickname            varchar(50)  not null
    );

alter table `member`
    add constraint `uk_member_provider_pid`
        unique (`provider`, `provider_id`);


/* =========================================================
   PRODUCT
   ========================================================= */
create table if not exists `product` (
                                         id                   bigint auto_increment primary key,
                                         is_delete            boolean not null default false,
                                         created_date         bigint not null,
                                         last_modified_date   bigint not null,

                                         store_number         bigint       not null,
                                         name                 varchar(200) not null,
    brand                varchar(120) not null,
    thumbnail_url        varchar(512) not null,
    store                varchar(20)  not null,

    first_category       varchar(80)  not null,
    second_category      varchar(80),

    price                decimal(15,0) not null,

    first_option         varchar(120),
    second_option        varchar(120),
    third_option         varchar(120),

    constraint `chk_price_non_negative` check (price >= 0)
    );

alter table `product`
    add constraint `uk_store_store_number`
        unique (`store`, `store_number`);

alter table `product`
    add constraint `uk_product_options`
        unique (`store`, `store_number`, `first_option`, `second_option`, `third_option`);


/* =========================================================
   NOTIFICATION
   ========================================================= */
create table if not exists `notification` (
                                              id                  bigint auto_increment primary key,
                                              is_delete           boolean not null default false,
                                              created_date        bigint not null,
                                              last_modified_date  bigint not null,

                                              product_id          bigint not null,
                                              member_id           bigint not null,
                                              read_yn             boolean not null default false

    );


/* =========================================================
   FCM_TOKEN
   ========================================================= */
create table if not exists `fcm_token` (
                                           id                  bigint auto_increment primary key,
                                           is_delete           boolean not null default false,
                                           created_date        bigint not null,
                                           last_modified_date  bigint not null,

                                           token               varchar(512) not null,
    member_id           bigint       not null,
    is_active           boolean      not null default true,

    constraint `uk_fcm_token_token` unique (`token`)
    );


/* =========================================================
   BASKET
   ========================================================= */
create table if not exists `basket` (
                                        id                  bigint auto_increment primary key,
                                        is_delete           boolean not null default false,
                                        created_date        bigint not null,
                                        last_modified_date  bigint not null,

                                        member_id           bigint not null,
                                        product_id          bigint not null,

                                        is_notification     boolean not null default false,
                                        notification_date   bigint,

                                        is_hidden           boolean not null default false,

                                        constraint `uk_basket_member_product` unique (`member_id`, `product_id`),
    constraint `ck_basket_notification_date_required`
    check (is_notification = false or notification_date is not null)

    );

create index `idx_basket_member_active`
    on `basket`(`member_id`, `is_delete`);

create index `idx_basket_product_active`
    on `basket`(`product_id`, `is_delete`);


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
    image_order          int not null default 0,

    constraint `ux_product_image_url` unique (`image_url`)
    );

create index `idx_product_image_product_id`
    on `product_image`(`product_id`);

create index `idx_product_image_product_id_order`
    on `product_image`(`product_id`, `image_order`);