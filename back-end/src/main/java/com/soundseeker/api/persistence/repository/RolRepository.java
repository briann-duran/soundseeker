package com.soundseeker.api.persistence.repository;

import com.soundseeker.api.persistence.entity.RolEntity;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolRepository extends ListCrudRepository<RolEntity, Long> {
    RolEntity findByRolIgnoreCase(String rol);
}
