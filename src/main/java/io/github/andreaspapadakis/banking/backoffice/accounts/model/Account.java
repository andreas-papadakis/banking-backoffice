package io.github.andreaspapadakis.banking.backoffice.accounts.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ACCOUNTS")
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class Account {

  @Id
  @Column(length = 36)
  private UUID id;

  @Column(nullable = false)
  private double balance;

  @Column(length = 3)
  private String currency;

  @Column(updatable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdAt;

  @PrePersist
  protected void onCreate() {
    this.createdAt = new Date();
  }

}
