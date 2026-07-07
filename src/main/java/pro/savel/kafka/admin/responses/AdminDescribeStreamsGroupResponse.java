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
import pro.savel.kafka.admin.AdminResponseMapper;
import pro.savel.kafka.common.contract.Node;

import java.util.Collection;

import static pro.savel.kafka.common.CommonResponseMapper.mapNode;

@Getter
public class AdminDescribeStreamsGroupResponse implements AdminResponse {

    private String groupId;
    private int groupEpoch;
    private int targetAssignmentEpoch;
    private int topologyEpoch;
    private Collection<StreamsGroupSubtopologyDescription> subtopologies;
    private Collection<StreamsGroupMemberDescription> members;
    private String groupState;
    private Node coordinator;
    private Collection<String> authorizedOperations;

    private AdminDescribeStreamsGroupResponse() {
    }

    public static AdminDescribeStreamsGroupResponse map(org.apache.kafka.clients.admin.StreamsGroupDescription source) {
        if (source == null)
            return null;
        var result = new AdminDescribeStreamsGroupResponse();
        result.groupId = source.groupId();
        result.groupEpoch = source.groupEpoch();
        result.targetAssignmentEpoch = source.targetAssignmentEpoch();
        result.topologyEpoch = source.topologyEpoch();
        result.subtopologies = StreamsGroupSubtopologyDescription.map(source.subtopologies());
        result.members = StreamsGroupMemberDescription.map(source.members());
        result.groupState = AdminResponseMapper.mapGroupState(source.groupState());
        result.coordinator = mapNode(source.coordinator());
        result.authorizedOperations = AdminResponseMapper.mapAclOperations(source.authorizedOperations());
        return result;
    }
}
