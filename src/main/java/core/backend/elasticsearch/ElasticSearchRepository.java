package core.backend.elasticsearch;

import api.dto.PaginationDto;
import core.backend.SearchService;
import core.exceptions.ModuleNotFoundException;
import core.exceptions.ProviderNotFoundException;
import core.exceptions.ReportNotFoundException;
import core.terraform.Module;
import core.terraform.Provider;
import extensions.core.Report;
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
import org.elasticsearch.client.ResponseException;
import org.elasticsearch.client.RestClient;

@LookupIfProperty(name = "registry.search.backend", stringValue = "elasticsearch")
@ApplicationScoped
public class ElasticSearchRepository extends SearchService {

  static final Logger LOGGER = Logger.getLogger(ElasticSearchRepository.class.getName());
  RestClient restClient;

  public ElasticSearchRepository(RestClient restClient) {
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
            .append("if(versions!=null && !versions.stream()")
            .append(".anyMatch(v-> v.version.equals(params.release.version))){")
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
  public Provider getProviderById(String id) throws Exception {
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
  public void ingestProviderData(Provider provider) throws IOException {
    Request request = new Request(
            HttpMethod.POST,
            String.format("/providers/_update/%s",
                    provider.getId()
            )
    );
    String upsertScript = new StringBuilder("Map versions=ctx._source.versions; ")
            .append("if(versions!=null && !versions.containsKey(params.release)){")
            .append("ctx._source.versions.put(params.release, params.platform)")
            .append("}").toString();
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
    String targetRequestPath = new StringBuilder("/reports/_doc/").append("reports-")
            .append(report.getModuleNamespace())
            .append("-")
            .append(report.getModuleName())
            .append("-")
            .append(report.getProvider())
            .append("-")
            .append(report.getModuleVersion()).toString();
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
    String searchPath = new StringBuilder("/reports/_doc/").append("reports-")
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
  }

  PaginationDto getModules(String query) throws IOException {
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
    return new PaginationDto(results);
  }

  public PaginationDto findModules(String identifier,
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

  public Module getModuleById(String name) throws IOException, ModuleNotFoundException {
    Request request = new Request(
            HttpMethod.GET,
            "/modules/_doc/" + name);
    try {
      return doRequestAndReturnEntity(request).mapTo(Module.class);
    } catch (ResponseException e) {
      throw new ModuleNotFoundException(name, e);
    }
  }

  @Override
  public Module getModuleVersions(Module module) throws IOException, ModuleNotFoundException {
    Request request = new Request(
            HttpMethod.GET,
            String.format("/modules/_doc/%s-%s-%s?_source=versions",
                    module.getNamespace(), module.getName(), module.getProvider())
    );
    try {
      return doRequestAndReturnEntity(request).mapTo(Module.class);
    } catch (ResponseException e) {
      throw new ModuleNotFoundException(module.getId(), e);
    }
  }

  private JsonObject doRequestAndReturnEntity(Request request) throws IOException {
    Response response = restClient.performRequest(request);
    String responseBody = EntityUtils.toString(response.getEntity());
    return new JsonObject(responseBody).getJsonObject("_source");
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
