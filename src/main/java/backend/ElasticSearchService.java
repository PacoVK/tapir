package backend;

import api.dto.ModulePagination;
import core.service.backend.SearchService;
import core.terraform.Module;
import extensions.core.SastReport;
import io.quarkus.arc.lookup.LookupIfProperty;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.HttpMethod;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;

@LookupIfProperty(name = "registry.search.backend", stringValue = "elasticsearch")
@ApplicationScoped
public class ElasticSearchService extends SearchService {

  static final Logger LOGGER = Logger.getLogger(ElasticSearchService.class.getName());
  RestClient restClient;

  public ElasticSearchService(RestClient restClient) {
    this.restClient = restClient;
  }

  @Override
  public void ingestModuleData(Module module) throws IOException {
    Request request = new Request(
            HttpMethod.POST,
            String.format("/modules/_update/%s-%s-%s",
                    module.getNamespace(),
                    module.getName(),
                    module.getProvider()
            )
    );
    String upsertScript = new StringBuilder("List versions=ctx._source.versions; ")
            .append("if(versions!=null && !versions.stream().anyMatch(v-> v.version.equals(params.release.version))){")
            .append("ctx._source.versions.add(params.release)")
            .append("}").toString();
    String payload = JsonObject.of("script", JsonObject.of(
            "source", upsertScript,
            "lang", "painless",
            "params", JsonObject.of("release", module.getVersions().first())
    ), "upsert", module).toString();
    request.setJsonEntity(payload);
    restClient.performRequest(request);
  }

  @Override
  public void ingestSecurityScanResult(SastReport sastReport) throws IOException {
    String targetRequestPath = new StringBuilder("/reports/_doc/").append("sast-")
            .append(sastReport.getModuleNamespace())
            .append("-")
            .append(sastReport.getModuleName())
            .append("-")
            .append(sastReport.getProvider())
            .append("-")
            .append(sastReport.getModuleVersion()).toString();
    Request request = new Request(
            HttpMethod.POST,
            targetRequestPath
    );
    request.setJsonEntity(JsonObject.mapFrom(sastReport).toString());
    restClient.performRequest(request);
  }

  @Override
  public void increaseDownloadCounter(Module module) throws IOException {
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
    request.setJsonEntity(payload);
    restClient.performRequest(request);
  }

  @Override
  public SastReport getReportByModuleVersion(Module module) throws IOException {
    String searchPath = new StringBuilder("/reports/_doc/").append("sast-")
            .append(module.getNamespace())
            .append("-")
            .append(module.getName())
            .append("-")
            .append(module.getProvider())
            .append("-")
            .append(module.getCurrentVersion()).toString();
    Request request = new Request(
            HttpMethod.GET,
            searchPath);
    Response response = restClient.performRequest(request);
    String responseBody = EntityUtils.toString(response.getEntity());
    JsonObject json = new JsonObject(responseBody);
    return json.getJsonObject("_source").mapTo(SastReport.class);
  }

  public void bootstrap() throws IOException {
    createIndexIfNotExists("modules");
    createIndexIfNotExists("reports");
  }

  ModulePagination getModules(String query) throws IOException {
    Request request = new Request(
            HttpMethod.GET,
            "/modules/_search"
    );
    request.setJsonEntity(query);
    HttpEntity httpEntity = restClient.performRequest(request).getEntity();
    String responseBody = EntityUtils.toString(httpEntity);
    JsonObject queryResult = new JsonObject(responseBody).getJsonObject("hits");
    JsonArray hits = queryResult.getJsonArray("hits");
    List<Module> results = hits.stream()
            .map(hit -> ((JsonObject) hit).getJsonObject("_source").mapTo(Module.class))
            .collect(Collectors.toList());
    return new ModulePagination(results);
  }

  public ModulePagination findModules(String identifier,
                                      Integer limit,
                                      String term) throws IOException {
    StringBuilder queryBuilder = new StringBuilder("{").append(
            String.format("\"sort\": [ {\"_id\": \"asc\"} ], \"size\": %s", limit)
    );
    if (!identifier.isEmpty()) {
      queryBuilder.append(String.format(",\"search_after\": [\"%s\"]", identifier));
    }
    if (!term.isEmpty()) {
      queryBuilder.append(
              String.format(", \"query\": {\"query_string\": {\"query\": \"*%s*\", \"fields\": [\"name\", \"namespace\", \"provider\"]}}", term)
      );
    }
    queryBuilder.append("}");
    String query = queryBuilder.toString();
    return getModules(query);
  }

  public Module getModuleById(String name) throws IOException {
    Request request = new Request(
            HttpMethod.GET,
            "/modules/_doc/" + name);
    Response response = restClient.performRequest(request);
    String responseBody = EntityUtils.toString(response.getEntity());
    JsonObject json = new JsonObject(responseBody);
    return json.getJsonObject("_source").mapTo(Module.class);
  }

  @Override
  public Module getModuleVersions(Module module) throws IOException {
    Request request = new Request(
            HttpMethod.GET,
            String.format("/modules/_doc/%s-%s-%s?_source=versions",
                    module.getNamespace(), module.getName(), module.getProvider())
    );
    Response response = restClient.performRequest(request);
    String responseBody = EntityUtils.toString(response.getEntity());
    JsonObject json = new JsonObject(responseBody);
    return json.getJsonObject("_source").mapTo(Module.class);
  }

  private void  createIndexIfNotExists(String indexName) throws IOException {
    Integer statusCode = restClient.performRequest(
            new Request(HttpMethod.HEAD, String.format("/%s", indexName))
    ).getStatusLine().getStatusCode();
    if (!statusCode.equals(200)) {
      Request request = new Request(HttpMethod.PUT, String.format("/%s", indexName));
      restClient.performRequest(request);
      LOGGER.info(String.format("Created index [%s]", indexName));
    }
  }
}
