package datadog.trace.agent.tooling.context;

import datadog.trace.agent.tooling.Utils;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.jar.asm.ClassVisitor;
import net.bytebuddy.jar.asm.ClassWriter;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.pool.TypePool;

public class ContextVisitor implements AsmVisitorWrapper {
  private final ElementMatcher methodMatcher;

  public ContextVisitor(ElementMatcher methodMatcher) {
    this.methodMatcher = methodMatcher;
  }

  @Override
  public int mergeWriter(int flags) {
    return flags | ClassWriter.COMPUTE_MAXS;
  }

  @Override
  public int mergeReader(int flags) {
    return flags;
  }

  @Override
  public ClassVisitor wrap(TypeDescription instrumentedType, ClassVisitor classVisitor, Implementation.Context implementationContext, TypePool typePool, FieldList<FieldDescription.InDefinedShape> fields, MethodList<?> methods, int writerFlags, int readerFlags) {
    return new ContextGetRemapper(classVisitor);
  }

  private class ContextGetRemapper extends ClassVisitor {
    private String className;

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
      className = name;
      super.visit(version, access, name, signature, superName, interfaces);
    }

    public ContextGetRemapper(ClassVisitor classVisitor) {
      super(Opcodes.ASM6, classVisitor);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
      final MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
      return new ContextGetMethodRemapper(name, mv);
    }

    private class ContextGetMethodRemapper extends MethodVisitor {
      final String methodName;

      public ContextGetMethodRemapper(String methodName, MethodVisitor mv) {
        super(Opcodes.ASM6, mv);
        this.methodName = methodName;
      }

      @Override
      public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        if(owner.equals(Utils.getInternalName(InstrumentationContextStore.class.getName())) && name.equals("get")) {
          System.out.println("-- REMAP CLASS: " + className + "#" + methodName + " : " + owner + "#" + name);
        }
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
      }
    }
  }
}
