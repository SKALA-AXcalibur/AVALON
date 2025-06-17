package com.sk.skala.axcalibur.apitest.feature.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.sk.skala.axcalibur.apitest.feature.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TestcaseRepositoryTest {
  @Autowired
  TestcaseRepository testcaseRepository;
  @Autowired
  ScenarioRepository scenarioRepository;
  @Autowired
  MappingRepository mappingRepository;
  @Autowired
  ApiListRepository apiListRepository;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("findByMapping_Scenario_ScenarioId: 시나리오 ID로 테스트케이스를 정상 조회한다")
  void findByMapping_Scenario_ScenarioId_success() {
    // given
    ScenarioEntity scenario = scenarioRepository.save(
        ScenarioEntity.builder()
            .scenarioId("SCENARIO-1")
            .name("시나리오1")
            .projectKey(1)
            .build()
    );
    ApiListEntity apiList = apiListRepository.save(
        ApiListEntity.builder()
            .url("/api/test")
            .path("/test")
            .method("GET")
            .build()
    );
    MappingEntity mapping = mappingRepository.save(
        MappingEntity.builder()
            .mappingId("MAPPING-1")
            .step(1)
            .scenario(scenario)
            .apiList(apiList)
            .build()
    );
    TestcaseEntity tc1 = testcaseRepository.save(
        TestcaseEntity.builder()
            .description("desc1")
            .precondition("pre1")
            .expected("exp1")
            .mapping(mapping)
            .build()
    );
    TestcaseEntity tc2 = testcaseRepository.save(
        TestcaseEntity.builder()
            .description("desc2")
            .precondition("pre2")
            .expected("exp2")
            .mapping(mapping)
            .build()
    );

    // when
    List<TestcaseEntity> result = testcaseRepository.findByMapping_Scenario_ScenarioId("SCENARIO-1");

    // then
    assertNotNull(result);
    assertEquals(2, result.size());
    assertTrue(result.stream().anyMatch(tc -> tc.getDescription().equals("desc1")));
    assertTrue(result.stream().anyMatch(tc -> tc.getDescription().equals("desc2")));
    assertTrue(result.stream().allMatch(tc -> tc.getMapping().getScenario().getScenarioId().equals("SCENARIO-1")));
  }

  @Test
  @DisplayName("findByMapping_Scenario_ScenarioId: 존재하지 않는 시나리오 ID로 조회하면 빈 리스트를 반환한다")
  void findByMapping_Scenario_ScenarioId_notFound() {
    // when
    List<TestcaseEntity> result = testcaseRepository.findByMapping_Scenario_ScenarioId("NOT_EXIST");
    // then
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
}