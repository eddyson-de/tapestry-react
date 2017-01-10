package de.eddyson.tapestry.react.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.tapestry5.internal.webresources.RhinoExecutor;
import org.apache.tapestry5.ioc.Invokable;
import org.apache.tapestry5.ioc.OperationTracker;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.ioc.internal.util.InternalUtils;
import org.apache.tapestry5.ioc.util.ExceptionUtils;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.NativeFunction;
import org.mozilla.javascript.ScriptableObject;

/**
 * Manages a pool of initialized {@link RhinoExecutor} instances. The instances
 * are initialized for a particular
 */
// This is a copy of
// org.apache.tapestry5.internal.webresources.RhinoExecutorPool from the
// tapestry-webresources package with a fix for
// https://github.com/eddyson-de/tapestry-react/issues/62
public class FixedRhinoExecutorPool {

  private final OperationTracker tracker;

  private final List<Resource> scripts;

  private final Queue<RhinoExecutor> executors = new ConcurrentLinkedQueue<RhinoExecutor>();

  private final ContextFactory contextFactory = new ContextFactory();

  public FixedRhinoExecutorPool(final OperationTracker tracker, final List<Resource> scripts) {
    this.tracker = tracker;
    this.scripts = scripts;
  }

  /**
   * Gets or creates an available executor. It is expected that
   * {@link #put(RhinoExecutor)} will be invoked after the executor completes.
   *
   * @return executor
   */
  public RhinoExecutor get() {

    RhinoExecutor executor = executors.poll();
    if (executor != null) {
      return executor;
    }

    return createExecutor();
  }

  private void put(final RhinoExecutor executor) {
    executors.add(executor);
  }

  private RhinoExecutor createExecutor() {
    return tracker.invoke(String.format("Creating Rhino executor for source(s) %s.", InternalUtils.join(scripts)),
        new Invokable<RhinoExecutor>() {
          @Override
          public RhinoExecutor invoke() {
            final Context context = contextFactory.enterContext();

            final ScriptableObject scope = context.initStandardObjects();

            try {
              context.setOptimizationLevel(-1);

              for (Resource script : scripts) {
                loadScript(context, scope, script);
              }

            } finally {
              Context.exit();
            }

            return new RhinoExecutor() {
              @Override
              public ScriptableObject invokeFunction(final String functionName, final Object... arguments) {
                contextFactory.enterContext(context);

                try {
                  NativeFunction function = (NativeFunction) scope.get(functionName, scope);

                  return (ScriptableObject) function.call(context, scope, null, arguments);
                } finally {
                  Context.exit();
                }
              }

              @Override
              public void discard() {
                put(this);
              }
            };
          }
        });
  }

  private void loadScript(final Context context, final ScriptableObject scope, final Resource script) {
    tracker.run(String.format("Loading script %s.", script), new Runnable() {
      @Override
      public void run() {
        InputStream in = null;
        Reader r = null;

        try {
          in = script.openStream();
          r = new InputStreamReader(in, StandardCharsets.UTF_8);

          context.evaluateReader(scope, r, script.toString(), 1, null);
        } catch (IOException ex) {
          throw new RuntimeException(
              String.format("Unable to read script %s: %s", script, ExceptionUtils.toMessage(ex)), ex);
        } finally {
          InternalUtils.close(r);
          InternalUtils.close(in);
        }
      }
    });

  }
}
