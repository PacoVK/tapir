package core.backend.elasticsearch;

import api.dto.PaginationDto;
import core.backend.TapirRepository;
import core.exceptions.DeployKeyNotFoundException;
import core.exceptions.ModuleNotFoundException;
import core.exceptions.ProviderNotFoundException;
import core.exceptions.ReportNotFoundException;
import core.tapir.CoreEntity;
import core.tapir.DeployKey;
import core.terraform.Module;
import core.terraform.Provider;
import extensions.core.Report;
import io.quarkus.arc.lookup.LookupIfProperty;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.HttpMethod;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseException;
import org.elasticsearch.client.RestClient;

@LookupIfProperty(name = "registry.search.backend", stringValue = "elasticsearch")
@ApplicationScoped
public class ElasticSearchRepository extends TapirRepository {

  static final Logger LOGGER = Logger.getLogger(ElasticSearchRepository.class.getName());
  RestClient restClient;

  public ElasticSearchRepository(RestClient restClient) {
    this.restClient = restClient;
  }

  @Override
  public void ingestModuleData(Module module) throws IOException {
    Request request = new Request(
            HttpMethod.POST,
            String.format("/modules/_update/%s-%s-%s?refresh=wait_for",
                    module.getNamespace(),
                    module.getName(),
                    module.getProvider()
            )
    );
    String upsertScript = "List versions=ctx._source.versions; "
            + "if(versions!=null && !versions.stream()"
            + ".anyMatch(v-> v.version.equals(params.release.version))){"
            + "ctx._source.versions.add(params.release)"
            + "}";
    String payload = JsonObject.of("script", JsonObject.of(
            "source", upsertScript,
            "lang", "painless",
            "params", JsonObject.of("release", module.getVersions().first())
    ), "upsert", module).toString();
    request.setJsonEntity(payload);
    restClient.performRequest(request);
  }

  @Override
  public Provider getProvider(String id) throws Exception {
    Request request = new Request(
            HttpMethod.GET,
            "/providers/_doc/" + id);
    try {
      return doRequestAndReturnEntity(request).mapTo(Provider.class);
    } catch (ResponseException e) {
      throw new ProviderNotFoundException(id, e);
    }
  }

  @Override
  public DeployKey getDeployKeyById(String id) throws DeployKeyNotFoundException {
    Request request = new Request(
        HttpMethod.GET,
        "/deploykeys/_doc/" + id);
    try {
      return doRequestAndReturnEntity(request).mapTo(DeployKey.class);
    } catch (IOException e) {
      throw new DeployKeyNotFoundException(id, e);
    }
  }

  @Override
  public DeployKey getDeployKeyByValue(String value) throws DeployKeyNotFoundException {
      try {
        Collection<DeployKey> deployKeys = (Collection<DeployKey>) findDeployKeys("", 1, value).getEntities();
        if (deployKeys == null || deployKeys.size() != 1) {
          throw new DeployKeyNotFoundException("Could not find matching key");
        }
        return deployKeys.stream().findFirst().orElseThrow(() -> new DeployKeyNotFoundException("Could not find matching key"));
      } catch (Exception e) {
        throw new DeployKeyNotFoundException("Could not find matching key");
      }
  }

  @Override
  public void saveDeployKey(DeployKey deployKey) throws Exception {
    Request request = new Request(
        HttpMethod.POST,
        String.format("/deploykeys/_doc/%s?refresh=wait_for",
            deployKey.getId()
        )
    );
    request.setJsonEntity(JsonObject.mapFrom(deployKey).toString());
    restClient.performRequest(request);
  }

  @Override
  public void updateDeployKey(DeployKey deployKey) throws Exception {
    String targetRequestPath = "/deploykeys/_doc/"
        + deployKey.getId();
    Request request = new Request(
        HttpMethod.PUT,
        targetRequestPath
    );
    request.setJsonEntity(JsonObject.mapFrom(deployKey).toString());
    restClient.performRequest(request);
  }

  @Override
  public void deleteDeployKey(String id) throws Exception {
    String targetRequestPath = "/deploykeys/_doc/"
        + id;
    Request request = new Request(
        HttpMethod.DELETE,
        targetRequestPath
    );
    restClient.performRequest(request);
  }

  @Override
  public void ingestProviderData(Provider provider) throws IOException {
    Request request = new Request(
            HttpMethod.POST,
            String.format("/providers/_update/%s?refresh=wait_for",
                    provider.getId()
            )
    );
    String upsertScript = "Map versions=ctx._source.versions; "
            + "if(versions!=null && !versions.containsKey(params.release)){"
            + "ctx._source.versions.put(params.release, params.platform)"
            + "}";
    String payload = JsonObject.of("script", JsonObject.of(
            "source", upsertScript,
            "lang", "painless",
            "params", JsonObject.of(
                    "release", provider.getVersions().firstKey().getVersion(),
                    "platform", provider.getVersions().firstEntry().getValue()
            )
    ), "upsert", provider).toString();
    request.setJsonEntity(payload);
    restClient.performRequest(request);
  }

  @Override
  public void ingestSecurityScanResult(Report report) throws IOException {
    String targetRequestPath = "/reports/_doc/reports-"
            + report.getModuleNamespace()
            + "-"
            + report.getModuleName()
            + "-"
            + report.getProvider()
            + "-"
            + report.getModuleVersion();
    Request request = new Request(
            HttpMethod.POST,
            targetRequestPath
    );
    request.setJsonEntity(JsonObject.mapFrom(report).toString());
    restClient.performRequest(request);
  }

