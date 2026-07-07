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
public class StreamsGroupMemberAssignment {

    private Collection<TaskIds> activeTasks;
    private Collection<TaskIds> standbyTasks;
    private Collection<TaskIds> warmupTasks;

    @Getter
    public static class TaskIds {

        private String subtopologyId;
        private Collection<Integer> partitions;

        private TaskIds() {
        }
    }

    private StreamsGroupMemberAssignment() {
    }

    public static Collection<StreamsGroupMemberAssignment> map(Collection<org.apache.kafka.clients.admin.StreamsGroupMemberAssignment> source) {
        if (source == null)
            return null;
        var result = new ArrayList<StreamsGroupMemberAssignment>(source.size());
        source.forEach(item -> result.add(map(item)));
        return result;
    }

    public static StreamsGroupMemberAssignment map(org.apache.kafka.clients.admin.StreamsGroupMemberAssignment source) {
        if (source == null)
            return null;
        var result = new StreamsGroupMemberAssignment();
        result.activeTasks = mapTaskIdsCollection(source.activeTasks());
        result.standbyTasks = mapTaskIdsCollection(source.standbyTasks());
        result.warmupTasks = mapTaskIdsCollection(source.warmupTasks());
        return result;
    }

    private static Collection<TaskIds> mapTaskIdsCollection(Collection<org.apache.kafka.clients.admin.StreamsGroupMemberAssignment.TaskIds> source) {
        if (source == null)
            return null;
        var result = new ArrayList<TaskIds>(source.size());
        source.forEach(item -> result.add(mapTaskIds(item)));
        return result;
    }

    private static TaskIds mapTaskIds(org.apache.kafka.clients.admin.StreamsGroupMemberAssignment.TaskIds source) {
        if (source == null)
            return null;
        var result = new TaskIds();
        result.subtopologyId = source.subtopologyId();
        result.partitions = source.partitions();
        return result;
    }
}
