package com.shubham.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.time.LocalDateTime;

import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

@Schema(description = "Represents audit information in the system")
@Getter
@Setter
@MappedSuperclass
public class AbstractEntity {

    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    @Schema(description = "The date and time when the entity was created", example = "2024-01-15T10:00:00")
    private LocalDateTime createdDate = LocalDateTime.now();

    @CreatedBy
    @Column(name = "created_by")
    @Schema(description = "The user who created the entity", example = "john.doe")
    private String createdBy = "System";

    @LastModifiedDate
    @Column(name = "updated_date", nullable = false)
    @Schema(description = "The date and time when the entity was last updated", example = "2024-01-15T12:30:00")
    private LocalDateTime updatedDate = LocalDateTime.now();

    @LastModifiedBy
    @Column(name = "updated_by", updatable = false)
    @Schema(description = "The user who last updated the entity", example = "jane.smith")
    private String updatedBy = "System";
}

