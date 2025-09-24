package io.github.andreaspapadakis.banking.backoffice.accounts.controller;

import io.github.andreaspapadakis.banking.backoffice.accounts.dto.AccountCreateRequest;
import io.github.andreaspapadakis.banking.backoffice.accounts.dto.AccountResponseDto;
import io.github.andreaspapadakis.banking.backoffice.accounts.dto.AccountUpdateRequest;
import io.github.andreaspapadakis.banking.backoffice.accounts.service.AccountService;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController implements AccountApi {
  private final AccountService accountService;

  @Override
  @PostMapping
  public ResponseEntity<AccountResponseDto> save(@RequestBody
                                                 AccountCreateRequest accountCreateRequest) {
    AccountResponseDto responseBody = accountService.save(accountCreateRequest);
    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(responseBody.id())
        .toUri();

    return ResponseEntity.created(location).body(responseBody);
  }

  @Override
  @GetMapping
  public ResponseEntity<List<AccountResponseDto>> getAllAccounts() {
    List<AccountResponseDto> responseBody = accountService.getAll();

    return new ResponseEntity<>(responseBody, HttpStatus.OK);
  }

  @Override
  @GetMapping(value = "/{id}")
  public ResponseEntity<AccountResponseDto> getAccountById(@PathVariable
                                                           UUID id) {
    AccountResponseDto responseBody = accountService.getAccountById(id);

    return new ResponseEntity<>(responseBody, HttpStatus.OK);
  }

  @Override
  @GetMapping(params = "currency")
  public ResponseEntity<List<AccountResponseDto>> getAccountsByCurrency(String currency) {
    List<AccountResponseDto> responseBody = accountService.getAccountsByCurrency(currency);

    return new ResponseEntity<>(responseBody, HttpStatus.OK);
  }

  @Override
  @PatchMapping("/{id}")
  public ResponseEntity<AccountResponseDto> update(@PathVariable UUID id,
                                                   @RequestBody
                                                   AccountUpdateRequest accountUpdateRequest) {
    AccountResponseDto responseBody = accountService.update(id, accountUpdateRequest);

    return new ResponseEntity<>(responseBody, HttpStatus.ACCEPTED);
  }

  @Override
  @DeleteMapping(value = "/{id}")
  public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
    accountService.deleteById(id);

    return ResponseEntity.noContent().build();
  }

  @Override
  @DeleteMapping
  public ResponseEntity<Void> deleteAll() {
    accountService.deleteAll();

    return ResponseEntity.noContent().build();
  }

  // for fun
  @Override
  @GetMapping(value = "xarizeiToMagazi")
  public ResponseEntity<List<AccountResponseDto>> clearDebts() {
    List<AccountResponseDto> responseBody = accountService.clearDebts();

    return new ResponseEntity<>(responseBody, HttpStatus.ACCEPTED);
  }

  @Override
  @PostMapping(value = "/russianRoulette/{loggedInId}")
  public ResponseEntity<Object> russianRoulette(@PathVariable UUID id)
      throws IOException, URISyntaxException {
    Object responseBody = accountService.russianRoulette(id);

    if (responseBody instanceof String) {
      return new ResponseEntity<>(responseBody, HttpStatus.GONE);
    } else if (responseBody instanceof AccountResponseDto) {
      if (((AccountResponseDto) responseBody).balance() == 0d) {
        return new ResponseEntity<>(responseBody, HttpStatus.RESET_CONTENT);
      }
      return new ResponseEntity<>(responseBody, HttpStatus.ACCEPTED);
    }

    return new ResponseEntity<>((Object) null, HttpStatus.I_AM_A_TEAPOT);
  }
}
