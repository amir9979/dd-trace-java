package datadog.trace.agent.tooling.context;

/**
 * Context bean to associate with an instrumented instance.
 */
public abstract class InstrumentationContextStore {
  /**
   * TODO: doc
   */
  public static <T extends InstrumentationContextStore> T get(Object instance, Class<T> extensionClass) {
    // throw new RuntimeException("TODO");
    System.out.println("DIRECT INVOKE!!");
    return null;
  }

  public InstrumentationContextStore() {
  }
}
