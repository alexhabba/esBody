package com.logicaScoolBot.repository;

import com.logicaScoolBot.entity.Qr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QrRepository extends JpaRepository<Qr, UUID> {

    @Modifying
    @Query(value = "update Qr as q set q.status = 'Accepted' where q.qrcId in (:qrsIdList)")
    void updateStatuses(List<String> qrsIdList);

    @Query(value = "select qr from Qr qr where qr.qrcId in (:qrsIdList)")
    List<Qr> findAllByQRId(List<String> qrsIdList);
}
