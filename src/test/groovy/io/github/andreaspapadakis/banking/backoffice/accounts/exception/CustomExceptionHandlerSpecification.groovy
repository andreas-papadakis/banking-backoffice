package io.github.andreaspapadakis.banking.backoffice.accounts.exception

import io.github.andreaspapadakis.banking.backoffice.shared.exception.ApiException
import io.github.andreaspapadakis.banking.backoffice.shared.exception.ApiExceptionFactory
import io.github.andreaspapadakis.banking.backoffice.shared.exception.ErrorCode
import org.springframework.context.MessageSourceResolvable
import org.springframework.http.HttpStatus
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.validation.method.ParameterValidationResult
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest
import org.springframework.web.method.HandlerMethod
import org.springframework.web.method.annotation.HandlerMethodValidationException
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

class CustomExceptionHandlerSpecification extends Specification {
  private final def apiExceptionFactory = Mock(ApiExceptionFactory)

  @Subject
  private final CustomExceptionHandler exceptionHandler =
          new CustomExceptionHandler(apiExceptionFactory)

  def "test handleNoSuchElementException"() {
    given: "an id"
    def id = UUID.randomUUID().toString()

    and: "a request"
    def request = Mock(ServletWebRequest)
    request.getAttribute("id", 0) >> id

    and: "an expected ApiException"
    def apiException = Mock(ApiException)

    when:
    def result = exceptionHandler.handleNoSuchElementException(request)

    then:
    1 * apiExceptionFactory.fromErrorCode(ErrorCode.ACCOUNT_NOT_FOUND, { Object[] args ->
      args.length == 1 && args[0] == id
    } as Object[]) >> apiException
    result == apiException
  }

  def "test handleRussianRouletteException"() {
    given: "an id"
    def id = UUID.randomUUID().toString()

    and: "a request"
    def request = Mock(ServletWebRequest)
    request.getAttribute("id", 0) >> id

    and: "an expected ApiException"
    def apiException = Mock(ApiException)

    when:
    def result = exceptionHandler.handleRussianRouletteException(request)

    then:
    1 * apiExceptionFactory.fromErrorCode(ErrorCode.INSUFFICIENT_FUNDS, { Object[] args ->
      args.length == 1 && args[0] == id
    } as Object[]) >> apiException
    result == apiException
  }

  def "test handleNoUpdatesException"() {
    given: "an id"
    def id = UUID.randomUUID().toString()

    and: "a request"
    def request = Mock(ServletWebRequest)
    request.getAttribute("id", 0) >> id

    and: "an expected ApiException"
    def apiException = Mock(ApiException)

    when:
    def result = exceptionHandler.handleNoUpdatesException(request)

    then:
    1 * apiExceptionFactory.fromErrorCode(ErrorCode.NO_UPDATES, { Object[] args ->
      args.length == 1 && args[0] == id
    } as Object[]) >> apiException
    result == apiException
  }

  @Unroll("handleMethodArgumentTypeMismatchException: value=#value, requiredType=#requiredType should return #expectedValue and #expectedRequiredType")
  def "test handleMethodArgumentTypeMismatchException"() {
    given: "an exception"
    def ex = Mock(MethodArgumentTypeMismatchException)
    ex.getName() >> "argumentName"
    ex.getValue() >> value
    ex.getRequiredType() >> requiredType

    and: "an expected ApiException"
    def apiException = Mock(ApiException)

    when:
    def result = exceptionHandler.handleMethodArgumentTypeMismatchException(ex)

    then:
    1 * apiExceptionFactory.fromErrorCode(ErrorCode.INVALID_PARAMETER_TYPE, { Object[] args ->
      args.length == 3
      && args[0] == ex.getName()
      && args[1] == expectedValue
      && args[2] == expectedRequiredType
    } as Object[]) >> apiException
    result == apiException

    where:
    value | requiredType  || expectedValue | expectedRequiredType
    42    | Integer.class || 42            | "Integer"
    null  | Integer.class || "null"        | "Integer"
    42    | null          || 42            | "unknown"
    null  | null          || "null"        | "unknown"
  }

