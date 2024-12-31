package core.backend.elasticsearch;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.Random;

import org.elasticsearch.client.Request;
import org.elasticsearch.client.RestClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import core.backend.AbstractDeployKeysBackendTest;
import core.exceptions.DeployKeyNotFoundException;
import core.tapir.DeployKey;
import core.tapir.DeployKeyScope;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.HttpMethod;

@QuarkusTest
class ElasticSearchDeployKeyRepositoryTest extends AbstractDeployKeysBackendTest {

    ElasticSearchRepository repository;
    RestClient restClient;

    public ElasticSearchDeployKeyRepositoryTest(ElasticSearchRepository repository, RestClient restClient) {
        super(repository);
        this.repository = repository;
        this.restClient = restClient;
    }

    @AfterEach
    void tearDown() throws IOException {
        restClient.performRequest(new Request(HttpMethod.DELETE, "/deploykeys"));
    }

    @Test
    void testGetDeployKeyByValue() throws Exception {

        DeployKey deployKey = new DeployKey(DeployKeyScope.NAMESPACE, "", "double", "", "dcore");
        repository.saveDeployKey(deployKey);

        String deployKeyValue = deployKey.getKey();
        Random random = new Random();
        int end = random.nextInt(deployKeyValue.length() - 1);
        String deployKeyValueSub = deployKeyValue.substring(0, end);

        assertDoesNotThrow(() -> repository.getDeployKeyByValue(deployKeyValue));
        assertThrows(DeployKeyNotFoundException.class, () -> repository.getDeployKeyByValue(deployKeyValueSub));
        assertThrows(DeployKeyNotFoundException.class, () -> repository.getDeployKeyByValue("H0p3Fu11yf@k3k3y"));
    }
}
