package backend;

import api.dto.ModulePaginationDto;
import core.service.backend.SearchService;
import extensions.security.core.SastReport;
import io.quarkus.arc.lookup.LookupIfProperty;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import core.terraform.Module;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.HttpMethod;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@LookupIfProperty(name = "registry.search.backend", stringValue = "elasticsearch")
@ApplicationScoped
public class ElasticSearchService extends SearchService {
  RestClient restClient;

  public ElasticSearchService(RestClient restClient) {
    this.restClient = restClient;
  }

  @Override
  public void ingestModuleData(Module module) throws IOException {
    Request request = new Request(
            HttpMethod.POST,
            String.format("/modules/_update/%s-%s-%s",module.getNamespace(), module.getName(), module.getProvider())
    );
    String payload = JsonObject.of("script", JsonObject.of(
            "source", "List versions=ctx._source.versions; " +
                    "if(versions!=null && !versions.stream().anyMatch(v-> v.version.equals(params.release.version))){" +
                      "ctx._source.versions.add(params.release)" +
                    "}",
            "lang", "painless",
            "params", JsonObject.of("release", module.getVersions().getLast())
    ), "upsert", module).toString();
    request.setJsonEntity(payload);
    restClient.performRequest(request);
  }

  @Override
  public void ingestSecurityScanResult(SastReport sastReport) throws Exception {
    String targetIndexName = String.format("%s-%s-reports",sastReport.getModuleNamespace(), sastReport.getModuleName());
    createIndexIfNotExists(targetIndexName);
    Request request = new Request(
            HttpMethod.POST,
            String.format("/%s/_doc/sast-report-%s-%s",
                    targetIndexName,
                    sastReport.getProvider(),
                    sastReport.getModuleVersion()
            )
    );
    request.setJsonEntity(JsonObject.mapFrom(sastReport).toString());
    restClient.performRequest(request);
  }

  @Override
  public void increaseDownloadCounter(Module module) throws IOException {
    Request request = new Request(
            HttpMethod.POST,
            String.format("/modules/_update/%s-%s-%s",module.getNamespace(), module.getName(), module.getProvider())
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
    String reportIndex = String.format("%s-%s-reports", module.getNamespace(), module.getName());
    String searchPath = new StringBuilder(reportIndex).append("/_doc/").append("sast-report-")
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
    Integer statusCode = restClient.performRequest(new Request(HttpMethod.HEAD, "/modules")).getStatusLine().getStatusCode();
    if(statusCode.equals(200)){
      return;
    }
    Request request = new Request(HttpMethod.PUT, "/modules");
    restClient.performRequest(request);
  }

  public void index(Module module) throws IOException {
    Request request = new Request(
            HttpMethod.PUT,
            "/modules/_doc/" + module.getName());
    request.setJsonEntity(JsonObject.mapFrom(module).toString());
    restClient.performRequest(request);
  }

  ModulePaginationDto getModules(String query) throws IOException {
    Request request = new Request(
            HttpMethod.GET,
            "/modules/_search"
    );
    request.setJsonEntity(query);
    HttpEntity httpEntity = restClient.performRequest(request).getEntity();
    String responseBody = EntityUtils.toString(httpEntity);
    JsonObject queryResult = new JsonObject(responseBody).getJsonObject("hits");
    String totalModuleCount = queryResult.getJsonObject("total").getString("value");
    JsonArray hits = queryResult.getJsonArray("hits");
    List<Module> results = new ArrayList<>(hits.size());
    for (int i = 0; i < hits.size(); i++) {
      JsonObject hit = hits.getJsonObject(i);
      Module module = hit.getJsonObject("_source").mapTo(Module.class);
      results.add(module);
    }
    return new ModulePaginationDto(results, Integer.valueOf(totalModuleCount));
  }

  public ModulePaginationDto getModulesByRange(Integer offset, Integer limit) throws IOException {
    String query = String.format("{\"from\": %s,\"size\": %s}", offset, limit);
    return getModules(query);
  }

  public ModulePaginationDto getModulesByRangeAndTerm(String term, Integer offset, Integer limit) throws IOException {
    String sanitizedTerm = term.replace("/", " ");
    String query = String.format("{\"from\": %s,\"size\": %s, \"query\": {\"query_string\": {\"query\": \"*%s*\", \"fields\": [\"name\", \"namespace\", \"provider\"]}}}", offset, limit, sanitizedTerm);
    return getModules(query);
  }

  public Module getModuleByName(String name) throws IOException {
    Request request = new Request(
            HttpMethod.GET,
            "/modules/_doc/" + name);
    Response response = restClient.performRequest(request);
    String responseBody = EntityUtils.toString(response.getEntity());
    JsonObject json = new JsonObject(responseBody);
    return json.getJsonObject("_source").mapTo(Module.class);
  }

  @Override
  public Module getModuleVersions(Module module) throws Exception {
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
    if(!statusCode.equals(200)){
      Request request = new Request(HttpMethod.PUT, String.format("/%s", indexName));
      restClient.performRequest(request);
    }
  }
}
