package io.github.andreaspapadakis.banking.backoffice.accounts.controller;

import io.github.andreaspapadakis.banking.backoffice.accounts.dto.AccountCreateRequest;
import io.github.andreaspapadakis.banking.backoffice.accounts.dto.AccountResponseDto;
import io.github.andreaspapadakis.banking.backoffice.accounts.dto.AccountUpdateRequest;
import io.github.andreaspapadakis.banking.backoffice.shared.validation.AllowedCurrency;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestParam;

@Validated
public interface AccountApi {

  ResponseEntity<AccountResponseDto> save(@Valid
                                          AccountCreateRequest accountCreateRequest);

  ResponseEntity<List<AccountResponseDto>> getAllAccounts();

  ResponseEntity<AccountResponseDto> getAccountById(UUID id);

  ResponseEntity<List<AccountResponseDto>> getAccountsByCurrency(
      @RequestParam
      @Pattern(regexp = "^[A-Z]{3}$",
          message = "{currencyPatternErrorMessage}")
      @AllowedCurrency
      String currency);

  ResponseEntity<AccountResponseDto> update(UUID id,
                                            @Valid
                                            AccountUpdateRequest accountUpdateRequest);

  ResponseEntity<Void> deleteById(UUID id);

  ResponseEntity<Void> deleteAll();

  // for fun
  ResponseEntity<List<AccountResponseDto>> clearDebts();

  ResponseEntity<Object> russianRoulette(UUID id)
      throws IOException, URISyntaxException;
}
