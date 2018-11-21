package com.primeton.arturo.core.util;

import java.lang.reflect.Field;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.primeton.arturo.core.service.ILogService;
import com.primeton.arturo.specs.common.LogOpLevelType;
import com.primeton.arturo.specs.exception.ArturoException;
import com.primeton.arturo.specs.model.OPLog;
import com.primeton.arturo.specs.model.SystemMember;

@Aspect
@Component
public class LogAspect {

	private static String[] types = { "java.lang.Integer", "java.lang.Double", "java.lang.Float", "java.lang.Long", "java.lang.Short", "java.lang.Byte", "java.lang.Boolean", "java.lang.Char",
			"java.lang.String", "int", "double", "long", "short", "byte", "boolean", "char", "float" };

	// private static String ENTITYPACKAGE = "com.primeton.arturo.specs.model.";

	private static String IDFIELD = "Id";

	private static String NAMEFIELD = "Name";

	@Autowired
	private ILogService logService;

	// 拦截service包中所有create update方法，除了LogService中的
	@Pointcut("(execution(public * com.primeton.arturo.core.service.*.create*(..)) " 
			+ "|| execution(public * com.primeton.arturo.core.service.*.update*(..)))"
			+ "&& !execution(public * com.primeton.arturo.core.service.impl.LogService.*(..))" 
			+ "&& !execution(public * com.primeton.arturo.core.service.impl.BuildService.*(..))"
			+ "&& !execution(public * com.primeton.arturo.core.service.impl.ApplyService.*(..))")
	public void serviceMethodforCU() {

	}

	// 单独处理批量添加系统成员的操作
	@Pointcut("execution(public * com.primeton.arturo.core.service.impl.SystemMemberService.addSystemMember(..)) ")
	public void serviceMethodforA() {

	}

	// 拦截service包中所有delete方法
	@Pointcut("execution(public * com.primeton.arturo.core.service.*.delete*(..))")
	public void serviceMethodforDelete() {

	}

	/**
	 * 拦截器具体实现
	 * 
	 * @throws ArturoException
	 * @throws ClassNotFoundException
	 * @throws NotFoundException
	 */
	@AfterReturning(value = "serviceMethodforCU()", returning = "rvt")
	public void afterAddOrUpdate(JoinPoint joinPoint, Object rvt) throws ArturoException, ClassNotFoundException, NotFoundException {
		if (rvt == null) {
			return;
		}
		Class<?> entityClazz = Class.forName(rvt.getClass().getName());
		String entityClassName = entityClazz.getSimpleName();
		String entityId = getFieldsValue(rvt, IDFIELD);
		String entityName = getFieldsValue(rvt, NAMEFIELD);
		String opType = getOPType(joinPoint);

		String methodName = joinPoint.getSignature().getName();

		String[] paramNames = getFieldsName(this.getClass(), joinPoint.getTarget().getClass().getName(), methodName);
		String logContent = writeLogInfo(paramNames, joinPoint);

		System.out.println(logContent);
		OPLog log = new OPLog(LogOpLevelType.INFO.getDbValue(), entityClassName, entityId, entityName, opType, logContent);
		logService.createLogs(log);

	}

	// TODO 处理SystemMember[]
	// @AfterReturning(value = "serviceMethodforA()", returning = "rvt")
	public void afterAdd(JoinPoint joinPoint, SystemMember[] rvt) throws ArturoException, ClassNotFoundException, NotFoundException {
		Class<?> entityClazz = Class.forName(rvt.getClass().getName());
		String entityClassName = entityClazz.getSimpleName();
		String entityId = getFieldsValue(rvt, IDFIELD);
		String entityName = getFieldsValue(rvt, NAMEFIELD);
		String opType = getOPType(joinPoint);

		String methodName = joinPoint.getSignature().getName();

		String[] paramNames = getFieldsName(this.getClass(), joinPoint.getTarget().getClass().getName(), methodName);
		String logContent = writeLogInfo(paramNames, joinPoint);

		System.out.println(logContent);
		OPLog log = new OPLog(LogOpLevelType.INFO.getDbValue(), entityClassName, entityId, entityName, opType, logContent);
		logService.createLogs(log);

	}

