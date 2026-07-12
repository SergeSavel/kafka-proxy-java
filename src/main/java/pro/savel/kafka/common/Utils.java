package pro.savel.kafka.common;

public abstract class Utils {

    public static String combineErrorMessage(Throwable throwable) {
        if (throwable == null)
            return null;
        var builder = new StringBuilder();
        while (throwable != null) {
            var message = throwable.getMessage();
            if (message != null) {
                if (!builder.isEmpty())
                    builder.append("\n");
                builder.append(message);
            }
            throwable = throwable.getCause();
        }
        if (builder.isEmpty())
            return null;
        return builder.toString();
    }
}
