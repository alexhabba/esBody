package com.es.body.trading.repository;


import com.es.body.trading.entity.Fractal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FractalRepository extends JpaRepository<Fractal, UUID> {

    List<Fractal> findFractalByInterval(String interval);

}
