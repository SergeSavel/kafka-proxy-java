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
import java.util.Map;

@Getter
public class StreamsGroupMemberDescription {

    private String memberId;
    private int memberEpoch;
    private String instanceId;
    private String rackId;
    private String clientId;
    private String clientHost;
    private int topologyEpoch;
    private String processId;
    private Endpoint userEndpoint;
    private Map<String, String> clientTags;
    private Collection<TaskOffset> taskOffsets;
    private Collection<TaskOffset> taskEndOffsets;
    private StreamsGroupMemberAssignment assignment;
    private StreamsGroupMemberAssignment targetAssignment;
    private boolean isClassic;

    @Getter
    public static class Endpoint {

        private String host;
        private int port;

        private Endpoint() {
        }
    }

    @Getter
    public static class TaskOffset {

        private String subtopologyId;
        private int partition;
        private long offset;

        private TaskOffset() {
        }
    }

    private StreamsGroupMemberDescription() {
    }

    public static Collection<StreamsGroupMemberDescription> of(Collection<org.apache.kafka.clients.admin.StreamsGroupMemberDescription> source) {
        if (source == null)
            return null;
        var result = new ArrayList<StreamsGroupMemberDescription>(source.size());
        source.forEach(item -> result.add(of(item)));
        return result;
    }

    public static StreamsGroupMemberDescription of(org.apache.kafka.clients.admin.StreamsGroupMemberDescription source) {
        if (source == null)
            return null;
        var result = new StreamsGroupMemberDescription();
        result.memberId = source.memberId();
        result.memberEpoch = source.memberEpoch();
        result.instanceId = source.instanceId().orElse(null);
        result.rackId = source.rackId().orElse(null);
        result.clientId = source.clientId();
        result.clientHost = source.clientHost();
        result.topologyEpoch = source.topologyEpoch();
        result.processId = source.processId();
        result.userEndpoint = mapEndpoint(source.userEndpoint().orElse(null));
        result.clientTags = source.clientTags();
        result.taskOffsets = mapTaskOffsets(source.taskOffsets());
        result.taskEndOffsets = mapTaskOffsets(source.taskEndOffsets());
        result.assignment = StreamsGroupMemberAssignment.of(source.assignment());
        result.targetAssignment = StreamsGroupMemberAssignment.of(source.targetAssignment());
        result.isClassic = source.isClassic();
        return result;
    }

    private static Endpoint mapEndpoint(org.apache.kafka.clients.admin.StreamsGroupMemberDescription.Endpoint source) {
        if (source == null)
            return null;
        var result = new Endpoint();
        result.host = source.host();
        result.port = source.port();
        return result;
    }

    private static Collection<TaskOffset> mapTaskOffsets(Collection<org.apache.kafka.clients.admin.StreamsGroupMemberDescription.TaskOffset> source) {
        if (source == null)
            return null;
        var result = new ArrayList<TaskOffset>(source.size());
        source.forEach(item -> result.add(mapTaskOffset(item)));
        return result;
    }

    private static TaskOffset mapTaskOffset(org.apache.kafka.clients.admin.StreamsGroupMemberDescription.TaskOffset source) {
        if (source == null)
            return null;
        var result = new TaskOffset();
        result.subtopologyId = source.subtopologyId();
        result.partition = source.partition();
        result.offset = source.offset();
        return result;
    }
}
