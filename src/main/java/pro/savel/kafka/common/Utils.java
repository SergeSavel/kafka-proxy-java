package pro.savel.kafka.common;

public abstract class Utils {

    public static String combineErrorMessage(Throwable throwable) {
        if (throwable == null)
            return null;
        var message = throwable.getMessage();
        var cause = throwable.getCause();
        if (cause == null)
            return message;
        var builder = new StringBuilder(message.length());
        builder.append(message);
        while (cause != null) {
            builder.append("\n");
            builder.append(cause.getMessage());
            cause = cause.getCause();
        }
        return builder.toString();
    }
}
