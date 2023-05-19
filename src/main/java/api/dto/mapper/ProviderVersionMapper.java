package api.dto.mapper;

import api.dto.ProviderVersionsDto;
import core.terraform.Provider;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.LinkedList;
import java.util.List;

@ApplicationScoped
public class ProviderVersionMapper {

  public List<ProviderVersionsDto> toDto(Provider provider) {
    List<ProviderVersionsDto> versionsDtos = new LinkedList<>();
    provider.getVersions().forEach(
            (artifactVersion, platforms) ->
                    versionsDtos.add(
                            new ProviderVersionsDto(artifactVersion.getVersion(), platforms)
            )
    );
    return versionsDtos;
  }
}
