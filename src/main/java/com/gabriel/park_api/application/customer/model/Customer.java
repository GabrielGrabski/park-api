package com.gabriel.park_api.application.customer.model;

import com.gabriel.park_api.application.customer.enums.CustomerStatus;
import com.gabriel.park_api.application.customer.enums.CustomerType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "CUSTOMER")
@Entity(name = "CUSTOMER")
@EntityListeners(AuditingEntityListener.class)
public class Customer {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "TYPE")
    @Enumerated(EnumType.STRING)
    private CustomerType type;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private CustomerStatus status;

    @CreatedDate
    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;
}
