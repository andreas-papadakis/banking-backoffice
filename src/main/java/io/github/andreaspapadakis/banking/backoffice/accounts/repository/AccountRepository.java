package io.github.andreaspapadakis.banking.backoffice.accounts.repository;

import io.github.andreaspapadakis.banking.backoffice.accounts.model.Account;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends CrudRepository<Account, UUID> {

  @Query("select a from Account a where a.currency = :currency")
  List<Account> findByCurrency(@Param("currency") String currency);

  @Query("select a from Account a where balance < 0")
  List<Account> findAccountsInDebt();
}
