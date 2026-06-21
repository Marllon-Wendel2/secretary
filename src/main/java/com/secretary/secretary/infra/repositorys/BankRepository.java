package com.secretary.secretary.infra.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import com.secretary.secretary.domain.model.Bank;
import java.util.Optional;

public interface BankRepository extends JpaRepository<Bank, Long> {
    Optional<Bank> findByNameIgnoreCaseAndActiveTrue(String nameString);
}
