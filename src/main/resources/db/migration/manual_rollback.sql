-- V8__create_table_dic_block.sql
DROP TABLE dic_block;
DELETE FROM flyway_schema_history WHERE script = 'V8__create_table_dic_block.sql';
-- V10__add_external_number.sql
ALTER TABLE documents DROP COLUMN external_number;
DELETE FROM flyway_schema_history WHERE script = 'V10__add_external_number.sql';

