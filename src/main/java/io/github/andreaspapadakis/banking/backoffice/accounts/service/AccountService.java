package io.github.andreaspapadakis.banking.backoffice.accounts.service;

import io.github.andreaspapadakis.banking.backoffice.accounts.dto.AccountRequestDto;
import io.github.andreaspapadakis.banking.backoffice.accounts.dto.AccountResponseDto;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface AccountService {

  AccountResponseDto save(AccountRequestDto accountRequestDto);

  List<AccountResponseDto> getAll();

  AccountResponseDto getAccountById(String id);

  List<AccountResponseDto> getAccountsByCurrency(String currency);

  AccountResponseDto update(String id, AccountRequestDto accountRequestDto);

  void deleteById(String id);

  void deleteAll();

  List<AccountResponseDto> clearDebts();

  Object russianRoulette(String loggedInId) throws IOException, URISyntaxException;
}
