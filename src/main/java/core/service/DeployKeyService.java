package core.service;

import api.dto.PaginationDto;
import core.backend.TapirRepository;
import core.exceptions.DeployKeyNotFoundException;
import core.tapir.DeployKey;
import core.tapir.DeployKeyScope;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import java.time.Instant;
import org.apache.commons.lang3.RandomStringUtils;

@ApplicationScoped
public class DeployKeyService {

  TapirRepository tapirRepository;

  public DeployKeyService(Instance<TapirRepository> searchServiceInstance) {
    this.tapirRepository = searchServiceInstance.get();
  }

  public DeployKey createModuleDeployKey(DeployKeyScope scope, String source, String namespace, String name, String provider) throws Exception {
    DeployKey deployKey = new DeployKey(scope, source, namespace, name, provider, generateKey());
    deployKey.setLastModifiedAt(Instant.now());
    tapirRepository.saveDeployKey(deployKey);
    return deployKey;
  }

  public DeployKey createProviderDeployKey(DeployKeyScope scope, String source, String namespace, String type) throws Exception {
    DeployKey deployKey = new DeployKey(scope, source, namespace, type, generateKey());
    deployKey.setLastModifiedAt(Instant.now());
    tapirRepository.saveDeployKey(deployKey);
    return deployKey;
  }

  public DeployKey updateDeployKey(String id) throws Exception {
    DeployKey deployKey = tapirRepository.getDeployKeyById(id);
    deployKey.setKey(generateKey());
    deployKey.setLastModifiedAt(Instant.now());
    tapirRepository.updateDeployKey(deployKey);
    return deployKey;
  }

  public DeployKey getDeployKey(String id) throws DeployKeyNotFoundException {
    return tapirRepository.getDeployKeyById(id);
  }

  public DeployKey getDeployKeyByValue(String value) throws DeployKeyNotFoundException {
    return tapirRepository.getDeployKeyByValue(value);
  }

  public void deleteDeployKey(String id) throws Exception {
    DeployKey deployKey = tapirRepository.getDeployKeyById(id);
    tapirRepository.deleteDeployKey(deployKey.getId());
  }

  public PaginationDto listDeployKeys(
      String lastEvaluatedElementKey,
      Integer limit,
      String query
  ) throws Exception {
    return tapirRepository.findDeployKeys(
        lastEvaluatedElementKey,
        limit,
        query
    );
  }

  private String generateKey() {
    return RandomStringUtils.randomAlphanumeric(24);
  }
}
