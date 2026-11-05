package com.learning.springboot.orderinvoiceservice.repository;

import com.learning.springboot.orderinvoiceservice.entity.InvoiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<InvoiceEntity, Long> {
}
