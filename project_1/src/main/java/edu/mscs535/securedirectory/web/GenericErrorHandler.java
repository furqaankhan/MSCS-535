package edu.mscs535.securedirectory.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GenericErrorHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenericErrorHandler.class);

    @ExceptionHandler(DataAccessException.class)
    public String databaseError(DataAccessException exception, Model model) {
        LOGGER.error("A database operation failed", exception);
        model.addAttribute("message", "The request could not be completed. Please try again later.");
        return "error";
    }
}
