package com.sk.skala.axcalibur.apitest.feature.repository;

import com.sk.skala.axcalibur.apitest.feature.entity.ScenarioEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ScenarioRepositoryTest {

    @Autowired
    ScenarioRepository scenarioRepository;

    private ScenarioEntity createScenario(String scenarioId, String name, Integer projectKey) {
        return ScenarioEntity.builder()
                .scenarioId(scenarioId)
                .name(name)
                .projectKey(projectKey)
                .build();
    }

    @Test
    @DisplayName("findByScenarioId: 시나리오 ID로 조회")
    void findByScenarioId() {
        ScenarioEntity entity = scenarioRepository.save(createScenario("SCN-1", "테스트1", 1));
        Optional<ScenarioEntity> found = scenarioRepository.findByScenarioId("SCN-1");
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("테스트1");
    }

    @Test
    @DisplayName("findByScenarioIdIn: 여러 시나리오 ID로 조회")
    void findByScenarioIdIn() {
        scenarioRepository.save(createScenario("SCN-2", "테스트2", 1));
        scenarioRepository.save(createScenario("SCN-3", "테스트3", 1));
        List<ScenarioEntity> found = scenarioRepository.findByScenarioIdIn(Arrays.asList("SCN-2", "SCN-3"));
        assertThat(found).hasSize(2);
    }

    @Test
    @DisplayName("findByProjectKeyAndScenarioIdIn: 프로젝트와 여러 시나리오 ID로 조회")
    void findByProjectKeyAndScenarioIdIn() {
        scenarioRepository.save(createScenario("SCN-4", "테스트4", 2));
        scenarioRepository.save(createScenario("SCN-5", "테스트5", 2));
        scenarioRepository.save(createScenario("SCN-6", "테스트6", 3));
        List<ScenarioEntity> found = scenarioRepository.findByProjectKeyAndScenarioIdIn(2, Arrays.asList("SCN-4", "SCN-5", "SCN-6"));
        assertThat(found).hasSize(2);
        assertThat(found).extracting("scenarioId").containsExactlyInAnyOrder("SCN-4", "SCN-5");
    }

    @Test
    @DisplayName("findAllByProjectKey: 프로젝트로 전체 조회")
    void findAllByProjectKey() {
        scenarioRepository.save(createScenario("SCN-7", "테스트7", 4));
        scenarioRepository.save(createScenario("SCN-8", "테스트8", 4));
        scenarioRepository.save(createScenario("SCN-9", "테스트9", 5));
        List<ScenarioEntity> found = scenarioRepository.findAllByProjectKey(4);
        assertThat(found).hasSize(2);
        assertThat(found).extracting("scenarioId").containsExactlyInAnyOrder("SCN-7", "SCN-8");
    }

    @Test
    @DisplayName("findAllByProjectKeyAndScenarioIdGreaterThanOrderByScenarioIdAsc: 커서 기반 조회")
    void findAllByProjectKeyAndScenarioIdGreaterThanOrderByScenarioIdAsc() {
        scenarioRepository.save(createScenario("SCN-10", "테스트10", 6));
        scenarioRepository.save(createScenario("SCN-11", "테스트11", 6));
        scenarioRepository.save(createScenario("SCN-12", "테스트12", 6));
        scenarioRepository.save(createScenario("SCN-9", "테스트13", 6));
        Pageable pageable = PageRequest.of(0, 5, Sort.by("id").ascending());
        List<ScenarioEntity> found = scenarioRepository.findAllByProjectKeyAndScenarioIdGreaterThanOrderByIdAsc(6, "", pageable);
        System.out.println("Found");
        System.out.println(found);
        assertThat(found).hasSize(4);

    }
}