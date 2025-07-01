package com.sk.skala.axcalibur.apitest.feature.repository;

import com.sk.skala.axcalibur.apitest.feature.entity.ParameterEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParameterRepository extends JpaRepository<ParameterEntity, Integer>, ParameterRepositoryCustom {

    List<ParameterEntity> findByApiList_Id(Integer apiListId);

    List<ParameterEntity> findByApiList_IdIn(List<Integer> apiListIds);
}
