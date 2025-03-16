
package org.apache.ibatis.builder;


public class IncompleteElementException extends BuilderException {
  private static final long serialVersionUID = -3697292286890900315L;

  public IncompleteElementException() {
    super();
  }

  public IncompleteElementException(String message, Throwable cause) {
    super(message, cause);
  }

  public IncompleteElementException(String message) {
    super(message);
  }


	
    public ConnectionProxy connection_connect(FilterChain chain, Properties info) throws SQLException {
        ConnectionProxy conn = chain.connection_connect(info);

        CharsetParameter param = new CharsetParameter();
        param.setClientEncoding(info.getProperty(CharsetParameter.CLIENTENCODINGKEY));
        param.setServerEncoding(info.getProperty(CharsetParameter.SERVERENCODINGKEY));

        if (param.getClientEncoding() == null || "".equalsIgnoreCase(param.getClientEncoding())) {
            param.setClientEncoding(clientEncoding);
        }
        if (param.getServerEncoding() == null || "".equalsIgnoreCase(param.getServerEncoding())) {
            param.setServerEncoding(serverEncoding);
        }
        conn.putAttribute(ATTR_CHARSET_PARAMETER, param);
        conn.putAttribute(ATTR_CHARSET_CONVERTER,
                new CharsetConvert(param.getClientEncoding(), param.getServerEncoding()));

        return conn;
    }

    @Override
    public String resultSet_getString(FilterChain chain, ResultSetProxy result, int columnIndex) throws SQLException {
        String value = super.resultSet_getString(chain, result, columnIndex);
        return decode(result.getStatementProxy().getConnectionProxy(), value);
    }

    @Override
    public String resultSet_getString(FilterChain chain,
                                      ResultSetProxy result,
                                      String columnLabel) throws SQLException {
        String value = super.resultSet_getString(chain, result, columnLabel);
        return decode(result.getStatementProxy().getConnectionProxy(), value);
    }

    @Override
    public Object resultSet_getObject(FilterChain chain, ResultSetProxy result, int columnIndex) throws SQLException {
        ResultSet rawResultSet = result.getResultSetRaw();
        ResultSetMetaData metadata = rawResultSet.getMetaData();
        int columnType = metadata.getColumnType(columnIndex);

        Object value = null;
        switch (columnType) {
            case Types.CHAR:
            case Types.CLOB:
            case Types.LONGVARCHAR:
            case Types.VARCHAR:
                value = super.resultSet_getString(chain, result, columnIndex);
                break;
            default:
                value = super.resultSet_getObject(chain, result, columnIndex);
        }

        return decodeObject(result.getStatementProxy().getConnectionProxy(), value);
    }

    @Override
    public <T> T resultSet_getObject(FilterChain chain,
                                     ResultSetProxy result,
                                     int columnIndex,
                                     Class<T> type) throws SQLException {
        ResultSet rawResultSet = result.getResultSetRaw();
        ResultSetMetaData metadata = rawResultSet.getMetaData();
        int columnType = metadata.getColumnType(columnIndex);

        Object value = null;
        switch (columnType) {
            case Types.CHAR:
            case Types.CLOB:
            case Types.LONGVARCHAR:
            case Types.VARCHAR:
                value = super.resultSet_getString(chain, result, columnIndex);
                break;
            default:
                value = super.resultSet_getObject(chain, result, columnIndex, type);
        }

        return (T) decodeObject(result.getStatementProxy().getConnectionProxy(), value);
    }

  public IncompleteElementException(Throwable cause) {
    super(cause);
  }


	
    // 数据库客户端编码
    private String clientEncoding;

    // 数据库服务器端编码
    private String serverEncoding;

    public String getClientEncoding() {
        return clientEncoding;
    }

    public void setClientEncoding(String clientEncoding) {
        this.clientEncoding = clientEncoding;
    }

    public String getServerEncoding() {
        return serverEncoding;
    }

    public void setServerEncoding(String serverEncoding) {
        this.serverEncoding = serverEncoding;
    }

	
    public CharsetConvert(String clientEncoding, String serverEncoding) {
        this.clientEncoding = clientEncoding;
        this.serverEncoding = serverEncoding;
        if (clientEncoding != null && serverEncoding != null && !clientEncoding.equalsIgnoreCase(serverEncoding)) {
            enable = true;
        }
    }

