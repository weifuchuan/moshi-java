package com.moshi.im.common;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.19.0)",
    comments = "Source: im.proto")
public final class CourseServiceGrpc {

  private CourseServiceGrpc() {}

  public static final String SERVICE_NAME = "com.moshi.im.common.CourseService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.moshi.im.common.SubscribedCourseListReq,
      com.moshi.im.common.SubscribedCourseListReply> getSubscribedCourseListByMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SubscribedCourseListBy",
      requestType = com.moshi.im.common.SubscribedCourseListReq.class,
      responseType = com.moshi.im.common.SubscribedCourseListReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.moshi.im.common.SubscribedCourseListReq,
      com.moshi.im.common.SubscribedCourseListReply> getSubscribedCourseListByMethod() {
    io.grpc.MethodDescriptor<com.moshi.im.common.SubscribedCourseListReq, com.moshi.im.common.SubscribedCourseListReply> getSubscribedCourseListByMethod;
    if ((getSubscribedCourseListByMethod = CourseServiceGrpc.getSubscribedCourseListByMethod) == null) {
      synchronized (CourseServiceGrpc.class) {
        if ((getSubscribedCourseListByMethod = CourseServiceGrpc.getSubscribedCourseListByMethod) == null) {
          CourseServiceGrpc.getSubscribedCourseListByMethod = getSubscribedCourseListByMethod = 
              io.grpc.MethodDescriptor.<com.moshi.im.common.SubscribedCourseListReq, com.moshi.im.common.SubscribedCourseListReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "com.moshi.im.common.CourseService", "SubscribedCourseListBy"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.moshi.im.common.SubscribedCourseListReq.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.moshi.im.common.SubscribedCourseListReply.getDefaultInstance()))
                  .setSchemaDescriptor(new CourseServiceMethodDescriptorSupplier("SubscribedCourseListBy"))
                  .build();
          }
        }
     }
     return getSubscribedCourseListByMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.moshi.im.common.CourseIfSubscribedReq,
      com.moshi.im.common.CourseIfSubscribedReply> getCourseIfSubscribedMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CourseIfSubscribed",
      requestType = com.moshi.im.common.CourseIfSubscribedReq.class,
      responseType = com.moshi.im.common.CourseIfSubscribedReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.moshi.im.common.CourseIfSubscribedReq,
      com.moshi.im.common.CourseIfSubscribedReply> getCourseIfSubscribedMethod() {
    io.grpc.MethodDescriptor<com.moshi.im.common.CourseIfSubscribedReq, com.moshi.im.common.CourseIfSubscribedReply> getCourseIfSubscribedMethod;
    if ((getCourseIfSubscribedMethod = CourseServiceGrpc.getCourseIfSubscribedMethod) == null) {
      synchronized (CourseServiceGrpc.class) {
        if ((getCourseIfSubscribedMethod = CourseServiceGrpc.getCourseIfSubscribedMethod) == null) {
          CourseServiceGrpc.getCourseIfSubscribedMethod = getCourseIfSubscribedMethod = 
              io.grpc.MethodDescriptor.<com.moshi.im.common.CourseIfSubscribedReq, com.moshi.im.common.CourseIfSubscribedReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "com.moshi.im.common.CourseService", "CourseIfSubscribed"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.moshi.im.common.CourseIfSubscribedReq.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.moshi.im.common.CourseIfSubscribedReply.getDefaultInstance()))
                  .setSchemaDescriptor(new CourseServiceMethodDescriptorSupplier("CourseIfSubscribed"))
                  .build();
          }
        }
     }
     return getCourseIfSubscribedMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.moshi.im.common.SubscriberListOfCourseReq,
      com.moshi.im.common.SubscriberListOfCourseReply> getSubscriberListOfCourseMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SubscriberListOfCourse",
      requestType = com.moshi.im.common.SubscriberListOfCourseReq.class,
      responseType = com.moshi.im.common.SubscriberListOfCourseReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.moshi.im.common.SubscriberListOfCourseReq,
      com.moshi.im.common.SubscriberListOfCourseReply> getSubscriberListOfCourseMethod() {
    io.grpc.MethodDescriptor<com.moshi.im.common.SubscriberListOfCourseReq, com.moshi.im.common.SubscriberListOfCourseReply> getSubscriberListOfCourseMethod;
    if ((getSubscriberListOfCourseMethod = CourseServiceGrpc.getSubscriberListOfCourseMethod) == null) {
      synchronized (CourseServiceGrpc.class) {
        if ((getSubscriberListOfCourseMethod = CourseServiceGrpc.getSubscriberListOfCourseMethod) == null) {
          CourseServiceGrpc.getSubscriberListOfCourseMethod = getSubscriberListOfCourseMethod = 
              io.grpc.MethodDescriptor.<com.moshi.im.common.SubscriberListOfCourseReq, com.moshi.im.common.SubscriberListOfCourseReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "com.moshi.im.common.CourseService", "SubscriberListOfCourse"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.moshi.im.common.SubscriberListOfCourseReq.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.moshi.im.common.SubscriberListOfCourseReply.getDefaultInstance()))
                  .setSchemaDescriptor(new CourseServiceMethodDescriptorSupplier("SubscriberListOfCourse"))
                  .build();
          }
        }
     }
     return getSubscriberListOfCourseMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static CourseServiceStub newStub(io.grpc.Channel channel) {
    return new CourseServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static CourseServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new CourseServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static CourseServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new CourseServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class CourseServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void subscribedCourseListBy(com.moshi.im.common.SubscribedCourseListReq request,
        io.grpc.stub.StreamObserver<com.moshi.im.common.SubscribedCourseListReply> responseObserver) {
      asyncUnimplementedUnaryCall(getSubscribedCourseListByMethod(), responseObserver);
    }

    /**
     */
    public void courseIfSubscribed(com.moshi.im.common.CourseIfSubscribedReq request,
        io.grpc.stub.StreamObserver<com.moshi.im.common.CourseIfSubscribedReply> responseObserver) {
      asyncUnimplementedUnaryCall(getCourseIfSubscribedMethod(), responseObserver);
    }

    /**
     */
    public void subscriberListOfCourse(com.moshi.im.common.SubscriberListOfCourseReq request,
        io.grpc.stub.StreamObserver<com.moshi.im.common.SubscriberListOfCourseReply> responseObserver) {
      asyncUnimplementedUnaryCall(getSubscriberListOfCourseMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getSubscribedCourseListByMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.moshi.im.common.SubscribedCourseListReq,
                com.moshi.im.common.SubscribedCourseListReply>(
                  this, METHODID_SUBSCRIBED_COURSE_LIST_BY)))
          .addMethod(
            getCourseIfSubscribedMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.moshi.im.common.CourseIfSubscribedReq,
                com.moshi.im.common.CourseIfSubscribedReply>(
                  this, METHODID_COURSE_IF_SUBSCRIBED)))
          .addMethod(
            getSubscriberListOfCourseMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.moshi.im.common.SubscriberListOfCourseReq,
                com.moshi.im.common.SubscriberListOfCourseReply>(
                  this, METHODID_SUBSCRIBER_LIST_OF_COURSE)))
          .build();
    }
  }

  /**
   */
  public static final class CourseServiceStub extends io.grpc.stub.AbstractStub<CourseServiceStub> {
    private CourseServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private CourseServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CourseServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new CourseServiceStub(channel, callOptions);
    }

    /**
     */
    public void subscribedCourseListBy(com.moshi.im.common.SubscribedCourseListReq request,
        io.grpc.stub.StreamObserver<com.moshi.im.common.SubscribedCourseListReply> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getSubscribedCourseListByMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void courseIfSubscribed(com.moshi.im.common.CourseIfSubscribedReq request,
        io.grpc.stub.StreamObserver<com.moshi.im.common.CourseIfSubscribedReply> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getCourseIfSubscribedMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void subscriberListOfCourse(com.moshi.im.common.SubscriberListOfCourseReq request,
        io.grpc.stub.StreamObserver<com.moshi.im.common.SubscriberListOfCourseReply> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getSubscriberListOfCourseMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class CourseServiceBlockingStub extends io.grpc.stub.AbstractStub<CourseServiceBlockingStub> {
    private CourseServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private CourseServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CourseServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new CourseServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.moshi.im.common.SubscribedCourseListReply subscribedCourseListBy(com.moshi.im.common.SubscribedCourseListReq request) {
      return blockingUnaryCall(
          getChannel(), getSubscribedCourseListByMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.moshi.im.common.CourseIfSubscribedReply courseIfSubscribed(com.moshi.im.common.CourseIfSubscribedReq request) {
      return blockingUnaryCall(
          getChannel(), getCourseIfSubscribedMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.moshi.im.common.SubscriberListOfCourseReply subscriberListOfCourse(com.moshi.im.common.SubscriberListOfCourseReq request) {
      return blockingUnaryCall(
          getChannel(), getSubscriberListOfCourseMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class CourseServiceFutureStub extends io.grpc.stub.AbstractStub<CourseServiceFutureStub> {
    private CourseServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private CourseServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CourseServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new CourseServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.moshi.im.common.SubscribedCourseListReply> subscribedCourseListBy(
        com.moshi.im.common.SubscribedCourseListReq request) {
      return futureUnaryCall(
          getChannel().newCall(getSubscribedCourseListByMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.moshi.im.common.CourseIfSubscribedReply> courseIfSubscribed(
        com.moshi.im.common.CourseIfSubscribedReq request) {
      return futureUnaryCall(
          getChannel().newCall(getCourseIfSubscribedMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.moshi.im.common.SubscriberListOfCourseReply> subscriberListOfCourse(
        com.moshi.im.common.SubscriberListOfCourseReq request) {
      return futureUnaryCall(
          getChannel().newCall(getSubscriberListOfCourseMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_SUBSCRIBED_COURSE_LIST_BY = 0;
  private static final int METHODID_COURSE_IF_SUBSCRIBED = 1;
  private static final int METHODID_SUBSCRIBER_LIST_OF_COURSE = 2;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final CourseServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(CourseServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_SUBSCRIBED_COURSE_LIST_BY:
          serviceImpl.subscribedCourseListBy((com.moshi.im.common.SubscribedCourseListReq) request,
              (io.grpc.stub.StreamObserver<com.moshi.im.common.SubscribedCourseListReply>) responseObserver);
          break;
        case METHODID_COURSE_IF_SUBSCRIBED:
          serviceImpl.courseIfSubscribed((com.moshi.im.common.CourseIfSubscribedReq) request,
              (io.grpc.stub.StreamObserver<com.moshi.im.common.CourseIfSubscribedReply>) responseObserver);
          break;
        case METHODID_SUBSCRIBER_LIST_OF_COURSE:
          serviceImpl.subscriberListOfCourse((com.moshi.im.common.SubscriberListOfCourseReq) request,
              (io.grpc.stub.StreamObserver<com.moshi.im.common.SubscriberListOfCourseReply>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class CourseServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    CourseServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.moshi.im.common.ImGrpc.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("CourseService");
    }
  }

  private static final class CourseServiceFileDescriptorSupplier
      extends CourseServiceBaseDescriptorSupplier {
    CourseServiceFileDescriptorSupplier() {}
  }

  private static final class CourseServiceMethodDescriptorSupplier
      extends CourseServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    CourseServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (CourseServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new CourseServiceFileDescriptorSupplier())
              .addMethod(getSubscribedCourseListByMethod())
              .addMethod(getCourseIfSubscribedMethod())
              .addMethod(getSubscriberListOfCourseMethod())
              .build();
        }
      }
    }
    return result;
  }
}
