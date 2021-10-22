package gluon.deployment;

import gluon.annotations.SQL;
import gluon.runtime.sql.Connection;
import gluon.runtime.sql.Repository;
import gluon.runtime.utils.ISayHello;
import io.quarkus.arc.deployment.BeanDefiningAnnotationBuildItem;
import io.quarkus.arc.deployment.GeneratedBeanBuildItem;
import io.quarkus.arc.deployment.GeneratedBeanGizmoAdaptor;
import io.quarkus.arc.processor.BeanRegistrar;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.ApplicationIndexBuildItem;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.IndexDependencyBuildItem;
import io.quarkus.gizmo.*;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.DotName;
import org.jboss.jandex.IndexView;
import org.objectweb.asm.Opcodes;

import javax.inject.Singleton;
import org.jboss.logging.Logger;

// SEE: https://quarkus.io/guides/writing-extensions#build-step-processors
// SEE: https://github.com/quarkusio/gizmo/blob/main/src/test/java/io/quarkus/gizmo/VariableAssignmentTestCase.java
// SEE: https://quarkus.io/guides/cdi-integration

public class GluonExtensionProcessor {

    private static final String FEATURE = "gluon-extension";

    private static final Logger LOGGER = Logger.getLogger(GluonExtensionProcessor.class);

    private static final DotName CONNECTION = DotName.createSimple(Connection.class.getName());
    private static final DotName REPOSITORY = DotName.createSimple(Repository.class.getName());

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    BeanDefiningAnnotationBuildItem additionalBeanDefiningAnnotation() {
        LOGGER.info("additionalBeanDefiningAnnotation to application index");
        return new BeanDefiningAnnotationBuildItem(CONNECTION);
    }

    @BuildStep
    void addDependencies(BuildProducer<IndexDependencyBuildItem> indexDependency) {
        LOGGER.info("addDependencies to application index");
        indexDependency.produce(new IndexDependencyBuildItem("org.gluon", "gluon-extension"));
    }

    @BuildStep
    void analizeAnnotationsFromCombinedIndex(CombinedIndexBuildItem combinedIndexBuildItem, BuildProducer<GeneratedBeanBuildItem> generatedBeans) {
        LOGGER.info("Before analize annotations from combined index");
        IndexView index = combinedIndexBuildItem.getIndex();

        for (var repo : index.getAllKnownSubclasses(REPOSITORY)) {
            LOGGER.info("repository: " + repo.name() + "lkind: " + repo.kind());
            for (var annotation : repo.classAnnotations()) {
                LOGGER.info("    class annotation: " + annotation);
            }
            for (var annotation : repo.annotations().keySet()) {
                LOGGER.info("    annotation: " + annotation);
            }
        }

    }

    @BuildStep
    void generatedBean(BuildProducer<GeneratedBeanBuildItem> generatedBeans) {
        LOGGER.info("generatedBean BuildStep");
        ClassOutput beansClassOutput = new GeneratedBeanGizmoAdaptor(generatedBeans);

        try (var classCreator = ClassCreator.builder().classOutput(beansClassOutput)
                .className("gluon.impl.SayHelloImpl")
                .interfaces(ISayHello.class)
                .build()) {
            classCreator.addAnnotation(Singleton.class);
            MethodCreator method = classCreator.getMethodCreator("sayHello", String.class).setModifiers(Opcodes.ACC_PUBLIC);
            AssignableResultHandle val = method.createVariable(String.class);
            method.assign(val, method.load("HELLO!"));
            // ResultHandle ret = method.readStaticField(FieldDescriptor.of(String.class, "MAX_VALUE", int.class));
            method.returnValue(val);
        }

    }

}
