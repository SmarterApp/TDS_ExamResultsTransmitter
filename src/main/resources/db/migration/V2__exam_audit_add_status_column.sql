/***********************************************************************************************************************
  File: V2__exam_audit_add_status_column.sql

  Desc: Add a column to track status

***********************************************************************************************************************/

USE exam_audit;

ALTER TABLE exam_report add column status VARCHAR(100);

UPDATE exam_report
SET status = 'sent previously';

ALTER TABLE exam_report MODIFY COLUMN status VARCHAR(100) NOT NULL;