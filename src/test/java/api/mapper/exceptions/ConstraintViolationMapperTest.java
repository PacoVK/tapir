package api.mapper.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import api.mapper.exceptions.response.ErrorResponse;
import core.terraform.Module;
import java.util.List;
import java.util.Set;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

class ConstraintViolationMapperTest {

  ConstraintViolationMapper mapper = new ConstraintViolationMapper();

  @Test
  public void toResponse(){
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    Module module = new Module(null, "foo", "0.0.1");
    Set<ConstraintViolation<Module>> violations = validator.validate(module);
    Response badRequestResponse = mapper.toResponse(new ConstraintViolationException("oops", violations));
    List<ErrorResponse.ErrorMessage> errors = ((ErrorResponse) badRequestResponse.getEntity()).getErrors();
    assertEquals(badRequestResponse.getStatus(), 400);
    assertEquals(errors.size(), 1);
    assertEquals(errors.get(0).getPath(), "namespace");
    assertEquals(errors.get(0).getMessage(), "Module's namespace is required");
  }
}