	@AfterReturning(value = "serviceMethodforDelete()")
	public void afterDelete(JoinPoint joinPoint) throws ArturoException, ClassNotFoundException, NotFoundException {
		Class<?> entityClazz = Class.forName(joinPoint.getTarget().getClass().getName());
		String entityClassName = entityClazz.getSimpleName();
		Object[] args = joinPoint.getArgs();
		String entityId = args[args.length - 1].toString();
		String opType = getOPType(joinPoint);

		String methodName = joinPoint.getSignature().getName();

		String[] paramNames = getFieldsName(this.getClass(), joinPoint.getTarget().getClass().getName(), methodName);
		String logContent = writeLogInfo(paramNames, joinPoint);

		System.out.println(logContent);
		OPLog log = new OPLog(LogOpLevelType.INFO.getDbValue(), entityClassName, entityId, null, opType, logContent);
		logService.createLogs(log);
	}

	/**
	 * 日志操作类型
	 * 
	 * @param joinPoint
	 * @return
	 */
	private String getOPType(JoinPoint joinPoint) {
		String opType = joinPoint.getSignature().getName();
		if (opType.startsWith("create") || opType.startsWith("add")) {
			opType = "create";
		} else if (opType.startsWith("update")) {
			opType = "update";
		} else if (opType.startsWith("delete")) {
			opType = "delete";
		}
		return opType;
	}

	/**
	 * 打印变量的全部信息
	 * 
	 * @param paramNames
	 * @param joinPoint
	 * @return
	 */
	private static String writeLogInfo(String[] paramNames, JoinPoint joinPoint) {
		Object[] args = joinPoint.getArgs();
		StringBuilder sb = new StringBuilder();
		boolean clazzFlag = true;
		for (int k = 0; k < args.length; k++) {
			Object arg = args[k];
			sb.append(paramNames[k] + " ");
			// 获取对象类型
			String typeName = arg.getClass().getTypeName();

			for (String t : types) {
				if (t.equals(typeName)) {
					sb.append("=" + arg + "; ");
				}
			}
			if (clazzFlag) {
				sb.append(getFieldsValue(arg, null));
			}
		}
		return sb.toString();
	}

	/**
	 * 得到方法参数的名称
	 * 
	 * @param cls
	 * @param clazzName
	 * @param methodName
	 * @return
	 * @throws NotFoundException
	 */
	@SuppressWarnings("rawtypes")
	private static String[] getFieldsName(Class cls, String clazzName, String methodName) throws NotFoundException {
		ClassPool pool = ClassPool.getDefault();
		// ClassClassPath classPath = new ClassClassPath(this.getClass());
		ClassClassPath classPath = new ClassClassPath(cls);
		pool.insertClassPath(classPath);

		CtClass cc = pool.get(clazzName);
		CtMethod cm = cc.getDeclaredMethod(methodName);
		MethodInfo methodInfo = cm.getMethodInfo();
		CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
		LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
		if (attr == null) {
			// exception
		}
		String[] paramNames = new String[cm.getParameterTypes().length];
		int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;
		for (int i = 0; i < paramNames.length; i++) {
			paramNames[i] = attr.variableName(i + pos); // paramNames即参数名
		}
		return paramNames;
	}

	/**
	 * 得到参数的值
	 * 
	 * @param obj
	 * @param sfield
	 */
	public static String getFieldsValue(Object obj, String sfield) {
		if (obj == null) {
			return null;
		}

		Field[] fields = obj.getClass().getDeclaredFields();
		String typeName = obj.getClass().getTypeName();
		for (String t : types) {
			if (t.equals(typeName))
				return "";
		}
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (Field f : fields) {
			f.setAccessible(true);
			try {
				for (String str : types) {
					if (f.getType().getName().equals(str)) {
						// sfield不为空且属性名以 Id Name 等结尾，就返回
						if (sfield != null && f.getName().endsWith(sfield)) {
							Object value = f.get(obj);
							if (value != null) {
								return value.toString();
							}
						} else {
							sb.append(f.getName() + " = " + f.get(obj) + "; ");
						}
					}
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		sb.append("]");
		return sb.toString();
	}

}
