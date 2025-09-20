package io.github.andreaspapadakis.banking.backoffice.accounts.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.andreaspapadakis.banking.backoffice.accounts.dto.AccountRequestDto;
import io.github.andreaspapadakis.banking.backoffice.accounts.dto.AccountResponseDto;
import io.github.andreaspapadakis.banking.backoffice.accounts.exception.RussianRouletteException;
import io.github.andreaspapadakis.banking.backoffice.accounts.mapper.AccountMapper;
import io.github.andreaspapadakis.banking.backoffice.accounts.model.Account;
import io.github.andreaspapadakis.banking.backoffice.accounts.repository.AccountRepository;
import io.github.andreaspapadakis.banking.backoffice.shared.config.ApplicationProperties;
import io.github.andreaspapadakis.banking.backoffice.shared.config.RandomNumberGeneratorProperty;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {

  private final AccountMapper accountMapper;
  private final AccountRepository accountRepository;
  private final RandomNumberGeneratorProperty randomNumberGeneratorProperty;

  public AccountServiceImpl(AccountMapper accountMapper,
                            AccountRepository accountRepository,
                            ApplicationProperties appProperties) {
    this.accountMapper = accountMapper;
    this.accountRepository = accountRepository;
    this.randomNumberGeneratorProperty = appProperties.randomNumberGeneratorProperty();
  }

  @Override
  public AccountResponseDto save(AccountRequestDto accountRequestDto) {
    Account account = new Account();

    account.setId(UUID.randomUUID().toString());
    account.setBalance(0.0d);
    account.setCurrency(accountRequestDto.currency());

    return accountMapper.mapAllData(accountRepository.save(account));
  }

  @Override
  @Transactional(readOnly = true)
  public List<AccountResponseDto> getAll() {
    List<AccountResponseDto> accountResponseDtos = new ArrayList<>();

    accountRepository.findAll()
        .forEach(account -> accountResponseDtos.add(accountMapper.mapAllData(account)));

    return accountResponseDtos;
  }

  @Override
  @Transactional(readOnly = true)
  public AccountResponseDto getAccountById(String id) {
    return accountMapper.mapAllData(accountRepository.findById(id).orElseThrow());
  }

  @Override
  @Transactional(readOnly = true)
  public List<AccountResponseDto> getAccountsByCurrency(String currency) {
    List<Account> accountsByCurrency = accountRepository.findByCurrency(currency);

    if (accountsByCurrency.isEmpty()) {
      throw new NoSuchElementException("There are no accounts with provided currency");
    }

    return accountsByCurrency.stream()
        .map(accountMapper::mapAllData)
        .collect(Collectors.toList());
  }

  @Override
  public AccountResponseDto update(String id, AccountRequestDto accountRequestDto) {
    Account existingAccount = accountRepository.findById(id).orElseThrow();
    boolean update = false;

    if (accountRequestDto.balance() != null
        && !accountRequestDto.balance().equals(existingAccount.getBalance())) {
      existingAccount.setBalance(accountRequestDto.balance());
      update = true;
    }

    if (accountRequestDto.currency() != null
        && !accountRequestDto.currency().equals(existingAccount.getCurrency())) {
      existingAccount.setCurrency(accountRequestDto.currency());
      update = true;
    }

    return accountMapper.mapAllData(update
        ? accountRepository.save(existingAccount)
        : existingAccount);
  }

  @Override
  public void deleteById(String id) {
    if (!accountRepository.existsById(id)) {
      throw new NoSuchElementException("There is no account with provided ID");
    }

    accountRepository.deleteById(id);
  }

  @Override
  public void deleteAll() {
    accountRepository.deleteAll();
  }

  @Override
  public List<AccountResponseDto> clearDebts() {
    List<Account> accountsWithDebts = accountRepository.findAccountsInDebt();

    if (accountsWithDebts.isEmpty()) {
      throw new NoSuchElementException("There are no accounts in debt");
    }

    accountsWithDebts.forEach(account -> {
      account.setBalance(0.0);
      accountRepository.save(account);
    });

    return accountsWithDebts.stream()
        .map(accountMapper::mapAllData)
        .collect(Collectors.toList());
  }

  @Override
  public Object russianRoulette(String loggedInId) throws IOException, URISyntaxException {
    Account loggedInAccount = accountRepository.findById(loggedInId).orElseThrow();

    if (loggedInAccount.getBalance() < 100000) {
      throw new RussianRouletteException("You are too weak to play russian roulette....");
    }

    int randomPick = getRandomInt(0, 5);
    int bonusOrDeathPick = getRandomInt(0, 20);

    if (randomPick == 0) { // 1/6 probability to pass out; hehe
      if (bonusOrDeathPick <= 3) { // 3% probability to die; hehehehehe
        accountRepository.deleteById(loggedInId);

        return "RIP";
      }

      loggedInAccount.setBalance(0d);

      return accountMapper.mapAllData(accountRepository.save(loggedInAccount));
    } else if (bonusOrDeathPick < 2) {
      if (randomPick == 5) { // 1.5% probability to live happily :(
        loggedInAccount.setBalance(loggedInAccount.getBalance() * 1.5);
      } else { // 5% probability you are still shocked
        loggedInAccount.setBalance(loggedInAccount.getBalance() / 5.0);
        loggedInAccount.setCurrency("RMB"); // 1 euro == 0,13 RMB (chinese currency) ;)
      }

      return accountMapper.mapAllData(accountRepository.save(loggedInAccount));
    }

    return null;
  }

  private int getRandomInt(int min, int max) throws IOException, URISyntaxException {
    URL url = (new URI(randomNumberGeneratorProperty.apiUrl())).toURL();

    final String jsonRequest =
        "{\"jsonrpc\":\"2.0\",\"method\":\"generateIntegers\",\"params\":{\"apiKey\":\""
            + randomNumberGeneratorProperty.apiKey() + "\",\"n\":1,\"min\":" + min + ",\"max\":"
            + max + ",\"replacement\":true},\"id\":1}";

    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("POST");
    conn.setRequestProperty("Content-Type", "application/json");
    conn.setDoOutput(true);
    conn.getOutputStream().write(jsonRequest.getBytes());

    // Read the response
    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    String inputLine;
    StringBuilder response = new StringBuilder();

    while ((inputLine = in.readLine()) != null) {
      response.append(inputLine);
    }
    in.close();

    // Parse the JSON response
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode responseJson = objectMapper.readTree(response.toString());

    // Extract the random data
    JsonNode randomData = responseJson.path("result").path("random").path("data");

    // Output the random number(s)
    return randomData.get(0).asInt();
  }

}