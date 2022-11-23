package backend;

import core.service.backend.SearchService;
import core.service.upload.FormData;
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
import java.util.Collection;
import java.util.List;

@LookupIfProperty(name = "registry.search.backend", stringValue = "elasticsearch")
@ApplicationScoped
public class ElasticSearchService extends SearchService {
  RestClient restClient;

  public ElasticSearchService(RestClient restClient) {
    this.restClient = restClient;
  }

  @Override
  public void ingestModuleMetaData(Module module) throws IOException {
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
            "params", JsonObject.of("release", module.getVersions().get(0))
    ), "upsert", module).toString();
    request.setJsonEntity(payload);
    restClient.performRequest(request);
  }

  @Override
  public void updateSecurityScanResult(FormData archive) throws Exception {
    Module module = archive.getModule();
    Request request = new Request(
            HttpMethod.POST,
            String.format("/modules/_update/%s-%s-%s",module.getNamespace(), module.getName(), module.getProvider())
    );
    String payload = JsonObject.of("doc", JsonObject.of(
            "reports", JsonObject.of(
                    "security", module.getScanResults()
            )
    )).toString();
    request.setJsonEntity(payload);
    restClient.performRequest(request);
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

  public Collection<Module> getAllModules() throws IOException {
    Request request = new Request(
            HttpMethod.GET,
            "/modules/_search"
    );
    HttpEntity httpEntity = restClient.performRequest(request).getEntity();
    String responseBody = EntityUtils.toString(httpEntity);
    JsonObject json = new JsonObject(responseBody);
    JsonArray hits = json.getJsonObject("hits").getJsonArray("hits");
    List<Module> results = new ArrayList<>(hits.size());
    for (int i = 0; i < hits.size(); i++) {
      JsonObject hit = hits.getJsonObject(i);
      Module module = hit.getJsonObject("_source").mapTo(Module.class);
      results.add(module);
    }
    return results;
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
}
//versions als list speichern
