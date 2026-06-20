package com.secretary.secretary.domain.model;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditCycle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "bank_id")
    private Bank bank;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "adjusted_end_date", nullable = false)
    private LocalDate adjustedEndDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CycleStatus status;

    @PrePersist
    @PreUpdate
    public void calculateDatesAndStatus() {
        if (this.startDate == null) {
            this.startDate = LocalDate.now();
        }

        // First ruler: cycle by 10 days
        this.endDate = this.startDate.plusDays(10);

        // Second ruler¨ if end date is in weekend back to friday
        this.adjustedEndDate = adjustToBusinnesDayPrior(this.endDate);

        // Threed ruler: Updated status if not closer manual
        if (this.status != CycleStatus.CLOSED) {
            this.updateStatusBasedOnDate(LocalDate.now());
        }
    }

    public void updateStatusBasedOnDate(LocalDate today) {
        if (this.status == CycleStatus.CLOSED)
            return;

        if (today.isAfter(this.adjustedEndDate)) {
            this.status = CycleStatus.OVERDUE;
        } else {
            long daysToExpiry = ChronoUnit.DAYS.between(today, this.adjustedEndDate);
            if (daysToExpiry <= 3 && daysToExpiry >= 0) {
                this.status = CycleStatus.CLOSE_TO_EXPIRY;
            } else {
                this.status = CycleStatus.ACTIVE;
            }
        }
    }

    private LocalDate adjustToBusinnesDayPrior(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();

        if (day == DayOfWeek.SATURDAY) {
            return date.minusDays(1);
        } else if (day == DayOfWeek.SUNDAY) {
            return date.minusDays(2);
        }

        return date;
    }
}
