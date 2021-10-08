package gluon.deployment;

import gluon.runtime.annotations.SQL;
import gluon.runtime.utils.ISayHello;
import io.quarkus.arc.deployment.GeneratedBeanBuildItem;
import io.quarkus.arc.deployment.GeneratedBeanGizmoAdaptor;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.gizmo.*;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.DotName;
import org.jboss.jandex.IndexView;
import org.objectweb.asm.Opcodes;

import javax.inject.Inject;
import javax.inject.Singleton;
import org.jboss.logging.Logger;

// SEE: https://quarkus.io/guides/writing-extensions#build-step-processors
// SEE: https://github.com/quarkusio/gizmo/blob/main/src/test/java/io/quarkus/gizmo/VariableAssignmentTestCase.java
// SEE: https://quarkus.io/guides/cdi-integration

public class GluonExtensionProcessor {

    private static final String FEATURE = "gluon-extension";

    private static final Logger LOGGER = Logger.getLogger(GluonExtensionProcessor.class);

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @Inject
    CombinedIndexBuildItem combinedIndexBuildItem;

    @BuildStep
    void generatedBean(BuildProducer<GeneratedBeanBuildItem> generatedBeans) {
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

        LOGGER.info("Before analize annotations");
        DotName SQL_REPOSITORY = DotName.createSimple(SQL.Repository.class.getName());
        IndexView index = combinedIndexBuildItem.getIndex();
        for (var module : index.getKnownModules()) {
            LOGGER.info("module: " + module.name());
            for (var annotation : module.annotations()) {
                LOGGER.info("  annotation: " + annotation.name());
            }
        }
        for (AnnotationInstance repositoryDeclaration : index.getAnnotations(SQL_REPOSITORY)) {
            AnnotationTarget annotationTarget = repositoryDeclaration.target();
            if (AnnotationTarget.Kind.CLASS .equals(annotationTarget.kind())) {
                DotName dotName = annotationTarget.asClass().name();
                LOGGER.info(dotName + " is class");
            } else if (AnnotationTarget.Kind.TYPE.equals(annotationTarget.kind())) {
                var targetType = annotationTarget.asType().toString();
                LOGGER.info(targetType + " is type");
            } else {
                var kind = annotationTarget.kind();
                var target = repositoryDeclaration.name();
                LOGGER.warn(target + " has kind " + kind);
            }
            System.out.println("found element with annotation " + SQL.Repository.class.getName() + ": " + repositoryDeclaration.name());
        }
    }

}
