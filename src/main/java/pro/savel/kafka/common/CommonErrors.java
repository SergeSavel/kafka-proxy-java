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

package pro.savel.kafka.common;

import io.netty.channel.ChannelHandlerContext;
import org.apache.kafka.common.errors.*;
import pro.savel.kafka.common.exceptions.*;

public abstract class CommonErrors {
    public static boolean handle(ChannelHandlerContext ctx, RequestBearer requestBearer, Throwable error) {
        var handled = true;
        if (error instanceof BadRequestException)
            HttpUtils.writeBadRequestAndClose(ctx, requestBearer.protocolVersion(), Utils.combineErrorMessage(error));
        else if (error instanceof NotFoundException)
            HttpUtils.writeNotFoundAndClose(ctx, requestBearer.protocolVersion(), Utils.combineErrorMessage(error));
        else if (error instanceof MethodNotAllowedException)
            HttpUtils.writeMethodNotAllowedAndClose(ctx, requestBearer.protocolVersion(), Utils.combineErrorMessage(error));
        else if (error instanceof UnauthenticatedException)
            HttpUtils.writeUnauthorizedAndClose(ctx, requestBearer.protocolVersion(), Utils.combineErrorMessage(error));
        else if (error instanceof UnauthorizedException)
            HttpUtils.writeForbiddenAndClose(ctx, requestBearer.protocolVersion(), Utils.combineErrorMessage(error));
        else if (error instanceof InterruptedException)
            HttpUtils.writeInternalServerErrorAndClose(ctx, requestBearer.protocolVersion(), Utils.combineErrorMessage(error));
        else if (error instanceof IllegalArgumentException)
            HttpUtils.writeBadRequestAndClose(ctx, requestBearer.protocolVersion(), Utils.combineErrorMessage(error));
        else if (error instanceof IllegalStateException)
            HttpUtils.writeConflictAndClose(ctx, requestBearer.protocolVersion(), Utils.combineErrorMessage(error));
        else if (error instanceof WakeupException)
            HttpUtils.writeInternalServerErrorAndClose(ctx, requestBearer.protocolVersion(), Utils.combineErrorMessage(error));
        else if (error instanceof TimeoutException)
            HttpUtils.writeInternalServerErrorAndClose(ctx, requestBearer.protocolVersion(), Utils.combineErrorMessage(error));
        else if (error instanceof AuthorizationException)
            HttpUtils.writeForbiddenAndClose(ctx, requestBearer.protocolVersion(), Utils.combineErrorMessage(error));
        else if (error instanceof AuthenticationException)
            HttpUtils.writeUnauthorizedAndClose(ctx, requestBearer.protocolVersion(), Utils.combineErrorMessage(error));
        else if (error instanceof InvalidRequestException)
            HttpUtils.writeBadRequestAndClose(ctx, requestBearer.protocolVersion(), Utils.combineErrorMessage(error));
        else if (error instanceof InvalidOffsetException)
            HttpUtils.writeBadRequestAndClose(ctx, requestBearer.protocolVersion(), Utils.combineErrorMessage(error));
        else if (error instanceof ResourceNotFoundException)
            HttpUtils.writeBadRequestAndClose(ctx, requestBearer.protocolVersion(), Utils.combineErrorMessage(error));
        else if (error instanceof DuplicateResourceException)
            HttpUtils.writeBadRequestAndClose(ctx, requestBearer.protocolVersion(), Utils.combineErrorMessage(error));
        else if (error instanceof NotControllerException)
            HttpUtils.writeConflictAndClose(ctx, requestBearer.protocolVersion(), Utils.combineErrorMessage(error));
        else if (error instanceof UnsupportedByAuthenticationException)
            HttpUtils.writeUnauthorizedAndClose(ctx, requestBearer.protocolVersion(), Utils.combineErrorMessage(error));
        else if (error instanceof UnacceptableCredentialException)
            HttpUtils.writeBadRequestAndClose(ctx, requestBearer.protocolVersion(), Utils.combineErrorMessage(error));
        else if (error instanceof UnknownTopicOrPartitionException)
            HttpUtils.writeBadRequestAndClose(ctx, requestBearer.protocolVersion(), Utils.combineErrorMessage(error));
        else if (error instanceof InvalidConfigurationException)
            HttpUtils.writeBadRequestAndClose(ctx, requestBearer.protocolVersion(), Utils.combineErrorMessage(error));
        else if (error instanceof GroupNotEmptyException)
            HttpUtils.writeConflictAndClose(ctx, requestBearer.protocolVersion(), Utils.combineErrorMessage(error));
        else if (error instanceof GroupSubscribedToTopicException)
            HttpUtils.writeConflictAndClose(ctx, requestBearer.protocolVersion(), Utils.combineErrorMessage(error));
        else if (error instanceof UnknownMemberIdException)
            HttpUtils.writeConflictAndClose(ctx, requestBearer.protocolVersion(), Utils.combineErrorMessage(error));
        else if (error instanceof TopicExistsException)
            HttpUtils.writeConflictAndClose(ctx, requestBearer.protocolVersion(), Utils.combineErrorMessage(error));
        else if (error instanceof ReassignmentInProgressException)
            HttpUtils.writeConflictAndClose(ctx, requestBearer.protocolVersion(), Utils.combineErrorMessage(error));
        else if (error instanceof BrokerNotAvailableException)
            HttpUtils.writeConflictAndClose(ctx, requestBearer.protocolVersion(), Utils.combineErrorMessage(error));
        else if (error instanceof FencedInstanceIdException)
            HttpUtils.writeConflictAndClose(ctx, requestBearer.protocolVersion(), Utils.combineErrorMessage(error));
        else
            handled = false;
        return handled;
    }
}