  @Override
  public Module increaseDownloadCounter(Module module) throws IOException {
    Request request = new Request(
            HttpMethod.POST,
            String.format("/modules/_update/%s-%s-%s",
                    module.getNamespace(),
                    module.getName(),
                    module.getProvider()
            )
    );
    String payload = JsonObject.of("script", JsonObject.of(
            "source", "ctx._source.downloads += 1",
            "lang", "painless"
    )).toString();
    module.setDownloads(module.getDownloads() + 1);
    request.setJsonEntity(payload);
    restClient.performRequest(request);
    return module;
  }

  @Override
  public Report getReportByModuleVersion(Module module)
          throws IOException, ReportNotFoundException {
    String searchPath = "/reports/_doc/reports-"
            + module.getNamespace()
            + "-"
            + module.getName()
            + "-"
            + module.getProvider()
            + "-"
            + module.getCurrentVersion();
    Request request = new Request(
            HttpMethod.GET,
            searchPath);
    try {
      return doRequestAndReturnEntity(request).mapTo(Report.class);
    } catch (ResponseException e) {
      throw new ReportNotFoundException(module.getId(), e);
    }
  }

  public void bootstrap() throws IOException {
    createIndexIfNotExists("modules");
    createIndexIfNotExists("providers");
    createIndexIfNotExists("reports");
    createIndexIfNotExists("deploykeys");
  }

  Boolean indexHasDocuments(String indexName) throws IOException {
    Request countRequest = new Request(
        HttpMethod.GET,
        String.format("/%s/_count", indexName)
    );
    HttpEntity countEntity = restClient.performRequest(countRequest).getEntity();
    String countResponseBody = EntityUtils.toString(countEntity);
    Integer count = (Integer) new JsonObject(countResponseBody).getValue("count");
    return count > 0;
  }

  PaginationDto getEntities(String indexName, String query, Class<? extends CoreEntity> type)
          throws IOException {
    if (!indexHasDocuments(indexName)) {
      return new PaginationDto(Collections.EMPTY_LIST);
    }
    Request request = new Request(
            HttpMethod.GET,
            String.format("/%s/_search", indexName)
    );
    request.setJsonEntity(query);
    HttpEntity httpEntity = restClient.performRequest(request).getEntity();
    String responseBody = EntityUtils.toString(httpEntity);
    JsonObject queryResult = new JsonObject(responseBody).getJsonObject("hits");
    JsonArray hits = queryResult.getJsonArray("hits");
    List<? extends CoreEntity> results = hits.stream()
            .map(hit -> ((JsonObject) hit).getJsonObject("_source").mapTo(type))
            .collect(Collectors.toList());
    return new PaginationDto(results);
  }

  private String buildPaginationSearchQuery(String identifier,
                                            Integer limit,
                                            String term,
                                            String fields) {
    StringBuilder queryBuilder = new StringBuilder("{").append(
            String.format("\"sort\": [ {\"_id\": \"asc\"} ], \"size\": %s", limit)
    );
    if (!identifier.isEmpty()) {
      queryBuilder.append(String.format(",\"search_after\": [\"%s\"]", identifier));
    }
    if (!term.isEmpty()) {
      queryBuilder.append(
              String.format(
                      ", \"query\": {\"query_string\": {\"query\": \"*%s*\", \"fields\": [%s]}}",
                      term, fields
              )
      );
    }
    queryBuilder.append("}");
    return queryBuilder.toString();
  }

  public PaginationDto findModules(String identifier,
                                   Integer limit,
                                   String term) throws IOException {
    String fields = "\"name\", \"namespace\", \"provider\"";
    String query = buildPaginationSearchQuery(identifier, limit, term, fields);
    return getEntities("modules", query, Module.class);
  }

  @Override
  public PaginationDto findProviders(String identifier, Integer limit, String term)
          throws Exception {
    String fields = "\"namespace\", \"type\"";
    String query = buildPaginationSearchQuery(identifier, limit, term, fields);
    return getEntities("providers", query, Provider.class);
  }

  @Override
  public PaginationDto findDeployKeys(String identifier, Integer limit, String term)
      throws Exception {
    String fields = "\"id\", \"key\"";
    String query = buildPaginationSearchQuery(identifier, limit, term, fields);
    return getEntities("deploykeys", query, DeployKey.class);
  }

  public Module getModule(String id) throws IOException, ModuleNotFoundException {
    Request request = new Request(
            HttpMethod.GET,
            String.format("/modules/_doc/%s", id));
    try {
      return doRequestAndReturnEntity(request).mapTo(Module.class);
    } catch (ResponseException e) {
      throw new ModuleNotFoundException(id, e);
    }
  }

  private JsonObject doRequestAndReturnEntity(Request request) throws IOException {
    Response response = restClient.performRequest(request);
    String responseBody = EntityUtils.toString(response.getEntity());
    return new JsonObject(responseBody).getJsonObject("_source");
  }

  Integer  createIndexIfNotExists(String indexName) throws IOException {
    Integer statusCode = restClient.performRequest(
            new Request(HttpMethod.HEAD, String.format("/%s", indexName))
    ).getStatusLine().getStatusCode();
    if (!statusCode.equals(200)) {
      Request request = new Request(HttpMethod.PUT, String.format("/%s", indexName));
      statusCode = restClient.performRequest(request).getStatusLine().getStatusCode();
      LOGGER.info(String.format("Created index [%s]", indexName));
    }
    return statusCode;
  }
}
