package query;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

public class MyObjectSizeCalculator {
    private static final int REFERENCE_SIZE = 8;
    private static final int OBJECT_HEADER_SIZE = 12;
    private static final int ARRAY_HEADER_SIZE = 24; // 对于数组对象，头的大小可能不同
    private static final int ALIGNMENT = 8; // 对齐单位，通常是8字节

    public static long getObjectSize(Object obj) {
        if (obj == null) {
            return 0;
        }
        long size = OBJECT_HEADER_SIZE; // 对象头
        Class<?> clazz = obj.getClass();
        if (clazz.isArray()) {
            size += ARRAY_HEADER_SIZE; // 数组对象的头
            int length = Array.getLength(obj);
            Class<?> componentType = clazz.getComponentType();
            if (componentType.isPrimitive()) {
                int typeSize = getPrimitiveSize(componentType);
                size += (long) length * typeSize;
            } else {
                size += (long) length * REFERENCE_SIZE;
            }
            return size;
        }

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);
                if (value != null) {
                    if (field.getType().isPrimitive()) {
                        // 基础数据类型
                        size += getPrimitiveSize(field.getType());
                    } else {
                        // 对象引用
                        size += REFERENCE_SIZE;
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        size = alignSize(size); // 对齐填充
        return size;
    }

    private static int getPrimitiveSize(Class<?> type) {
        switch (type.getName()) {
            case "int":
            case "float":
            case "boolean":
                return 4;
            case "long":
            case "double":
                return 8;
            case "short":
                return 2;
            case "byte":
                return 1;
            case "char":
                return 2;
            default:
                return REFERENCE_SIZE; // 对于非基础数据类型，这里默认返回一个引用的大小
        }
    }

    private static long alignSize(long size) {
        return ((size + ALIGNMENT - 1) / ALIGNMENT) * ALIGNMENT;
    }

    private static int getReferenceSize() {
        return REFERENCE_SIZE;
    }
}
