package io.github.andreaspapadakis.banking.backoffice.accounts.controller;

import io.github.andreaspapadakis.banking.backoffice.accounts.dto.AccountCreateRequest;
import io.github.andreaspapadakis.banking.backoffice.accounts.dto.AccountResponse;
import io.github.andreaspapadakis.banking.backoffice.accounts.dto.AccountUpdateRequest;
import io.github.andreaspapadakis.banking.backoffice.shared.validation.AllowedCurrency;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@Validated
public interface AccountApi {

  @PostMapping
  ResponseEntity<AccountResponse> save(@Valid AccountCreateRequest accountCreateRequest);

  @ResponseStatus(HttpStatus.OK)
  @GetMapping
  List<AccountResponse> getAllAccounts();

  @ResponseStatus(HttpStatus.OK)
  @GetMapping(value = "/{id}")
  AccountResponse getAccountById(UUID id);

  @ResponseStatus(HttpStatus.OK)
  @GetMapping(params = "currency")
  List<AccountResponse> getAccountsByCurrency(
      @RequestParam
      @Pattern(regexp = "^[A-Z]{3}$",
          message = "{currencyPatternErrorMessage}")
      @AllowedCurrency
      String currency);

  @ResponseStatus(HttpStatus.ACCEPTED)
  @PatchMapping("/{id}")
  AccountResponse update(UUID id, @Valid AccountUpdateRequest accountUpdateRequest);

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping(value = "/{id}")
  void deleteById(UUID id);

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping
  void deleteAll();

  // for fun
  @ResponseStatus(HttpStatus.ACCEPTED)
  @GetMapping(value = "xarizeiToMagazi")
  List<AccountResponse> clearDebts();

  @PostMapping(value = "/russianRoulette/{id}")
  ResponseEntity<Object> russianRoulette(UUID id) throws IOException, URISyntaxException;
}
