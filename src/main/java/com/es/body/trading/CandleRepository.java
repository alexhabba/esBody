package com.es.body.trading;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CandleRepository extends JpaRepository<Candle, UUID> {

    boolean existsBySymbolAndCreateDate(String symbol, LocalDateTime createDate);

//    @Query(value = "WITH latest_dates AS (\n" +
//            "    SELECT symbol, MAX(create_date) AS max_date\n" +
//            "    FROM bot.bar\n" +
//            "    WHERE symbol IN :symbols\n" +
//            "    GROUP BY symbol\n" +
//            ")\n" +
//            "SELECT b.*\n" +
//            "FROM bot.bar b\n" +
//            "JOIN latest_dates ld ON b.symbol = ld.symbol AND b.create_date = ld.max_date\n" +
//            "ORDER BY b.symbol;", nativeQuery = true)
//    List<Bar> findLastBarBySymbol(List<String> symbols);
//
//    @Query(value = "SELECT *\n" +
//            "FROM bar where symbol = :symbol " +
//            "ORDER BY create_date DESC\n" +
//            "LIMIT 1", nativeQuery = true)
//    Optional<Bar> findLastBarBySymbol(String symbol);
//
//    @Query(value = "SELECT *\n" +
//            "FROM bar where symbol = :symbol " +
//            "ORDER BY create_date DESC\n" +
//            "LIMIT :count", nativeQuery = true)
//    List<Bar> findLastBarBySymbol(String symbol, int count);
//
//    List<Bar> findAllBySymbol(Symbol symbol);
//
//    @Query(value = "select sum(CAST(b.close AS numeric)) / 113 from bot.bar b " +
//            "where b.symbol = :symbol and b.create_date >= :createDateStart  and b.create_date <= :createDateEnd", nativeQuery = true)
//    double getAvg(String symbol, LocalDateTime createDateStart, LocalDateTime createDateEnd);
}
