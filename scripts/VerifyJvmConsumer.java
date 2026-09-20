import com.tencent.tmm.kmmresource.resource.ColorResource;
import com.tencent.tmm.kmmresource.resource.PluralsResource;
import com.tencent.tmm.kmmresource.resource.StringResource;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Loads an already-built consumer JAR with the runtime on this command's classpath.
 * Usage: java --class-path <candidate-runtime-and-dependencies> scripts/VerifyJvmConsumer.java
 *        <unchanged-consumer.jar> <qualified-MR-class> [full-resource-name=expected-text ...]
 * The consumer's MR bytecode and packaged tables are used without regeneration or recompilation.
 */
class VerifyJvmConsumer {
    public static void main(String[] args) throws Exception {
        if (args.length < 2) throw new IllegalArgumentException("Expected consumer JAR and MR class");
        var previousLoader = Thread.currentThread().getContextClassLoader();
        try (var loader = new URLClassLoader(
                new java.net.URL[] { Path.of(args[0]).toUri().toURL() }, previousLoader)) {
            Thread.currentThread().setContextClassLoader(loader);
            Map<String, String> strings = new LinkedHashMap<>();
            int pluralCount = 0;
            int colorCount = 0;
            for (String category : new String[] { "strings", "plurals", "colors" }) {
                var owner = Class.forName(args[1] + "$" + category, true, loader);
                var instance = owner.getField("INSTANCE").get(null);
                for (Method getter : owner.getDeclaredMethods()) {
                    if (getter.getParameterCount() != 0) continue;
                    if (getter.getReturnType() == StringResource.class) {
                        var resource = (StringResource) getter.invoke(instance);
                        strings.put(resource.getResourceName(), resource.getString());
                    } else if (getter.getReturnType() == PluralsResource.class) {
                        var resource = (PluralsResource) getter.invoke(instance);
                        resource.getQuantityString(1);
                        resource.getQuantityString(2);
                        pluralCount++;
                    } else if (getter.getReturnType() == ColorResource.class) {
                        ((ColorResource) getter.invoke(instance)).getColor();
                        colorCount++;
                    }
                }
            }
            if (strings.isEmpty()) throw new AssertionError("No string declarations were exercised");
            for (int i = 2; i < args.length; i++) {
                var check = args[i].split("=", 2);
                if (check.length != 2 || !check[1].equals(strings.get(check[0]))) {
                    throw new AssertionError("Mismatch for " + check[0] + ": " + strings.get(check[0]));
                }
                System.out.println(check[0] + " => " + strings.get(check[0]));
            }
            System.out.printf("PASS: %d strings, %d plurals, %d colors; %d independent text assertions%n",
                    strings.size(), pluralCount, colorCount, args.length - 2);
        } finally {
            Thread.currentThread().setContextClassLoader(previousLoader);
        }
    }
}
