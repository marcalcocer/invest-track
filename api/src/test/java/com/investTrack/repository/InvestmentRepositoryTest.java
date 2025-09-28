package com.investTrack.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.investTrack.model.Investment;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class InvestmentRepositoryTest {

  @InjectMocks private InvestmentRepository repository;

  private final Investment investment =
      Investment.builder().name("test").description("desc").currency("USD").build();

  @Test
  public void testFindAll_ShouldReturnEmptyList_WhenNoInvestments() {
    List<Investment> result = repository.findAll();

    assertEquals(0, result.size());
  }

  @Test
  public void testFindAll_ShouldReturnAllInvestments_WhenInvestmentsExist() {
    repository.save(investment);

    List<Investment> result = repository.findAll();

    assertEquals(1, result.size());
    assertEquals(investment, result.get(0));
  }

  @Test
  public void testSave_ShouldAssignId_WhenInvestmentHasNoId() {
    assertNull(investment.getId());

    repository.save(investment);

    assertNotNull(investment.getId());
    assertEquals(1L, investment.getId());
  }

  @Test
  public void testSave_ShouldNotAssignId_WhenInvestmentHasId() {
    investment.setId(999L);

    repository.save(investment);

    assertEquals(999L, investment.getId());
  }

  @Test
  public void testSave_ShouldOverwriteExistingInvestment_WhenIdExists() {
    repository.save(investment);
    Long originalId = investment.getId();

    Investment updatedInvestment = Investment.builder().build();
    updatedInvestment.setId(originalId);
    updatedInvestment.setName("Updated Name");
    updatedInvestment.setDescription("Updated Description");
    updatedInvestment.setCurrency("EUR");

    repository.save(updatedInvestment);

    List<Investment> result = repository.findAll();
    assertEquals(1, result.size());
    assertEquals(updatedInvestment, result.get(0));
    assertEquals("Updated Name", result.get(0).getName());
  }

  @Test
  public void testSaveAll_ShouldSaveMultipleInvestments() {
    Investment investment1 = Investment.builder().build();
    investment1.setName("Investment 1");

    Investment investment2 = Investment.builder().build();
    investment2.setName("Investment 2");

    List<Investment> investments = List.of(investment1, investment2);

    repository.saveAll(investments);

    List<Investment> result = repository.findAll();
    assertEquals(2, result.size());
    assertEquals("Investment 1", result.get(0).getName());
    assertEquals("Investment 2", result.get(1).getName());
  }

  @Test
  public void testSaveAll_ShouldAssignIdsToAllInvestments() {
    Investment investment1 = Investment.builder().build();
    Investment investment2 = Investment.builder().build();

    List<Investment> investments = List.of(investment1, investment2);

    repository.saveAll(investments);

    assertNotNull(investment1.getId());
    assertNotNull(investment2.getId());
    assertEquals(1L, investment1.getId());
    assertEquals(2L, investment2.getId());
  }

  @Test
  public void testDelete_ShouldRemoveInvestment_WhenInvestmentExists() {
    repository.save(investment);
    assertEquals(1, repository.findAll().size());

    repository.delete(investment);

    assertEquals(0, repository.findAll().size());
  }

  @Test
  public void testDelete_ShouldDoNothing_WhenInvestmentNotExists() {
    Investment nonExistentInvestment = Investment.builder().build();
    nonExistentInvestment.setId(999L);

    repository.save(investment);
    assertEquals(1, repository.findAll().size());

    repository.delete(nonExistentInvestment);

    assertEquals(1, repository.findAll().size());
  }

  @Test
  public void testIdGenerator_ShouldIncrementIdsCorrectly() {
    Investment investment1 = Investment.builder().build();
    Investment investment2 = Investment.builder().build();
    Investment investment3 = Investment.builder().build();

    repository.save(investment1);
    repository.save(investment2);
    repository.save(investment3);

    assertEquals(1L, investment1.getId());
    assertEquals(2L, investment2.getId());
    assertEquals(3L, investment3.getId());
  }

  @Test
  public void testSave_ShouldHandleConsecutiveSaves() {
    repository.save(investment);
    Long firstId = investment.getId();

    Investment secondInvestment = Investment.builder().build();
    repository.save(secondInvestment);

    assertEquals(firstId + 1, secondInvestment.getId().longValue());
  }
}
