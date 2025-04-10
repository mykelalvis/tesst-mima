package testme;

@javax.inject.Singleton
@javax.inject.Named
public class TrialRun {
  private eu.maveniverse.maven.mima.context.Runtime runtime;

  @javax.inject.Inject
  public TrialRun(eu.maveniverse.maven.mima.context.Runtime runtime) {
    this.runtime = runtime;
    // TODO Auto-generated constructor stub
  }

  public static void main(String[] args) {
    ClassLoader cl = TrialRun.class.getClassLoader();
    com.google.inject.Injector inj = com.google.inject.Guice.createInjector( //
        new org.eclipse.sisu.wire.WireModule( // auto-wires unresolved dependencies
            new org.eclipse.sisu.space.SpaceModule( // scans and binds @Named components
                new org.eclipse.sisu.space.URLClassSpace(cl) //
                , org.eclipse.sisu.space.BeanScanning.ON //
                , false) //
        ) //
    );

    TrialRun xx = inj.getInstance(TrialRun.class);

  }
}
