package org.example.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@Builder
@ToString
@Setter
@Getter
@Table(name = "user")
public class User {

    @Id
    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "balance")
    private BigInteger balance;

    @Column(name = "name")
    private String name;

    @Column(name = "pin")
    private String pin;

    @Column(name = "created_at")
    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime deletedAt;

    @Column(name = "is_active")
    private Boolean isActive;
}
