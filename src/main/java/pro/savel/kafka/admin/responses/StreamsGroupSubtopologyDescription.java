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
import java.util.HashMap;
import java.util.Map;

@Getter
public class StreamsGroupSubtopologyDescription {

    private String subtopologyId;
    private Collection<String> sourceTopics;
    private Collection<String> repartitionSinkTopics;
    private Map<String, TopicInfo> stateChangelogTopics;
    private Map<String, TopicInfo> repartitionSourceTopics;

    @Getter
    public static class TopicInfo {

        private int partitions;
        private int replicationFactor;
        private Map<String, String> topicConfigs;

        private TopicInfo() {
        }
    }

    private StreamsGroupSubtopologyDescription() {
    }

    public static Collection<StreamsGroupSubtopologyDescription> of(Collection<org.apache.kafka.clients.admin.StreamsGroupSubtopologyDescription> source) {
        if (source == null)
            return null;
        var result = new ArrayList<StreamsGroupSubtopologyDescription>(source.size());
        source.forEach(item -> result.add(of(item)));
        return result;
    }

    public static StreamsGroupSubtopologyDescription of(org.apache.kafka.clients.admin.StreamsGroupSubtopologyDescription source) {
        if (source == null)
            return null;
        var result = new StreamsGroupSubtopologyDescription();
        result.subtopologyId = source.subtopologyId();
        result.sourceTopics = source.sourceTopics();
        result.repartitionSinkTopics = source.repartitionSinkTopics();
        result.stateChangelogTopics = mapTopicInfos(source.stateChangelogTopics());
        result.repartitionSourceTopics = mapTopicInfos(source.repartitionSourceTopics());
        return result;
    }

    private static TopicInfo mapTopicInfo(org.apache.kafka.clients.admin.StreamsGroupSubtopologyDescription.TopicInfo source) {
        if (source == null)
            return null;
        var result = new TopicInfo();
        result.partitions = source.partitions();
        result.replicationFactor = source.replicationFactor();
        result.topicConfigs = source.topicConfigs();
        return result;
    }

    private static Map<String, TopicInfo> mapTopicInfos(Map<String, org.apache.kafka.clients.admin.StreamsGroupSubtopologyDescription.TopicInfo> source) {
        if (source == null)
            return null;
        var result = new HashMap<String, TopicInfo>(source.size());
        source.forEach((topicName, topicInfoSource) -> result.put(topicName, mapTopicInfo(topicInfoSource)));
        return result;
    }
}
