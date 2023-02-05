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
      this.lastEvaluatedItem = entities.get(entities.size() - 1);
    }
  }

  Collection<? extends CoreEntity> entities;
  CoreEntity lastEvaluatedItem;

  public Collection<? extends CoreEntity> getEntities() {
    return entities;
  }

  public void setEntities(Collection<? extends CoreEntity> entities) {
    this.entities = entities;
  }

  public CoreEntity getLastEvaluatedItem() {
    return lastEvaluatedItem;
  }

  public void setLastEvaluatedItem(CoreEntity lastEvaluatedItem) {
    this.lastEvaluatedItem = lastEvaluatedItem;
  }
}
