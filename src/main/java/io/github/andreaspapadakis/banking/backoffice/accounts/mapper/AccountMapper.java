package io.github.andreaspapadakis.banking.backoffice.accounts.mapper;

import io.github.andreaspapadakis.banking.backoffice.accounts.dto.AccountResponseDto;
import io.github.andreaspapadakis.banking.backoffice.accounts.model.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountMapper {
  AccountResponseDto mapAllData(Account account);

  @Mapping(target = "id", ignore = true)
  AccountResponseDto mapWithoutId(Account account);
}
