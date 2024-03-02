package com.soundseeker.api.persistence.repository;

import com.soundseeker.api.persistence.entity.CaracteristicaEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaracteristicaRepository extends ListCrudRepository<CaracteristicaEntity, Long> {
    @Query("SELECT c FROM CaracteristicaEntity c WHERE c.productos IS EMPTY")
    List<CaracteristicaEntity> findCaracteristicasSinProductos();
}

