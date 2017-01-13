package ru.tersoft.ticketsale.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.tersoft.ticketsale.entity.ErrorResponse;

/**
 * Project ticketsale.
 * Created by ivyanni on 13.01.2017.
 */
public class ResponseFactory {
    public static ResponseEntity<?> createErrorResponse(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>
                (new ErrorResponse(Long.parseLong(httpStatus.toString()), message), httpStatus);
    }

    public static ResponseEntity<?> createResponse(Object object) {
        return new ResponseEntity<>(object, HttpStatus.OK);
    }

    public static ResponseEntity<?> createResponse() {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
