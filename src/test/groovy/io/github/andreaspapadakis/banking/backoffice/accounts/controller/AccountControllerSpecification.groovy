package io.github.andreaspapadakis.banking.backoffice.accounts.controller

import io.github.andreaspapadakis.banking.backoffice.accounts.dto.AccountCreateRequest
import io.github.andreaspapadakis.banking.backoffice.accounts.dto.AccountResponse
import io.github.andreaspapadakis.banking.backoffice.accounts.dto.AccountUpdateRequest
import io.github.andreaspapadakis.banking.backoffice.accounts.service.AccountService
import jakarta.servlet.http.HttpServletRequest
import org.mockito.Mockito
import org.springframework.http.HttpStatus
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import org.springframework.web.util.UriComponents
import org.springframework.web.util.UriComponentsBuilder
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

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

    and: "a HttpServletRequest"
    def request = Mock(HttpServletRequest)

    and: "a response from service"
    def accountResponse = new AccountResponse(id, 0.0d, "EUR", null)

    when:
    def response = accountController.getAccountById(id, request)

    then:
    1 * request.setAttribute("id", id.toString())
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

    and: "a HttpServletRequest"
    def request = Mock(HttpServletRequest)

    and: "an account update request"
    def accountUpdateRequest = new AccountUpdateRequest(1.0d, "EUR")

    and: "a response from service"
    def accountResponse = new AccountResponse(id, accountUpdateRequest.balance(),
            accountUpdateRequest.currency(), null)

    when:
    def response = accountController.update(id, accountUpdateRequest, request)

    then:
    1 * request.setAttribute("id", id.toString())
    1 * accountService.update(id, accountUpdateRequest) >> accountResponse
    response == accountResponse
  }

  def "test delete by ID"() {
    given: "an ID"
    def id = UUID.randomUUID()

    and: "a HttpServletRequest"
    def request = Mock(HttpServletRequest)

    when:
    accountController.deleteById(id, request)

    then:
    1 * request.setAttribute("id", id.toString())
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

  @Unroll("Russian Roulette: when #scenario then expect #expectedStatus")
  def "test Russian Roulette"() {
    given: "a HttpServletRequest"
    def request = Mock(HttpServletRequest)

    when:
    def response = accountController.russianRoulette(id, request)

    then:
    1 * request.setAttribute("id", id.toString())
    1 * accountService.russianRoulette(id) >> serviceResponse
    response.statusCode == expectedStatus
    response.body == serviceResponse

    where:
    scenario                           | id                | serviceResponse                            || expectedStatus
    "String response"                  | UUID.randomUUID() | "RIP"                                      || HttpStatus.GONE
    "account response with 0 balance"  | UUID.randomUUID() | new AccountResponse(id, 0.0d, "EUR", null) || HttpStatus.RESET_CONTENT
    "account response with >0 balance" | UUID.randomUUID() | new AccountResponse(id, 1.0d, "EUR", null) || HttpStatus.ACCEPTED
    "anything else"                    | UUID.randomUUID() | null                                       || HttpStatus.I_AM_A_TEAPOT
  }
}