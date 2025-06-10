CREATE TABLE candle (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    create_date TIMESTAMP NOT NULL,
    symbol VARCHAR(50) NOT NULL,
    vol_buy DOUBLE PRECISION,
    vol_sell DOUBLE PRECISION,
    vol DOUBLE PRECISION NOT NULL,
    open DOUBLE PRECISION NOT NULL,
    close DOUBLE PRECISION NOT NULL,
    low DOUBLE PRECISION NOT NULL,
    high DOUBLE PRECISION NOT NULL,
    interval INTEGER NOT NULL
);

-- Создание индексов для улучшения производительности запросов
CREATE INDEX idx_candle_symbol ON candle(symbol);
CREATE INDEX idx_candle_create_date ON candle(create_date);
CREATE INDEX idx_candle_symbol_create_date ON candle(symbol, create_date);

-- Комментарии к таблице и колонкам
COMMENT ON TABLE candle IS 'Таблица для хранения свечных данных';
COMMENT ON COLUMN candle.id IS 'Уникальный идентификатор записи (UUID)';
COMMENT ON COLUMN candle.create_date IS 'Дата и время создания записи';
COMMENT ON COLUMN candle.symbol IS 'Торговый символ/пара';
COMMENT ON COLUMN candle.vol_buy IS 'Объем покупок';
COMMENT ON COLUMN candle.vol_sell IS 'Объем продаж';
COMMENT ON COLUMN candle.vol IS 'Суммарный объем';
COMMENT ON COLUMN candle.open IS 'Цена открытия';
COMMENT ON COLUMN candle.close IS 'Цена закрытия';
COMMENT ON COLUMN candle.low IS 'Минимальная цена';
COMMENT ON COLUMN candle.high IS 'Максимальная цена';
COMMENT ON COLUMN candle.interval IS 'Интервал свечи в минутах';