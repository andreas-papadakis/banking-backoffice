package io.github.andreaspapadakis.banking.backoffice.accounts.service;

import io.github.andreaspapadakis.banking.backoffice.accounts.dto.AccountCreateRequest;
import io.github.andreaspapadakis.banking.backoffice.accounts.dto.AccountResponse;
import io.github.andreaspapadakis.banking.backoffice.accounts.dto.AccountUpdateRequest;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;
import org.springframework.validation.annotation.Validated;

@Validated
public interface AccountService {

  @NotNull
  AccountResponse save(AccountCreateRequest accountRequestDto);

  @NotNull
  List<AccountResponse> getAll();

  @NotNull
  AccountResponse getAccountById(UUID id);

  @NotNull
  List<AccountResponse> getAccountsByCurrency(String currency);

  @NotNull
  AccountResponse update(UUID id, AccountUpdateRequest accountUpdateRequest);

  void deleteById(UUID id);

  void deleteAll();

  @NotNull
  List<AccountResponse> clearDebts();

  @NotNull
  Object russianRoulette(UUID id) throws IOException, URISyntaxException;
}
