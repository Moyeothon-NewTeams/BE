//package com.example.moyeothon.Config;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.client.HttpClientErrorException;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@ControllerAdvice
//public class GlobalExceptionHandler {
//
//    @ExceptionHandler(HttpClientErrorException.class)
//    public ResponseEntity<Map<String, Object>> handleHttpClientError(HttpClientErrorException e) {
//        Map<String, Object> errorResponse = new HashMap<>();
//        errorResponse.put("status", e.getStatusCode().value());
//        errorResponse.put("error", e.getStatusText());
//        errorResponse.put("message", e.getResponseBodyAsString());
//
//        return ResponseEntity.status(e.getStatusCode()).body(errorResponse);
//    }
//
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception e) {
//        Map<String, Object> errorResponse = new HashMap<>();
//        errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
//        errorResponse.put("error", "Internal Server Error");
//        errorResponse.put("message", e.getMessage());
//
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
//    }
//}
//
