package io.github.andreaspapadakis.banking.backoffice.accounts.mapper;

import io.github.andreaspapadakis.banking.backoffice.accounts.dto.AccountCreateRequest;
import io.github.andreaspapadakis.banking.backoffice.accounts.dto.AccountResponse;
import io.github.andreaspapadakis.banking.backoffice.accounts.model.Account;
import org.jspecify.annotations.Nullable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountMapper {
  AccountResponse toAccountResponseWithAllData(@Nullable Account account);

  @Mapping(target = "id", ignore = true)
  AccountResponse toAccountResponseWithoutId(@Nullable Account account);

  @Mapping(target = "id", expression = "java(UUID.randomUUID())")
  @Mapping(target = "balance", constant = "0.0")
  @Mapping(target = "createdAt", ignore = true)
  Account fromAccountCreateRequest(@Nullable AccountCreateRequest accountCreateRequest);
}
