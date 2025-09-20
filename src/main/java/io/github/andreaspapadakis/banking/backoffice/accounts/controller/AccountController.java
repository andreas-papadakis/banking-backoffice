package io.github.andreaspapadakis.banking.backoffice.accounts.controller;

import io.github.andreaspapadakis.banking.backoffice.accounts.dto.AccountRequestDto;
import io.github.andreaspapadakis.banking.backoffice.accounts.dto.AccountResponseDto;
import io.github.andreaspapadakis.banking.backoffice.accounts.service.AccountService;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

  private final AccountService accountService;

  public AccountController(AccountService accountService) {
    this.accountService = accountService;
  }

  @PostMapping
  public ResponseEntity<AccountResponseDto> save(@RequestBody AccountRequestDto accountRequestDto) {
    AccountResponseDto responseBody = accountService.save(accountRequestDto);
    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(responseBody.id())
        .toUri();

    return ResponseEntity.created(location).body(responseBody);
  }

  @GetMapping
  public ResponseEntity<List<AccountResponseDto>> getAllAccounts() {
    List<AccountResponseDto> responseBody = accountService.getAll();

    return new ResponseEntity<>(responseBody, HttpStatus.OK);
  }

  @GetMapping(value = "/{id}")
  public ResponseEntity<AccountResponseDto> getAccountById(@PathVariable String id) {
    AccountResponseDto responseBody = accountService.getAccountById(id);

    return new ResponseEntity<>(responseBody, HttpStatus.OK);
  }

  @GetMapping(params = "currency")
  public ResponseEntity<List<AccountResponseDto>> getAccountsByCurrency(
      @RequestParam String currency) {
    List<AccountResponseDto> responseBody = accountService.getAccountsByCurrency(currency);

    return new ResponseEntity<>(responseBody, HttpStatus.OK);
  }

  @PatchMapping("/{id}")
  public ResponseEntity<AccountResponseDto> update( @PathVariable String id,
      @RequestBody AccountRequestDto accountRequestDto) {
    AccountResponseDto responseBody = accountService.update(id, accountRequestDto);

    return new ResponseEntity<>(responseBody, HttpStatus.ACCEPTED);
  }

  @DeleteMapping(value = "/{id}")
  public ResponseEntity<Void> deleteById(@PathVariable String id) {
    accountService.deleteById(id);

    return ResponseEntity.noContent().build();
  }

  @DeleteMapping
  public ResponseEntity<Void> deleteAll() {
    accountService.deleteAll();

    return ResponseEntity.noContent().build();
  }

  // for fun
  @GetMapping(value = "xarizeiToMagazi")
  public ResponseEntity<List<AccountResponseDto>> clearDebts() {
    List<AccountResponseDto> responseBody = accountService.clearDebts();

    return new ResponseEntity<>(responseBody, HttpStatus.ACCEPTED);
  }

  @RequestMapping(method = RequestMethod.POST, value = "/russianRoulette/{loggedInId}")
  public ResponseEntity<Object> russianRoulette(@PathVariable String loggedInId)
      throws IOException, URISyntaxException {
    Object responseBody = accountService.russianRoulette(loggedInId);

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
