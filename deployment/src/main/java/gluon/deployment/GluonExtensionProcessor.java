package gluon.deployment;

import gluon.runtime.utils.ISayHello;
import io.quarkus.arc.deployment.GeneratedBeanBuildItem;
import io.quarkus.arc.deployment.GeneratedBeanGizmoAdaptor;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.gizmo.*;
import org.objectweb.asm.Opcodes;

import javax.inject.Singleton;

// SEE: https://quarkus.io/guides/writing-extensions#build-step-processors
// SEE: https://github.com/quarkusio/gizmo/blob/main/src/test/java/io/quarkus/gizmo/VariableAssignmentTestCase.java
// SEE: https://quarkus.io/guides/cdi-integration

public class GluonExtensionProcessor {

    private static final String FEATURE = "gluon-extension";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

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
    }

}
