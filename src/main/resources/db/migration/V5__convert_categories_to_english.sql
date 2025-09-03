-- Convert Korean categories to English keys

-- Update first_category
UPDATE product SET first_category = 'SPORTS_LEISURE' WHERE first_category IN ('스포츠/레저', '스포츠', '레저', '운동', '아웃도어');
UPDATE product SET first_category = 'TOPS' WHERE first_category IN ('상의', '티셔츠', '셔츠', '블라우스', '탑', '후드티', '맨투맨', '니트', '스웨터');
UPDATE product SET first_category = 'SHOES' WHERE first_category IN ('신발', '운동화', '구두', '샌들', '부츠', '슬리퍼', '스니커즈');
UPDATE product SET first_category = 'OUTERWEAR' WHERE first_category IN ('아우터', '자켓', '코트', '점퍼', '패딩', '가디건', '조끼', '바람막이');
UPDATE product SET first_category = 'BAGS' WHERE first_category IN ('가방', '백팩', '토트백', '크로스백', '클러치', '지갑', '숄더백');
UPDATE product SET first_category = 'PANTS' WHERE first_category IN ('바지', '청바지', '팬츠', '레깅스', '조거팬츠', '슬랙스', '반바지', '데님');
UPDATE product SET first_category = 'UNDERWEAR_HOMEWEAR' WHERE first_category IN ('속옷/홈웨어', '속옷', '홈웨어', '잠옷', '언더웨어', '이너웨어');
UPDATE product SET first_category = 'DRESSES_SKIRTS' WHERE first_category IN ('원피스/스커트', '원피스', '스커트', '드레스', '치마');
UPDATE product SET first_category = 'FASHION_ACCESSORIES' WHERE first_category IN ('패션소품', '액세서리', '모자', '벨트', '스카프', '주얼리', '시계', '장갑', '머플러');
UPDATE product SET first_category = 'BEAUTY' WHERE first_category IN ('뷰티', '화장품', '스킨케어', '메이크업', '향수');
UPDATE product SET first_category = 'DIGITAL' WHERE first_category IN ('디지털/라이프', '디지털', '라이프', '전자제품', '가전', '테크');

-- Update second_category (nullable)
UPDATE product SET second_category = 'SPORTS_LEISURE' WHERE second_category IN ('스포츠/레저', '스포츠', '레저', '운동', '아웃도어');
UPDATE product SET second_category = 'TOPS' WHERE second_category IN ('상의', '티셔츠', '셔츠', '블라우스', '탑', '후드티', '맨투맨', '니트', '스웨터');
UPDATE product SET second_category = 'SHOES' WHERE second_category IN ('신발', '운동화', '구두', '샌들', '부츠', '슬리퍼', '스니커즈');
UPDATE product SET second_category = 'OUTERWEAR' WHERE second_category IN ('아우터', '자켓', '코트', '점퍼', '패딩', '가디건', '조끼', '바람막이');
UPDATE product SET second_category = 'BAGS' WHERE second_category IN ('가방', '백팩', '토트백', '크로스백', '클러치', '지갑', '숄더백');
UPDATE product SET second_category = 'PANTS' WHERE second_category IN ('바지', '청바지', '팬츠', '레깅스', '조거팬츠', '슬랙스', '반바지', '데님');
UPDATE product SET second_category = 'UNDERWEAR_HOMEWEAR' WHERE second_category IN ('속옷/홈웨어', '속옷', '홈웨어', '잠옷', '언더웨어', '이너웨어');
UPDATE product SET second_category = 'DRESSES_SKIRTS' WHERE second_category IN ('원피스/스커트', '원피스', '스커트', '드레스', '치마');
UPDATE product SET second_category = 'FASHION_ACCESSORIES' WHERE second_category IN ('패션소품', '액세서리', '모자', '벨트', '스카프', '주얼리', '시계', '장갑', '머플러');
UPDATE product SET second_category = 'BEAUTY' WHERE second_category IN ('뷰티', '화장품', '스킨케어', '메이크업', '향수');
UPDATE product SET second_category = 'DIGITAL' WHERE second_category IN ('디지털/라이프', '디지털', '라이프', '전자제품', '가전', '테크');

-- Set remaining categories to 'OTHERS'
UPDATE product SET first_category = 'OTHERS' WHERE first_category NOT IN ('SPORTS_LEISURE', 'TOPS', 'SHOES', 'OUTERWEAR', 'BAGS', 'PANTS', 'UNDERWEAR_HOMEWEAR', 'DRESSES_SKIRTS', 'FASHION_ACCESSORIES', 'BEAUTY', 'DIGITAL');