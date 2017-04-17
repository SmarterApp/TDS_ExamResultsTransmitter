/***********************************************************************************************************************
  File: V1__exam_audit_create_table_exam_report.sql

  Desc: Creates the exam_report table, which stores the TRT for an exam for auditing purposes.

***********************************************************************************************************************/

USE exam_audit;

DROP TABLE IF EXISTS exam_report;

CREATE TABLE exam_report (
  id BIGINT(20) NOT NULL AUTO_INCREMENT,
  exam_id CHAR(36) NOT NULL,
  report TEXT NOT NULL,
  created_at TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3) NOT NULL,
  KEY `ix_exam_report_id` (id)
);