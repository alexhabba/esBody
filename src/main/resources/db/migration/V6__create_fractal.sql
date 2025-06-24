CREATE TABLE fractal
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    create_date TIMESTAMP,
    symbol      VARCHAR(10) NULL,
    high        VARCHAR(20) NULL,
    low         VARCHAR(20) NULL,
    "interval"  VARCHAR(20)         NULL,
    count_candle   INT         NULL
);

CREATE UNIQUE INDEX idx_unique_candle ON candle(symbol, create_date, "interval");