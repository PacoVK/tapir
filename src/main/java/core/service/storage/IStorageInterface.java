package core.service.storage;

import core.service.upload.FormData;
import core.terraform.Module;

import javax.ws.rs.core.Response;

public interface IStorageInterface {
  Response.ResponseBuilder uploadModule(FormData archive);
}
