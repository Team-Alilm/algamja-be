-- Insert sample banner data
INSERT INTO banner (
    title,
    image_url,
    click_url,
    priority,
    start_date,
    end_date,
    is_active,
    is_delete,
    created_date,
    last_modified_date
) VALUES (
    '메인 배너',
    'https://file.notion.so/f/f/c345e317-1a77-4e86-8b67-b491a5db92b8/116fd972-a8c0-4df7-b55f-4c022112f372/main_banner.png?table=block&id=2717b278-57a0-803a-acfc-c4be64015de6&spaceId=c345e317-1a77-4e86-8b67-b491a5db92b8&expirationTimestamp=1758132000000&signature=D83iG32CiM7Aijck2-mXoDnY7nDMaKTs_F9FJipPW0k&downloadName=main_banner.png',
    NULL,
    100,
    1758108766000,  -- 현재 시간 (밀리초)
    1789644766000,  -- 1년 후 (밀리초)
    TRUE,
    FALSE,
    1758108766000,
    1758108766000
);