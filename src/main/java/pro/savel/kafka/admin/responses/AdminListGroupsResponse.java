// Copyright 2026 Sergey Savelev (serge@savel.pro)
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

package pro.savel.kafka.admin.responses;

import java.util.ArrayList;
import java.util.Collection;

public class AdminListGroupsResponse extends ArrayList<GroupListing> implements AdminResponse {

    private AdminListGroupsResponse(int initialCapacity) {
        super(initialCapacity);
    }

    public static AdminListGroupsResponse of(Collection<org.apache.kafka.clients.admin.GroupListing> source) {
        if (source == null)
            return null;
        var result = new AdminListGroupsResponse(source.size());
        source.forEach(item -> result.add(GroupListing.of(item)));
        return result;
    }
}
