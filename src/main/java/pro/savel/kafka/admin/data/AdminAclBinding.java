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

package pro.savel.kafka.admin.data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminAclBinding {

    @NotNull
    private ResourcePattern pattern;
    @NotNull
    private AccessControlEntry entry;

    @Data
    public static class ResourcePattern {
        @NotEmpty
        private String resourceType;
        @NotNull
        private String name;
        @NotEmpty
        private String patternType;
    }

    @Data
    public static class AccessControlEntry {
        @NotNull
        private String principal;
        @NotNull
        private String host;
        @NotEmpty
        private String operation;
        @NotEmpty
        private String permissionType;
    }
}
