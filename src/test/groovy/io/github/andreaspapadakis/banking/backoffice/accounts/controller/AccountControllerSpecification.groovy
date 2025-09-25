package io.github.andreaspapadakis.banking.backoffice.accounts.controller

import io.github.andreaspapadakis.banking.backoffice.accounts.dto.AccountCreateRequest
import io.github.andreaspapadakis.banking.backoffice.accounts.dto.AccountResponse
import io.github.andreaspapadakis.banking.backoffice.accounts.dto.AccountUpdateRequest
import io.github.andreaspapadakis.banking.backoffice.accounts.service.AccountService
import org.mockito.Mockito
import org.springframework.http.HttpStatus
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import org.springframework.web.util.UriComponents
import org.springframework.web.util.UriComponentsBuilder
import spock.lang.Specification
import spock.lang.Subject

class AccountControllerSpecification extends Specification {
  private final AccountService accountService = Mock()

  @Subject
  private final AccountController accountController = new AccountController(accountService)

  def "test successful save"() {
    given: "an account save request"
      def accountCreateRequest = new AccountCreateRequest("EUR")

    and: "a response from service"
      def accountResponse = new AccountResponse(UUID.randomUUID(), 0.0d,
              accountCreateRequest.currency(), null)

    and: "an expected location URI"
      def expectedLocation = new URI("http://localhost:8080/accounts/" + accountResponse.id())

    and: "mock the static ServletUriComponentsBuilder"
      def mockUriBuilder = Mock(ServletUriComponentsBuilder)
      def mockPathBuilder = Mock(UriComponentsBuilder)
      def mockBuildBuilder = Mock(UriComponents)
      def mockedBuilder =
              Mockito.mockStatic(ServletUriComponentsBuilder)
      mockedBuilder.when({ ServletUriComponentsBuilder.fromCurrentRequest() })
              .thenReturn(mockUriBuilder)
      mockUriBuilder.path(_ as String) >> mockPathBuilder
      mockPathBuilder.buildAndExpand(_ as Object) >> mockBuildBuilder
      mockBuildBuilder.toUri() >> expectedLocation

    when:
      def response = accountController.save(accountCreateRequest)

    then:
      1 * accountService.save(accountCreateRequest) >> accountResponse
      response.statusCode == HttpStatus.CREATED
      response.headers.location == expectedLocation
      response.body == accountResponse

    cleanup:
      mockedBuilder?.close()
  }

  def "test get all"() {
    given: "a response from service"
      def accountResponse = new AccountResponse(UUID.randomUUID(), 0.0d, "EUR",
              null)
      def accountResponseList = new ArrayList<>()
      accountResponseList.add(accountResponse)

    when:
    def response = accountController.getAllAccounts()

    then:
    1 * accountService.getAll() >> accountResponseList
    response == accountResponseList
  }

  def "test get by ID"() {
    given: "an ID"
    def id = UUID.randomUUID()

    and: "a response from service"
    def accountResponse = new AccountResponse(id, 0.0d, "EUR", null)

    when:
    def response = accountController.getAccountById(id)

    then:
    1 * accountService.getAccountById(id) >> accountResponse
    response == accountResponse
  }

  def "test get by currency"() {
    given: "a currency"
    def currency = "EUR"

    and: "a response from service"
    def accountResponse = new AccountResponse(UUID.randomUUID(), 0.0d, currency, null)
    def accountResponseList = new ArrayList<>()
    accountResponseList.add(accountResponse)

    when:
    def response = accountController.getAccountsByCurrency(currency)

    then:
    1 * accountService.getAccountsByCurrency(currency) >> accountResponseList
    response == accountResponseList
  }

  def "test update"() {
    given: "an ID"
    def id = UUID.randomUUID()

    and: "an account update request"
    def accountUpdateRequest = new AccountUpdateRequest(1.0d, "EUR")

    and: "a response from service"
    def accountResponse = new AccountResponse(id, accountUpdateRequest.balance(),
            accountUpdateRequest.currency(), null)

    when:
    def response = accountController.update(id, accountUpdateRequest)

    then:
    1 * accountService.update(id, accountUpdateRequest) >> accountResponse
    response == accountResponse
  }

  def "test delete by ID"() {
    given: "an ID"
    def id = UUID.randomUUID()

    when:
    accountController.deleteById(id)

    then:
    1 * accountService.deleteById(id)
  }

  def "test delete all"() {
    when:
    accountController.deleteAll()

    then:
    1 * accountService.deleteAll()
  }

  def "test clear debts"() {
    given: "a response from service"
    def accountResponse = new AccountResponse(UUID.randomUUID(), 0.0d,
            "EUR", null)
    def accountResponseList = new ArrayList<>()
    accountResponseList.add(accountResponse)

    when:
    def response = accountController.clearDebts()

    then:
    1 * accountService.clearDebts() >> accountResponseList
    response == accountResponseList
  }

  def "test Russian Roulette - String response"() {
    given: "an ID"
    def id = UUID.randomUUID()

    and: "a response from service"
    def serviceResponse = "RIP"

    when:
    def response = accountController.russianRoulette(id)

    then:
    1 * accountService.russianRoulette(id) >> serviceResponse
    response.statusCode == HttpStatus.GONE
    response.body == serviceResponse
  }

  def "test Russian Roulette - AccountResponse response with 0 balance"() {
    given: "an ID"
    def id = UUID.randomUUID()

    and: "a response from service"
    def accountResponse = new AccountResponse(id, 0.0d, "EUR", null)

    when:
    def response = accountController.russianRoulette(id)

    then:
    1 * accountService.russianRoulette(id) >> accountResponse
    response.statusCode == HttpStatus.RESET_CONTENT
    response.body == accountResponse
  }

  def "test Russian Roulette - AccountResponseDTO response with balance > 0"() {
    given: "an ID"
    def id = UUID.randomUUID()

    and: "a response from service"
    def accountResponse = new AccountResponse(id, 1.0d, "EUR", null)

    when:
    def response = accountController.russianRoulette(id)

    then:
    1 * accountService.russianRoulette(id) >> accountResponse
    response.statusCode == HttpStatus.ACCEPTED
    response.body == accountResponse
  }

  def "test Russian Roulette - no response"() {
    given: "an ID"
    def id = UUID.randomUUID()

    when:
    def response = accountController.russianRoulette(id)

    then:
    1 * accountService.russianRoulette(id)
    response.statusCode == HttpStatus.I_AM_A_TEAPOT
    response.body == null
  }
}