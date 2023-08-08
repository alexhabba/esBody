package com.logicaScoolBot.repository;

import com.logicaScoolBot.entity.Qr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface QrRepository extends JpaRepository<Qr, UUID> {

    @Modifying
    @Query("update Qr as q set q.status = 'Accepted', q.updateDate = :dateTime where q.qrcId in (:qrsIdList)")
    void updateStatuses(@Param("qrsIdList")List<String> qrsIdList, @Param("dateTime") LocalDateTime dateTime);

    @Query("select qr from Qr qr where qr.qrcId in (:qrsIdList)")
    List<Qr> findAllByQrId(List<String> qrsIdList);

    @Modifying
    @Query("delete from Qr q where q.createDate < :dateTime and q.status = 'NotStarted'")
    void deleteQr(@Param("dateTime") LocalDateTime dateTime);

    @Query("select sum(q.amount) from Qr q where q.createDate >= :dateTime and q.status = 'Accepted'")
    Integer getAmountSumToMonth(@Param("dateTime") LocalDateTime dateTime);

    @Query("select sum(q.amount) from Qr q where q.updateDate >= :dateTime and q.status = 'Accepted'")
    Integer getAmountSumToDay(@Param("dateTime") LocalDateTime dateTime);
}
