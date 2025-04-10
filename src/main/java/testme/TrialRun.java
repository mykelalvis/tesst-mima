package testme;

import static java.util.Objects.requireNonNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap;

import com.google.inject.AbstractModule;
import eu.maveniverse.maven.mima.context.internal.MavenUserHomeImpl;
import eu.maveniverse.maven.mima.runtime.shared.PreBoot;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.resolution.ArtifactDescriptorException;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.VersionResolutionException;

import eu.maveniverse.maven.mima.context.Context;
import eu.maveniverse.maven.mima.context.ContextOverrides;
import eu.maveniverse.maven.mima.context.Runtime;
import eu.maveniverse.maven.mima.context.Runtimes;
import eu.maveniverse.maven.mima.extensions.mmr.MavenModelReader;
import eu.maveniverse.maven.mima.extensions.mmr.ModelRequest;
import eu.maveniverse.maven.mima.extensions.mmr.ModelResponse;

import javax.inject.Named;

@javax.inject.Singleton
@javax.inject.Named
public class TrialRun {
  //  @javax.inject.Inject
//  public TrialRun() {
//    this.runtime = Runtimes.INSTANCE.getRuntime();
//
//  }

  @Named
  public static class PreBootInstaller extends AbstractModule {
    @Override
    protected void configure() {
      bind(PreBoot.class).toInstance(new PreBoot(ContextOverrides.create().withUserSettings(true).build(), new MavenUserHomeImpl(Paths.get(System.getProperty("user.home"))), null, Paths.get(System.getProperty("user.dir"))));
    }
  }

  public String model(ContextOverrides overrides, String artifactStr)
      throws VersionResolutionException, ArtifactResolutionException, ArtifactDescriptorException, IOException {
    requireNonNull(artifactStr);
    Runtime runtime = Runtimes.INSTANCE.getRuntime();

    try (Context context = runtime.create(overrides)) {
      MavenModelReader mmr = new MavenModelReader(context);
      ModelResponse response = mmr.readModel(ModelRequest.builder().setArtifact(new DefaultArtifact(artifactStr))
          .setRequestContext("classpath-demo").build());
      Model model = response.getEffectiveModel();
      try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
        String encoding = model.getModelEncoding();
        if (encoding == null || encoding.length() <= 0) {
          encoding = "UTF-8";
        }

        try (Writer out = new OutputStreamWriter(outputStream, encoding)) {
          new MavenXpp3Writer().write(out, model);
        }
        return outputStream.toString(encoding);
      }
    }
  }

  public static void main(String[] args) throws Exception {
    ClassLoader cl = TrialRun.class.getClassLoader();
    com.google.inject.Injector inj = com.google.inject.Guice.createInjector( //
            new org.eclipse.sisu.wire.WireModule( // auto-wires unresolved dependencies
                    new org.eclipse.sisu.space.SpaceModule( // scans and binds @Named components
                            new org.eclipse.sisu.space.URLClassSpace(cl) //
                            , org.eclipse.sisu.space.BeanScanning.ON //
                            , false) //
            ) //
    );

    System.out.println(inj.getInstance(TrialRun.class).model(ContextOverrides.create().withUserSettings(true).build(), "junit:junit:4.13.2"));
  }
}
