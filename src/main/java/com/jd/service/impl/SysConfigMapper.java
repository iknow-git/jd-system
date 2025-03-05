package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysConfig;

/**
 * 参数配置 数据层
 * 
 * @author ruoyi
 */
public interface SysConfigMapper
{
    /**
     * 查询参数配置信息
     * 
     * @param config 参数配置信息
     * @return 参数配置信息
     */
    public SysConfig selectConfig(SysConfig config);

    /**
     * 通过ID查询配置
     * 
     * @param configId 参数ID
     * @return 参数配置信息
     */
    public SysConfig selectConfigById(Long configId);

    /**
     * 查询参数配置列表
     * 
     * @param config 参数配置信息
     * @return 参数配置集合
     */
    public List<SysConfig> selectConfigList(SysConfig config);

    /**
     * 根据键名查询参数配置信息
     * 
     * @param configKey 参数键名
     * @return 参数配置信息
     */
    public SysConfig checkConfigKeyUnique(String configKey);

    /**
     * 新增参数配置
     * 
     * @param config 参数配置信息
     * @return 结果
     */
    public int insertConfig(SysConfig config);

    /**
     * 修改参数配置
     * 
     * @param config 参数配置信息
     * @return 结果
     */
    public int updateConfig(SysConfig config);

    /**
     * 删除参数配置
     * 
     * @param configId 参数ID
     * @return 结果
     */
    public int deleteConfigById(Long configId);

    /**
     * 批量删除参数信息
     * 
     * @param configIds 需要删除的参数ID
     * @return 结果
     */
    public int deleteConfigByIds(Long[] configIds);


    
    // TODO 入参使用 MybatisSqlSessionFactoryBean
    private void applyConfiguration(MybatisSqlSessionFactoryBean factory) {
        // TODO 使用 MybatisConfiguration
        MybatisConfiguration configuration = this.properties.getConfiguration();
        if (configuration == null && !StringUtils.hasText(this.properties.getConfigLocation())) {
            configuration = new MybatisConfiguration();
        }
        if (configuration != null && !CollectionUtils.isEmpty(this.configurationCustomizers)) {
            for (ConfigurationCustomizer customizer : this.configurationCustomizers) {
                customizer.customize(configuration);
            }
        }
        factory.setConfiguration(configuration);
    }

    private void applySqlSessionFactoryBeanCustomizers(MybatisSqlSessionFactoryBean factory) {
        if (!CollectionUtils.isEmpty(this.sqlSessionFactoryBeanCustomizers)) {
            for (SqlSessionFactoryBeanCustomizer customizer : this.sqlSessionFactoryBeanCustomizers) {
                customizer.customize(factory);
            }
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        ExecutorType executorType = this.properties.getExecutorType();
        if (executorType != null) {
            return new SqlSessionTemplate(sqlSessionFactory, executorType);
        } else {
            return new SqlSessionTemplate(sqlSessionFactory);
        }
    }


     public static boolean isValidAnnotationMemberType(Class<?> type) {
        if (type == null) {
            return false;
        }
        if (type.isArray()) {
            type = type.getComponentType();
        }
        return type.isPrimitive() || type.isEnum() || type.isAnnotation()
                || String.class.equals(type) || Class.class.equals(type);
    }

    //besides modularity, this has the advantage of autoboxing primitives:
    /**
     * Helper method for generating a hash code for a member of an annotation.
     *
     * @param name the name of the member
     * @param value the value of the member
     * @return a hash code for this member
     */
    private static int hashMember(final String name, final Object value) {
        final int part1 = name.hashCode() * 127;
        if (value.getClass().isArray()) {
            return part1 ^ arrayMemberHash(value.getClass().getComponentType(), value);
        }
        if (value instanceof Annotation) {
            return part1 ^ hashCode((Annotation) value);
        }
        return part1 ^ value.hashCode();
    }

    /**
     * Helper method for checking whether two objects of the given type are
     * equal. This method is used to compare the parameters of two annotation
     * instances.
     *
     * @param type the type of the objects to be compared
     * @param o1 the first object
     * @param o2 the second object
     * @return a flag whether these objects are equal
     */
    private static boolean memberEquals(final Class<?> type, final Object o1, final Object o2) {
        if (o1 == o2) {
            return true;
        }
        if (o1 == null || o2 == null) {
            return false;
        }
        if (type.isArray()) {
            return arrayMemberEquals(type.getComponentType(), o1, o2);
        }
        if (type.isAnnotation()) {
            return equals((Annotation) o1, (Annotation) o2);
        }
        return o1.equals(o2);
    }

    /**
     * Helper method for comparing two objects of an array type.
     *
     * @param componentType the component type of the array
     * @param o1 the first object
     * @param o2 the second object
     * @return a flag whether these objects are equal
     */
    private static boolean arrayMemberEquals(final Class<?> componentType, final Object o1, final Object o2) {
        if (componentType.isAnnotation()) {
            return annotationArrayMemberEquals((Annotation[]) o1, (Annotation[]) o2);
        }
        if (componentType.equals(Byte.TYPE)) {
            return Arrays.equals((byte[]) o1, (byte[]) o2);
        }
        if (componentType.equals(Short.TYPE)) {
            return Arrays.equals((short[]) o1, (short[]) o2);
        }
        if (componentType.equals(Integer.TYPE)) {
            return Arrays.equals((int[]) o1, (int[]) o2);
        }
        if (componentType.equals(Character.TYPE)) {
            return Arrays.equals((char[]) o1, (char[]) o2);
        }
        if (componentType.equals(Long.TYPE)) {
            return Arrays.equals((long[]) o1, (long[]) o2);
        }
        if (componentType.equals(Float.TYPE)) {
            return Arrays.equals((float[]) o1, (float[]) o2);
        }
        if (componentType.equals(Double.TYPE)) {
            return Arrays.equals((double[]) o1, (double[]) o2);
        }
        if (componentType.equals(Boolean.TYPE)) {
            return Arrays.equals((boolean[]) o1, (boolean[]) o2);
        }
        return Arrays.equals((Object[]) o1, (Object[]) o2);
    }

    /**
     * Helper method for comparing two arrays of annotations.
     *
     * @param a1 the first array
     * @param a2 the second array
     * @return a flag whether these arrays are equal
     */
    private static boolean annotationArrayMemberEquals(final Annotation[] a1, final Annotation[] a2) {
        if (a1.length != a2.length) {
            return false;
        }
        for (int i = 0; i < a1.length; i++) {
            if (!equals(a1[i], a2[i])) {
                return false;
            }
        }
        return true;
    }

}