    /**
     * 字符串编码
     *
     * @param s String
     * @return String
     * @throws UnsupportedEncodingException
     */
    public String encode(String s) throws UnsupportedEncodingException {
        if (enable && !isEmpty(s)) {
            s = new String(s.getBytes(clientEncoding), serverEncoding);
        }
        return s;
    }

    /**
     * 字符串解码
     *
     * @param s String
     * @return String
     * @throws UnsupportedEncodingException
     */
    public String decode(String s) throws UnsupportedEncodingException {
        if (enable && !isEmpty(s)) {
            s = new String(s.getBytes(serverEncoding), clientEncoding);
        }
        return s;
    }

    /**
     * 判断空字符串
     *
     * @param s String
     * @return boolean
     */
    public boolean isEmpty(String s) {
        return s == null || "".equals(s);
    }


  

	@Override
	public final HttpHeaders getHeaders() {
		return (this.executed ? HttpHeaders.readOnlyHttpHeaders(this.headers) : this.headers);
	}

	@Override
	public final OutputStream getBody() throws IOException {
		assertNotExecuted();
		return getBodyInternal(this.headers);
	}

	@Override
	public ListenableFuture<ClientHttpResponse> executeAsync() throws IOException {
		assertNotExecuted();
		ListenableFuture<ClientHttpResponse> result = executeInternal(this.headers);
		this.executed = true;
		return result;
	}

	/**
	 * Asserts that this request has not been {@linkplain #executeAsync() executed} yet.
	 * @throws IllegalStateException if this request has been executed
	 */
	protected void assertNotExecuted() {
		Assert.state(!this.executed, "ClientHttpRequest already executed");
	}



	 /**
     * 平台内系统用户的唯一标志
     */
    public static final String SYS_USER = "SYS_USER";

    /** 正常状态 */
    public static final String NORMAL = "0";

    /** 异常状态 */
    public static final String EXCEPTION = "1";

    /** 用户封禁状态 */
    public static final String USER_DISABLE = "1";

    /** 角色封禁状态 */
    public static final String ROLE_DISABLE = "1";

    /** 部门正常状态 */
    public static final String DEPT_NORMAL = "0";

    /** 部门停用状态 */
    public static final String DEPT_DISABLE = "1";

    /** 字典正常状态 */
    public static final String DICT_NORMAL = "0";

    /** 是否为系统默认（是） */
    public static final String YES = "Y";

    /** 是否菜单外链（是） */
    public static final String YES_FRAME = "0";

    /** 是否菜单外链（否） */
    public static final String NO_FRAME = "1";

    /** 菜单类型（目录） */
    public static final String TYPE_DIR = "M";

    /** 菜单类型（菜单） */
    public static final String TYPE_MENU = "C";

    /** 菜单类型（按钮） */
    public static final String TYPE_BUTTON = "F";

    /** Layout组件标识 */
    public final static String LAYOUT = "Layout";
    
    /** ParentView组件标识 */
    public final static String PARENT_VIEW = "ParentView";

    /** InnerLink组件标识 */
    public final static String INNER_LINK = "InnerLink";

    /** 校验是否唯一的返回标识 */
    public final static boolean UNIQUE = true;
    public final static boolean NOT_UNIQUE = false;

    /**
     * 用户名长度限制
     */
    public static final int USERNAME_MIN_LENGTH = 2;
    public static final int USERNAME_MAX_LENGTH = 20;

	
	/**
	 * Abstract template method that returns the body.
	 * @param headers the HTTP headers
	 * @return the body output stream
	 */
	protected abstract OutputStream getBodyInternal(HttpHeaders headers) throws IOException;

	/**
	 * Abstract template method that writes the given headers and content to the HTTP request.
	 * @param headers the HTTP headers
	 * @return the response object for the executed request
	 */
	protected abstract ListenableFuture<ClientHttpResponse> executeInternal(HttpHeaders headers)
			throws IOException;

}
