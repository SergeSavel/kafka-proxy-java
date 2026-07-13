// Copyright 2025 Sergey Savelev (serge@savel.pro)
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

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
