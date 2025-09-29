package io.github.andreaspapadakis.banking.backoffice.accounts.mapper

import io.github.andreaspapadakis.banking.backoffice.accounts.dto.AccountCreateRequest
import io.github.andreaspapadakis.banking.backoffice.accounts.model.Account
import spock.lang.Specification
import spock.lang.Subject

class AccountMapperSpecification extends Specification {
  @Subject
  private final AccountMapper accountMapper = new AccountMapperImpl()

  def "test toAccountResponseWithAllData"() {
    given: "an account"
    def account = new Account(UUID.randomUUID(), 1.0d, "EUR", new Date())

    when:
    def result = accountMapper.toAccountResponseWithAllData(account)

    then:
    result.id() == account.id
    result.balance() == account.balance
    result.currency() == account.currency
    result.createdAt() == account.createdAt
  }

  def "test toAccountResponseWithoutId"() {
    given: "an account"
    def account = new Account(UUID.randomUUID(), 1.0d, "EUR", new Date())

    when:
    def result = accountMapper.toAccountResponseWithoutId(account)

    then:
    result.id() == null
    result.balance() == account.balance
    result.currency() == account.currency
    result.createdAt() == account.createdAt
  }

  def "test fromAccountCreateRequest"() {
    given: "an account create request"
    def accountCreateRequest = new AccountCreateRequest("EUR")

    when:
    def result = accountMapper.fromAccountCreateRequest(accountCreateRequest)

    then:
    result.id != null
    result.balance == 0.0d
    result.currency== accountCreateRequest.currency()
    result.createdAt == null
  }
}
