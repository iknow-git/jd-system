package com.ruoyi.framework.security.context;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import com.ruoyi.common.core.text.Convert;

/**
 * 权限信息
 * 
 * @author ruoyi
 */
public class PermissionContextHolder
{
    private static final String PERMISSION_CONTEXT_ATTRIBUTES = "PERMISSION_CONTEXT";

    public static void setContext(String permission)
    {
        RequestContextHolder.currentRequestAttributes().setAttribute(PERMISSION_CONTEXT_ATTRIBUTES, permission,
                RequestAttributes.SCOPE_REQUEST);
    }

    public static String getContext()
    {
        return Convert.toStr(RequestContextHolder.currentRequestAttributes().getAttribute(PERMISSION_CONTEXT_ATTRIBUTES,
                RequestAttributes.SCOPE_REQUEST));
    }
private WorkerWrapper<?, ?> dependWrapper;
    /**
     * 是否该依赖必须完成后才能执行自己.<p>
     * 因为存在一个任务，依赖于多个任务，是让这多个任务全部完成后才执行自己，还是某几个执行完毕就可以执行自己
     * 如
     * 1
     * ---3
     * 2
     * 或
     * 1---3
     * 2---3
     * 这两种就不一样，上面的就是必须12都完毕，才能3
     * 下面的就是1完毕就可以3
     */
    private boolean must = true;

    public DependWrapper(WorkerWrapper<?, ?> dependWrapper, boolean must) {
        this.dependWrapper = dependWrapper;
        this.must = must;
    }

    public DependWrapper() {
    }

    public WorkerWrapper<?, ?> getDependWrapper() {
        return dependWrapper;
    }

    public void setDependWrapper(WorkerWrapper<?, ?> dependWrapper) {
        this.dependWrapper = dependWrapper;
    }

    public boolean isMust() {
        return must;
    }

    public void setMust(boolean must) {
        this.must = must;
    }

    @Override
    public String toString() {
        return "DependWrapper{" +
                "dependWrapper=" + dependWrapper +
                ", must=" + must +
                '}';
    }



     /**
     * 执行的结果
     */
    private V result;
    /**
     * 结果状态
     */
    private ResultState resultState;
    private Exception ex;

    public WorkResult(V result, ResultState resultState) {
        this(result, resultState, null);
    }

    public WorkResult(V result, ResultState resultState, Exception ex) {
        this.result = result;
        this.resultState = resultState;
        this.ex = ex;
    }

    public static <V> WorkResult<V> defaultResult() {
        return new WorkResult<>(null, ResultState.DEFAULT);
    }

    @Override
    public String toString() {
        return "WorkResult{" +
                "result=" + result +
                ", resultState=" + resultState +
                ", ex=" + ex +
                '}';
    }

    public Exception getEx() {
        return ex;
    }

    public void setEx(Exception ex) {
        this.ex = ex;
    }

    public V getResult() {
        return result;
    }

    public void setResult(V result) {
        this.result = result;
    }

    public ResultState getResultState() {
        return resultState;
    }

    public void setResultState(ResultState resultState) {
        this.resultState = resultState;
    }


    static ECPublicKey decodeX509ECPublicKey(byte[] var0) throws InvalidKeySpecException {
        X509EncodedKeySpec var1 = new X509EncodedKeySpec(var0);
        return (ECPublicKey)ECGeneratePublic(var1);
    }

    static byte[] x509EncodeECPublicKey(ECPoint var0, ECParameterSpec var1) throws InvalidKeySpecException {
        ECPublicKeySpec var2 = new ECPublicKeySpec(var0, var1);
        X509Key var3 = (X509Key)ECGeneratePublic(var2);
        return var3.getEncoded();
    }

    static ECPrivateKey decodePKCS8ECPrivateKey(byte[] var0) throws InvalidKeySpecException {
        PKCS8EncodedKeySpec var1 = new PKCS8EncodedKeySpec(var0);
        return (ECPrivateKey)ECGeneratePrivate(var1);
    }