  @Unroll("handleMethodArgumentNotValid - #scenario")
  def "test handleMethodArgumentNotValid"() {
    given: "a BindingResult with global and field errors and an expected message"
    def target = new Object()
    def br = new BeanPropertyBindingResult(target, "objectName")
    def objectErrorDefaultMessage
    def fieldError1DefaultMessage = "fieldError1DefaultMessage"
    def expectedMessage

    br.addError(new FieldError("objectName", "field1",
            "rejectedValue", false, null, null,
            fieldError1DefaultMessage))

    if (includeObjectError) {
      objectErrorDefaultMessage = "objectErrorDefaultMessage"
      br.addError(new ObjectError("objectName", objectErrorDefaultMessage))
      expectedMessage = objectErrorDefaultMessage + ", " + fieldError1DefaultMessage
    } else {
      expectedMessage = fieldError1DefaultMessage
    }

    and: "a MethodArgumentNotValidException wrapping the BindingResult"
    def ex = new MethodArgumentNotValidException(null, br)

    and: "an expected ApiException"
    def apiException = new ApiException(ErrorCode.METHOD_ARGUMENT_NOT_VALID, expectedMessage)

    when:
    def response = exceptionHandler.handleMethodArgumentNotValid(ex,
            null, null, Mock(WebRequest))

    then:
    1 * apiExceptionFactory.fromErrorCodeWithOverrideMessage(ErrorCode.METHOD_ARGUMENT_NOT_VALID,
            expectedMessage) >> apiException
    and:
    response != null
    response.statusCode == HttpStatus.BAD_REQUEST
    response.body instanceof ApiException
    ((ApiException) response.body).message == expectedMessage

    where:
    scenario               | includeObjectError
    "with object error"    | true
    "without object error" | false
  }

  @Unroll("handleHandlerMethodValidationException - #scenario")
  def "test handleHandlerMethodValidationException"() {
    given: "a HandlerMethodValidationException"
    def ex = Mock(HandlerMethodValidationException)
    ex.getParameterValidationResults() >> paramValidationResults(paramMessages)
    ex.getCrossParameterValidationResults() >> getResolvableMessages(crossParamMessages)

    and: "an expected ApiException"
    def apiException = new ApiException(ErrorCode.METHOD_VALIDATION_FAILED, expectedMessage)

    when:
    def response = exceptionHandler.handleHandlerMethodValidationException(ex,
            null, null, Mock(WebRequest))

    then:
    1 * apiExceptionFactory.fromErrorCodeWithOverrideMessage(ErrorCode.METHOD_VALIDATION_FAILED,
            expectedMessage) >> apiException
    and:
    response != null
    response.statusCode == HttpStatus.BAD_REQUEST
    response.body instanceof ApiException
    ((ApiException) response.body).message == expectedMessage

    where:
    scenario                | paramMessages     | crossParamMessages     || expectedMessage
    "with both types"       | ["param1Message"] | ["crossParamMessage1"] || "param1Message, crossParamMessage1"
    "with only cross param" | ["param1Message"] | []                     || "param1Message"
  }

  @Unroll("getHandlerMethodInfo: when #scenario expect #expectedResult")
  def "test getHandlerMethodInfo"() {
    given: "a request"
    def request = Mock(WebRequest)

    switch(scenario) {
      case "handler is handlerMethod":
        def mockHandlerMethod = new HandlerMethod(DummyController, DummyController.getDeclaredMethod("dummyMethod", String))

        request.getAttribute("org.springframework.web.servlet.HandlerMapping.bestMatchingHandler", 0) >> mockHandlerMethod
        break
      case "handler is not handlerMethod":
        request.getAttribute("org.springframework.web.servlet.HandlerMapping.bestMatchingHandler", 0) >> "someOtherHandler"
        break
      case "exception":
        request.getAttribute("org.springframework.web.servlet.HandlerMapping.bestMatchingHandler", 0) >> {
          throw new RuntimeException("test")
        }
        break
    }

    when:
    def result = exceptionHandler.getHandlerMethodInfo(request)

    then:
    result == expectedResult

    where:
    scenario                       || expectedResult
    "handler is handlerMethod"     || "Class#dummyMethod"
    "handler is not handlerMethod" || "unknown-handler"
    "exception"                    || "unknown-handler"
  }

  private def paramValidationResults(List<String> messages) {
    def resolvable = Mock(ParameterValidationResult)
    resolvable.getResolvableErrors() >> getResolvableMessages(messages)

    return [resolvable]
  }

  private def getResolvableMessages(List<String> messages) {
    return messages.collect { msg ->
      Mock(MessageSourceResolvable) {
        getDefaultMessage() >> (msg ?: "")
      }
    }
  }

  private class DummyController {
    String dummyMethod(String param) {
      return param
    }
  }
}