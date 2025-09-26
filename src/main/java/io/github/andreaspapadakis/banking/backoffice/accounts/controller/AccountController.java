package io.github.andreaspapadakis.banking.backoffice.accounts.controller;

import io.github.andreaspapadakis.banking.backoffice.accounts.dto.AccountCreateRequest;
import io.github.andreaspapadakis.banking.backoffice.accounts.dto.AccountResponse;
import io.github.andreaspapadakis.banking.backoffice.accounts.dto.AccountUpdateRequest;
import io.github.andreaspapadakis.banking.backoffice.accounts.service.AccountService;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
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
  public ResponseEntity<AccountResponse> save(@RequestBody
                                              AccountCreateRequest accountCreateRequest) {
    AccountResponse responseBody = accountService.save(accountCreateRequest);
    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(responseBody.id())
        .toUri();

    return ResponseEntity.created(location).body(responseBody);
  }

  @Override
  public List<AccountResponse> getAllAccounts() {
    return accountService.getAll();
  }

  @Override
  public AccountResponse getAccountById(@PathVariable UUID id, HttpServletRequest request) {
    request.setAttribute("id", id.toString());
    return accountService.getAccountById(id);
  }

  @Override
  public List<AccountResponse> getAccountsByCurrency(String currency) {
    return accountService.getAccountsByCurrency(currency);
  }

  @Override
  public AccountResponse update(@PathVariable UUID id,
                                @RequestBody AccountUpdateRequest accountUpdateRequest,
                                HttpServletRequest request) {
    request.setAttribute("id", id.toString());
    return accountService.update(id, accountUpdateRequest);
  }

  @Override
  public void deleteById(@PathVariable UUID id, HttpServletRequest request) {
    request.setAttribute("id", id.toString());
    accountService.deleteById(id);
  }

  @Override
  public void deleteAll() {
    accountService.deleteAll();
  }

  // for fun
  @Override
  public List<AccountResponse> clearDebts() {
    return accountService.clearDebts();
  }

  @Override
  public ResponseEntity<Object> russianRoulette(@PathVariable UUID id, HttpServletRequest request)
      throws IOException, URISyntaxException {
    request.setAttribute("id", id.toString());
    Object responseBody = accountService.russianRoulette(id);

    if (responseBody instanceof String) {
      return ResponseEntity.status(HttpStatus.GONE).body(responseBody);
    } else if (responseBody instanceof AccountResponse) {
      if (((AccountResponse) responseBody).balance() == 0d) {
        return ResponseEntity.status(HttpStatus.RESET_CONTENT).body(responseBody);
      }
      return ResponseEntity.status(HttpStatus.ACCEPTED).body(responseBody);
    }

    return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).build();
  }
}
