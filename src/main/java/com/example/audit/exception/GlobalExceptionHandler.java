    package com.example.audit.exception;

    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.validation.ConstraintViolationException;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.http.converter.HttpMessageNotReadableException;
    import org.springframework.validation.FieldError;
    import org.springframework.web.bind.MethodArgumentNotValidException;
    import org.springframework.web.bind.annotation.ExceptionHandler;
    import org.springframework.web.bind.annotation.RestControllerAdvice;
    import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

    import java.time.Instant;
    import java.util.stream.Collectors;

    /**
     * Standardizes validation and request parsing failures into a consistent JSON body.
     */
    @RestControllerAdvice
    public class GlobalExceptionHandler {

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(
                MethodArgumentNotValidException ex,
                HttpServletRequest request
        ) {
            String message = ex.getBindingResult().getFieldErrors().stream()
                    .map(this::formatFieldError)
                    .collect(Collectors.joining("; "));

            if (message.isBlank()) {
                message = ex.getMessage();
            }

            return buildResponse(HttpStatus.BAD_REQUEST, message, request);
        }

        @ExceptionHandler(ConstraintViolationException.class)
        public ResponseEntity<ApiErrorResponse> handleConstraintViolation(
                ConstraintViolationException ex,
                HttpServletRequest request
        ) {
            String message = ex.getConstraintViolations().stream()
                    .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                    .collect(Collectors.joining("; "));
            return buildResponse(HttpStatus.BAD_REQUEST, message, request);
        }

        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ApiErrorResponse> handleUnreadableBody(
                HttpMessageNotReadableException ex,
                HttpServletRequest request
        ) {
            return buildResponse(HttpStatus.BAD_REQUEST, "Malformed JSON request body.", request);
        }

        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        public ResponseEntity<ApiErrorResponse> handleTypeMismatch(
                MethodArgumentTypeMismatchException ex,
                HttpServletRequest request
        ) {
            String message = String.format(
                    "Parameter '%s' has invalid value '%s'.",
                    ex.getName(),
                    ex.getValue()
            );
            return buildResponse(HttpStatus.BAD_REQUEST, message, request);
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ApiErrorResponse> handleIllegalArgument(
                IllegalArgumentException ex,
                HttpServletRequest request
        ) {
            return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
        }

        private String formatFieldError(FieldError fieldError) {
            return fieldError.getField() + ": " + fieldError.getDefaultMessage();
        }

        private ResponseEntity<ApiErrorResponse> buildResponse(
                HttpStatus status,
                String message,
                HttpServletRequest request
        ) {
            ApiErrorResponse body = new ApiErrorResponse(
                    Instant.now(),
                    status.value(),
                    status.getReasonPhrase(),
                    message,
                    request.getRequestURI()
            );
            return ResponseEntity.status(status).body(body);
        }
    }
