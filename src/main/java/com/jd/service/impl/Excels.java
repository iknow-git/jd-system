

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Excel注解集
 * 
 * @author ruoyi
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Excels
{
    public Excel[] value();
}



    public void setDataSourceLogger(Log dataSourceLogger) {
        this.dataSourceLogger = dataSourceLogger;
        if (dataSourceLogger instanceof Log4JLogger) {
            this.dataSourceLoggerName = ((Log4JLogger) dataSourceLogger).getLogger().getName();
        }
    }

    @Override
    public String getDataSourceLoggerName() {
        return dataSourceLoggerName;
    }

    @Override
    public void setDataSourceLoggerName(String dataSourceLoggerName) {
        this.dataSourceLoggerName = dataSourceLoggerName;
        dataSourceLogger = LogFactory.getLog(dataSourceLoggerName);
    }

    @Override
    public String getConnectionLoggerName() {
        return connectionLoggerName;
    }

    @Override
    public void setConnectionLoggerName(String connectionLoggerName) {
        this.connectionLoggerName = connectionLoggerName;
        connectionLogger = LogFactory.getLog(connectionLoggerName);
    }

    public void setConnectionLogger(Log connectionLogger) {
        this.connectionLogger = connectionLogger;
        if (connectionLogger instanceof Log4JLogger) {
            this.connectionLoggerName = ((Log4JLogger) connectionLogger).getLogger().getName();
        }
    }

    @Override
    public String getStatementLoggerName() {
        return statementLoggerName;
    }

    @Override
    public void setStatementLoggerName(String statementLoggerName) {
        this.statementLoggerName = statementLoggerName;
        statementLogger = LogFactory.getLog(statementLoggerName);
    }

    public void setStatementLogger(Log statementLogger) {
        this.statementLogger = statementLogger;
        if (statementLogger instanceof Log4JLogger) {
            this.statementLoggerName = ((Log4JLogger) statementLogger).getLogger().getName();
        }
    }

    @Override
    public String getResultSetLoggerName() {
        return resultSetLoggerName;
    }

    @Override
    public void setResultSetLoggerName(String resultSetLoggerName) {
        this.resultSetLoggerName = resultSetLoggerName;
        resultSetLogger = LogFactory.getLog(resultSetLoggerName);
    }

    public void setResultSetLogger(Log resultSetLogger) {
        this.resultSetLogger = statementLogger;
        if (resultSetLogger instanceof Log4JLogger) {
            this.resultSetLoggerName = ((Log4JLogger) resultSetLogger).getLogger().getName();
        }
    }
