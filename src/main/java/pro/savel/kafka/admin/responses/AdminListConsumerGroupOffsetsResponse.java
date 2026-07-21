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
import java.util.Map;

public class AdminListConsumerGroupOffsetsResponse extends ArrayList<TopicPartitionOffsetMetadata> implements AdminResponse {

    private AdminListConsumerGroupOffsetsResponse(int initialCapacity) {
        super(initialCapacity);
    }

    public static AdminListConsumerGroupOffsetsResponse of(Map<org.apache.kafka.common.TopicPartition, org.apache.kafka.clients.consumer.OffsetAndMetadata> source) {
        if (source == null)
            return null;
        var result = new AdminListConsumerGroupOffsetsResponse(source.size());
        source.forEach((topicPartition, offsetAndMetadata) -> result.add(TopicPartitionOffsetMetadata.of(topicPartition, offsetAndMetadata)));
        return result;
    }
}
