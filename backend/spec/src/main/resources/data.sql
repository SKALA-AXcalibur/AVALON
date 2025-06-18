-- 우선순위 코드 데이터
INSERT INTO priority (`key`, name) VALUES (1, 'HIGH');
INSERT INTO priority (`key`, name) VALUES (2, 'MEDIUM');
INSERT INTO priority (`key`, name) VALUES (3, 'LOW');

-- 파일 타입 코드 데이터
INSERT INTO file_type (`key`, name) VALUES (1, 'REQUIREMENT');
INSERT INTO file_type (`key`, name) VALUES (2, 'INTERFACE_DEFINITION');
INSERT INTO file_type (`key`, name) VALUES (3, 'INTERFACE_DESIGN');
INSERT INTO file_type (`key`, name) VALUES (4, 'ERD');

-- 카테고리 코드 데이터
INSERT INTO category (`key`, name) VALUES (1, 'PATH_QUERY');
INSERT INTO category (`key`, name) VALUES (2, 'REQUEST');
INSERT INTO category (`key`, name) VALUES (3, 'RESPONSE');

-- 컨텍스트 코드 데이터
INSERT INTO context (`key`, name) VALUES (1, 'BODY');
INSERT INTO context (`key`, name) VALUES (2, 'HEADER');
INSERT INTO context (`key`, name) VALUES (3, 'QUERY');
INSERT INTO context (`key`, name) VALUES (4, 'PATH'); 

-- 모든 관련 테이블 문자셋 변경
ALTER TABLE priority CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE request_major CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE request_middle CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE request_minor CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE category CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE context CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE request CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE api_list CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE parameter CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE db_design CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE db_column CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;