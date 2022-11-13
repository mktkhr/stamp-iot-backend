package com.example.stamp_app.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Entity
@Data
public class MicroController {

    @Id
    @NotNull
    private UUID uuid;

    @NotNull
    @Column(unique = true)
    private String macAddress;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "microController")
    private List<MeasuredDataMaster> measuredDataMasters;

    @ManyToOne
    private Account account;
}
