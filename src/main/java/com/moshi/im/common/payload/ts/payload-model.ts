export interface IPayload{

}

export interface ICallReqPayload extends IPayload {
    
    
    payload: string;
    
    
    action: string;
    
    
    id: string;
    
}

export interface ICallRespPayload extends IPayload {
    
    
    ret: string;
    
    
    id: string;
    
}

export interface IChatReqPayload extends IPayload {
    
    // 发送到 用户id || 群号
    to: string;
    
    // 聊天室类型：1对1（1）、群聊（2）
    type: number;
    
    
    uuid: string;
    
    // 内容
    content: string;
    
}

export interface IChatRespPayload extends IPayload {
    
    // ok | fail
    state: string;
    
    
    uuid: string;
    
}

export interface IClearRemindReqPayload extends IPayload {
    
    
    roomKey: string;
    
}

export interface IHeartbeatPayload extends IPayload {
    
}

export interface IJoinGroupNotifyPushPayload extends IPayload {
    
    
    accountId: number;
    
    
    nickName: string;
    
    
    groupId: number;
    
    
    avatar: string;
    
}

export interface IJoinGroupPayload extends IPayload {
    
    
    accountId: number;
    
    
    groupId: number;
    
}

export interface IOnlineNotifySubscribeReqPayload extends IPayload {
    
    
    who: string[];
    
}

export interface IOnlineOfflinePushPayload extends IPayload {
    
    
    nickName: string;
    
    
    id: string;
    
    
    avatar: string;
    
}

export interface IRemindPushPayload extends IPayload {
    
    
    from: string;
    
    
    msgKey: string;
    
    
    sendAt: number;
    
    
    to: string;
    
    
    roomKey: string;
    
    
    type: number;
    
    // 消息内容
    content: string;
    
}