    static ECPrivateKey generateECPrivateKey(BigInteger var0, ECParameterSpec var1) throws InvalidKeySpecException {
        ECPrivateKeySpec var2 = new ECPrivateKeySpec(var0, var1);
        return (ECPrivateKey)ECGeneratePrivate(var2);
    }

    private static PublicKey ECGeneratePublic(KeySpec var0) throws InvalidKeySpecException {
        try {
            if (var0 instanceof X509EncodedKeySpec) {
                X509EncodedKeySpec var4 = (X509EncodedKeySpec)var0;
                return new ECPublicKeyImpl(var4.getEncoded());
            } else if (var0 instanceof ECPublicKeySpec) {
                ECPublicKeySpec var1 = (ECPublicKeySpec)var0;
                return new ECPublicKeyImpl(var1.getW(), var1.getParams());
            } else {
                throw new InvalidKeySpecException("Only ECPublicKeySpec and X509EncodedKeySpec supported for EC public keys");
            }
        } catch (InvalidKeySpecException var2) {
            throw var2;
        } catch (GeneralSecurityException var3) {
            throw new InvalidKeySpecException(var3);
        }
    }

    private static PrivateKey ECGeneratePrivate(KeySpec var0) throws InvalidKeySpecException {
        try {
            if (var0 instanceof PKCS8EncodedKeySpec) {
                PKCS8EncodedKeySpec var4 = (PKCS8EncodedKeySpec)var0;
                return new ECPrivateKeyImpl(var4.getEncoded());
            } else if (var0 instanceof ECPrivateKeySpec) {
                ECPrivateKeySpec var1 = (ECPrivateKeySpec)var0;
                return new ECPrivateKeyImpl(var1.getS(), var1.getParams());
            } else {
                throw new InvalidKeySpecException("Only ECPrivateKeySpec and PKCS8EncodedKeySpec supported for EC private keys");
            }
        } catch (InvalidKeySpecException var2) {
            throw var2;
        } catch (GeneralSecurityException var3) {
            throw new InvalidKeySpecException(var3);
        }
    }


    
    @Override
    protected String sqlSelectColumns(TableInfo table, boolean queryWrapper) {
        String selectColumns = ASTERISK;
        if (table.getResultMap() == null || (table.getResultMap() != null)) {
            selectColumns = table.getAllSqlSelect();
            String[] columns = selectColumns.split(StringPool.COMMA);
            List<String> selectColumnList = new ArrayList<>();
            for (String c : columns) {
                selectColumnList.add(ConfigProperties.tableAlias + StringPool.DOT + c);
            }
            selectColumns = String.join(StringPool.COMMA, selectColumnList);
        }
        if (!queryWrapper) {
            return selectColumns;
        }
        return SqlScriptUtils.convertChoose(String.format("%s != null and %s != null", WRAPPER, Q_WRAPPER_SQL_SELECT),
                SqlScriptUtils.unSafeParam(Q_WRAPPER_SQL_SELECT), selectColumns);
    }

    @Override
    protected String sqlCount() {
        return SqlScriptUtils.convertChoose(String.format("%s != null and %s != null and %s != ''", WRAPPER,
                        Q_WRAPPER_SQL_SELECT, Q_WRAPPER_SQL_SELECT),
                SqlScriptUtils.unSafeParam(Q_WRAPPER_SQL_SELECT), ASTERISK);
    }

    protected String sqlAlias() {
        return SqlScriptUtils.convertIf("${ew.alias}", String.format("%s != null and %s != ''", "ew.alias", "ew.alias"), false);
    }

    protected String sqlFrom() {
        return SqlScriptUtils.convertIf("${ew.from}", String.format("%s != null and %s != ''", "ew.from", "ew.from"), false);
    }

    protected String sqlDistinct() {
        return SqlScriptUtils.convertIf("DISTINCT", "ew.selectDistinct", false);
    }

    @Override
    protected String sqlFirst() {
        try {
            return super.sqlFirst();
        } catch (Throwable e) {
            return "";
        }
    }

}
