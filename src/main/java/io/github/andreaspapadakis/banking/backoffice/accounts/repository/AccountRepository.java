package io.github.andreaspapadakis.banking.backoffice.accounts.repository;

import io.github.andreaspapadakis.banking.backoffice.accounts.model.Account;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends CrudRepository<Account, String> {

  @Query("select a from Account a where a.currency = :currency")
  List<Account> findByCurrency(String currency);

  @Query("select a from Account a where balance < 0")
  List<Account> findAccountsInDebt();
}
