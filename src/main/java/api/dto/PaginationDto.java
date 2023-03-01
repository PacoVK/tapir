package api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import core.terraform.CoreEntity;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.util.Collection;
import java.util.List;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaginationDto {

  public PaginationDto(List<? extends CoreEntity> entities) {
    this.entities = entities;
    if (!entities.isEmpty()) {
      this.lastEvaluatedItemId = entities.get(entities.size() - 1).getId();
    }
  }

  public PaginationDto(List<? extends CoreEntity> entities, String lastEvaluatedItemId) {
    this.entities = entities;
    this.lastEvaluatedItemId = lastEvaluatedItemId;
  }

  Collection<? extends CoreEntity> entities;
  String lastEvaluatedItemId;

  public Collection<? extends CoreEntity> getEntities() {
    return entities;
  }

  public void setEntities(Collection<? extends CoreEntity> entities) {
    this.entities = entities;
  }

  public String getLastEvaluatedItemId() {
    return lastEvaluatedItemId;
  }

  public void setLastEvaluatedItemId(String lastEvaluatedItemId) {
    this.lastEvaluatedItemId = lastEvaluatedItemId;
  }
}
