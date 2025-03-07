
package com.baomidou.mybatisplus.annotation;

/**
 * 字段策略枚举类
 * <p>
 * 如果字段是基本数据类型则最终效果等同于 {@link #IGNORED}
 *
 * @author hubin
 * @since 2016-09-09
 */
public enum FieldStrategy {
    /**
     * 忽略判断
     */
    IGNORED,
    /**
     * 非NULL判断
     */
    NOT_NULL,
    /**
     * 非空判断(只对字符串类型字段,其他类型字段依然为非NULL判断)
     */
    NOT_EMPTY,
    /**
     * 默认的,一般只用于注解里
     * <p>1. 在全局里代表 NOT_NULL</p>
     * <p>2. 在注解里代表 跟随全局</p>
     */
    DEFAULT,
    /**
     * 不加入 SQL
     */
    NEVER
}





  private void parse(String expression) {
    int p = skipWS(expression, 0);
    if (expression.charAt(p) == '(') {
      expression(expression, p + 1);
    } else {
      property(expression, p);
    }
  }

  private void expression(String expression, int left) {
    int match = 1;
    int right = left + 1;
    while (match > 0) {
      if (expression.charAt(right) == ')') {
        match--;
      } else if (expression.charAt(right) == '(') {
        match++;
      }
      right++;
    }
    put("expression", expression.substring(left, right - 1));
    jdbcTypeOpt(expression, right);
  }

  private void property(String expression, int left) {
    if (left < expression.length()) {
      int right = skipUntil(expression, left, ",:");
      put("property", trimmedStr(expression, left, right));
      jdbcTypeOpt(expression, right);
    }
  }

  private int skipWS(String expression, int p) {
    for (int i = p; i < expression.length(); i++) {
      if (expression.charAt(i) > 0x20) {
        return i;
      }
    }
    return expression.length();
  }

  private int skipUntil(String expression, int p, final String endChars) {
    for (int i = p; i < expression.length(); i++) {
      char c = expression.charAt(i);
      if (endChars.indexOf(c) > -1) {
        return i;
      }
    }
    return expression.length();
  }

  private void jdbcTypeOpt(String expression, int p) {
    p = skipWS(expression, p);
    if (p < expression.length()) {
      if (expression.charAt(p) == ':') {
        jdbcType(expression, p + 1);
      } else if (expression.charAt(p) == ',') {
        option(expression, p + 1);
      } else {
        throw new BuilderException("Parsing error in {" + expression + "} in position " + p);
      }
    }
  }

  private void jdbcType(String expression, int p) {
    int left = skipWS(expression, p);
    int right = skipUntil(expression, left, ",");
    if (right > left) {
      put("jdbcType", trimmedStr(expression, left, right));
    } else {
      throw new BuilderException("Parsing error in {" + expression + "} in position " + p);
    }
    option(expression, right + 1);
  }

  private void option(String expression, int p) {
    int left = skipWS(expression, p);
    if (left < expression.length()) {
      int right = skipUntil(expression, left, "=");
      String name = trimmedStr(expression, left, right);
      left = right + 1;
      right = skipUntil(expression, left, ",");
      String value = trimmedStr(expression, left, right);
      put(name, value);
      option(expression, right + 1);
    }
  }

  private String trimmedStr(String str, int start, int end) {
    while (str.charAt(start) <= 0x20) {
      start++;
    }
    while (str.charAt(end - 1) <= 0x20) {
      end--;
    }
    return start >= end ? "" : str.substring(start, end);
  }
