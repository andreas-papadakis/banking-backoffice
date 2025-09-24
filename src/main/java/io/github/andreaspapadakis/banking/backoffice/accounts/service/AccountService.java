package io.github.andreaspapadakis.banking.backoffice.accounts.service;

import io.github.andreaspapadakis.banking.backoffice.accounts.dto.AccountCreateRequest;
import io.github.andreaspapadakis.banking.backoffice.accounts.dto.AccountResponseDto;
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
  AccountResponseDto save(AccountCreateRequest accountRequestDto);

  @NotNull
  List<AccountResponseDto> getAll();

  @NotNull
  AccountResponseDto getAccountById(UUID id);

  @NotNull
  List<AccountResponseDto> getAccountsByCurrency(String currency);

  @NotNull
  AccountResponseDto update(UUID id, AccountUpdateRequest accountUpdateRequest);

  @NotNull
  void deleteById(UUID id);

  @NotNull
  void deleteAll();

  @NotNull
  List<AccountResponseDto> clearDebts();

  @NotNull
  Object russianRoulette(UUID loggedInId) throws IOException, URISyntaxException;
}
