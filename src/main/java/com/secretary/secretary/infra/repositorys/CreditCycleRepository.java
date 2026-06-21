package com.secretary.secretary.infra.repositorys;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.secretary.secretary.domain.model.CreditCycle;
import com.secretary.secretary.domain.model.CycleStatus;

public interface CreditCycleRepository extends JpaRepository<CreditCycle, Long> {
    List<CreditCycle> findByStatusIn(List<CycleStatus> statuses);
}
