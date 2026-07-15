package com.caiohenrique.demo_park_api.web.exception;

import com.caiohenrique.demo_park_api.exception.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApiExceptionHandlerTest {

    @Mock
    private HttpServletRequest request;

    private ApiExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new ApiExceptionHandler();
        when(request.getRequestURI()).thenReturn("/api/v1/test");
        when(request.getMethod()).thenReturn("GET");
    }

    @Test
    void accessDeniedException_shouldReturnForbidden() {
        AccessDeniedException exception = new AccessDeniedException("Acesso negado");

        ResponseEntity<ErrorMessage> response = handler.accessDeniedException(exception, request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(403, response.getBody().getStatus());
        assertEquals("Acesso negado", response.getBody().getMessage());
    }

    @Test
    void methodArgumentNotValidException_shouldReturnUnprocessableEntity() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError fieldError = new FieldError("object", "username", "Email inválido");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<ErrorMessage> response = handler.methodArgumentNotValidException(exception, request, bindingResult);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(422, response.getBody().getStatus());
        assertEquals("Erro de validação nos dados informados.", response.getBody().getMessage());
        assertNotNull(response.getBody().getErros());
        assertTrue(response.getBody().getErros().containsKey("username"));
        assertEquals("Email inválido", response.getBody().getErros().get("username"));
    }

    @Test
    void uniqueViolationException_withUserNameViolation_shouldReturnConflict() {
        RuntimeException exception = new UserNameUniqueViolationException("Username { test } já cadastrado");

        ResponseEntity<ErrorMessage> response = handler.uniqueViolationException(exception, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(409, response.getBody().getStatus());
    }

    @Test
    void uniqueViolationException_withCpfViolation_shouldReturnConflict() {
        RuntimeException exception = new CpfUniqueViolationException("CPF já cadastrado");

        ResponseEntity<ErrorMessage> response = handler.uniqueViolationException(exception, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void uniqueViolationException_withCodeViolation_shouldReturnConflict() {
        RuntimeException exception = new CodeUniqueViolationException("Código já cadastrado");

        ResponseEntity<ErrorMessage> response = handler.uniqueViolationException(exception, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void uniqueViolationException_withActiveSessionViolation_shouldReturnConflict() {
        RuntimeException exception = new ActiveParkingSessionAlreadyExistsException("Sessão ativa já existe");

        ResponseEntity<ErrorMessage> response = handler.uniqueViolationException(exception, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void entityNotFoundException_shouldReturnNotFound() {
        RuntimeException exception = new EntityNotFoundException("Recurso não encontrado");

        ResponseEntity<ErrorMessage> response = handler.entityNotFoundException(exception, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Recurso não encontrado", response.getBody().getMessage());
    }

    @Test
    void passwordInvalidException_shouldReturnBadRequest() {
        RuntimeException exception = new PasswordInvalidException("Senha inválida");

        ResponseEntity<ErrorMessage> response = handler.passwordInvalidException(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Senha inválida", response.getBody().getMessage());
    }

    @Test
    void handleInvalidEnum_shouldReturnBadRequest() {
        HttpMessageNotReadableException exception = mock(HttpMessageNotReadableException.class);
        com.fasterxml.jackson.databind.exc.InvalidFormatException cause =
                mock(com.fasterxml.jackson.databind.exc.InvalidFormatException.class);

        when(exception.getCause()).thenReturn(cause);
        when(cause.getTargetType()).thenReturn((Class) com.caiohenrique.demo_park_api.enums.SpotStatus.class);
        when(cause.getValue()).thenReturn("INVALIDO");

        ResponseEntity<ErrorMessage> response = handler.handleInvalidEnum(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Valor inválido"));
        assertTrue(response.getBody().getMessage().contains("INVALIDO"));
    }

    @Test
    void handleInvalidEnum_withNonEnumException_shouldReturnGenericMessage() {
        HttpMessageNotReadableException exception = mock(HttpMessageNotReadableException.class);
        when(exception.getCause()).thenReturn(new RuntimeException("Some other error"));

        ResponseEntity<ErrorMessage> response = handler.handleInvalidEnum(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Erro na requisição.", response.getBody().getMessage());
    }

    @Test
    void internalServerError_shouldReturnInternalServerError() {
        Exception exception = new RuntimeException("Erro interno");

        ResponseEntity<ErrorMessage> response = handler.internalServerError(exception, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("Erro interno no servidor. Tente novamente mais tarde.", response.getBody().getMessage());
    }
}
