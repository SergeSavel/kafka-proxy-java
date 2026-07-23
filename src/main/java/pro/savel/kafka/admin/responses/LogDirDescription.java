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

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;

@Getter
public class LogDirDescription {

    private String path;
    private String errorMessage;
    private Collection<TopicPartitionReplicaInfo> replicaInfos;

    private LogDirDescription() {
    }

    public static LogDirDescription of(String path, org.apache.kafka.clients.admin.LogDirDescription source) {
        var result = new LogDirDescription();
        result.path = path;
        if (source != null) {
            result.errorMessage = source.error() == null ? null : source.error().getMessage();
            var sourceReplicaInfos = source.replicaInfos();
            if (sourceReplicaInfos != null) {
                result.replicaInfos = new ArrayList<>(sourceReplicaInfos.size());
                sourceReplicaInfos.forEach((topicPartitionSource, replicaInfoSource) -> {
                    var item = TopicPartitionReplicaInfo.of(topicPartitionSource.topic(), topicPartitionSource.partition(), replicaInfoSource);
                    result.replicaInfos.add(item);
                });
            }
        }
        return result;
    }
}
