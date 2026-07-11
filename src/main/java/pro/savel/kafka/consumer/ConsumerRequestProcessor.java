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

package pro.savel.kafka.consumer;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.ReferenceCountUtil;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.InvalidOffsetException;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.consumer.SubscriptionPattern;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.savel.kafka.common.*;
import pro.savel.kafka.common.exceptions.BadRequestException;
import pro.savel.kafka.consumer.requests.*;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletionException;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@ChannelHandler.Sharable
public class ConsumerRequestProcessor extends ChannelInboundHandlerAdapter implements AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerRequestProcessor.class);

    private final ConsumerProvider provider = new ConsumerProvider();

//region Overrides

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof RequestBearer bearer && bearer.request() instanceof ConsumerRequest) {
            try {
                processRequest(ctx, bearer);
            } catch (Exception e) {
                if (!handleError(ctx, bearer, e)) {
                    logger.error("An unexpected error occurred while processing consumer request.", e);
                    HttpUtils.writeInternalServerErrorAndClose(ctx, bearer.protocolVersion(), Utils.combineErrorMessage(e));
                }
            } finally {
                ReferenceCountUtil.release(msg);
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void close() {
        provider.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("An error occurred while processing consumer request.", cause);
        ctx.close();
    }

//endregion

//region Management

    private void processList(ChannelHandlerContext ctx, RequestBearer requestBearer) {
        var wrappers = provider.getItems();
        var response = ConsumerResponseMapper.mapListResponse(wrappers);
        var responseBearer = new ConsumerResponseBearer(requestBearer, HttpResponseStatus.OK, response);
        ctx.writeAndFlush(responseBearer);
    }

    private void processCreate(ChannelHandlerContext ctx, RequestBearer requestBearer) {
        var request = (ConsumerCreateRequest) requestBearer.request();
        var owner = ctx.channel().attr(NettyAttributes.USERNAME).get();
        var wrapper = provider.createConsumer(request.getName(), request.getConfig(), request.getExpirationTimeout(), owner);
        var response = ConsumerResponseMapper.mapCreateResponse(wrapper);
        var responseBearer = new ConsumerResponseBearer(requestBearer, HttpResponseStatus.CREATED, response);
        ctx.writeAndFlush(responseBearer);
    }

    private void processRemove(ChannelHandlerContext ctx, RequestBearer requestBearer) {
        var request = (ConsumerReleaseRequest) requestBearer.request();
        provider.removeConsumer(request.getConsumerId(), request.getToken());
        var responseBearer = new ConsumerResponseBearer(requestBearer, HttpResponseStatus.NO_CONTENT, null);
        ctx.writeAndFlush(responseBearer);
    }

    private void processTouch(ChannelHandlerContext ctx, RequestBearer requestBearer) {
        var request = (ConsumerTouchRequest) requestBearer.request();
        var wrapper = provider.getConsumer(request.getConsumerId(), request.getToken());
        wrapper.touch();
        var responseBearer = new ConsumerResponseBearer(requestBearer, HttpResponseStatus.NO_CONTENT, null);
        ctx.writeAndFlush(responseBearer);
    }

//endregion

    private void processPoll(ChannelHandlerContext ctx, RequestBearer requestBearer) {
        var request = (ConsumerPollRequest) requestBearer.request();
        var wrapper = provider.getConsumer(request.getConsumerId(), request.getToken());
        wrapper.touch();
        var consumer = wrapper.getConsumer();
        ConsumerRecords<byte[], byte[]> records;
        records = consumer.poll(Duration.ofMillis(request.getTimeout()));
        var response = ConsumerResponseMapper.mapPollResponse(records);
        var responseBearer = new ConsumerResponseBearer(requestBearer, HttpResponseStatus.OK, response);
        ctx.writeAndFlush(responseBearer);
    }

    private void processCommit(ChannelHandlerContext ctx, RequestBearer requestBearer) {
        var request = (ConsumerPollRequest) requestBearer.request();
        var wrapper = provider.getConsumer(request.getConsumerId(), request.getToken());
        wrapper.touch();
        var consumer = wrapper.getConsumer();
        var callback = new org.apache.kafka.clients.consumer.OffsetCommitCallback() {
            @Override
            public void onComplete(Map<TopicPartition, OffsetAndMetadata> offsets, Exception exception) {
                if (exception == null) {
                    var responseBearer = new ConsumerResponseBearer(requestBearer, HttpResponseStatus.NO_CONTENT, null);
                    ctx.writeAndFlush(responseBearer);
                } else {
                    logger.error("Unable to commit offsets.", exception);
                    HttpUtils.writeInternalServerErrorAndClose(ctx, requestBearer.protocolVersion(), exception.getMessage());
                }
            }
        };
        consumer.commitAsync(callback);
    }

    private void processAssign(ChannelHandlerContext ctx, RequestBearer requestBearer) {
        var request = (ConsumerAssignRequest) requestBearer.request();
        var wrapper = provider.getConsumer(request.getConsumerId(), request.getToken());
        wrapper.touch();
        var consumer = wrapper.getConsumer();
        var assignment = CommonRequestMapper.mapPartitions(request.getPartitions());
        consumer.assign(assignment);
        var responseBearer = new ConsumerResponseBearer(requestBearer, HttpResponseStatus.NO_CONTENT, null);
        ctx.writeAndFlush(responseBearer);
    }

    private void processGetAssignment(ChannelHandlerContext ctx, RequestBearer requestBearer) {
        var request = (ConsumerGetAssignmentRequest) requestBearer.request();
        var wrapper = provider.getConsumer(request.getConsumerId(), request.getToken());
        wrapper.touch();
        var consumer = wrapper.getConsumer();
        var assignment = consumer.assignment();
        var response = ConsumerResponseMapper.mapAssignmentResponse(assignment);
        var responseBearer = new ConsumerResponseBearer(requestBearer, HttpResponseStatus.OK, response);
        ctx.writeAndFlush(responseBearer);
    }

    private void processSeek(ChannelHandlerContext ctx, RequestBearer requestBearer) {
        var request = (ConsumerSeekRequest) requestBearer.request();
        var wrapper = provider.getConsumer(request.getConsumerId(), request.getToken());
        wrapper.touch();
        var consumer = wrapper.getConsumer();
        var topicPartition = new TopicPartition(request.getTopic(), request.getPartition());
        consumer.seek(topicPartition, request.getOffset());
        var responseBearer = new ConsumerResponseBearer(requestBearer, HttpResponseStatus.NO_CONTENT, null);
        ctx.writeAndFlush(responseBearer);
    }

    private void processRequest(ChannelHandlerContext ctx, RequestBearer requestBearer) {
        var requestClass = requestBearer.request().getClass();
        if (requestClass == ConsumerPollRequest.class)
            processPoll(ctx, requestBearer);
        else if (requestClass == ConsumerCommitRequest.class)
            processCommit(ctx, requestBearer);
        else if (requestClass == ConsumerSeekRequest.class)
            processSeek(ctx, requestBearer);
        else if (requestClass == ConsumerListPartitionsRequest.class)
            processListPartitions(ctx, requestBearer);
        else if (requestClass == ConsumerAssignRequest.class)
            processAssign(ctx, requestBearer);
        else if (requestClass == ConsumerSubscribeRequest.class)
            processSubscribe(ctx, requestBearer);
        else if (requestClass == ConsumerGetBeginningOffsetsRequest.class)
            processGetBeginningOffsets(ctx, requestBearer);
        else if (requestClass == ConsumerGetEndOffsetsRequest.class)
            processGetEndOffsets(ctx, requestBearer);
        else if (requestClass == ConsumerListTopicsRequest.class)
            processListTopics(ctx, requestBearer);
        else if (requestClass == ConsumerGetPositionRequest.class)
            processGetPosition(ctx, requestBearer);
        else if (requestClass == ConsumerGetAssignmentRequest.class)
            processGetAssignment(ctx, requestBearer);
        else if (requestClass == ConsumerGetSubscriptionRequest.class)
            processGetSubscription(ctx, requestBearer);
        else if (requestClass == ConsumerCreateRequest.class)
            processCreate(ctx, requestBearer);
        else if (requestClass == ConsumerReleaseRequest.class)
            processRemove(ctx, requestBearer);
        else if (requestClass == ConsumerListRequest.class)
            processList(ctx, requestBearer);
        else if (requestClass == ConsumerTouchRequest.class)
            processTouch(ctx, requestBearer);
        else
            throw new RuntimeException("Unexpected consumer request type: " + requestClass.getName());
    }

    private void processSubscribe(ChannelHandlerContext ctx, RequestBearer requestBearer) {
        var request = (ConsumerSubscribeRequest) requestBearer.request();
        var wrapper = provider.getConsumer(request.getConsumerId(), request.getToken());
        wrapper.touch();
        var consumer = wrapper.getConsumer();
        if (request.getTopics() != null)
            consumer.subscribe(request.getTopics());
        else {
            var pattern = new SubscriptionPattern(request.getPattern());
            consumer.subscribe(pattern);
        }
        var responseBearer = new ConsumerResponseBearer(requestBearer, HttpResponseStatus.NO_CONTENT, null);
        ctx.writeAndFlush(responseBearer);
    }

    private void processGetSubscription(ChannelHandlerContext ctx, RequestBearer requestBearer) {
        var request = (ConsumerGetSubscriptionRequest) requestBearer.request();
        var wrapper = provider.getConsumer(request.getConsumerId(), request.getToken());
        wrapper.touch();
        var consumer = wrapper.getConsumer();
        var subscription = consumer.subscription();
        var response = ConsumerResponseMapper.mapSubscriptionResponse(subscription);
        var responseBearer = new ConsumerResponseBearer(requestBearer, HttpResponseStatus.OK, response);
        ctx.writeAndFlush(responseBearer);
    }

    private void processGetPosition(ChannelHandlerContext ctx, RequestBearer requestBearer) {
        var request = (ConsumerGetPositionRequest) requestBearer.request();
        var wrapper = provider.getConsumer(request.getConsumerId(), request.getToken());
        wrapper.touch();
        var consumer = wrapper.getConsumer();
        var topicPartition = new TopicPartition(request.getTopic(), request.getPartition());
        long position = consumer.position(topicPartition);
        var response = ConsumerResponseMapper.mapPositionResponse(position);
        var responseBearer = new ConsumerResponseBearer(requestBearer, HttpResponseStatus.OK, response);
        ctx.writeAndFlush(responseBearer);
    }

    private void processListPartitions(ChannelHandlerContext ctx, RequestBearer requestBearer) {
        var request = (ConsumerListPartitionsRequest) requestBearer.request();
        var wrapper = provider.getConsumer(request.getConsumerId(), request.getToken());
        wrapper.touch();
        var consumer = wrapper.getConsumer();
        var partitions = consumer.partitionsFor(request.getTopic());
        var response = ConsumerResponseMapper.mapPartitionsResponse(partitions);
        var responseBearer = new ConsumerResponseBearer(requestBearer, HttpResponseStatus.OK, response);
        ctx.writeAndFlush(responseBearer);
    }

    private void processGetBeginningOffsets(ChannelHandlerContext ctx, RequestBearer requestBearer) {
        var request = (ConsumerGetBeginningOffsetsRequest) requestBearer.request();
        var wrapper = provider.getConsumer(request.getConsumerId(), request.getToken());
        wrapper.touch();
        var consumer = wrapper.getConsumer();
        var partitions = CommonRequestMapper.mapPartitions(request.getPartitions());
        var offsets = consumer.beginningOffsets(partitions);
        var response = ConsumerResponseMapper.mapOffsetsResponse(offsets);
        var responseBearer = new ConsumerResponseBearer(requestBearer, HttpResponseStatus.OK, response);
        ctx.writeAndFlush(responseBearer);
    }

    private void processGetEndOffsets(ChannelHandlerContext ctx, RequestBearer requestBearer) {
        var request = (ConsumerGetEndOffsetsRequest) requestBearer.request();
        var wrapper = provider.getConsumer(request.getConsumerId(), request.getToken());
        wrapper.touch();
        var consumer = wrapper.getConsumer();
        var partitions = CommonRequestMapper.mapPartitions(request.getPartitions());
        var offsets = consumer.endOffsets(partitions);
        var response = ConsumerResponseMapper.mapOffsetsResponse(offsets);
        var responseBearer = new ConsumerResponseBearer(requestBearer, HttpResponseStatus.OK, response);
        ctx.writeAndFlush(responseBearer);
    }

    private void processListTopics(ChannelHandlerContext ctx, RequestBearer requestBearer) {
        var request = (ConsumerListTopicsRequest) requestBearer.request();
        var wrapper = provider.getConsumer(request.getConsumerId(), request.getToken());
        wrapper.touch();
        var consumer = wrapper.getConsumer();
        var topics = consumer.listTopics();
        if (request.getPattern() != null) {
            Pattern pattern;
            try {
                pattern = Pattern.compile(request.getPattern());
            } catch (PatternSyntaxException e) {
                throw new BadRequestException("Invalid pattern.", e);
            }
            topics.keySet().removeIf(topic -> !pattern.matcher(topic).matches());
        }
        var response = ConsumerResponseMapper.mapTopicsResponse(topics);
        var responseBearer = new ConsumerResponseBearer(requestBearer, HttpResponseStatus.OK, response);
        ctx.writeAndFlush(responseBearer);
    }

    private static boolean handleError(ChannelHandlerContext ctx, RequestBearer requestBearer, Throwable error) {
        var handled = true;
        if (error instanceof CompletionException)
            handled = handleError(ctx, requestBearer, error.getCause());
        else if (error instanceof InvalidOffsetException e)
            HttpUtils.writeConflictAndClose(ctx, requestBearer.protocolVersion(), Utils.combineErrorMessage(e));
        else if (!CommonErrors.handle(ctx, requestBearer, error))
            handled = false;
        else
            handled = false;
        return handled;
    }
}