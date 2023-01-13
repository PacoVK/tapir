package extensions.docs.generator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.backend.SearchService;
import core.upload.FormData;
import extensions.cli.CliCommandProcessor;
import extensions.docs.report.TerraformDocumentation;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.common.annotation.Blocking;
import io.vertx.mutiny.core.eventbus.EventBus;
import java.io.File;
import java.util.logging.Logger;
import javax.enterprise.inject.Instance;

public class DocumentationGenerator {

  static final Logger LOGGER = Logger.getLogger(DocumentationGenerator.class.getName());
  EventBus eventBus;
  CliCommandProcessor commandProcessor;
  ObjectMapper mapper = new ObjectMapper();

  SearchService searchService;

  public DocumentationGenerator(EventBus eventBus, CliCommandProcessor commandProcessor, Instance<SearchService> searchServiceInstance) {
    this.eventBus = eventBus;
    this.commandProcessor = commandProcessor;
    this.searchService = searchServiceInstance.get();
  }

  @Blocking
  @ConsumeEvent("module.documentation.generate")
  public TerraformDocumentation generateDocs(FormData archive) throws Exception {
    LOGGER.info(String.format("Generating docs for module %s, version %s",
            archive.getModule().getName(),
            archive.getModule().getCurrentVersion()
    ));
    File workingDirectory = archive.getCompressedModule().getParentFile();
    String output = commandProcessor.runCommand(
            workingDirectory,
            "sh", "-c", "terraform-docs json .");
    TerraformDocumentation tfDocumentation;
    try {
      tfDocumentation = mapper.readValue(output, TerraformDocumentation.class);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    return tfDocumentation;
  }
}
