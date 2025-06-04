ALTER TABLE consumption
  ADD COLUMN credit_debit VARCHAR(10),
  ADD COLUMN transaction_id VARCHAR(50),
  ADD COLUMN payment_id VARCHAR(70),
  ADD COLUMN org_type VARCHAR(10);
