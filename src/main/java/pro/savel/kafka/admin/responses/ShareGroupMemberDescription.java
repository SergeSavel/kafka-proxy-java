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
import pro.savel.kafka.common.contract.TopicPartition;

import java.util.ArrayList;
import java.util.Collection;

@Getter
public class ShareGroupMemberDescription {

    private String consumerId;
    private String clientId;
    private String host;
    private Collection<TopicPartition> assignment;
    private int memberEpoch;

    private ShareGroupMemberDescription() {
    }

    public static Collection<ShareGroupMemberDescription> of(Collection<org.apache.kafka.clients.admin.ShareMemberDescription> source) {
        if (source == null)
            return null;
        var result = new ArrayList<ShareGroupMemberDescription>(source.size());
        source.forEach(memberDescription -> result.add(of(memberDescription)));
        return result;
    }

    public static ShareGroupMemberDescription of(org.apache.kafka.clients.admin.ShareMemberDescription source) {
        if (source == null)
            return null;
        var result = new ShareGroupMemberDescription();
        result.consumerId = source.consumerId();
        result.clientId = source.clientId();
        result.host = source.host();
        result.assignment = AdminResponseMapper.mapMemberAssignment(source.assignment());
        result.memberEpoch = source.memberEpoch();
        return result;
    }
}
