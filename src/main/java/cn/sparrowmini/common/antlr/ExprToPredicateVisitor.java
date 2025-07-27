package cn.sparrowmini.common.antlr;
import jakarta.persistence.criteria.*;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * String expr = "(status = 'SENT' and createdAt > '2024-07-01') or category in ('A', 'B') and expireAt is not null";
 * Predicate predicate = PredicateBuilder.buildPredicate(expr, cb, root);
 */

public class ExprToPredicateVisitor extends ExprBaseVisitor<Predicate> {

    private final CriteriaBuilder cb;
    private final Root<?> root;

    public ExprToPredicateVisitor(CriteriaBuilder cb, Root<?> root) {
        this.cb = cb;
        this.root = root;
    }

    @Override
    public Predicate visitAndExpr(ExprParser.AndExprContext ctx) {
        Predicate left = visit(ctx.expr(0));
        Predicate right = visit(ctx.expr(1));
        return cb.and(left, right);
    }

    @Override
    public Predicate visitOrExpr(ExprParser.OrExprContext ctx) {
        Predicate left = visit(ctx.expr(0));
        Predicate right = visit(ctx.expr(1));
        return cb.or(left, right);
    }

    @Override
    public Predicate visitNotExpr(ExprParser.NotExprContext ctx) {
        return cb.not(visit(ctx.expr()));
    }

    @Override
    public Predicate visitParenExpr(ExprParser.ParenExprContext ctx) {
        return visit(ctx.expr());
    }

    @Override
    public Predicate visitCompareExpr(ExprParser.CompareExprContext ctx) {
        String fieldName = ctx.field().getText();
        String operator = ctx.comparator().getText();
        Object value = parseValue(ctx.value());

        Path<?> path = root.get(fieldName);

        // 默认类型判断：字符串 or 数值
        if (value instanceof String) {
            Path<String> stringPath = (Path<String>) path;
            return switch (operator) {
                case "=" -> cb.equal(stringPath, value);
                case "!=" -> cb.notEqual(stringPath, value);
                case "like" -> cb.like(stringPath, value.toString());
                default -> throw new IllegalArgumentException("字符串不支持操作符: " + operator);
            };
        } else if (value instanceof LocalDate date) {
            Path<LocalDate> typedPath = root.get(fieldName);
            return switch (operator) {
                case ">" -> cb.greaterThan(typedPath, date);
                case "<" -> cb.lessThan(typedPath, date);
                case ">=" -> cb.greaterThanOrEqualTo(typedPath, date);
                case "<=" -> cb.lessThanOrEqualTo(typedPath, date);
                case "=" -> cb.equal(typedPath, date);
                case "!=" -> cb.notEqual(typedPath, date);
                default -> throw new IllegalArgumentException("Unsupported operator: " + operator);
            };
        } else if (value instanceof LocalDateTime dateTime) {
            Path<LocalDateTime> typedPath = root.get(fieldName);
            return switch (operator) {
                case ">" -> cb.greaterThan(typedPath, dateTime);
                case "<" -> cb.lessThan(typedPath, dateTime);
                case ">=" -> cb.greaterThanOrEqualTo(typedPath, dateTime);
                case "<=" -> cb.lessThanOrEqualTo(typedPath, dateTime);
                case "=" -> cb.equal(typedPath, dateTime);
                case "!=" -> cb.notEqual(typedPath, dateTime);
                default -> throw new IllegalArgumentException("Unsupported operator: " + operator);
            };
        } else if (value instanceof OffsetDateTime offsetDateTime) {
            Path<OffsetDateTime> typedPath = root.get(fieldName);
            return switch (operator) {
                case ">" -> cb.greaterThan(typedPath, offsetDateTime);
                case "<" -> cb.lessThan(typedPath, offsetDateTime);
                case ">=" -> cb.greaterThanOrEqualTo(typedPath, offsetDateTime);
                case "<=" -> cb.lessThanOrEqualTo(typedPath, offsetDateTime);
                case "=" -> cb.equal(typedPath, offsetDateTime);
                case "!=" -> cb.notEqual(typedPath, offsetDateTime);
                default -> throw new IllegalArgumentException("Unsupported operator: " + operator);
            };
        } else if (value instanceof Number number) {
            Path<Number> typedPath = root.get(fieldName);
            return switch (operator) {
                case ">" -> cb.gt(typedPath, number);
                case "<" -> cb.lt(typedPath, number);
                case ">=" -> cb.ge(typedPath, number);
                case "<=" -> cb.le(typedPath, number);
                case "=" -> cb.equal(typedPath, number);
                case "!=" -> cb.notEqual(typedPath, number);
                default -> throw new IllegalArgumentException("Unsupported operator: " + operator);
            };
        } else if (value instanceof String str) {
            Path<String> stringPath = root.get(fieldName);
            return switch (operator) {
                case "=" -> cb.equal(stringPath, str);
                case "!=" -> cb.notEqual(stringPath, str);
                case "like" -> cb.like(stringPath, "%" + str + "%");
                default -> throw new IllegalArgumentException("Unsupported string operator: " + operator);
            };
        } else {
            throw new IllegalArgumentException("不支持的值类型: " + value.getClass());
        }

    }
    private Object parseValue(ExprParser.ValueContext ctx) {
        if (ctx.STRING() != null) {
            String raw = ctx.STRING().getText(); // e.g. '2024-07-05T12:34:56+08:00'
            String unquoted = raw.substring(1, raw.length() - 1).replace("\\'", "'");

            try {
                // Try OffsetDateTime first (most specific)
                if (unquoted.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}[+-]\\d{2}:\\d{2}")) {
                    return java.time.OffsetDateTime.parse(unquoted);
                }
                // Then LocalDateTime
                if (unquoted.length() >= 19 && unquoted.charAt(10) == 'T') {
                    return java.time.LocalDateTime.parse(unquoted);
                }
                // Then LocalDate
                if (unquoted.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    return java.time.LocalDate.parse(unquoted);
                }
            } catch (DateTimeParseException ignored) {}

            return unquoted;
        } else if (ctx.NUMBER() != null) {
            String number = ctx.NUMBER().getText();
            return number.contains(".") ? Double.parseDouble(number) : Integer.parseInt(number);
        }
        return null;
    }

    @Override
    public Predicate visitIsNullExpr(ExprParser.IsNullExprContext ctx) {
        String field = ctx.field().getText();
        return cb.isNull(root.get(field));
    }

    @Override
    public Predicate visitIsNotNullExpr(ExprParser.IsNotNullExprContext ctx) {
        String field = ctx.field().getText();
        return cb.isNotNull(root.get(field));
    }

    @Override
    public Predicate visitInExpr(ExprParser.InExprContext ctx) {
        String fieldName = ctx.field().getText();
        Path<Object> path = root.get(fieldName);

        // 获取字段的 Java 类型
        Class<?> javaType = path.getJavaType();

        // 如果是集合类型（如 Set、List 等）
        if (Collection.class.isAssignableFrom(javaType)) {
            List<Predicate> predicates = new ArrayList<>();
            for (ExprParser.ValueContext valueCtx : ctx.valueList().value()) {
                Object val = parseValue(valueCtx);
                predicates.add(cb.isMember(val, root.get(fieldName)));
            }
            return cb.or(predicates.toArray(new Predicate[0]));
        }

        // 普通字段处理
        CriteriaBuilder.In<Object> in = cb.in(path);
        for (ExprParser.ValueContext valueCtx : ctx.valueList().value()) {
            in.value(parseValue(valueCtx));
        }
        return in;
    }


}